package thu.declan.xi.server.mapper;

import thu.declan.xi.server.model.Code;

/**
 *
 * @author declan
 */
public interface CodeMapper extends BaseMapper<Code> {
    
    Code verifyCode(Code code);
    
}
