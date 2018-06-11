package thu.declan.xi.server.util;
import static org.hamcrest.CoreMatchers.nullValue;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import thu.declan.xi.server.task.SalaryTask;

/**
 * 
 * @author Sublime
 */
public class WorkingDaysUtils {
	
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd");
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryTask.class);
	
	/**
	 * 计算上月工作天数
	 * @return workDay 工作天数
	 */
	public static Integer getWorkingDays() {
		Integer totalDay, entryDay, workDay = 0, totalWeeks, beg, end;
		LOGGER.debug("==================== enter WorkingDaysUtils getWorkingDays ====================");
		try {
			Calendar begCalendar = Calendar.getInstance();
			Calendar endCalendar = Calendar.getInstance();
			begCalendar.add(Calendar.MONTH, -1);
			begCalendar.set(Calendar.DAY_OF_MONTH, 1);// 设置为上月第一天
			endCalendar.set(Calendar.DAY_OF_MONTH, 0);// 设置为上月
			totalDay = endCalendar.get(Calendar.DATE);// 获取上月总天数
			totalWeeks = totalDay / 7; // 总周数
			entryDay = begCalendar.get(Calendar.DATE);// 入职日期为月初
			if (totalDay % 7 == 0) {
				workDay = totalDay - 2 * totalWeeks;
			} else {
				// 周日为1，周六为7
				beg = begCalendar.get(Calendar.DAY_OF_WEEK);
				end = endCalendar.get(Calendar.DAY_OF_WEEK);
				if (beg > end) {
					workDay = totalDay - 2 * (totalWeeks + 1);
				} else {
					if (end == 7 || beg == 1 || beg == 7) {
						workDay = totalDay - 2 * totalWeeks - 1;
					} else {
						workDay = totalDay - 2 * totalWeeks;
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error(e.toString());
		}
		LOGGER.debug("==================== leave WorkingDaysUtils getWorkingDays return[workDay: " + workDay
				+ "]====================");
		return workDay;
	}
	
	
//	/**
//	 * 根据入职时间计算工作天数
//	 * @param offerTime 入职时间  (yyyy-MM-dd)
//	 * @return workDay 工作天数
//	 */
	/*public static Integer getWorkingDaysByOfferTime(String offerTime) {
		Integer totalDay, entryDay, workDay = 0, totalWeeks, beg, end;
		LOGGER.debug("==================== enter WorkingDaysUtils getWorkingDays param[offerTime: " + offerTime
				+ "]====================");
		try {
			Date offerDate = SDF.parse(offerTime);
			Calendar begCalendar = Calendar.getInstance();
			Calendar endCalendar = Calendar.getInstance();
			endCalendar.set(Calendar.DAY_OF_MONTH, 0);// 设置为上月
//			totalDay = endCalendar.get(Calendar.DATE);// 获取上月总天数
			totalDay = endCalendar.get(Calendar.DATE) - offerDate.getDay();
			totalWeeks = totalDay / 7; // 总周数
			begCalendar.setTime(offerDate);
			entryDay = begCalendar.get(Calendar.DATE);// 入职日期
			if (totalDay % 7 == 0) {
				workDay = totalDay - 2 * totalWeeks;
			} else {
				// 周日为1，周六为7
				beg = begCalendar.get(Calendar.DAY_OF_WEEK);
				end = endCalendar.get(Calendar.DAY_OF_WEEK);
				if (beg > end) {
					workDay = totalDay - 2 * (totalWeeks + 1);
				} else {
					if (end == 7 || beg == 1 || beg == 7) {
						workDay = totalDay - 2 * totalWeeks - 1;
					} else {
						workDay = totalDay - 2 * totalWeeks;
					}
				}
			}
		} catch (ParseException e) {
			LOGGER.error(e.toString());
		}
		LOGGER.debug("==================== leave WorkingDaysUtils getWorkingDays return[workDay: " + workDay
				+ "]====================");
		return workDay;
	}*/
	/**
	 * 根据入职时间、离职时间计算工作天数
	 * @param offerTime 入职时间(yyyy-MM-dd)  endTime 结束时间 (yyyy-MM-dd)
	 * @return workDay 工作天数
	 */
	public static Integer getWorkingDays(String offerTime, String endTime) {
		Integer totalDay, entryDay, workDay = 0, totalWeeks, beg, end;
		LOGGER.debug("==================== enter WorkingDaysUtils getWorkingDays param[offerTime: " + offerTime
				+ ", endTime " + endTime + " ]====================");
		try {
			Date offerDate = SDF.parse(offerTime);
			Date endDate = SDF.parse(endTime);
			Calendar begCalendar = Calendar.getInstance();
			Calendar endCalendar = Calendar.getInstance();
			totalDay = (int) (((endDate.getTime() - offerDate.getTime()) / (24 * 60 * 60 * 1000)) + 1); // 总天数
			totalWeeks = totalDay / 7; // 总周数
			begCalendar.setTime(offerDate);
			endCalendar.setTime(endDate);
			if (totalDay % 7 == 0) {
				workDay = totalDay - 2 * totalWeeks;
			} else {
				// 周日为1，周六为7
				beg = begCalendar.get(Calendar.DAY_OF_WEEK);
				end = endCalendar.get(Calendar.DAY_OF_WEEK);
				if (beg > end) {
					workDay = totalDay - 2 * (totalWeeks + 1);
				} else {
					if (end == 7 || beg == 1 || beg == 7) {
						workDay = totalDay - 2 * totalWeeks - 1;
					} else {
						workDay = totalDay - 2 * totalWeeks;
					}
				}
			}
		} catch (ParseException e) {
			LOGGER.error(e.toString());
		}
		LOGGER.debug("==================== leave WorkingDaysUtils getWorkingDays return[workDay: " + workDay
				+ "]====================");
		return workDay;

	}
//	/**
//	 * 根据结束时间 计算工作天数
//	 * @param endTime 结束时间 (yyyy-MM-dd)
//	 * @return workDay 工作天数
//	 */
	/*public static Integer getWorkingDaysByEndTime(String endTime) {
		Integer totalDay, entryDay, workDay = 0, totalWeeks, beg, end;
		LOGGER.debug("==================== enter WorkingDaysUtils getWorkingDaysByEndTime param[endTime: " + endTime+" ]====================");
		try {
			Date endDate = SDF.parse(endTime);
			Calendar begCalendar = Calendar.getInstance();
			Calendar endCalendar = Calendar.getInstance();
			totalDay = endDate.getDay(); // 总天数
			totalWeeks = totalDay / 7; // 总周数
//			begCalendar.setTime(offerDate);
			endCalendar.setTime(endDate);
			if (totalDay % 7 == 0) {
				workDay = totalDay - 2 * totalWeeks;
			} else {
				// 周日为1，周六为7
				beg = begCalendar.get(Calendar.DAY_OF_WEEK);
				end = endCalendar.get(Calendar.DAY_OF_WEEK);
				if (beg > end) {
					workDay = totalDay - 2 * (totalWeeks + 1);
				} else {
					if (end == 7 || beg == 1 || beg == 7) {
						workDay = totalDay - 2 * totalWeeks - 1;
					} else {
						workDay = totalDay - 2 * totalWeeks;
					}
				}
			}
		} catch (ParseException e) {
			LOGGER.error(e.toString());
		}
		LOGGER.debug("==================== leave WorkingDaysUtils getWorkingDays return[workDay: " + workDay
				+ "]====================");
		return workDay;

	}*/

	/**
	 * 获取上月的第一天
	 */
	public static Date getLastMonthFirstDay() {
		Calendar cale = Calendar.getInstance();// 获取当前日期
		cale.add(Calendar.MONTH, -1); // -1为上月 1为本月
		cale.set(Calendar.DAY_OF_MONTH, 1);// 1为本月第一天  0为上月最后一天
		return cale.getTime();
	}

	/**
	 * 获取本月的第一天
	 * 
	 * @return
	 */
	public static Date getThisMonthFirstDay() {
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 1);// 1为本月第一天  0为上月最后一天
		return cale.getTime();
	}
	/**
	 * 获取上月的最后一天
	 * @return
	 */
	public static Date getLastMonthLastDay(){
		Calendar cale = Calendar.getInstance();
		cale.set(Calendar.DAY_OF_MONTH, 0);// 1为本月第一天  0为上月最后一天
		return cale.getTime();
	}
	
	/**
	 * 获取本月最后一天
	 * @return
	 */
	public static Date getThisMonthLastDay() {
		Calendar cale = Calendar.getInstance();// 获取当前日期
		cale.add(Calendar.MONTH, 1); // -1为上月 1为本月
		cale.set(Calendar.DAY_OF_MONTH, 0);
		return cale.getTime();
	}
//	/**
//	 * 获取上月月份 
//	 * @return yyyy-MM
//	 */
//	public static Date getLastMonth() {
//		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
////		String month = format.format(getLastMonthFirstDay());
//		try {
//			return format.parse(format.format(getLastMonthFirstDay()));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	/**
	 * 对结果进行四舍五入
	 * @param value 原始数 
	 * @param resLen 保留的精度
	 * @return
	 */
	public static Number getRealVaule(double value,int resLen) {  
        if(resLen==0) {
            //原理:123.456*10=1234.56+5=1239.56/10=123  
            //原理:123.556*10=1235.56+5=1240.56/10=124  
            return Math.round(value*10+5)/10;
        }
        double db  = Math.pow(10, resLen);  
        return Math.round(value*db)/db;  
    }  

}
