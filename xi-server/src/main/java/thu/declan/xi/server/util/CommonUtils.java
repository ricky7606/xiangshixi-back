package thu.declan.xi.server.util;

import java.util.Random;

/**
 *
 * @author declan
 */
public class CommonUtils {

	/**
	 * 产生一个随机的字符
	 *
	 * @param length 长度
	 * @return 随机字符串
	 */
	public static String randomString(int length) {
		String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random random = new Random();
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(62);
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}

	public static double decimalDouble(Double v) {
		int v2 = (int) (v * 100 + 0.00001);
		return (double) v2 / 100;
	}

}
