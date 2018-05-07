package thu.declan.xi.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.NewsMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.model.News;
import thu.declan.xi.server.service.NewsService;

/**
 *
 * @author declan
 */
@Service("newsService")
public class NewsServiceImpl extends BaseTableServiceImpl<News> implements NewsService {

	@Autowired
	private NewsMapper newsMapper;

	@Override
	protected BaseMapper getMapper() {
		return newsMapper;
	}

	@Override
	public void delete(int id) {
		newsMapper.delete(id);
	}

	@Override
	public void incView(int id) {
		newsMapper.incViewCnt(id);
	}

	@Override
	public void incStar(int accId, int id) throws ServiceException {
		if (newsMapper.selectStar(accId, id) > 0) {
			throw new ServiceException(ServiceException.CODE_UK_CONSTRAINT, "Already starred");
		}
		newsMapper.insertStar(accId, id);
		newsMapper.incStarCnt(id);
	}

	@Override
	public void decStar(int accId, int id) throws ServiceException {
		if (newsMapper.selectStar(accId, id) == 0) {
			throw new ServiceException(ServiceException.CODE_UK_CONSTRAINT, "Already unstarred");
		}
		newsMapper.deleteStar(accId, id);
		newsMapper.decStarCnt(id);
	}

	@Override
	public void setStarred(int accId, News news) {
		news.setStarred(newsMapper.selectStar(accId, news.getId()) > 0);
	}

}
