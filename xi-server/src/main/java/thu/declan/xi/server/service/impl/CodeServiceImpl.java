package thu.declan.xi.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.mapper.CodeMapper;
import thu.declan.xi.server.mapper.BaseMapper;
import thu.declan.xi.server.model.Code;
import thu.declan.xi.server.service.CodeService;
import thu.declan.xi.server.util.EncryptionUtils;

/**
 *
 * @author declan
 */
@Service("codeService")
public class CodeServiceImpl extends BaseTableServiceImpl<Code> implements CodeService {

	@Autowired
	private CodeMapper codeMapper;

	@Override
	protected BaseMapper getMapper() {
		return codeMapper;
	}
    
    @Override
    protected void preAdd(Code code) throws ServiceException {
        if (codeMapper.selectCount(code) > 0) {
            throw new ServiceException(ServiceException.CODE_DUPLICATE_ELEMENT, "Just sent in 1 min.");
        }
    }

    @Override
    public Code addRandom(String phone) throws ServiceException {
        Code c = new Code(phone);
        c.setCode(EncryptionUtils.randomPassword(4));
        add(c);
        return c;
    }

    @Override
    public Code verify(Code code) throws ServiceException {
        Code c = codeMapper.verifyCode(code);
        if (c == null) {
            throw new ServiceException(ServiceException.CODE_VERIFY_FAILED, "Verify failed!");
        }
        return c;
    }

}
