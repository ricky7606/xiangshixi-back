package thu.declan.xi.server.mapper;

import org.apache.ibatis.annotations.Param;
import thu.declan.xi.server.model.Account;

/**
 *
 * @author declan
 */
public interface AccountMapper extends BaseMapper<Account> {
	
	public Account selectByIdentity(Account matcher);
    
    public void addPoint(@Param(value="id") int accountId, @Param(value="point") int point);
    
    public void addBalance(@Param(value="id") int accountId, @Param(value="balance") double balance);
	
}
