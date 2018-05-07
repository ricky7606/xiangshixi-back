package thu.declan.xi.server.resource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ApiException;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.SalaryMapper;
import thu.declan.xi.server.model.Account;
import thu.declan.xi.server.model.Company;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.model.Salary;
import thu.declan.xi.server.model.Student;
import thu.declan.xi.server.model.UploadResult;
import thu.declan.xi.server.service.AccountService;
import thu.declan.xi.server.service.CompanyService;
import thu.declan.xi.server.service.ResumeService;
import thu.declan.xi.server.service.StudentService;
import thu.declan.xi.server.util.EncryptionUtils;

/**
 *
 * @author declan
 */
@Path("upload")
@RolesAllowed({Constant.ROLE_ADMIN, Constant.ROLE_COMPANY, Constant.ROLE_STUDENT})
public class UploadResource extends BaseResource {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadResource.class);
    
	@Autowired
    private StudentService studentService;

    @Autowired
    private ResumeService resumeService;
	
	@Autowired
	private SalaryMapper salaryMapper;
	
	@Autowired
    private CompanyService companyService;
	
	@Autowired
    private AccountService accountService;
	
	@POST
	@PermitAll
	@Path("log")
	@Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
	public String uploadLog(String log) {
		LOGGER.error("Frontend Log: " + log);
		return log;
	}
	
    @POST
	@Path("{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public UploadResult uploadImage(@PathParam("type") String type,
			@FormDataParam("file") InputStream fileInputStream,
			@FormDataParam("file") FormDataContentDisposition fileDetail,
			@Context UriInfo ui,
			@Context HttpServletRequest request) throws ApiException {
        LOGGER.debug("==================== enter UploadResource uploadImage ====================");
		String oldName = fileDetail.getFileName();
		System.out.println(oldName);
		String [] tmp = oldName.split("\\.");
		String fileType = tmp[tmp.length-1];
		String newFileName = (new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_")).format(new Date())
				+ EncryptionUtils.randomPassword(5) + "." + fileType;
		String uploadDir = Constant.UPLOAD_DIR + "/" + type;
		File uploadDirF = new File(uploadDir);
		if (!uploadDirF.exists() && !uploadDirF.mkdirs()) {
			throw new ApiException(500, "Failed to create folder: " + uploadDir, "内部错误");
		}
		String fullPath = uploadDir + "/" + newFileName;
		URI uri = ui.getBaseUri();
		String url = request.getScheme() + "://" + uri.getHost();
		if (uri.getPort() > -1 && uri.getPort() != 80 && uri.getPort() != 443) {
			url = url + ":" + uri.getPort();
		}
		url = url + Constant.UPLOAD_CONTEXT_PATH + "/" + type + "/" + newFileName;
		UploadResult result = new UploadResult(url);
        try (OutputStream out = new FileOutputStream(new File(fullPath))) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = fileInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } catch (IOException e) {
			result.setSuccess(false);
			result.setMsg("文件上传失败");
			return result;
        }
        LOGGER.debug("==================== leave UploadResource uploadImage ====================");
		return result;
    }
	
	@GET
	@PermitAll
	@Path("/testSalary")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Resume> addTestingSalary(@QueryParam("stuAcc") String stu,
			@QueryParam("compAcc") String comp) throws ApiException {
		try {
			Account mapper = new Account();
			mapper.setPhone(stu);
			mapper.setRole(Account.Role.STUDENT);
			Account acc = accountService.getByMatcher(mapper);
			Student student = studentService.getByAccountId(acc.getId());
			mapper.setPhone(comp);
			mapper.setRole(Account.Role.COMPANY);
			acc = accountService.getByMatcher(mapper);
			Company company = companyService.getByAccountId(acc.getId());
			Resume sel = new Resume();
			sel.setStuId(student.getId());
			sel.setCompanyId(company.getId());
			sel.setState(Resume.RState.WORKING);
			List<Resume> resumes = resumeService.getList(sel);
			for (Resume r : resumes) {
				LOGGER.info("Generate for resume " + r.getId());
				Salary s = new Salary();
				s.setCompanyId(r.getCompanyId());
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
				s.setMonth(format.format(new Date((new Date()).getTime() - 20 * 24 * 60 * 60 * 1000)));
				s.setResumeId(r.getId());
				s.setStuId(r.getStuId());
				s.setState(Salary.SState.NEW_GENERATED);
				s.setWorkDays(22.0);
				s.updateValue(r);
				salaryMapper.insert(s);
			}
			return resumes;
		} catch (ServiceException ex) {
			handleServiceException(ex);
		}
		return null;
	}
    
}
