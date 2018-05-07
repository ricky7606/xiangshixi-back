package thu.declan.xi.server.resource;

import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Company;
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.Notification;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.model.PointLog;
import thu.declan.xi.server.model.Position;
import thu.declan.xi.server.model.QueryModel;
import thu.declan.xi.server.model.Rate;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.model.Salary;
import thu.declan.xi.server.model.Student;
import thu.declan.xi.server.service.CompanyService;
import thu.declan.xi.server.service.PositionService;
import thu.declan.xi.server.service.RateService;
import thu.declan.xi.server.service.ResumeService;
import thu.declan.xi.server.service.SalaryService;
import thu.declan.xi.server.service.StudentService;

/**
 *
 * @author declan
 */
@Path("companies")
@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_COMPANY})
public class CompanyResource extends BaseResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyResource.class);

    @Autowired
    private CompanyService companyService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private PositionService positionService;

    @Autowired
    private ResumeService resumeService;
	
	@Autowired
	private RateService rateService;
	
	@Autowired
	private SalaryService salaryService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    // 企业账号注册
    public Company createCompany(@Valid Company company) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource createCompany ====================");
        Account acc = company.getAccount();
        acc.setRole(Account.Role.COMPANY);
        AccountResource accRes = new AccountResource();
        beanFactory.autowireBean(accRes);
        String pwd = acc.getPassword();
        acc = accRes.createAccount(acc);
        company.setAccount(acc);
        try {
            company.setAccountId(acc.getId());
            companyService.add(company);
            authService.login(acc.getPhone(), pwd, Account.Role.COMPANY);
            addPoint(PointLog.PType.REGISTER, acc.getId());
			notiService.addNoti(0, Notification.NType.COMPANY, company.getId(), Notification.TPL_BACK_COMPANY_CREATION, acc.getPhone());
			if (company.getCert() != null) {
				notiService.addNoti(0, Notification.NType.COMPANY, company.getId(), Notification.TPL_BACK_COMPANY_VERIFY, acc.getPhone());
			}
        } catch (ServiceException ex) {
            accRes.deleteAccount(acc.getId());
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave CompanyResource createCompany ====================");
        return company;
    }

    @PUT
    @Path("/{companyId}")
    @Produces(MediaType.APPLICATION_JSON)
    // 修改企业信息
    public Company editCompany(@PathParam("companyId") int companyId, Company company) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource editCompany ====================");
        LOGGER.debug("companyId: " + companyId);
        if (currentRole() == Account.Role.COMPANY) {
            if (companyId == 0) {
                companyId = currentEntityId();
            } else if (companyId != currentEntityId()) {
                throw new ApiException(403, "Company Id not equal to authorized one", "权限不足");
            }
			company.setFrozen(null);
            company.setVerified(null);
        }
        try {
            company.setId(companyId);
            companyService.update(company);
            if (company.isVerified() != null) {
                Company com = companyService.get(companyId);
                if (company.isVerified()) {
                    notiService.addNoti(com.getAccountId(), Notification.NType.BACKEND, companyId, Notification.TPL_COMPANY_VERIFY_SUC, com.getName());
                } else {
                    notiService.addNoti(com.getAccountId(), Notification.NType.BACKEND, companyId, Notification.TPL_COMPANY_VERIFY_FAIL, com.getName());
                }
            }
			if (company.getCert() != null) {
				Company com = companyService.get(companyId);
				notiService.addNoti(0, Notification.NType.COMPANY, company.getId(), Notification.TPL_BACK_COMPANY_VERIFY, com.getName());
			}
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该公司不存在！");
            }
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave CompanyResource editCompany ====================");
        return company;
    }
	
	@GET
	@Path("/export")
	@Produces("application/xls")
	@RolesAllowed({Constant.ROLE_ADMIN})
	public Response exportCompanies(
			@QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize,
			@QueryParam("verified") Boolean verified,
			@QueryParam("frozen") Boolean frozen,
            @QueryParam("industry") String industry,
            @QueryParam("type") String type,
            @QueryParam("scale") String scale,
			@QueryParam("keyword") String keyword) throws ApiException {
		LOGGER.debug("==================== enter CompanyResource exportCompanies ====================");
		ListResponse<Company> res = getCompanies(pageIndex, pageSize, verified, frozen, industry, type, scale, keyword, null);
		final List<Map<String, Object>> data = new LinkedList<>();
		for (Company comp : res.getItems()) {
			Map<String, Object> d = new HashMap<>();
			d.put("id", comp.getId());
			d.put("name", comp.getName());
			d.put("addr", comp.getAddr());
			d.put("phone", comp.getPhone());
			d.put("email", comp.getEmail());
			d.put("contact", comp.getContact());
			d.put("contactPos", comp.getContactPos());
			d.put("contactPhone", comp.getContactPhone());
			d.put("industry", comp.getIndustry());
			d.put("type", comp.getType());
			d.put("scale", comp.getScale());
			d.put("code", comp.getCode());
			d.put("link", comp.getLink());
			d.put("intro", comp.getIntro());
			d.put("verified", comp.isVerified());
			d.put("frozen", comp.isFrozen());
			data.add(d);
		}
		final Map<String, String> titles = new LinkedHashMap<>();
		titles.put("id", "ID");
		titles.put("name", "名称");
		titles.put("addr", "地址");
		titles.put("phone", "电话");
		titles.put("email", "邮箱");
		titles.put("contact", "联系人");
		titles.put("contactPos", "联系人职位");
		titles.put("contactPhone", "联系电话");
		titles.put("industry", "产业");
		titles.put("type", "公司类型");
		titles.put("scale", "公司大小");
		titles.put("code", "组织机构代码");
		titles.put("link", "公司链接");
		titles.put("intro", "公司简介");
		titles.put("verified", "认证状态");
		titles.put("frozen", "冻结状态");
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					ExcelUtil.exportExcel(titles, data, output);
					LOGGER.debug("==================== leave CompanyResource exportCompanies ====================");
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}
		};
		String filename = pageIndex == null ? "companies_export_all.xls" : String.format("companies_export_page%d.xls", pageIndex);
		return Response.ok(stream).header("content-disposition", "attachment; filename = " + filename).build();
	}

    @GET
	@PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Company> getCompanies(@QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("verified") Boolean verified,
			@QueryParam("frozen") Boolean frozen,
            @QueryParam("industry") String industry,
            @QueryParam("type") String type,
            @QueryParam("scale") String scale,
			@QueryParam("keyword") String keyword,
			@QueryParam("accountPhone") String accountPhone) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource getCompanys ====================");
		Company selector = new Company();
		if (accountPhone != null) {
			Account sel = new Account();
			sel.setPhone(accountPhone);
			sel.setRole(Account.Role.COMPANY);
			try {
				Account acc = accountService.getByMatcher(sel);
				selector.setAccountId(acc.getId());
			} catch (ServiceException ex) {
				String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
				LOGGER.debug(devMsg);
				handleServiceException(ex);
			}
		}
        selector.setVerified(verified);
        selector.setIndustry(industry);
        selector.setType(type);
        selector.setScale(scale);
        selector.setFrozen(frozen);
		selector.setQueryParam(QueryModel.SEARCH_KEY, keyword);
        List<Company> companys = null;
        Pagination pagination = new Pagination(pageSize, pageIndex);
        try {
            companys = companyService.getList(selector, pagination);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave CompanyResource getCompanys ====================");
        return new ListResponse(companys, pagination);
    }

    @GET
    @Path("/subscription")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({Constant.ROLE_STUDENT})
    public ListResponse<Company> getSubscribedCompanies(@QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) throws ApiException {
        LOGGER.debug("==================== enter PositionResource getSubscribedCompanies ====================");
        Student stu = null;
        try {
            stu = studentService.get(currentEntityId());
        } catch (ServiceException ex) {
            java.util.logging.Logger.getLogger(PositionResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        Boolean verified = null;
		Boolean frozen = false;
        String industry = null;
        String type = null;
        String scale = null;
        Map<String, String> sub = null;
        if (stu != null) {
            sub = stu.getSubscription();
        }
        if (sub != null) {
            industry = sub.get("industry");
            type = sub.get("type");
            scale = sub.get("scale");
        }
        LOGGER.debug("==================== leave PositionResource getSubscribedCompanies ====================");
        return this.getCompanies(pageIndex, pageSize, verified, frozen, industry, type, scale, null, null);
    }

    @POST
    @Path("/login")
    @PermitAll
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Company login(Account acc) throws ApiException {
        acc.setRole(Account.Role.COMPANY);
        acc = loginAccount(acc);
        try {
            Company comp = companyService.getByAccountId(acc.getId());
			if (comp.isFrozen()) {
				authService.logout();
				throw new ApiException(403, "Account Frozen", "你的账号当前已被禁用，详情请联系网站客服400-820-4818");
			}
            comp.setAccount(acc);
            return comp;
        } catch (ServiceException ex) {
            handleServiceException(ex);
            return null;
        }
    }

    @GET
	@PermitAll
    @Path("/{companyId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Company getCompany(@PathParam("companyId") int companyId) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource getCompany ====================");
        LOGGER.debug("companyId: " + companyId);
        Company company = null;
        Account acc = null;
        try {
            if (Account.Role.COMPANY == currentRole() && companyId == 0) {
                companyId = currentEntityId();
                acc = accountService.get(currentAccountId());
            }
            company = companyService.get(companyId);
            company.setAccount(acc);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
                throw new ApiException(404, devMsg, "该企业不存在！");
            }
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave CompanyResource getCompany ====================");
        return company;
    }

    @GET
    @PermitAll
    @Path("/{companyId}/positions")
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Position> getCompanyPositions(@PathParam("companyId") int companyId,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize,
            @QueryParam("active") Boolean active) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource getPositiones ====================");
        if (companyId == 0) {
            companyId = currentEntityId();
        }
        Position selector = new Position();
        selector.setCompanyId(companyId);
        selector.setActive(active);
        Pagination pagination = new Pagination(pageSize, pageIndex);
        List<Position> positions = null;
        try {
            positions = positionService.getList(selector, pagination);
        } catch (ServiceException ex) {
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave CompanyResource getPositiones ====================");
        return new ListResponse(positions, pagination);
    }

    @GET
    @Path("/{companyId}/resumes")
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Resume> getCompanyResumes(@PathParam("companyId") int companyId,
            @QueryParam("state") List<Resume.RState> states,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource getResumes ====================");
        if (companyId == 0) {
            companyId = currentEntityId();
        }
        Resume selector = new Resume();
        if (!states.isEmpty()) {
            selector.setQueryStates(states);
        }
        selector.setCompanyId(companyId);
        List<Resume> resumes = null;
        Pagination pagination = new Pagination(pageSize, pageIndex);
        try {
            resumes = resumeService.getList(selector, pagination);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave CompanyResource getResumes ====================");
        return new ListResponse(resumes, pagination);
    }
	
	@GET
    @Path("/{companyId}/resumes/count")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, Integer> getCompanyResumesCount(@PathParam("companyId") int companyId) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource getCompanyResumesCount ====================");
        if (companyId == 0) {
            companyId = currentEntityId();
        }
		HashMap<String, Integer> cnts = new HashMap<>();
        Resume selector = new Resume();
		selector.setCompanyId(companyId);
        for (Resume.RState st : Resume.RState.values()) {
			selector.setState(st);
			cnts.put(st.toString(), resumeService.getCount(selector));
		}
        LOGGER.debug("==================== leave CompanyResource getCompanyResumesCount ====================");
        return cnts;
    }
	
	@GET
    @Path("/{companyId}/salaries/count")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, Integer> getCompanySalariesCount(@PathParam("companyId") int companyId,
			@QueryParam("month") String month) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource getCompanySalariesCount ====================");
        if (companyId == 0) {
            companyId = currentEntityId();
        }
		HashMap<String, Integer> cnts = new HashMap<>();
        Salary selector = new Salary();
		selector.setCompanyId(companyId);
		selector.setMonth(month);
        for (Salary.SState st : Salary.SState.values()) {
			selector.setState(st);
			cnts.put(st.toString(), salaryService.getCount(selector));
		}
        LOGGER.debug("==================== leave CompanyResource getCompanySalariesCount ====================");
        return cnts;
    }
	
	@GET
    @Path("/{companyId}/salaries")
    @Produces(MediaType.APPLICATION_JSON)
    public ListResponse<Salary> getCompanySalaries(@PathParam("companyId") int companyId,
			@QueryParam("state") List<Salary.SState> states,
			@QueryParam("month") String month,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource getCompanySalaries ====================");
        if (companyId == 0) {
            companyId = currentEntityId();
        }
        Salary selector = new Salary();
        if (!states.isEmpty()) {
            selector.setQueryStates(states);
        }
		selector.setMonth(month);
        selector.setCompanyId(companyId);
        List<Salary> salaries = null;
        Pagination pagination = new Pagination(pageSize, pageIndex);
        try {
            salaries = salaryService.getList(selector, pagination);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave CompanyResource getCompanySalaries ====================");
        return new ListResponse(salaries, pagination);
    }
	
	@GET
    @Path("/{companyId}/rates")
    @Produces(MediaType.APPLICATION_JSON)
	@PermitAll
    public ListResponse<Rate> getCompanyRates(@PathParam("companyId") int companyId,
            @QueryParam("pageIndex") Integer pageIndex,
            @QueryParam("pageSize") Integer pageSize) throws ApiException {
        LOGGER.debug("==================== enter CompanyResource getCompanyRates ====================");
        if (companyId == 0 && Account.Role.COMPANY.equals(currentRole())) {
            companyId = currentEntityId();
        }
        Rate selector = new Rate();
		selector.setCompanyId(companyId);
        selector.setDirection(Rate.Direction.STU_TO_COMP);
        List<Rate> rates = null;
        Pagination pagination = new Pagination(pageSize, pageIndex);
        try {
            rates = rateService.getList(selector, pagination);
        } catch (ServiceException ex) {
            String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
            LOGGER.debug(devMsg);
            handleServiceException(ex);
        }
        LOGGER.debug("==================== leave CompanyResource getCompanyRates ====================");
        return new ListResponse(rates, pagination);
    }

}
