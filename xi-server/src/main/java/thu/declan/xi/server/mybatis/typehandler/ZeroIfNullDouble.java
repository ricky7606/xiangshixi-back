package thu.declan.xi.server.mybatis.typehandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 *
 * @author declan
 */
public class ZeroIfNullDouble implements TypeHandler<Double> {

	@Override
	public void setParameter(PreparedStatement ps, int i, Double t, JdbcType jt) throws SQLException {
	}

	@Override
	public Double getResult(ResultSet rs, String col) throws SQLException {
		return rs.getDouble(col);
	}

	@Override
	public Double getResult(ResultSet rs, int i) throws SQLException {
		return rs.getDouble(i);
	}

	@Override
	public Double getResult(CallableStatement cs, int i) throws SQLException {
		return cs.getDouble(i);
	}
	
}
