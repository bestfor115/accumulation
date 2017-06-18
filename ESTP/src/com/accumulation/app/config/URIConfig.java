package com.accumulation.app.config;

public class URIConfig {
	/** ���� */
	public static final String DOMAIN = "http://www.apingtai.cn";
//	 public static final String DOMAIN = "http://192.168.3.116";
	// /ͼƬ--------------------------------------------------------------------------------
	public static final String IMAGE = "/Image";
	/** ��ȡ��̬�б��ַ */
	public static final String IMAGE_URL = DOMAIN + IMAGE + "/Index/";
	public static final String SUBJECT_IMAGE_URL = DOMAIN
			+ "/Upload/SubjectPic/";
	// /��̬--------------------------------------------------------------------------------
	public static final String BROADCAST = "/Broadcast";
	/** ��ȡ��̬�б��ַ */
	public static final String BROADCAST_LIST_URL = DOMAIN + BROADCAST
			+ "/Get?datatype=json";
	/** �㲥���޵�ַ */
	public static final String BROADCAST_LIKE_URL = DOMAIN + BROADCAST
			+ "/Like";
	/** �㲥ɾ����ַ */
	public static final String BROADCAST_REMOVE_URL = DOMAIN + BROADCAST
			+ "/Remove";
	/** �㲥�ղص�ַ */
	public static final String BROADCAST_COLLECT_URL = DOMAIN + BROADCAST
			+ "/Collect";
	/** �㲥ȡ���ղص�ַ */
	public static final String BROADCAST_CANCEL_COLLECT_URL = DOMAIN
			+ BROADCAST + "/CancelCollect";
	/** ��ȡ��̬���б��۵�ַ */
	public static final String REPLAY_LIST_URL = DOMAIN + BROADCAST
			+ "/GetReply";
	/** ������۵�ַ */
	public static final String REPLAY_APPEND_URL = DOMAIN + BROADCAST
			+ "/Reply";
	/** ɾ����̬���б��۵�ַ */
	public static final String REPLAY_DELETE_URL = DOMAIN + BROADCAST
			+ "/RemoveReply";
	/** ��ȡ�����б��ַ */
	public static final String TARGET_LIST_URL = DOMAIN + BROADCAST
			+ "/GetSendToTarget";
	/** ���Ͷ�̬��ַ */
	public static final String BROADCAST_SEND_URL = DOMAIN + BROADCAST
			+ "/Send";
	/** ת����̬��ַ */
	public static final String BROADCAST_RESEND_URL = DOMAIN + BROADCAST
			+ "/Resend";
	/** ���� ��̬��ַ */
	public static final String BROADCAST_SEARCH_URL = DOMAIN + BROADCAST
			+ "/Search";

	// /�û�--------------------------------------------------------------------------------
	public static final String ACCOUNT = "/account";
	/** �û���¼��ַ */
	public static final String LOGIN_URL = DOMAIN + ACCOUNT + "/LoginForApp";
	/** �û���¼��ַ */
	public static final String LOGOUT_URL = DOMAIN + ACCOUNT + "/Logout";
	/** ��ȡ�û���Ϣ��ַ */
	public static final String SELF_PROFILE_URL = DOMAIN + ACCOUNT
			+ "/getmyprofile";
	/** ��ȡ�û���Ϣ��ַ */
	public static final String USER_PROFILE_URL = DOMAIN + ACCOUNT
			+ "/GetUserProfile";
	/** ��ȡ�û���Ϣ��ַ */
	public static final String EMAIL_USER_PROFILE_URL = DOMAIN + ACCOUNT
			+ "/GetUserProfileByEmail";
	/** �޸��û���Ϣ��ַ */
	public static final String UPDATE_USER_URL = DOMAIN + ACCOUNT
			+ "/UpdateUserInfoById";
	/** ��ע��ַ */
	public static final String USER_FOLLOW_URL = DOMAIN + ACCOUNT + "/Follow";
	/** ��ע��ַ */
	public static final String CANCEL_USER_FOLLOW_URL = DOMAIN + ACCOUNT
			+ "/CancelFollow";
	/** �����û���ַ */
	public static final String USER_SEARCH_URL = DOMAIN + ACCOUNT + "/FindV2";
	/** �����û���ַ */
	public static final String USER_SEARCH_URL_1 = DOMAIN + ACCOUNT + "/Search";

