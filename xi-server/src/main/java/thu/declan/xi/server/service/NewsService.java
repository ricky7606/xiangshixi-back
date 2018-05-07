package thu.declan.xi.server.service;

import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.News;

/**
 *
 * @author declan
 */
public interface NewsService extends BaseTableService<News> {
	
	public void delete(int id);
	
	public void incView(int id);

	public void incStar(int accId, int id) throws ServiceException;
	
	public void decStar(int accId, int id) throws ServiceException;
	
	public void setStarred(int accId, News news);
	
}
