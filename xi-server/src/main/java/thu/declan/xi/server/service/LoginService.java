package thu.declan.xi.server.service;

import thu.declan.xi.server.exception.ServiceException;

/**
 *
 * @author declan
 * @param <T>
 */
public interface LoginService<T> {
    
    public T login(String username, String password) throws ServiceException;
    
}