	/** �ҵ�ͬ�µ�ַ */
	public static final String MY_COLLEAGUE_URL = DOMAIN + ACCOUNT
			+ "/GetMyColleague";
	/** �ҵĹ�ע��ַ */
	public static final String MY_FANS_URL = DOMAIN + ACCOUNT + "/GetMyFans";
	/** �ҵ����ڵ�ַ */
	public static final String MY_FOLLOW_URL = DOMAIN + ACCOUNT
			+ "/GetFollowers";
	/** �ҵ����ڵ�ַ */
	public static final String MENBER_INFO_URL = DOMAIN + ACCOUNT
			+ "/GetUserInfoById";
	/** ����˽�ŵ�ַ */
	public static final String PERSON_MESSAGE_URL = DOMAIN + ACCOUNT
			+ "/SendMessage";
	/** ���뵥λ��ַ */
	public static final String JOIN_COMPANY_URL = DOMAIN + ACCOUNT
			+ "/JoinCompany";
	/** ȡ�����뵥λ��ַ */
	public static final String CANCEL_JOIN_COMPANY_URL = DOMAIN + ACCOUNT
			+ "/CancelJoinCompany";
	/** �����Ⱥ��ַ */
	public static final String INVIE_JOIN_GROUP_URL = DOMAIN + ACCOUNT
			+ "/InsideInvitation";
	/** �޸������ַ */
	public static final String MODIFY_PWD_URL = DOMAIN + ACCOUNT
			+ "/PassWordChange";
	// /��˾--------------------------------------------------------------------------------
	public static final String COMPANY = "/Company";
	/** ��ȡ��˾������Ϣ��ַ */
	public static final String COMPANY_DEPARTMENT_URL = DOMAIN + COMPANY
			+ "/GetDepartMentByCompanyId";
	/** ������λ��ַ */
	public static final String COMPANY_SEARCH_URL = DOMAIN + COMPANY
			+ "/Search";
	/** ��˾�����ַ */
	public static final String COMPANY_PROFILE_URL = DOMAIN + COMPANY
			+ "/GetCompanyProfile";
	/** ��˾��֪ʶ��ַ */
	public static final String COMPANY_KNOWLEDGE_URL = DOMAIN + COMPANY
			+ "/GetKnowledgeByCompany";
	/** ��ɢȺ���ַ */
	public static final String DEPARTMENT_LIST_URL = DOMAIN + COMPANY
			+ "/GetDepartments";
	/** ������λ��ַ */
	public static final String CREATE_COMPANY_URL = DOMAIN + COMPANY
			+ "/Create";
	/** ������λ��ַ */
	public static final String UPDATE_COMPANY_URL = DOMAIN + COMPANY
			+ "/UpdateCompanyById";
	/** �˳���λ��ַ */
	public static final String EXIT_COMPANY_URL = DOMAIN + COMPANY
			+ "/ExitCompany";
	/** ��λ�����ߵ�ַ */
	public static final String VISIT_COMPANY_URL = DOMAIN + COMPANY
			+ "/GetCompanyAccessByCompanyID";
	/** ��λ�Ƴ���Ա��ַ */
	public static final String REMOVE_COMPANY_URL = DOMAIN + COMPANY
			+ "/RemoveUser";
	/** ��λ����˵�ַ */
	public static final String UN_VERIFY_COMPANY_URL = DOMAIN + COMPANY
			+ "/ApprovalQueue";
	/** �ܾ���λ�����ַ */
	public static final String REFUSE_COMPANY_MENBER_URL = DOMAIN + COMPANY
			+ "/RefuseApplication";
	/** ͬ�ⵥλ�����ַ */
	public static final String AGREE_COMPANY_MENBER_URL = DOMAIN + COMPANY
			+ "/ApproveJoin";
	/** ͬ�ⵥλ�����ַ */
	public static final String USER_COMPANY_LIST_URL = DOMAIN + COMPANY
			+ "/GetCompanyListByUserId";
	/** ��λ��Ա��ַ */
	public static final String COMPANY_MENBER_LIST_URL = DOMAIN + COMPANY
			+ "/GetEmployies";
	/** ���õ�λ����Ա��ַ */
	public static final String SET_COMPANY_ADMIN_URL = DOMAIN + COMPANY
			+ "/SetAdmin";

