package thu.declan.xi.server.resource;

import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.Notification;
import thu.declan.xi.server.model.Salary;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.model.Salary.SState;
import thu.declan.xi.server.service.SalaryService;
import thu.declan.xi.server.service.WechatService;

/**
 *
 * @author declan
 */
@Path("salaries")
@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT, Constant.ROLE_COMPANY})
public class SalaryResource extends BaseResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryResource.class);

	@Autowired
	private SalaryService salaryService;

	@Autowired
	private WechatService wechatService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public ListResponse<Salary> getSalaryList(
			@QueryParam("month") String month,
			@QueryParam("stuName") String stuName,
			@QueryParam("compName") String compName,
			@QueryParam("stuAccount") String stuAccount,
			@QueryParam("state") List<Salary.SState> states,
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
		LOGGER.debug("==================== enter SalaryResource getSalaryes ====================");
		Salary selector = new Salary();
		selector.setMonth(month);
		selector.setQueryParam("stuName", stuName);
		selector.setQueryParam("stuAccount", stuAccount);
		selector.setQueryParam("compName", compName);
		if (states != null && states.size() > 0) {
			selector.setQueryStates(states);
		}
		Pagination pagination = new Pagination(pageSize, pageIndex);
		List<Salary> salaries = null;
		try {
			salaries = salaryService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave SalaryResource getSalaryes ====================");
		return new ListResponse(salaries, pagination);
	}
	
	@GET
	@Path("/export")
	@Produces("application/xls")
	@RolesAllowed({Constant.ROLE_ADMIN})
	public Response exportSalaries(
			@QueryParam("month") String month,
			@QueryParam("stuName") String stuName,
			@QueryParam("compName") String compName,
			@QueryParam("stuAccount") String stuAccount,
			@QueryParam("state") List<Salary.SState> states,
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
		LOGGER.debug("==================== enter SalaryResource exportSalaries ====================");
		ListResponse<Salary> res = getSalaryList(month, stuName, compName, stuAccount, states, pageIndex, pageSize);
		final List<Map<String, Object>> data = new LinkedList<>();
		for (Salary salary : res.getItems()) {
			Map<String, Object> d = new HashMap<>();
			d.put("id", salary.getId());
			d.put("stuName", salary.getResume().getStudent().getName());
			d.put("stuAccount", salary.getStuAccount());
			d.put("compName", salary.getResume().getPosition().getCompany().getName());
			d.put("position", salary.getResume().getPosition().getTitle());
			d.put("month", salary.getMonth());
			d.put("resumeState", salary.getResume().getState());
			d.put("workDays", salary.getWorkDays());
			d.put("value", salary.getValue());
			d.put("stuValue", salary.getStuValue());
			d.put("stuComment", salary.getStuComment());
			d.put("state", salary.getState());
			data.add(d);
		}
		final Map<String, String> titles = new LinkedHashMap<>();
		titles.put("id", "ID");
		titles.put("stuName", "学生姓名");
		titles.put("stuAccount", "学生账号");
		titles.put("compName", "实习公司");
		titles.put("position", "实习职位");
		titles.put("month", "月份");
		titles.put("resumeState", "实习状态");
		titles.put("workDays", "当月实习天数");
		titles.put("value", "企业应付");
		titles.put("stuValue", "学生到手工资");
		titles.put("stuComment", "学生备注");
		titles.put("state", "发放状态");
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					ExcelUtil.exportExcel(titles, data, output);
					LOGGER.debug("==================== leave SalaryResource exportSalaries ====================");
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}
		};
		String filename = pageIndex == null ? "salaries_export_all.xls" : String.format("salaries_export_page%d.xls", pageIndex);
		return Response.ok(stream).header("content-disposition", "attachment; filename = " + filename).build();
	}

	@GET
	@Path("/{salaryId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Salary getSalary(@PathParam("salaryId") int salaryId) throws ApiException {
		LOGGER.debug("==================== enter SalaryResource getSalary ====================");
		LOGGER.debug("salaryId: " + salaryId);
		Salary salary = null;
		try {
			salary = salaryService.get(salaryId);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该通知不存在！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave SalaryResource getSalary ====================");
		return salary;
	}

	@PUT
	@Path("/{salaryId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Salary editSalary(@PathParam("salaryId") int salaryId, Salary updater) throws ApiException {
		LOGGER.debug("==================== enter SalaryResource editSalary ====================");
		LOGGER.debug("salaryId: " + salaryId);
		Salary oldSalary = getSalary(salaryId);
		if (oldSalary.getState() == SState.CONFIRMED || oldSalary.getState() == SState.PAID) {
			throw new ApiException(403, "wrong state, not allowed to edit", "薪资已确认，不可修改");
		}
		Salary salary = null;
		try {
			if (updater.getWorkDays() != null) {
				updater.updateValue(oldSalary.getResume());
			}
			updater.setId(salaryId);
			salaryService.update(updater);
			salary = getSalary(salaryId);
			String compName = salary.getResume().getPosition().getCompany().getName();
			Account acc = accountService.get(salary.getResume().getStudent().getAccountId());
			if (updater.getState() == Salary.SState.WAIT_STU_CONFIRM) {
				Notification noti = this.notiService.addNoti(salary.getResume().getStudent().getAccountId(),
						Notification.NType.SALARY, salaryId,
						Notification.TPL_SALARY_CONFIRM,
						compName,
						oldSalary.getMonth());
				Map<String, String> data = new HashMap<>();
				data.put("first", String.format("您好!您在【%s】工作的工资条已发放", compName));
				data.put("keyword1", salary.getMonth());
				data.put("keyword2", String.format("%.2f", salary.getStuValue()));
				data.put("remark", "请尽快确认");
				String openid = acc.getOpenId();
				try {
					if (openid != null) {
						wechatService.sendTemplateMessage(Notification.WX_TPL_ID_SALARY, openid, noti, data);
					}
				} catch (ServiceException ex) {
				}
			} else if (updater.getState() == Salary.SState.CONFIRMED || updater.getState() == Salary.SState.PAID) {
				Notification noti = this.notiService.addNoti(oldSalary.getResume().getStudent().getAccountId(),
						Notification.NType.SALARY, salaryId,
						Notification.TPL_SALARY_GET,
						compName,
						oldSalary.getMonth());
				Map<String, String> data = new HashMap<>();
				data.put("first", String.format("您好!您在【%s】工作的工资已到账", compName));
				data.put("keyword1", salary.getMonth());
				data.put("keyword2", String.format("%.2f", salary.getStuValue()));
				data.put("remark", "请登录查看账户余额");
				String openid = acc.getOpenId();
				try {
					if (openid != null) {
						wechatService.sendTemplateMessage(Notification.WX_TPL_ID_SALARY_GET, openid, noti, data);
					}
				} catch (ServiceException ex) {
				}
			}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该薪资记录不存在！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave SalaryResource editSalary ====================");
		return salary;
	}

}
