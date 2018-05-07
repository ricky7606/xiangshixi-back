package thu.declan.xi.server.service.impl;

import java.util.List;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.model.Pagination;
import thu.declan.xi.server.service.BaseTableService;

/**
 *
 * @author declan
 * @param <T>
 */
public abstract class BaseTableServiceImpl<T> implements BaseTableService<T> {

	
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseTableServiceImpl.class);
	
	protected abstract BaseMapper<T> getMapper();
	
	protected void preAdd(T object) throws ServiceException {
		
	}
	
	protected void postAdd(T object) throws ServiceException {
		
	}
	
	protected void preUpdate(T object) throws ServiceException {
		
	}
	
	protected void postUpdate(T object) throws ServiceException {
		
	}
	
	protected void postGetList(List<T> objects) {
		for (T obj : objects) {
			postGet(obj);
		}
	}
	
	protected void postGet(T object) {
		
	}
    
	@Override
	public List<T> getList(T objectSelector, Pagination pagination) throws ServiceException {
		int limit = pagination.getPageSize();
		int offset = (pagination.getPageIndex() - 1) * limit;
		List<T> objects = getMapper().selectList(objectSelector, new RowBounds(offset, limit));
		int count = getMapper().selectCount(objectSelector);
		pagination.setRowCnt(count);
		pagination.setPageCnt((count - 1) / limit + 1);
		postGetList(objects);
		return objects;
	}
	
	@Override
	public List<T> getList(T objectSelector) throws ServiceException {
		List<T> objects = getMapper().selectList(objectSelector);
		postGetList(objects);
		return objects;
	}
    
    @Override
	public List<T> getList() throws ServiceException {
		List<T> objects = getMapper().selectList();
		postGetList(objects);
		return objects;
	}
    
    @Override
    public int getCount(T objectSelector) {
        return getMapper().selectCount(objectSelector);
    }
	
	@Override
	public void add(T object) throws ServiceException {
		preAdd(object);
		try {	
			getMapper().insert(object);
		} catch (Exception e) {
			LOGGER.error("exception in inserting object: " + e);
			throw new ServiceException(ServiceException.CODE_DATABASE_ERR, "Mysql error: " + e);
		}
		postAdd(object);
	}

	@Override
	public void update(T update) throws ServiceException {
		preUpdate(update);
		try {
			getMapper().update(update);
		} catch (Exception e) {
			LOGGER.error("exception in updating object: " + e);
			throw new ServiceException(ServiceException.CODE_DATABASE_ERR, "Mysql error: " + e);
		}
		postUpdate(update);
	}

	@Override
	public T get(int id) throws ServiceException {
        T obj = null;
		try {
			obj = getMapper().selectOne(id);
		} catch (Exception e) {
			LOGGER.error("exception in get object: " + e);
			throw new ServiceException(ServiceException.CODE_DATABASE_ERR, "Mysql error: " + e);
		}
        if (obj == null) {
            throw new ServiceException(ServiceException.CODE_NO_SUCH_ELEMENT, "No such element");
        }
		postGet(obj);
        return obj;
	}
	
}
