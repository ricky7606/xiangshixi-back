package thu.declan.xi.server.util;


import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;

public class EncryptionUtils {

    public static String md5(String data) {
        return DigestUtils.md5Hex(data);
    }

    public static String sha1(String data) {
        return DigestUtils.sha1Hex(data);
    }

    public static String sha256(String data) {
        return DigestUtils.sha256Hex(data);
    }

    public static String randomPassword(int length) {
        String seed = String.format("%s%f", new Date(), Math.random());
        return sha256(seed).substring(0, length);
    }
	
	public static String genProtectedPassword(String originPwd) {
		String seed = CommonUtils.randomString(8);
		return seed + "$" + md5(seed + originPwd);
	}

	public static boolean checkPassword(String originPwd, String protectedPwd) {
		int p = protectedPwd.indexOf("$");
		if (p == -1) {
			return false;
		}
		String seed = protectedPwd.substring(0, p);
		String md5 = protectedPwd.substring(p+1);
		return md5(seed + originPwd).equals(md5);
	}
	
}