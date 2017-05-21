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
					String subject = type == MAIL_TYPE_SUGGEST ? "�������"
							: "������־";
					MailSenderInfo mailInfo = new MailSenderInfo();
					mailInfo.setMailServerHost("smtp.163.com");
					mailInfo.setMailServerPort("25");
					mailInfo.setValidate(true);
					mailInfo.setUserName(from); // ��������ַ
					mailInfo.setPassword(pwd);// ������������
					mailInfo.setFromAddress(from); // ���͵�����
					mailInfo.setToAddress(to); // �����ĸ��ʼ�ȥ
					mailInfo.setSubject(subject); // �ʼ�����
					mailInfo.setContent(content); // �ʼ��ı�
					// �������Ҫ�������ʼ�
					SimpleMailSender sms = new SimpleMailSender();
					sms.sendTextMail(mailInfo);// ���������ʽ
					// sms.sendHtmlMail(mailInfo);//����html��ʽ
				} catch (Exception e) {
					Log.e("==SendMail", e.getMessage(), e);
				}
			}
		}).start();

	}
}