	// /Ⱥ��--------------------------------------------------------------------------------
	public static final String GROUP = "/group";
	/** �����������ַ */
	public static final String GROUP_SEARCH_URL = DOMAIN + GROUP + "/Search";
	/** ��ȡȺ��֪ʶ��ַ */
	public static final String GROUP_KNOWLEDGE_URL = DOMAIN + GROUP
			+ "/GetKnowledgeByGroup";
	public static final String MY_GROUP_URL = DOMAIN + GROUP
			+ "/GetMyGroupsInPage";
	/** �ҵ�ͬ�µ�ַ */
	public static final String GROUP_PROFILE_URL = DOMAIN + GROUP
			+ "/GetGroupProfile";
	/** ��������Ӧ��Ⱥ���ַ */
	public static final String SUBJECT_GROUP_URL = DOMAIN + GROUP
			+ "/GetSubjectGroups";
	/** �ҵ�ͬ�µ�ַ */
	public static final String NAME_GROUP_PROFILE_URL = DOMAIN + GROUP
			+ "/GetGroupProfileByName";
	/** ����Ⱥ���ַ */
	public static final String GROUP_CHANGE_URL = DOMAIN + GROUP
			+ "/CreateOrEdit";
	/** Ⱥ�ڳ�Ա�б��ַ */
	public static final String GROUP_USER_URL = DOMAIN + GROUP
			+ "/GetUserInfoByGroup";
	/** �˳�Ⱥ���ַ */
	public static final String QUIT_GROUP__URL = DOMAIN + GROUP + "/Remove";
	/** ����Ⱥ���ַ */
	public static final String JOIN_GROUP_URL = DOMAIN + GROUP
			+ "/ApplyToJoinGroup";
	/** ��ȡȺ������ַ */
	public static final String GROUP_TYPE_LIST_URL = DOMAIN + GROUP
			+ "/GroupTypes";
	/** ��ɢȺ���ַ */
	public static final String DISSOLVE_GROUP_URL = DOMAIN + GROUP
			+ "/Dissolve";
	/** Ⱥ������ߵ�ַ */
	public static final String VISIT_GROUP_URL = DOMAIN + GROUP
			+ "/GetGroupAccessByGroupID";
	/** Ⱥ�����˵�ַ */
	public static final String UN_VERIFY_GROUP_URL = DOMAIN + GROUP
			+ "/ApprovalQueue";
	/** Ⱥ��ܾ�����˵�ַ */
	public static final String REFUSE_GROUP_URL = DOMAIN + GROUP + "/Refuse";
	/** Ⱥ��ͬ�����˵�ַ */
	public static final String APPROVE_GROUP_URL = DOMAIN + GROUP + "/Approve";
	/** Ⱥ���Ƴ���Ա��ַ */
	public static final String REMOVE_GROUP_URL = DOMAIN + GROUP + "/Remove";
	// /�ϴ�--------------------------------------------------------------------------------
	public static final String UPLOAD = "/Upload";
	/** ͷ���ϴ���ַ */
	public static final String AVATAR_UPDADE_URL = DOMAIN + UPLOAD
			+ "/Save?type=avatar";
//	/** ֪ʶ�ϴ���ַ */
//	public static final String KNOWLEDGE_UPLOAD_URL = DOMAIN + UPLOAD
//			+ "/SaveFile?type=knowledge";
	/** ֪ʶ�ϴ���ַ */
	public static final String KNOWLEDGE_UPLOAD_URL = DOMAIN + UPLOAD
			+ "/Save?type=knowledge";
	/** ��˾ͷ���ϴ���ַ */
	public static final String LOGO_UPDADE_URL = DOMAIN + UPLOAD
			+ "/Save?type=logo";
	/** �û�ͷ���ϴ���ַ */
	public static final String IMAGE_UPDADE_URL = DOMAIN + UPLOAD
			+ "/Save?type=image";
	/** �ϴ���ַ */
	public static final String MAGE_UPDADE_URL = DOMAIN + UPLOAD + "/Save/";
	// /֪ʶ--------------------------------------------------------------------------------
	public static final String KNOWLEDGE = "/Knowledge";
	/** ֪ʶ�б��ַ */
	public static final String KNOWLEDGE_LIST_URL = DOMAIN + KNOWLEDGE
			+ "/GetList";
	/** ����֪ʶ�б��ַ */
	public static final String SUBJECT_KNOWLEDGE_LIST_URL = DOMAIN + KNOWLEDGE
			+ "/GetList";
	/** �ҵ�֪ʶ�б��ַ */
	public static final String TYPE_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/Profile";
	/** �Ҵ����ĵ�֪ʶ�б��ַ */
	public static final String MY_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/GetMyKnowledgeRepositories";
	public static final String COMMON_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/Public";
	/** �ҵ�֪ʶ�б��ַ */
	public static final String GET_SHARE_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/ShareList";
	/** �½�֪ʶ���ַ */
	public static final String KNOWLEDGE_CHANGE_URL = DOMAIN + KNOWLEDGE
			+ "/CreateOrEdit";
	/** ����Ⱥ���ַ */
	public static final String CREATE_FOLDER_URL = DOMAIN + KNOWLEDGE
			+ "/CreateFolder";
	/** ����֪ʶ��ַ */
	public static final String SHARE_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/Share";
	/** ����Ⱥ���ַ */
	public static final String DELETE_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/RemoveRepository";
	/** ɾ��֪ʶ���ļ�Ⱥ���ַ */
	public static final String DELETE_KNOWLEDGE_FOLDER_URL = DOMAIN + KNOWLEDGE
			+ "/RemoveKnowledgeAndFolder";
	/** ɾ��֪ʶ���ļ�Ⱥ���ַ */
	public static final String RENAME_KNOWLEDGE_FOLDER_URL = DOMAIN + KNOWLEDGE
			+ "/RenameFolder";
	/** ���� ֪ʶ��ַ */
	public static final String KNOWLEDGE_SEARCH_URL = DOMAIN + KNOWLEDGE
			+ "/Search";
	/** ����֪ʶ״̬��ַ */
	public static final String KNOWLEDGE_STATUS_URL = DOMAIN + KNOWLEDGE
			+ "/ShareStatus";
	/** ɾ������֪ʶ��ַ */
	public static final String KNOWLEDGE_DELETE_URL = DOMAIN + KNOWLEDGE
			+ "/ShareRemove";
	/** �ƶ�֪ʶ��ַ */
	public static final String KNOWLEDGE_MOVE_URL = DOMAIN + KNOWLEDGE
			+ "/Move";
	/** ����֪ʶ��ַ */
	public static final String KNOWLEDGE_COPY_URL = DOMAIN + KNOWLEDGE
			+ "/Copy";
	/** ͨ��ID��ȡ֪ʶ��ַ */
	public static final String GET_KNOWLEDGE_DETAIL_URL = DOMAIN + KNOWLEDGE
			+ "/GetRepositoryProfile";
	// /֪ͨ--------------------------------------------------------------------------------
	public static final String NOTICE = "/Notice";
	/** ��ȡ֪ͨ�б��ַ */
	public static final String NOTICE_LIST_URL = DOMAIN + NOTICE + "/Get";
	/** ˽���б��ַ */
	public static final String NOTICE_UPDATE_URL = DOMAIN + NOTICE
			+ "/GetTheNumberOfNew";
	/** ɾ��֪ͨ��ַ */
	public static final String HANDLE_GROUP_JOIN_NOTICE_URL = DOMAIN + NOTICE
			+ "/GroupResponse";
	public static final String HANDLE_COMPANY_JOIN_NOTICE_URL = DOMAIN + NOTICE
			+ "/CompanyResponse";
	/** ��ȡ֪ͨ��ַ */
	public static final String NOTICE_GET_URL = DOMAIN + NOTICE + "/Get";
	/** ɾ��֪ͨ��ַ */
	public static final String REMOVE_NOTICE_URL = DOMAIN + NOTICE + "/Remove";
	// /˽��--------------------------------------------------------------------------------
	public static final String MESSAGE = "/Message";
	/** ˽���б��ַ */
	public static final String MESSAGE_LIST_URL = DOMAIN + MESSAGE + "/Get";
	/** ˽���б��ַ */
	public static final String MESSAGE_TALK_LIST_URL = DOMAIN + MESSAGE
			+ "/GetTalkTo";
	/** ˽���б��ַ */
	public static final String MESSAGE_HISTORY_LIST_URL = DOMAIN + MESSAGE
			+ "/GetMessagesByTarget";
	/** ˽���б��ַ */
	public static final String MESSAGE_UPDATE_URL = DOMAIN + MESSAGE
			+ "/GetTheNumberOfNew";
	/** ɾ����Ϣ��ַ */
	public static final String MESSAGE_REMOVE_URL = DOMAIN + MESSAGE
			+ "/RemoveMessage";
	/** �������˻Ự��ַ */
	public static final String CREATE_DISCUSS_URL = DOMAIN + MESSAGE
			+ "/CreateMessageGroup";
	/** �������˻Ự��ַ */
	public static final String SEND_DISCUSS_MESSAGE_URL = DOMAIN + MESSAGE
			+ "/Send";
	/** ��ȡ���˻Ự��Ա��ַ */
	public static final String GET_DISCUSS_MENBER_URL = DOMAIN + MESSAGE
			+ "/getmembers";
	/** ��նԻ���¼��ַ */
	public static final String CLEAR_DISCUSS_MESSAGE_URL = DOMAIN + MESSAGE
			+ "/RemoveDialogue";
	/** ��ɢ�Ի���ַ */
	public static final String REMOVE_DISCUSS_URL = DOMAIN + MESSAGE
			+ "/disband";
	/** �˳��Ի���ַ */
	public static final String EXIT_DISCUSS_URL = DOMAIN + MESSAGE + "/exit";
	/** ��ȡ�Ի���ַ */
	public static final String GET_DISCUSS_LIST_URL = DOMAIN + MESSAGE
			+ "/GetMessageGroups";
	/** ��ȡ�Ի���ַ */
	public static final String CHANGE_DISCUSS_MENBER_URL = DOMAIN + MESSAGE
			+ "/changegroupmembers";

