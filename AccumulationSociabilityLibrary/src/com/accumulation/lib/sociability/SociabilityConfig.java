package com.accumulation.lib.sociability;

import com.accumulation.lib.tool.config.BaseConfig;
import com.accumulation.lib.tool.net.RequestSetting;
import com.accumulation.lib.tool.net.ResponseSetting;
import com.accumulation.lib.tool.net.ResponseSetting.Builder;

public abstract class SociabilityConfig extends BaseConfig{
	
	// /用户--------------------------------------------------------------------------------
	public static final String ACCOUNT = "/account";
	// /动态--------------------------------------------------------------------------------
	public static final String BROADCAST = "/Broadcast";
	// /上传--------------------------------------------------------------------------------
	public static final String UPLOAD = "/Upload";
	// /信息分类--------------------------------------------------------------------------------
	public static final String SUBJECT = "/subject";
	// /私信--------------------------------------------------------------------------------
	public static final String MESSAGE = "/Message";
	// /私信--------------------------------------------------------------------------------
	public static final String ARTICLE = "/Article";
	

	public Class<? extends RequestSetting.Builder> getRequestBuilderClass(){
		return BaseRequestBuilder.class;
	}

	public  <T> Class< ? extends ResponseSetting.Builder<T>>  getResponseBuilderClass(Class<T> c){
		return (Class<? extends Builder<T>>) BaseResponseBuilder.class;
	}
	
	/** 用户登录地址 */
	public String getLoginURL(){
		return getDomain()+ACCOUNT + "/LoginForApp";
	}
	
	/** 用户退出登录地址 */
	public String getLogoutURL(){
		return getDomain()+ACCOUNT + "/Logout";
	}
	public String getRegisterURL(){
		return getDomain()+ACCOUNT + "/RegistUserInfo";
	}
	
	
	/** 用户退出登录地址 */
	public String getUserProfileURL(){
		return getDomain()+ACCOUNT + "/GetUserProfile";
	}
	/** 用户退出登录地址 */
	public String getUserProfileByEmailURL(){
		return getDomain()+ACCOUNT + "/GetUserProfileByEmail";
	}
	/** 发送私信地址 */
	public String sendMessageURL(){
		return getDomain()+ACCOUNT + "/SendMessage";
	}
	/** 发送私信地址 */
	public String getMessageURL(){
		return getDomain()+MESSAGE + "/GetMessagesByTarget";
	}
	/** 用户退出登录地址 */
	public String getMyProfileURL(){
		return getDomain()+ACCOUNT + "/getmyprofile";
	}
	
	/** 用户退出登录地址 */
	public String getAllSubjectURL(){
		return getDomain()+SUBJECT + "/GetAllSubject";
	}
	
	/** 获取动态列表地址 */
	public String getBroadcastListURL(){
		return getDomain()+BROADCAST + "/Get";
	}
	/** 获取动态列表地址 */
	public String getBroadcastByIdURL(){
		return getDomain()+BROADCAST + "/Get";
	}
	/** 获取动态列表地址 */
	public String getBroadcastReplyURL(){
		return getDomain()+BROADCAST + "/GetReply";
	}
	
	/** 获取动态列表地址 */
	public String deleteBroadcastReplyURL(){
		return getDomain()+BROADCAST + "/RemoveReply";
	}
	/** 获取动态列表地址 */
	public String deleteBroadcastURL(){
		return getDomain()+BROADCAST + "/Remove";
	}
	public String addBroadcastReplyURL(){
		return getDomain()+BROADCAST + "/Reply";
	}
	public String addArticleURL(){
		return getDomain()+ARTICLE + "/CreateOrEdit";
	}
	public String likeBroadcastURL(){
		return getDomain()+BROADCAST + "/Like";
	}
	
	public String favoriteBroadcastReplyURL(){
		return getDomain()+BROADCAST + "/Collect";
	}
	
	public String cancelFavoriteBroadcastURL(){
		return getDomain()+BROADCAST + "/CancelCollect";
	}
	
	public String resendBroadcastURL(){
		return getDomain()+BROADCAST + "/Resend";
	}
	
	public String sendBroadcastURL(){
		return getDomain()+BROADCAST + "/Send";
	}
	public String getBroadcastTargetURL(){
		return getDomain()+BROADCAST + "/GetSendToTarget";
	}
	
	public String getUploadURL(){
		return getDomain()+UPLOAD + "/Save";
	}
	/** 私信列表地址 */
	public String getRecentChatURL(){
		return getDomain()+MESSAGE + "/GetTalkTo";
	}
	
}
