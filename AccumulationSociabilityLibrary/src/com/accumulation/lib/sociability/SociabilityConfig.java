package com.accumulation.lib.sociability;

import com.accumulation.lib.tool.config.BaseConfig;
import com.accumulation.lib.tool.net.RequestSetting;
import com.accumulation.lib.tool.net.ResponseSetting;
import com.accumulation.lib.tool.net.ResponseSetting.Builder;

public abstract class SociabilityConfig extends BaseConfig{
	
	// /�û�--------------------------------------------------------------------------------
	public static final String ACCOUNT = "/account";
	// /��̬--------------------------------------------------------------------------------
	public static final String BROADCAST = "/Broadcast";
	// /�ϴ�--------------------------------------------------------------------------------
	public static final String UPLOAD = "/Upload";
	// /��Ϣ����--------------------------------------------------------------------------------
	public static final String SUBJECT = "/subject";
	// /˽��--------------------------------------------------------------------------------
	public static final String MESSAGE = "/Message";
	// /˽��--------------------------------------------------------------------------------
	public static final String ARTICLE = "/Article";
	

	public Class<? extends RequestSetting.Builder> getRequestBuilderClass(){
		return BaseRequestBuilder.class;
	}

	public  <T> Class< ? extends ResponseSetting.Builder<T>>  getResponseBuilderClass(Class<T> c){
		return (Class<? extends Builder<T>>) BaseResponseBuilder.class;
	}
	
	/** �û���¼��ַ */
	public String getLoginURL(){
		return getDomain()+ACCOUNT + "/LoginForApp";
	}
	
	/** �û��˳���¼��ַ */
	public String getLogoutURL(){
		return getDomain()+ACCOUNT + "/Logout";
	}
	public String getRegisterURL(){
		return getDomain()+ACCOUNT + "/RegistUserInfo";
	}
	
	
	/** �û��˳���¼��ַ */
	public String getUserProfileURL(){
		return getDomain()+ACCOUNT + "/GetUserProfile";
	}
	/** �û��˳���¼��ַ */
	public String getUserProfileByEmailURL(){
		return getDomain()+ACCOUNT + "/GetUserProfileByEmail";
	}
	/** ����˽�ŵ�ַ */
	public String sendMessageURL(){
		return getDomain()+ACCOUNT + "/SendMessage";
	}
	/** ����˽�ŵ�ַ */
	public String getMessageURL(){
		return getDomain()+MESSAGE + "/GetMessagesByTarget";
	}
	/** �û��˳���¼��ַ */
	public String getMyProfileURL(){
		return getDomain()+ACCOUNT + "/getmyprofile";
	}
	
	/** �û��˳���¼��ַ */
	public String getAllSubjectURL(){
		return getDomain()+SUBJECT + "/GetAllSubject";
	}
	
	/** ��ȡ��̬�б��ַ */
	public String getBroadcastListURL(){
		return getDomain()+BROADCAST + "/Get";
	}
	/** ��ȡ��̬�б��ַ */
	public String getBroadcastByIdURL(){
		return getDomain()+BROADCAST + "/Get";
	}
	/** ��ȡ��̬�б��ַ */
	public String getBroadcastReplyURL(){
		return getDomain()+BROADCAST + "/GetReply";
	}
	
	/** ��ȡ��̬�б��ַ */
	public String deleteBroadcastReplyURL(){
		return getDomain()+BROADCAST + "/RemoveReply";
	}
	/** ��ȡ��̬�б��ַ */
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
	/** ˽���б��ַ */
	public String getRecentChatURL(){
		return getDomain()+MESSAGE + "/GetTalkTo";
	}
	
}
