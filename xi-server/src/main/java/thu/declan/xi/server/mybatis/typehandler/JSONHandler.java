package thu.declan.xi.server.mybatis.typehandler;

import com.alibaba.fastjson.JSON;
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
public class JSONHandler implements TypeHandler<Object> {

	/**
	 * json数据和类名的分隔符号
	 */
	private static final char SPLIT = '/';

	@Override
	public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			ps.setString(i, null);
			return;
		}
		String json = JSON.toJSONString(parameter);
		json = json + SPLIT + parameter.getClass().getName();
		ps.setString(i, json);
	}

	@Override
	public Object getResult(ResultSet rs, String columnName) throws SQLException {
		String json = rs.getString(columnName);
		return jsonToObject(json);
	}
	
	@Override
	public Object getResult(ResultSet rs, int columnIndex) throws SQLException {
		String json = rs.getString(columnIndex);
		return jsonToObject(json);
	}

	@Override
	public Object getResult(CallableStatement cs, int columnIndex) throws SQLException {
		String json = cs.getString(columnIndex);
		return jsonToObject(json);
	}

	/**
	 * json 转换成对象
	 */
	private Object jsonToObject(String json) {
		if (json == null) {
			return null;
		}
		int index = json.lastIndexOf(SPLIT);
		if (index < 0) {
			return null;
		}
		String key = json.substring(index + 1, json.length());
		json = json.substring(0, index);
		Class<?> cls = null;
		try {
			cls = Class.forName(key);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("序列化成json时找不到指定的类", e);
		}
		Object ob = JSON.parseObject(json, cls);
		return ob;
	}

}
