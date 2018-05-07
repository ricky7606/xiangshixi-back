package thu.declan.xi.server.resource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Company;
import thu.declan.xi.server.model.Position;
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.model.PointLog.PType;
import thu.declan.xi.server.model.QueryModel;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.model.Resume.RState;
import thu.declan.xi.server.model.Student;
import thu.declan.xi.server.service.CompanyService;
import thu.declan.xi.server.service.PositionService;
import thu.declan.xi.server.service.ResumeService;
import thu.declan.xi.server.service.StudentService;

/**
 *
 * @author declan
 */
@Path("positions")
@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_COMPANY})
public class PositionResource extends BaseResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(PositionResource.class);

	@Autowired
	private StudentService studentService;

	@Autowired
	private PositionService positionService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private ResumeService resumeService;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// 职位账号注册
	public Position createPosition(@Valid Position position) throws ApiException {
		LOGGER.debug("==================== enter PositionResource createPosition ====================");
		if (Account.Role.COMPANY.equals(currentRole())) {
			try {
				Company comp = companyService.getByAccountId(currentAccountId());
				position.setCompanyId(comp.getId());
			} catch (ServiceException ex) {
				handleServiceException(ex);
			}
		}
		try {
			positionService.add(position);
			addPoint(PType.POSITION, position.getId());
		} catch (ServiceException ex) {
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PositionResource createPosition ====================");
		return position;
	}

	@PUT
	@Path("/{positionId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// 修改职位信息
	public Position editPosition(@PathParam("positionId") int positionId, Position position) throws ApiException {
		LOGGER.debug("==================== enter PositionResource editPosition ====================");
		LOGGER.debug("positionId: " + positionId);
		if (currentRole() == Account.Role.COMPANY) {
			try {
				Position oldPos = positionService.get(positionId);
				if (!Objects.equals(oldPos.getCompanyId(), currentEntityId())) {
					throw new ApiException(403, "Company Id not equal to authorized one", "权限不足");
				}
			} catch (ServiceException ex) {
				throw new ApiException(404, "Position not found", "职位id错误");
			}
		}
		try {
			position.setId(positionId);
			positionService.update(position);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该职位不存在！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PositionResource editPosition ====================");
		return position;
	}

	@GET
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_COMPANY, Constant.ROLE_STUDENT})
	public ListResponse<Position> getPositions(@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize,
			@QueryParam("keyword") String keyword,
			@QueryParam("verified") Boolean verified,
			@QueryParam("industry") List<String> industry,
			@QueryParam("type") List<String> type,
            @QueryParam("ptype") List<String> ptype,
			@QueryParam("area") String area,
            @QueryParam("active") Boolean active) throws ApiException {
		LOGGER.debug("==================== enter PositionResource getPositions ====================");
		Position selector = new Position();
		selector.setActive(active);
		Company compSel = new Company();
		compSel.setVerified(verified);
		if (industry != null) {
			if (industry.size() == 1) {
				compSel.setIndustry(industry.get(0));
			} else if (industry.size() > 1) {
				compSel.setQueryParam("industry", industry);
			}
		}
		if (type != null) {
			if (type.size() == 1) {
				compSel.setType(type.get(0));
			} else if (type.size() > 1) {
				compSel.setQueryParam("type", type);
			}
		}
		if (ptype != null) {
			if (ptype.size() == 1) {
				selector.setPtype(ptype.get(0));
			} else if (ptype.size() > 1) {
				selector.setQueryParam("ptype", ptype);
			}
		}
		selector.setArea(area);
		selector.setCompany(compSel);
		if (keyword != null) {
			selector.setQueryParam(QueryModel.SEARCH_KEY, keyword);
		}
		List<Position> positions = null;
		Pagination pagination = new Pagination(pageSize, pageIndex);
		try {
			positions = positionService.getList(selector, pagination);
			if (Account.Role.STUDENT.equals(currentRole())) {
				for (Position position : positions) {
					positionService.setCollected(currentEntityId(), position);
				}
			}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PositionResource getPositions ====================");
		return new ListResponse(positions, pagination);
	}

	@GET
	@Path("/subscription")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_STUDENT})
	public ListResponse<Position> getSubscribedPositions(@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
		LOGGER.debug("==================== enter PositionResource getSubscribedPositions ====================");
		Student stu = null;
		try {
			stu = studentService.get(currentEntityId());
		} catch (ServiceException ex) {
			java.util.logging.Logger.getLogger(PositionResource.class.getName()).log(Level.SEVERE, null, ex);
		}
		Boolean verified = null;
		List<String> industry = null;
		List<String> type = null;
		List<String> ptype = null;
		String area = null;
		Map<String, String> sub = null;
		if (stu != null) {
			sub = stu.getSubscription();
		}
		if (sub != null) {
			String industryStr = sub.get("industry");
			if (industryStr != null) {
				industry = Arrays.asList(industryStr.split("&"));
			}
			String typeStr = sub.get("type");
			if (typeStr != null) {
				type = Arrays.asList(typeStr.split("&"));
			}
			String ptypeStr = sub.get("ptype");
			if (typeStr != null) {
				ptype = Arrays.asList(ptypeStr.split("&"));
			}
			area = sub.get("area");
		}
		LOGGER.debug("==================== leave PositionResource getSubscribedPositions ====================");
		return this.getPositions(pageIndex, pageSize, null, verified, industry, type, ptype, area, true);
	}

	@GET
	@PermitAll
	@Path("/{positionId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Position getPosition(@PathParam("positionId") int positionId) throws ApiException {
		LOGGER.debug("==================== enter PositionResource getPosition ====================");
		LOGGER.debug("positionId: " + positionId);
		Position position = null;
		try {
			position = positionService.get(positionId);
			if (Account.Role.STUDENT.equals(currentRole())) {
				positionService.setCollected(currentEntityId(), position);
			}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该职位不存在！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PositionResource getPosition ====================");
		return position;
	}

	@GET
	@Path("/{positionId}/resumes")
	@Produces(MediaType.APPLICATION_JSON)
	public ListResponse<Resume> getPositionResumes(@PathParam("positionId") int positionId,
			@QueryParam("state") List<RState> states,
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
		LOGGER.debug("==================== enter PositionResource getResumes ====================");
		LOGGER.debug(states.toString());
		Resume selector = new Resume();
		if (!states.isEmpty()) {
			selector.setQueryStates(states);
		}
		selector.setPositionId(positionId);
		List<Resume> resumes = null;
		Pagination pagination = new Pagination(pageSize, pageIndex);
		try {
			resumes = resumeService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PositionResource getResumes ====================");
		return new ListResponse(resumes, pagination);
	}

	@POST
	@Path("/{positionId}/collect")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_STUDENT})
	public Position collectPosition(@PathParam("positionId") int positionId) throws ApiException {
		LOGGER.debug("==================== enter PositionResource collectPosition ====================");
		try {
			Position pos = positionService.get(positionId);
			positionService.collect(currentEntityId(), positionId);
			pos.setCollected(true);
			return pos;
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_UK_CONSTRAINT) {
				throw new ApiException(403, devMsg, "已经收藏！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PositionResource collectPosition ====================");
		return null;
	}

	@DELETE
	@Path("/{positionId}/collect")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_STUDENT})
	public Position uncollectPosition(@PathParam("positionId") int positionId) throws ApiException {
		LOGGER.debug("==================== enter PositionResource uncollectPosition ====================");
		try {
			Position pos = positionService.get(positionId);
			positionService.uncollect(currentEntityId(), positionId);
			pos.setCollected(false);
			return pos;
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_UK_CONSTRAINT) {
				throw new ApiException(403, devMsg, "未收藏！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PositionResource uncollectPosition ====================");
		return null;
	}

	@GET
	@Path("/collect")
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_STUDENT})
	public ListResponse<Position> collectedPositions(
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
		LOGGER.debug("==================== enter PositionResource collectedPositions ====================");
		List<Position> positions = null;
		Pagination pagination = new Pagination(pageSize, pageIndex);
		try {
			positions = positionService.getCollectedList(currentEntityId(), pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave PositionResource collectedPositions ====================");
		return new ListResponse(positions, pagination);
	}

}
