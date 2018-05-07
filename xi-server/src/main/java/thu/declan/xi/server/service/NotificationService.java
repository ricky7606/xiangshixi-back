package thu.declan.xi.server.service;

import thu.declan.xi.server.model.Notification;

/**
 *
 * @author declan
 */
public interface NotificationService extends BaseTableService<Notification> {

	public Notification addNoti(int accountId, Notification.NType type, int refId, String msgTpl, Object... args);
	
	public Notification addNoti(int accountId, Notification.NType type, int refId, boolean sendEmail, String msgTpl, Object... args);
	
    public Integer unreadCnt(int accountId);
    
}
