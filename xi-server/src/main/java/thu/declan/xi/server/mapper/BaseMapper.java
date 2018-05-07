package thu.declan.xi.server.mapper;

import java.util.List;
import org.apache.ibatis.session.RowBounds;

/**
 *
 * @author declan
 * @param <T>
 */
public interface BaseMapper<T> {

    void insert(T object);

    void delete(int id);
    
    void update(T object);

    T selectOne(int id);

    List<T> selectList();
    
    List<T> selectList(RowBounds bounds);
    
    List<T> selectList(T object);

    List<T> selectList(T object, RowBounds bounds);

    Integer selectCount(T object);

}