	// /�Ƽ�--------------------------------------------------------------------------------
	public static final String USER = "/User";
	/** ����Ⱥ���ַ */
	public static final String RECOMEND_FOLLOW_URL = DOMAIN + USER
			+ "/GetSuggestedFollows";
	/** �����û���ǰ��λ��ַ */
	public static final String UPDATE_USER_COMPANY_URL = DOMAIN + USER
			+ "/UpdateCurrentCompanyId";
	// /��Ϣ����--------------------------------------------------------------------------------
	public static final String SUBJECT = "/subject";
	/** ��ȡ�������ַ */
	public static final String ROOT_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/GetRootSubject";
	/** ��ȡ���з����ַ */
	public static final String ALL_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/GetAllSubject";
	/** ��ȡ���з����ַ */
	public static final String GET_ROOT_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/GetRootSubject";
	/** ��ȡ��������ַ */
	public static final String GET_SUB_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/ChooseSub";
	/** ��ע�����ַ */
	public static final String LIKE_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/LikeSubject";
	/** ��ȡ��������ַ */
	public static final String FAVORITE_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/FavorateSubject";
	/** ��ȡ��������ַ */
	public static final String HOT_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/GetHotSubject";

	// /������API--------------------------------------------------------------------------------
	public static final String API = "/Api";
	/** ��ȡ���з����ַ */
	public static final String QQ_AUTH_URL = DOMAIN + API + "/Callback/qq";
	public static final String CHECK_OPENID_URL = DOMAIN + API + "/IsOpenId";
	public static final String AUTH_URL = DOMAIN + API + "/Callback/";
	public static final String UN_AUTH_URL = DOMAIN + API + "/RemoveBind/";
	// /�ʼ�--------------------------------------------------------------------------------
	public static final String NOTE = "/Note";
	/** ��ȡ�ҵıʼǱ��б�ĵ�ַ */
	public static final String MY_BOARDS_LIST_URL = DOMAIN + NOTE
			+ "/GetMyBoards";
	/** ��ȡ����ıʼǱ��б�ĵ�ַ */
	public static final String SHARE_BOARDS_LIST_URL = DOMAIN + NOTE
			+ "/GetSharedForMeBoards";
	/** ��ȡ�رյıʼǱ��б�ĵ�ַ */
	public static final String CLOSED_BOARDS_LIST_URL = DOMAIN + NOTE
			+ "/GetMyColosedBoards";
	/** �����ʼǱ��ĵ�ַ */
	public static final String CREATE_BOARDS_URL = DOMAIN + NOTE
			+ "/CreateBoard";
	/** ��ȡ�ʼ��б��ַ */
	public static final String GET_NOTECARD_LIST_URL = DOMAIN + NOTE
			+ "/GetCardLists";
	/** ��ȡ�ʼ��б��ַ */
	public static final String CREATE_NOTECARD_LIST_URL = DOMAIN + NOTE
			+ "/CreateCardList";
	/** �������ʼǵ�ַ */
	public static final String RENAME_BOARD_URL = DOMAIN + NOTE
			+ "/RenameBoard";
	/** �����ʼǿ�Ƭ��ַ */
	public static final String CREATE_CARD_URL = DOMAIN + NOTE + "/CreateCard";
	/** ��ȡ�ʼǿ�Ƭ�����ַ */
	public static final String GET_CARD_DETAIL_URL = DOMAIN + NOTE + "/GetCard";
	/** ��ȡ�ʼǿ�Ƭ�����ַ */
	public static final String REPLAY_CARD_URL = DOMAIN + NOTE + "/ReplyCard";
	/** ��ȡ�ʼǿ�Ƭ�����ַ */
	public static final String DELETE_CARD_REPLY_URL = DOMAIN + NOTE
			+ "/DeleteReply";
	/** �浵�ʼǿ�Ƭ�����ַ */
	public static final String ARCHIVE_CARD_URL = DOMAIN + NOTE
			+ "/ArchiveCard";
	/** ɾ���ʼǿ�Ƭ�����ַ */
	public static final String DELETE_CARD_URL = DOMAIN + NOTE + "/DeleteCard";
	/** ɾ����Ƭ�б������ַ */
	public static final String DELETE_CARDLIST_URL = DOMAIN + NOTE
			+ "/RemoveCardList";
	/** �鵵��Ƭ�б������ַ */
	public static final String ARCHIVE_CARDLIST_URL = DOMAIN + NOTE
			+ "/ArchiveCardList";
	/** ��ȡ�ҵıʼǱ����ַ */
	public static final String GET_MY_BOARDS_URL = DOMAIN + NOTE
			+ "/GetMyBoards";
	/** ���Ʊʼ��б��ַ */
	public static final String COPY_CARDLIST_URL = DOMAIN + NOTE
			+ "/CopyCardList";
	/** �ƶ��ʼ��б��ַ */
	public static final String MOVE_CARDLIST_URL = DOMAIN + NOTE
			+ "/MoveCardList";
	/** ��ñʼǱ��ıʼ��б��ַ */
	public static final String GET_BOARDS_CARDLIST_URL = DOMAIN + NOTE
			+ "/GetCardLists";
	/** �ƶ��ʼǱ��ıʼ��б��ַ */
	public static final String MOVE_CARDS_URL = DOMAIN + NOTE + "/MoveCards";
	/** �������ʼǱ��ıʼ��б��ַ */
	public static final String RENAME_CARDS_URL = DOMAIN + NOTE
			+ "/RenameCardList";
	/** �ƶ��ʼǿ�Ƭ��ַ */
	public static final String MOVE_CARD_URL = DOMAIN + NOTE + "/MoveCard";
	/** �ƶ��ʼǿ�Ƭ��ַ */
	public static final String COPY_CARD_URL = DOMAIN + NOTE + "/CopyCard";
	/** ��ȡ�ʼǿ�Ƭ��ַ */
	public static final String GET_CARDS_URL = DOMAIN + NOTE + "/GetCards";
	/** �༭�ʼǿ�Ƭ��ַ */
	public static final String EDIT_CARDS_URL = DOMAIN + NOTE + "/EditCard";
	/** �༭�ʼǿ�Ƭ��ַ */
	public static final String EDIT_CARD_CONTENT_URL = DOMAIN + NOTE
			+ "/editcardcontent";
	/** ��ȡ�ʼǱ������ַ */
	public static final String GET_BOAARD_URL = DOMAIN + NOTE + "/GetBoard";
	/** �ı�ʼǱ���Χ��ַ */
	public static final String CHANGE_BOAARD_VISIT_URL = DOMAIN + NOTE
			+ "/ChangeBoardVisit";

