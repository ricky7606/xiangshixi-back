package thu.declan.xi.server.resource;

import com.sargeraswang.util.ExcelUtil.ExcelUtil;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import thu.declan.xi.server.model.Student;
import thu.declan.xi.server.model.ListResponse;
import thu.declan.xi.server.model.Notification;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.model.PointLog;
import thu.declan.xi.server.model.PointLog.PType;
import thu.declan.xi.server.model.Rate;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.model.Salary;
import thu.declan.xi.server.service.RateService;
import thu.declan.xi.server.service.ResumeService;
import thu.declan.xi.server.service.SalaryService;
import thu.declan.xi.server.service.StudentService;

/**
 *
 * @author declan
 */
@Path("students")
@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT})
public class StudentResource extends BaseResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(StudentResource.class);

	@Autowired
	private StudentService studentService;

	@Autowired
	private ResumeService resumeService;

	@Autowired
	private SalaryService salaryService;

	@Autowired
	private RateService rateService;

	@POST
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	// 学生账号注册
	public Student createStudent(@Valid Student student) throws ApiException {
		LOGGER.debug("==================== enter StudentResource createStudent ====================");
		Account acc = student.getAccount();
		acc.setRole(Account.Role.STUDENT);
		AccountResource accRes = new AccountResource();
		beanFactory.autowireBean(accRes);
		String pwd = acc.getPassword();
		acc = accRes.createAccount(acc);
		student.setAccount(acc);
		try {
			student.setAccountId(acc.getId());
			studentService.add(student);
			authService.login(acc.getPhone(), pwd, Account.Role.STUDENT);
			addPoint(PointLog.PType.REGISTER, acc.getId());
			notiService.addNoti(0, Notification.NType.STUDENT, student.getId(), Notification.TPL_BACK_STUDENT_CREATION, acc.getPhone());
		} catch (ServiceException ex) {
			accRes.deleteAccount(acc.getId());
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave StudentResource createStudent ====================");
		return student;
	}

	@PUT
	@Path("/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	// 修改学生信息
	public Student editStudent(@PathParam("studentId") int studentId, Student student) throws ApiException {
		LOGGER.debug("==================== enter StudentResource editStudent ====================");
		LOGGER.debug("studentId: " + studentId);
		if (currentRole() != Account.Role.ADMIN) {
			if (studentId == 0) {
				studentId = currentEntityId();
			}
			if (currentEntityId() != studentId) {
				throw new ApiException(401, "Student Id not equal to authorized one", "权限不足");
			}
		}
		try {
			student.setId(studentId);
			studentService.update(student);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该学生不存在！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave StudentResource editStudent ====================");
		return student;
	}

	@GET
	@Path("/export")
	@Produces("application/xls")
	@RolesAllowed({Constant.ROLE_ADMIN})
	public Response exportStudents(
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize,
			@QueryParam("school") String school,
			@QueryParam("name") String name,
			@QueryParam("phone") String phone,
			@QueryParam("frozen") Boolean frozen) throws ApiException {
		LOGGER.debug("==================== enter StudentResource exportStudents ====================");
		SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		ListResponse<Student> res = getStudents(pageIndex, pageSize, school, name, phone, frozen);
		final List<Map<String, Object>> data = new LinkedList<>();
		for (Student stu : res.getItems()) {
			Map<String, Object> d = new HashMap<>();
			d.put("id", stu.getId());
			d.put("name", stu.getName());
			d.put("gender", stu.getGender());
			d.put("phone", stu.getPhone());
			d.put("email", stu.getEmail());
			d.put("area", stu.getArea());
			d.put("school", stu.getSchool());
			d.put("edu", stu.getEducation() == null ? "" : stu.getEducation().toChsString());
			d.put("major", stu.getMajor());
			d.put("grade", stu.getGrade());
			d.put("lang", stu.getLanguage());
			d.put("langLevel", stu.getLangLevel());
			d.put("workExp", stu.getWorkExp());
			d.put("socialExp", stu.getSocialExp());
			d.put("selfVal", stu.getSelfEval());
			d.put("createTime", stu.getCreateTime() == null ? "" : format.format(stu.getCreateTime()));
			data.add(d);
		}
		final Map<String, String> titles = new LinkedHashMap<>();
		titles.put("id", "ID");
		titles.put("name", "姓名");
		titles.put("gender", "性别");
		titles.put("phone", "电话");
		titles.put("email", "邮箱");
		titles.put("area", "地区");
		titles.put("school", "学校");
		titles.put("edu", "学历");
		titles.put("major", "专业");
		titles.put("grade", "年级");
		titles.put("lang", "外语");
		titles.put("langLevel", "外语水平");
		titles.put("workExp", "工作经验");
		titles.put("socialExp", "社会经验");
		titles.put("selfVal", "自我评价");
		titles.put("createTime", "注册时间");
		StreamingOutput stream = new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				try {
					ExcelUtil.exportExcel(titles, data, output);
					LOGGER.debug("==================== leave StudentResource exportStudents ====================");
				} catch (Exception e) {
					throw new WebApplicationException(e);
				}
			}
		};
		String filename = pageIndex == null ? "students_export_all.xls" : String.format("students_export_page%d.xls", pageIndex);
		return Response.ok(stream).header("content-disposition", "attachment; filename = " + filename).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_COMPANY})
	public ListResponse<Student> getStudents(@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize,
			@QueryParam("school") String school,
			@QueryParam("name") String name,
			@QueryParam("phone") String phone,
			@QueryParam("frozen") Boolean frozen) throws ApiException {
		LOGGER.debug("==================== enter StudentResource getStudents ====================");
		Student selector = new Student();
		selector.setSchool(school);
		selector.setName(name);
		selector.setPhone(phone);
		selector.setFrozen(frozen);
		List<Student> students = null;
		if (currentRole() != Account.Role.ADMIN && name == null && phone == null) {
			throw new ApiException(400, "ether name or phone should be set", "请输入姓名或者手机号检索");
		}
		Pagination pagination = new Pagination(pageSize, pageIndex);
		try {
			students = studentService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave StudentResource getStudents ====================");
		return new ListResponse(students, pagination);
	}

	@POST
	@Path("/login")
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Student login(Account acc) throws ApiException {
		acc.setRole(Account.Role.STUDENT);
		acc = loginAccount(acc);
		try {
			Student stu = studentService.getByAccountId(acc.getId());
			if (stu.isFrozen()) {
				authService.logout();
				throw new ApiException(403, "Account Frozen", "你的账号当前已被禁用，详情请联系网站客服400-820-4818！");
			}
			stu.setAccount(acc);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());
			try {
				long refid = sdf.parse(date).getTime() / 1000;
				addPoint(PType.LOGIN, (int) refid);
			} catch (ParseException ex) {
				java.util.logging.Logger.getLogger(StudentResource.class.getName()).log(Level.SEVERE, null, ex);
			}
			return stu;
		} catch (ServiceException ex) {
			handleServiceException(ex);
			return null;
		}
	}

	@POST
	@Path("login/wechat")
	@PermitAll
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Student wechatLogin(@QueryParam("unionid") String unionId) throws ApiException {
		try {
			Account acc = authService.wechatLogin(unionId, Account.Role.STUDENT);
			Student stu = studentService.getByAccountId(acc.getId());
			stu.setAccount(acc);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(new Date());
			try {
				long refid = sdf.parse(date).getTime() / 1000;
				addPoint(PType.LOGIN, (int) refid);
			} catch (ParseException ex) {
				java.util.logging.Logger.getLogger(StudentResource.class.getName()).log(Level.SEVERE, null, ex);
			}
			return stu;
		} catch (ServiceException ex) {
			handleServiceException(ex);
			return null;
		}
	}

	@GET
	@Path("/{studentId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Student getStudent(@PathParam("studentId") Integer studentId) throws ApiException {
		LOGGER.debug("==================== enter StudentResource getStudent ====================");
		LOGGER.debug("studentId: " + studentId);
		Student student = null;
		Account acc = null;
		try {
			if (studentId == 0) {
				studentId = currentEntityId();
				if (studentId == null) {
					studentId = 0;
				}
				acc = accountService.get(currentAccountId());
			}
			student = studentService.get(studentId);
			student.setAccount(acc);
			if (!Account.Role.ADMIN.equals(currentRole()) && !Objects.equals(student.getAccountId(), currentAccountId())) {
				throw new ApiException(403, "Access Forbidden", "不允许获取其他学生信息！");
			}
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			if (ex.getCode() == ServiceException.CODE_NO_SUCH_ELEMENT) {
				throw new ApiException(404, devMsg, "该学生不存在！");
			}
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave StudentResource getStudent ====================");
		return student;
	}

	@GET
	@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_STUDENT, Constant.ROLE_COMPANY})
	@Path("/{studentId}/resumes")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public ListResponse<Resume> getResumes(@PathParam("studentId") int studentId,
			@QueryParam("state") List<Resume.RState> states,
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
		LOGGER.debug("==================== enter StudentResource getResumes ====================");
		if (studentId == 0 && currentRole() == Account.Role.STUDENT) {
			studentId = currentEntityId();
		}
		Resume selector = new Resume();
		if (!states.isEmpty()) {
			selector.setQueryStates(states);
		}
		selector.setStuId(studentId);
		List<Resume> resumes = null;
		Pagination pagination = new Pagination(pageSize, pageIndex);
		try {
			resumes = resumeService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave StudentResource getResumes ====================");
		return new ListResponse(resumes, pagination);
	}
	
	@GET
    @Path("/{studentId}/resumes/count")
    @Produces(MediaType.APPLICATION_JSON)
    public HashMap<String, Integer> getStudentResumesCount(@PathParam("studentId") int studentId) throws ApiException {
        LOGGER.debug("==================== enter StudentResource getStudentResumesCount ====================");
        if (studentId == 0) {
            studentId = currentEntityId();
        }
		HashMap<String, Integer> cnts = new HashMap<>();
        Resume selector = new Resume();
		selector.setStuId(studentId);
        for (Resume.RState st : Resume.RState.values()) {
			selector.setState(st);
			cnts.put(st.toString(), resumeService.getCount(selector));
		}
        LOGGER.debug("==================== leave StudentResource getStudentResumesCount ====================");
        return cnts;
    }

	@GET
	@Path("/{studentId}/salaries")
	@Produces(MediaType.APPLICATION_JSON)
	public ListResponse<Salary> getStudentSalaries(@PathParam("studentId") int studentId,
			@QueryParam("state") List<Salary.SState> states,
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
		LOGGER.debug("==================== enter StudentResource getStudentSalaries ====================");
		if (studentId == 0) {
			studentId = currentEntityId();
		}
		Salary selector = new Salary();
		if (!states.isEmpty()) {
			selector.setQueryStates(states);
		}
		selector.setStuId(studentId);
		List<Salary> salaries = null;
		Pagination pagination = new Pagination(pageSize, pageIndex);
		try {
			salaries = salaryService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave StudentResource getStudentSalaries ====================");
		return new ListResponse(salaries, pagination);
	}

	@GET
	@Path("/{studentId}/rates")
	@Produces(MediaType.APPLICATION_JSON)
	@PermitAll
	public ListResponse<Rate> getStudentRates(@PathParam("studentId") int studentId,
			@QueryParam("pageIndex") Integer pageIndex,
			@QueryParam("pageSize") Integer pageSize) throws ApiException {
		LOGGER.debug("==================== enter StudentResource getStudentRates ====================");
		if (studentId == 0 && Account.Role.STUDENT.equals(currentRole())) {
			studentId = currentEntityId();
		}
		Rate selector = new Rate();
		selector.setStuId(studentId);
		selector.setDirection(Rate.Direction.COMP_TO_STU);
		List<Rate> rates = null;
		Pagination pagination = new Pagination(pageSize, pageIndex);
		try {
			rates = rateService.getList(selector, pagination);
		} catch (ServiceException ex) {
			String devMsg = "Service Exception [" + ex.getCode() + "] " + ex.getReason();
			LOGGER.debug(devMsg);
			handleServiceException(ex);
		}
		LOGGER.debug("==================== leave StudentResource getStudentRates ====================");
		return new ListResponse(rates, pagination);
	}

}
