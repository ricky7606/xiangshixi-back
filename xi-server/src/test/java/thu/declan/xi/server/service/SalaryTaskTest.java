package thu.declan.xi.server.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import thu.declan.xi.server.mapper.ResumeMapper;
import thu.declan.xi.server.mapper.SalaryMapper;
import thu.declan.xi.server.model.Resume;
import thu.declan.xi.server.task.SalaryTask;
import thu.declan.xi.server.util.WorkingDaysUtils;

/**
 *
 * @author Sublime
 */
@RunWith(SpringJUnit4ClassRunner.class) // 整合 
@ContextConfiguration(locations = "classpath:spring.xml") // 加载配置
public class SalaryTaskTest {

    @Autowired
    private SalaryService salaryService;
	
	@Autowired
    private ResumeMapper resumeMapper;
	
	@Autowired
	private SalaryMapper salaryMapper;
	@Autowired
	private SalaryTask salaryTask;
	
	private static String firstDay;  
	private static String lastDay;
	
    @Test
    public void testAuthService() throws Exception {
        System.out.println("-------------testSalaryTask begin------------");
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date begDate = sdf.parse("2018-05-01");
        Date endDate = sdf.parse("2018-05-31");
        if (begDate.after(endDate))
            throw new Exception("日期范围非法");
        // 总天数
        int days = (int) ((endDate.getTime() - begDate.getTime()) / (24 * 60 * 60 * 1000)) + 1;
        // 总周数，
        int weeks = days / 7;
        int rs = 0;
        // 整数周
        if (days % 7 == 0) {
            rs = days - 2 * weeks;
        }else {
            Calendar begCalendar = Calendar.getInstance();
            Calendar endCalendar = Calendar.getInstance();
            begCalendar.setTime(begDate);
            endCalendar.setTime(endDate);
            // 周日为1，周六为7
            int beg = begCalendar.get(Calendar.DAY_OF_WEEK);
            int end = endCalendar.get(Calendar.DAY_OF_WEEK);
            if (beg > end) {
                rs = days - 2 * (weeks + 1);
            }else{
                if (end == 7 || beg == 1 || beg == 7) {
                    rs = days - 2 * weeks - 1;
                } else {
                    rs = days - 2 * weeks;
                }
            } //else {
//                if (beg == 1 || beg == 7) {
//                    rs = days - 2 * weeks - 1;
//                } else {
//                    rs = days - 2 * weeks;
//                }
//            }
        }
        System.out.println(sdf.format(begDate)+"到"+sdf.format(endDate)+"中间有"+rs+"个工作日");*/
//        salaryTask.generateSalaries();
//        Resume resume = new Resume();
//        resume.setOfferTime(WorkingDaysUtils.getThisMonthFirstDay());//设置查询条件   offerTime<=计算月份 and (endTime is null or endTime>=计算月份)
//        resume.setEndTime(WorkingDaysUtils.getLastMonthFirstDay());
//        List<Resume> list = resumeMapper.selectListByOfferTime(resume);
//        for (int i = 0; i < list.size(); i++) {
//			System.out.println("list["+i+"]: "+list.get(i).toString());
//		}
//        salaryTask.generateSalaries();
//        salaryTask.autoConfirmSalary();
        // date: Tue May 01 00:00:00 CST 2018
        // Tue May 01 15:41:41 CST 2018
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        Boolean b = false;
//        Date begDate = sdf.parse("2018-05-31");
//        Date endDate = sdf.parse("2018-05-31");
//        if (begDate.after(endDate)){
//        	b = true;
//        }
//        WorkingDaysUtils wkUtils = new WorkingDaysUtils();
//        Number number = wkUtils.getRealVaule(100.1234, 2);
//        Double db = number.doubleValue();
//        System.out.println("number: "+number+", db: "+db);
//        Integer workingDay = wkUtils.getWorkingDays("2018-05-15","2018-05-31");
//        Date month = wkUtils.getLastMonth();
//        String date = format.format(month);
//        Integer workingDays = wkUtils.getWorkingDays();
//        Date lastMonthFirstDay = wkUtils.getLastMonthFirstDay();
//        Date lastMonthLastDay = wkUtils.getLastMonthLastDay();
//        Date thisMonthFirstDay = wkUtils.getThisMonthFirstDay();
//        Date thisMonthLastDay = wkUtils.getThisMonthLastDay();
//        SimpleDateFormat format = new SimpleDateFormat("YYYY-MM-dd HH:ss:mm");
//        String MFD = format.format(thisMonthFirstDay);
//        String MLD = format.format(thisMonthLastDay);
//        String MLD = format.format(lastMonthLastDay);
//        String MFD = format.format(lastMonthFirstDay);
//        System.out.println("lastMonthLastDay: "+ MLD+", lastMonthFirstDay: "+MLD);
//        System.out.println("thisMonthFirstDay: "+MFD+", thisMonthLastDay: "+MLD);
//        System.out.println("workingDays: "+workingDays);
//        System.out.println("date: "+month);
//        System.out.println("b: "+b);
        
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");   
//        String dateStr = "2016-05-18";
//        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        //获取前月的第一天  
//        Calendar cale_1=Calendar.getInstance();//获取当前日期   
//        cale_1.add(Calendar.MONTH, -1);  
//        cale_1.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天   
//        firstDay = format.format(cale_1.getTime());  
//        System.out.println("-----cal_1.getTime():"+cale_1.getTime());  
        //获取前月的最后一天  
//        Calendar cale_2 = Calendar.getInstance();
//        cale_2.set(Calendar.DAY_OF_MONTH,0);//设置为1号,当前日期既为本月第一天
//        cale_2.setTime(date);
//        System.out.println("day: "+cale_2.get(Calendar.DATE));
//        int day1 = cale_1.get(Calendar.DATE);
//        int day2 = cale_2.get(Calendar.DATE);
//        lastDay = format.format(cale_2.getTime()); 
        
//        Calendar cale_3 = Calendar.getInstance();
//        cale_3.setTime(date);
//        System.out.println("firstDay: "+ firstDay + " lastDay: "+lastDay + " day1: "+day1+" day2: "+day2 + " date1: "+date1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:ss:mm");
        String valueString = format.format(ndaysBefore(1));
        String vaString = format.format(WorkingDaysUtils.getThisMonthFirstDay());
        System.out.println("valueString: "+valueString+", vaString: "+vaString);
        System.out.println("-------------testSalaryTask end------------");
    }
    
    public static int getMonthLastDay(int year, int month){
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);
        a.set(Calendar.MONTH, month - 1);
        a.set(Calendar.DATE, 1);//把日期设置为当月第一天  
        a.roll(Calendar.DATE, -1);//日期回滚一天，也就是最后一天  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }
    
    public Date ndaysBefore(int n) {
        return new Date((new Date()).getTime() - n * 24 * 60 * 60 * 1000);
    }
}
