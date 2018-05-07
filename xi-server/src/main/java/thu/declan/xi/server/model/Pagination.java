package thu.declan.xi.server.model;

/**
 *
 * @author declan
 */
public class Pagination {

	public Pagination() {
	}

	public Pagination(Integer pageSize, Integer pageIndex) {
		if (pageSize == null || pageIndex == null) {
			this.pageSize = Integer.MAX_VALUE;
			this.pageIndex = 1;
		} else {
			this.pageSize = Math.max(1, pageSize);
			this.pageIndex = Math.max(1, pageIndex);
		}
	}
	
    private int pageSize = Integer.MAX_VALUE;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    private int pageIndex = 1;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    private int pageCnt;

    public int getPageCnt() {
        return pageCnt;
    }

    public void setPageCnt(int pageCnt) {
        this.pageCnt = pageCnt;
    }

    private int rowCnt;

    public int getRowCnt() {
        return rowCnt;
    }

    public void setRowCnt(int rowCnt) {
        this.rowCnt = rowCnt;
    }
    
}