	/** ��ȡ�ѹ鵵�ʼǵ�ַ */
	public static final String GET_ARCHIVE_LIST_URL = DOMAIN + NOTE
			+ "/GetArchiveCardList";
	/** ��ȡ�ѹ鵵�ʼǵ�ַ */
	public static final String RECOVERY_ARCHIVE_LIST_URL = DOMAIN + NOTE
			+ "/OpenCardList";
	/** ��ȡ�ѹ鵵�ʼǵ�ַ */
	public static final String OPEN_BOARD_URL = DOMAIN + NOTE
			+ "/OpenBoard";
	/** ��ȡ�ѹ鵵�ʼǵ�ַ */
	public static final String CLOSE_BOARD_URL = DOMAIN + NOTE
			+ "/CloseBoard";
	/** ��ȡ�ѹ鵵�ʼǵ�ַ */
	public static final String CHANGE_BOARD_MENBER_URL = DOMAIN + NOTE
			+ "/ChangeBoardMembers";
	/** ��ȡ�ѹ鵵�ʼǵ�ַ */
	public static final String CHANGE_CARD_DUE_TIME_URL = DOMAIN + NOTE
			+ "/SetCardDueDate";
	/** ��ȡ�ѹ鵵�ʼǵ�ַ */
	public static final String CHANGE_CARD_MENBER_URL = DOMAIN + NOTE
			+ "/ChangeMembers";
	/** ��ȡ�ѹ鵵�ʼǵ�ַ */
	public static final String COMPLETE_CARD__URL = DOMAIN + NOTE
			+ "/SetCardComplate";
	
	// /�г�--------------------------------------------------------------------------------
	public static final String SCHEDULE = "/WorkSchedule";
	/** �������ʼǵ�ַ */
	public static final String CREATE_SCHEDULE_URL = DOMAIN + SCHEDULE
			+ "/Create";
	/** �������ʼǵ�ַ */
	public static final String LIST_SCHEDULE_URL = DOMAIN + SCHEDULE
			+ "/GetMySchedule";
	// /��Ŀ--------------------------------------------------------------------------------
	public static final String PROJECT = "/Project";
	/** �����༭��Ŀ��ַ */
	public static final String CREATE_PROJECT_URL = DOMAIN + PROJECT
			+ "/CreateOrEdit";
	/** ���� ��Ŀ��ַ */
	public static final String PROJECT_SEARCH_URL = DOMAIN + PROJECT
			+ "/Search";
}
