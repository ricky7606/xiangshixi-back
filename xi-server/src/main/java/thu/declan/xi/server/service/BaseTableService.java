package thu.declan.xi.server.service;

import java.util.List;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Pagination;

/**
 *
 * @author declan
 * @param <T>
 */
public interface BaseTableService<T> {
    
    public void add(T object) throws ServiceException;
    
    public void update(T update) throws ServiceException;
    
    public T get(int id) throws ServiceException;
	
    public List<T> getList(T object, Pagination pagination) throws ServiceException;
	
	public List<T> getList(T object) throws ServiceException;
    
    public List<T> getList() throws ServiceException;
    
    public int getCount(T object);
    
}
