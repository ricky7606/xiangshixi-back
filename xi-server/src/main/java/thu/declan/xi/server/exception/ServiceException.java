package thu.declan.xi.server.exception;

/**
 *
 * @author declan
 */
public class ServiceException extends Throwable {
    
    public static final int CODE_DATABASE_ERR = 100;
    public static final int CODE_WRONG_PARA = 101;
    public static final int CODE_NO_SUCH_ELEMENT = 102;
	public static final int CODE_DUPLICATE_ELEMENT = 103;
	public static final int CODE_WRONG_PASSWORD = 104;
    public static final int CODE_ACCOUNT_FROZEN = 105;
	public static final int CODE_FK_CONSTRAINT = 106;
	public static final int CODE_UK_CONSTRAINT = 107;
	public static final int CODE_VERIFY_FAILED = 108;
	public static final int CODE_EXTERNAL_ERROR = 109;

	

    public ServiceException(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }
    
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
}
