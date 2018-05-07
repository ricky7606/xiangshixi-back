package thu.declan.xi.server.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.ResumeMapper;
import thu.declan.xi.server.mapper.SalaryMapper;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.model.Salary;
import thu.declan.xi.server.service.SalaryService;

/**
 * backup databases
 *
 * @author declan
 */
@Component
public class SalaryTask {

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryTask.class);
	
	private static final double SALARY_COMMON_DAYS = 22;

	@Autowired
    private SalaryMapper salaryMapper;
    
    @Autowired
    private ResumeMapper resumeMapper;
    
    @Autowired
    private SalaryService salaryService;
    
    private Date ndaysBefore(int n) {
        return new Date((new Date()).getTime() - n * 24 * 60 * 60 * 1000);
    }
    
    private void generateSalaries(Resume sel) {
        List<Resume> resumes = resumeMapper.selectList(sel);
        for (Resume r : resumes) {
            LOGGER.info("Generate for resume " + r.getId());
            Salary s = new Salary();
            s.setCompanyId(r.getCompanyId());
            SimpleDateFormat format = new SimpleDateFormat("YYYY-MM");
            s.setMonth(format.format(ndaysBefore(1)));
            s.setResumeId(r.getId());
			s.setStuId(r.getStuId());
            s.setState(Salary.SState.NEW_GENERATED);
            s.setWorkDays(SALARY_COMMON_DAYS);
            s.updateValue(r);
            salaryMapper.insert(s);
        }
    }
	
    // 每月1日0点
	@Scheduled(cron = "0 0 0 1 * ? ")
	public void generateSalaries() {
        LOGGER.info("******************************** Start Generate Salaries ********************************");
        LOGGER.info("For working resumes");
        Date lastDay = ndaysBefore(1);
        Resume sel = new Resume();
        sel.setState(Resume.RState.WORKING);
        generateSalaries(sel);
        LOGGER.info("For last month ended resumes");
        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        SimpleDateFormat format1 = new SimpleDateFormat("YYYY-MM-01 00:00:00");
        sel.setState(Resume.RState.ENDED);
        try {
            sel.setEndTime(format.parse(format1.format(lastDay)));
        } catch (ParseException ex) {
            LOGGER.error("parse datetime error " + format1.format(lastDay));
            return;
        }
        generateSalaries(sel);
        LOGGER.info("******************************** Finish Generate Salaries ********************************");
	}
    
    // 每天8点
	@Scheduled(cron = "0 0 8 * * ? ")
    public void autoConfirmSalary() {
        LOGGER.info("******************************** Start Auto Confirm Salaries ********************************");
        Salary sel = new Salary();
        sel.setUpdateTime(ndaysBefore(5));
        sel.setState(Salary.SState.WAIT_STU_CONFIRM);
        List<Salary> salaries = salaryMapper.selectList(sel);
        for (Salary s : salaries) {
            LOGGER.info("Auto confirm for salary " + s.getId());
            s.setState(Salary.SState.CONFIRMED);
            try {
                salaryService.update(s);
            } catch (ServiceException ex) {
                LOGGER.error("Service error while auto confirming salary " + s.getId() + " [" + ex.getCode() + "] " + ex.getReason());
            }
        }
        LOGGER.info("******************************** Finish Auto Confirm Salaries ********************************");
    }
    
    // 每天2点
//    @Scheduled(cron = "0 0 2 * * ? ")
//    public void paySalaries() {
//        LOGGER.info("******************************** Start Pay Salaries ********************************");
//        Salary sel = new Salary();
//        sel.setState(Salary.SState.CONFIRMED);
//        List<Salary> salaries = salaryMapper.selectList(sel);
//        for (Salary s : salaries) {
//            LOGGER.info("pay for salary %d", s.getId());
//            Student stu = studentMapper.selectOne(s.getStuId());
//            if (stu == null) {
//                LOGGER.info("Student not found for stuid: %d", s.getStuId());
//                continue;
//            }
//            accountMapper.addBalance(stu.getAccountId(), s.getStuValue());
//            s.setPayTime(new Date());
//            s.setState(Salary.SState.PAID);
//            salaryMapper.update(s);
//        }
//        LOGGER.info("******************************** Finish Pay Salaries ********************************");
//    }

}
