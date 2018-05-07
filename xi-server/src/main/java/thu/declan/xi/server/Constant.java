package thu.declan.xi.server;

/**
 *
 * @author declan
 */
public class Constant {
	
    //Auth roles

    public static final String ROLE_ADMIN = "ADMIN";
    
    public static final String ROLE_STUDENT = "STUDENT";

    public static final String ROLE_COMPANY = "COMPANY";
	
	//Session
	
	public static final String SESSION_ACCOUNT = "ACCOUNT";
	
	public static final String SESSION_ENTITY_ID = "ENTITY_ID";
    
    //uploads
	
	public static final String UPLOAD_DIR = "/opt/web/upload";
	
	public static final String UPLOAD_CONTEXT_PATH = "/upload";
    
    // rate configurations
    
    public static final Double SERVICE_FEE_RATE = 0.1;
	
	/*------------- email info -------------*/
//	public static final String EMAIL_SMTPHOST = "smtp.ym.163.com";
//	public static final String EMAIL_SMTPPORT = "25";
//	public static final String EMAIL_USERNAME = "wangzc@xiangshixi.cc";
//	public static final String EMAIL_PASSWORD = "1qaz2wsX";
//	public static final boolean EMAIL_USESSL = false;
    
	/*------------- email info -------------*/
	public static final String EMAIL_SMTPHOST = "smtp.ryanljr.cn";
	public static final String EMAIL_SMTPPORT = "465";
	public static final String EMAIL_USERNAME = "ceshi@umtey.com";
	public static final String EMAIL_PASSWORD = "Ceshi123";
	public static final boolean EMAIL_USESSL = true;
    
    /*------------- wechat info -------------*/
    public static final String WECHAT_OPEN_APPID = "wx44895bec275b61e6";
    public static final String WECHAT_OPEN_SECRET = "15d2424d3ec81b316972fc72b6ba6cec";
    public static final String WECHAT_APPID = "wx3157ff5481ec5025";
    public static final String WECHAT_SECRET = "3af4a208195f3b7aa3ddc50f5d6e9e09";
    public static final String WECHAT_MCH_ID = "1452897002";
    public static final String WECHAT_MCH_KEYPATH = "/opt/web/mchpay_api_cert.p12";
	public static final String WECHAT_MCH_SECRET = "xiangshixixiangshixixiangshixixi";
	
	public static final String SERVER_IP = "10.9.39.15";
    public static final String SERVER_DOMAIN = "www.xiangshixi.com";
    public static final String SERVER_SCHEME = "http";
    public static final String API_BASE = "backend/api";
    
}
