package thu.declan.xi.server.util.email;

import java.util.UUID;

public class EmailInfo {

    public EmailInfo() {
        this.uuid = UUID.randomUUID().toString();
    }

    private String mailTo;

    private String subject;

    private String body;

    private boolean html;

    private String attachedFile;

    private String attachedFileName;

    /**
     * 可选属性，通常保存和此邮件相关的实体信息
     */
    private Object data;

    /**
     * 可选属性，邮件的唯一标识符，有默认值
     */
    private String uuid;

    /**
     * 可选属性，可设置邮件的类型信息
     */
    private String type;

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getMailTo() {
        return this.mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getSubject() {
        return this.subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isHtml() {
        return this.html;
    }

    public void setHtml(boolean html) {
        this.html = html;
    }

    public String getAttachedFileName() {
        return attachedFileName;
    }

    public void setAttachedFileName(String attachedFileName) {
        this.attachedFileName = attachedFileName;
    }

    public String getAttachedFile() {
        return attachedFile;
    }

    public void setAttachedFile(String attachedFile) {
        this.attachedFile = attachedFile;
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
