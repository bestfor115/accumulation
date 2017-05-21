package com.accumulation.lib.tool.mail;

import android.content.Context;

import android.util.Log;

public class MailUtil {

	public static final int MAIL_TYPE_SUGGEST = 0;
	public static final int MAIL_TYPE_LOG = 1;

	public static void sendMail(final int type, final String content,
			Context context) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// TODO Auto-generated method stub
					String from = "bestfor114@163.com";
					String to = "489277210@qq.com";
					String pwd = "Qq545852";
					String subject = type == MAIL_TYPE_SUGGEST ? "意见建议"
							: "错误日志";
					MailSenderInfo mailInfo = new MailSenderInfo();
					mailInfo.setMailServerHost("smtp.163.com");
					mailInfo.setMailServerPort("25");
					mailInfo.setValidate(true);
					mailInfo.setUserName(from); // 你的邮箱地址
					mailInfo.setPassword(pwd);// 您的邮箱密码
					mailInfo.setFromAddress(from); // 发送的邮箱
					mailInfo.setToAddress(to); // 发到哪个邮件去
					mailInfo.setSubject(subject); // 邮件主题
					mailInfo.setContent(content); // 邮件文本
					// 这个类主要来发送邮件
					SimpleMailSender sms = new SimpleMailSender();
					sms.sendTextMail(mailInfo);// 发送文体格式
					// sms.sendHtmlMail(mailInfo);//发送html格式
				} catch (Exception e) {
					Log.e("==SendMail", e.getMessage(), e);
				}
			}
		}).start();

	}
}
