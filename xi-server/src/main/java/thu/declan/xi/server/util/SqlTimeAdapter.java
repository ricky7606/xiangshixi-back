package thu.declan.xi.server.util;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author declan
 */
public class SqlTimeAdapter extends XmlAdapter<java.sql.Time, java.util.Date> {

    @Override
    public java.sql.Time marshal(java.util.Date utilDate) throws Exception {
        if(null == utilDate) {
            return null;
        }
        return new java.sql.Time(utilDate.getTime());
    }

	@Override
    public java.util.Date unmarshal(java.sql.Time sqlTime) throws Exception {
        if(null == sqlTime) {
            return null;
        }
        return new java.util.Date(sqlTime.getTime());
    }
 
}
