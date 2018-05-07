package thu.declan.xi.server.service.impl;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import thu.declan.xi.server.CacheConfig;
import thu.declan.xi.server.Constant;
import thu.declan.xi.server.exception.ServiceException;
import thu.declan.xi.server.model.Notification;
import thu.declan.xi.server.service.WechatService;
import weixin.popular.api.MessageAPI;
import weixin.popular.api.PayMchAPI;
import weixin.popular.api.TicketAPI;
import weixin.popular.api.TokenAPI;
import weixin.popular.bean.message.templatemessage.TemplateMessage;
import weixin.popular.bean.message.templatemessage.TemplateMessageItem;
import weixin.popular.bean.paymch.Transfers;
import weixin.popular.bean.paymch.TransfersResult;
import weixin.popular.bean.ticket.Ticket;
import weixin.popular.bean.token.Token;
import weixin.popular.client.LocalHttpClient;

/**
 *
 * @author declan
 */
@Service("wechatService")
public class WechatServiceImpl implements WechatService, InitializingBean {

	@Override
	public void afterPropertiesSet() throws Exception {
		LocalHttpClient.initMchKeyStore(Constant.WECHAT_MCH_ID, Constant.WECHAT_MCH_KEYPATH);
	}

	@Override
	@Cacheable(CacheConfig.CACHE_ACCESS_TOKEN)
	public String getAccessToken() throws ServiceException {
		Token token = TokenAPI.token(Constant.WECHAT_APPID, Constant.WECHAT_SECRET);
		if (token.getErrmsg() != null && !token.getErrmsg().isEmpty()
				&& Integer.parseInt(token.getErrcode()) != 0) {

			throw new ServiceException(ServiceException.CODE_EXTERNAL_ERROR, "Get access_token failed: " + token.getErrcode() + " " + token.getErrmsg());
		}
		return token.getAccess_token();
	}

	@Cacheable(CacheConfig.CACHE_JSAPI_TICKET)
	private String getJsAPITicket() throws ServiceException {
		Ticket ticket = TicketAPI.ticketGetticket(getAccessToken());
		if (ticket.getErrmsg() != null && !ticket.getErrmsg().isEmpty()
				&& Integer.parseInt(ticket.getErrcode()) != 0) {
			throw new ServiceException(ServiceException.CODE_EXTERNAL_ERROR, "Get jsapi_ticket failed: " + ticket.getErrcode() + " " + ticket.getErrmsg());
		}
		return ticket.getTicket();
	}

	@Override
	public Map<String, String> getJSSDKSign(String url) throws ServiceException {
		Map<String, String> ret = new HashMap<>();
		String nonce_str = create_nonce_str();
		String timestamp = create_timestamp();
		String string1;
		String signature = "";

		//注意这里参数名必须全部小写，且必须有序
		String jsapi_ticket = getJsAPITicket();
		string1 = "jsapi_ticket=" + jsapi_ticket
				+ "&noncestr=" + nonce_str
				+ "&timestamp=" + timestamp
				+ "&url=" + url;

		try {
			MessageDigest crypt = MessageDigest.getInstance("SHA-1");
			crypt.reset();
			crypt.update(string1.getBytes("UTF-8"));
			signature = byteToHex(crypt.digest());
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		ret.put("url", url);
		ret.put("jsapi_ticket", jsapi_ticket);
		ret.put("nonceStr", nonce_str);
		ret.put("timestamp", timestamp);
		ret.put("signature", signature);

		return ret;
	}

	@Async
	@Override
	public void sendTemplateMessage(String tplId, String openid, String url, Map<String, String> data) throws ServiceException {
		TemplateMessage tplMsg = new TemplateMessage();
		tplMsg.setTemplate_id(tplId);
		tplMsg.setTouser(openid);
		tplMsg.setUrl(url == null ? Constant.SERVER_SCHEME + "://" + Constant.SERVER_DOMAIN : url);
		LinkedHashMap<String, TemplateMessageItem> d = new LinkedHashMap<>();
		for (String k : data.keySet()) {
			TemplateMessageItem item = new TemplateMessageItem(data.get(k), "#173177");
			d.put(k, item);
		}
		tplMsg.setData(d);
		MessageAPI.messageTemplateSend(getAccessToken(), tplMsg);
	}

	@Override
	public void sendTemplateMessage(String tplId, String openid, Notification noti, Map<String, String> data) throws ServiceException {
		String url = noti == null ? null : String.format("%s://%s/%s/wechat/redirect/notification/%d", 
                Constant.SERVER_SCHEME, Constant.SERVER_DOMAIN, Constant.API_BASE, noti.getId());
		sendTemplateMessage(tplId, openid, url, data);
	}

	private String byteToHex(final byte[] hash) {
		String result;
		try (Formatter formatter = new Formatter()) {
			for (byte b : hash) {
				formatter.format("%02x", b);
			}
			result = formatter.toString();
		}
		return result;
	}

	private String create_nonce_str() {
		return UUID.randomUUID().toString();
	}

	private String create_timestamp() {
		return Long.toString(System.currentTimeMillis() / 1000);
	}

	@Override
	public void transfer(String openid, String checkname, int tradeNo, double value) throws ServiceException {
		Transfers trans = new Transfers();
		String nonce = create_nonce_str().substring(0, 32);
		trans.setAmount(String.format("%.0f", value*100));
//		trans.setCheck_name("FORCE_CHECK");
		trans.setCheck_name("NO_CHECK");
		trans.setDesc("提现");
		trans.setMch_appid(Constant.WECHAT_APPID);
		trans.setMchid(Constant.WECHAT_MCH_ID);
		trans.setNonce_str(nonce);
		trans.setOpenid(openid);
		trans.setPartner_trade_no(String.format("%d", tradeNo));
		trans.setRe_user_name(checkname);
		trans.setSpbill_create_ip(Constant.SERVER_IP);
		trans.setSign_type(null);
//		String string1 = "amount=" + trans.getAmount()
//				+ "&check_name=" + trans.getCheck_name()
//				+ "&desc=" + trans.getDesc()
//				+ "&mch_appid=" + trans.getMch_appid()
//				+ "&mchid=" + trans.getMchid()
//				+ "&nonce_str=" + trans.getNonce_str()
//				+ "&openid=" + trans.getOpenid()
//				+ "&partner_trade_no=" + trans.getPartner_trade_no()
//				+ "&re_user_name=" + trans.getRe_user_name()
//				+ "&spbill_create_ip=" + trans.getSpbill_create_ip()
//				+ "&key=" + Constant.WECHAT_MCH_SECRET;
//		String sign = EncryptionUtils.md5(string1).toUpperCase();
//		trans.setSign(sign);
		TransfersResult result = PayMchAPI.mmpaymkttransfersPromotionTransfers(trans, Constant.WECHAT_MCH_SECRET);
		if (result.getErr_code() != null) {
			throw new ServiceException(ServiceException.CODE_EXTERNAL_ERROR, "Wechat transfer failed: [" + result.getErr_code() + "] " + result.getErr_code_des());
		}
	}

}
