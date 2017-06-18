package com.accumulation.app.config;

public class URIConfig {
	/** 域名 */
	public static final String DOMAIN = "http://www.apingtai.cn";
//	 public static final String DOMAIN = "http://192.168.3.116";
	// /图片--------------------------------------------------------------------------------
	public static final String IMAGE = "/Image";
	/** 获取动态列表地址 */
	public static final String IMAGE_URL = DOMAIN + IMAGE + "/Index/";
	public static final String SUBJECT_IMAGE_URL = DOMAIN
			+ "/Upload/SubjectPic/";
	// /动态--------------------------------------------------------------------------------
	public static final String BROADCAST = "/Broadcast";
	/** 获取动态列表地址 */
	public static final String BROADCAST_LIST_URL = DOMAIN + BROADCAST
			+ "/Get?datatype=json";
	/** 广播点赞地址 */
	public static final String BROADCAST_LIKE_URL = DOMAIN + BROADCAST
			+ "/Like";
	/** 广播删除地址 */
	public static final String BROADCAST_REMOVE_URL = DOMAIN + BROADCAST
			+ "/Remove";
	/** 广播收藏地址 */
	public static final String BROADCAST_COLLECT_URL = DOMAIN + BROADCAST
			+ "/Collect";
	/** 广播取消收藏地址 */
	public static final String BROADCAST_CANCEL_COLLECT_URL = DOMAIN
			+ BROADCAST + "/CancelCollect";
	/** 获取动态评列表论地址 */
	public static final String REPLAY_LIST_URL = DOMAIN + BROADCAST
			+ "/GetReply";
	/** 添加评论地址 */
	public static final String REPLAY_APPEND_URL = DOMAIN + BROADCAST
			+ "/Reply";
	/** 删除动态评列表论地址 */
	public static final String REPLAY_DELETE_URL = DOMAIN + BROADCAST
			+ "/RemoveReply";
	/** 获取分组列表地址 */
	public static final String TARGET_LIST_URL = DOMAIN + BROADCAST
			+ "/GetSendToTarget";
	/** 发送动态地址 */
	public static final String BROADCAST_SEND_URL = DOMAIN + BROADCAST
			+ "/Send";
	/** 转播动态地址 */
	public static final String BROADCAST_RESEND_URL = DOMAIN + BROADCAST
			+ "/Resend";
	/** 搜索 动态地址 */
	public static final String BROADCAST_SEARCH_URL = DOMAIN + BROADCAST
			+ "/Search";

	// /用户--------------------------------------------------------------------------------
	public static final String ACCOUNT = "/account";
	/** 用户登录地址 */
	public static final String LOGIN_URL = DOMAIN + ACCOUNT + "/LoginForApp";
	/** 用户登录地址 */
	public static final String LOGOUT_URL = DOMAIN + ACCOUNT + "/Logout";
	/** 获取用户信息地址 */
	public static final String SELF_PROFILE_URL = DOMAIN + ACCOUNT
			+ "/getmyprofile";
	/** 获取用户信息地址 */
	public static final String USER_PROFILE_URL = DOMAIN + ACCOUNT
			+ "/GetUserProfile";
	/** 获取用户信息地址 */
	public static final String EMAIL_USER_PROFILE_URL = DOMAIN + ACCOUNT
			+ "/GetUserProfileByEmail";
	/** 修改用户信息地址 */
	public static final String UPDATE_USER_URL = DOMAIN + ACCOUNT
			+ "/UpdateUserInfoById";
	/** 关注地址 */
	public static final String USER_FOLLOW_URL = DOMAIN + ACCOUNT + "/Follow";
	/** 关注地址 */
	public static final String CANCEL_USER_FOLLOW_URL = DOMAIN + ACCOUNT
			+ "/CancelFollow";
	/** 搜索用户地址 */
	public static final String USER_SEARCH_URL = DOMAIN + ACCOUNT + "/FindV2";
	/** 搜索用户地址 */
	public static final String USER_SEARCH_URL_1 = DOMAIN + ACCOUNT + "/Search";

	/** 我的同事地址 */
	public static final String MY_COLLEAGUE_URL = DOMAIN + ACCOUNT
			+ "/GetMyColleague";
	/** 我的关注地址 */
	public static final String MY_FANS_URL = DOMAIN + ACCOUNT + "/GetMyFans";
	/** 我的听众地址 */
	public static final String MY_FOLLOW_URL = DOMAIN + ACCOUNT
			+ "/GetFollowers";
	/** 我的听众地址 */
	public static final String MENBER_INFO_URL = DOMAIN + ACCOUNT
			+ "/GetUserInfoById";
	/** 发送私信地址 */
	public static final String PERSON_MESSAGE_URL = DOMAIN + ACCOUNT
			+ "/SendMessage";
	/** 加入单位地址 */
	public static final String JOIN_COMPANY_URL = DOMAIN + ACCOUNT
			+ "/JoinCompany";
	/** 取消加入单位地址 */
	public static final String CANCEL_JOIN_COMPANY_URL = DOMAIN + ACCOUNT
			+ "/CancelJoinCompany";
	/** 邀请加群地址 */
	public static final String INVIE_JOIN_GROUP_URL = DOMAIN + ACCOUNT
			+ "/InsideInvitation";
	/** 修改密码地址 */
	public static final String MODIFY_PWD_URL = DOMAIN + ACCOUNT
			+ "/PassWordChange";
	// /公司--------------------------------------------------------------------------------
	public static final String COMPANY = "/Company";
	/** 获取公司部门信息地址 */
	public static final String COMPANY_DEPARTMENT_URL = DOMAIN + COMPANY
			+ "/GetDepartMentByCompanyId";
	/** 搜索单位地址 */
	public static final String COMPANY_SEARCH_URL = DOMAIN + COMPANY
			+ "/Search";
	/** 公司详情地址 */
	public static final String COMPANY_PROFILE_URL = DOMAIN + COMPANY
			+ "/GetCompanyProfile";
	/** 获公司组知识地址 */
	public static final String COMPANY_KNOWLEDGE_URL = DOMAIN + COMPANY
			+ "/GetKnowledgeByCompany";
	/** 解散群组地址 */
	public static final String DEPARTMENT_LIST_URL = DOMAIN + COMPANY
			+ "/GetDepartments";
	/** 创建单位地址 */
	public static final String CREATE_COMPANY_URL = DOMAIN + COMPANY
			+ "/Create";
	/** 创建单位地址 */
	public static final String UPDATE_COMPANY_URL = DOMAIN + COMPANY
			+ "/UpdateCompanyById";
	/** 退出单位地址 */
	public static final String EXIT_COMPANY_URL = DOMAIN + COMPANY
			+ "/ExitCompany";
	/** 单位访问者地址 */
	public static final String VISIT_COMPANY_URL = DOMAIN + COMPANY
			+ "/GetCompanyAccessByCompanyID";
	/** 单位移除成员地址 */
	public static final String REMOVE_COMPANY_URL = DOMAIN + COMPANY
			+ "/RemoveUser";
	/** 单位待审核地址 */
	public static final String UN_VERIFY_COMPANY_URL = DOMAIN + COMPANY
			+ "/ApprovalQueue";
	/** 拒绝单位申请地址 */
	public static final String REFUSE_COMPANY_MENBER_URL = DOMAIN + COMPANY
			+ "/RefuseApplication";
	/** 同意单位申请地址 */
	public static final String AGREE_COMPANY_MENBER_URL = DOMAIN + COMPANY
			+ "/ApproveJoin";
	/** 同意单位申请地址 */
	public static final String USER_COMPANY_LIST_URL = DOMAIN + COMPANY
			+ "/GetCompanyListByUserId";
	/** 单位成员地址 */
	public static final String COMPANY_MENBER_LIST_URL = DOMAIN + COMPANY
			+ "/GetEmployies";
	/** 设置单位管理员地址 */
	public static final String SET_COMPANY_ADMIN_URL = DOMAIN + COMPANY
			+ "/SetAdmin";

	// /群组--------------------------------------------------------------------------------
	public static final String GROUP = "/group";
	/** 搜索讨论组地址 */
	public static final String GROUP_SEARCH_URL = DOMAIN + GROUP + "/Search";
	/** 获取群组知识地址 */
	public static final String GROUP_KNOWLEDGE_URL = DOMAIN + GROUP
			+ "/GetKnowledgeByGroup";
	public static final String MY_GROUP_URL = DOMAIN + GROUP
			+ "/GetMyGroupsInPage";
	/** 我的同事地址 */
	public static final String GROUP_PROFILE_URL = DOMAIN + GROUP
			+ "/GetGroupProfile";
	/** 分类所对应的群组地址 */
	public static final String SUBJECT_GROUP_URL = DOMAIN + GROUP
			+ "/GetSubjectGroups";
	/** 我的同事地址 */
	public static final String NAME_GROUP_PROFILE_URL = DOMAIN + GROUP
			+ "/GetGroupProfileByName";
	/** 更新群组地址 */
	public static final String GROUP_CHANGE_URL = DOMAIN + GROUP
			+ "/CreateOrEdit";
	/** 群内成员列表地址 */
	public static final String GROUP_USER_URL = DOMAIN + GROUP
			+ "/GetUserInfoByGroup";
	/** 退出群组地址 */
	public static final String QUIT_GROUP__URL = DOMAIN + GROUP + "/Remove";
	/** 加入群组地址 */
	public static final String JOIN_GROUP_URL = DOMAIN + GROUP
			+ "/ApplyToJoinGroup";
	/** 获取群组分类地址 */
	public static final String GROUP_TYPE_LIST_URL = DOMAIN + GROUP
			+ "/GroupTypes";
	/** 解散群组地址 */
	public static final String DISSOLVE_GROUP_URL = DOMAIN + GROUP
			+ "/Dissolve";
	/** 群组访问者地址 */
	public static final String VISIT_GROUP_URL = DOMAIN + GROUP
			+ "/GetGroupAccessByGroupID";
	/** 群组待审核地址 */
	public static final String UN_VERIFY_GROUP_URL = DOMAIN + GROUP
			+ "/ApprovalQueue";
	/** 群组拒绝待审核地址 */
	public static final String REFUSE_GROUP_URL = DOMAIN + GROUP + "/Refuse";
	/** 群组同意待审核地址 */
	public static final String APPROVE_GROUP_URL = DOMAIN + GROUP + "/Approve";
	/** 群组移除成员地址 */
	public static final String REMOVE_GROUP_URL = DOMAIN + GROUP + "/Remove";
	// /上传--------------------------------------------------------------------------------
	public static final String UPLOAD = "/Upload";
	/** 头像上传地址 */
	public static final String AVATAR_UPDADE_URL = DOMAIN + UPLOAD
			+ "/Save?type=avatar";
//	/** 知识上传地址 */
//	public static final String KNOWLEDGE_UPLOAD_URL = DOMAIN + UPLOAD
//			+ "/SaveFile?type=knowledge";
	/** 知识上传地址 */
	public static final String KNOWLEDGE_UPLOAD_URL = DOMAIN + UPLOAD
			+ "/Save?type=knowledge";
	/** 公司头像上传地址 */
	public static final String LOGO_UPDADE_URL = DOMAIN + UPLOAD
			+ "/Save?type=logo";
	/** 用户头像上传地址 */
	public static final String IMAGE_UPDADE_URL = DOMAIN + UPLOAD
			+ "/Save?type=image";
	/** 上传地址 */
	public static final String MAGE_UPDADE_URL = DOMAIN + UPLOAD + "/Save/";
	// /知识--------------------------------------------------------------------------------
	public static final String KNOWLEDGE = "/Knowledge";
	/** 知识列表地址 */
	public static final String KNOWLEDGE_LIST_URL = DOMAIN + KNOWLEDGE
			+ "/GetList";
	/** 分类知识列表地址 */
	public static final String SUBJECT_KNOWLEDGE_LIST_URL = DOMAIN + KNOWLEDGE
			+ "/GetList";
	/** 我的知识列表地址 */
	public static final String TYPE_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/Profile";
	/** 我创建的的知识列表地址 */
	public static final String MY_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/GetMyKnowledgeRepositories";
	public static final String COMMON_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/Public";
	/** 我的知识列表地址 */
	public static final String GET_SHARE_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/ShareList";
	/** 新建知识库地址 */
	public static final String KNOWLEDGE_CHANGE_URL = DOMAIN + KNOWLEDGE
			+ "/CreateOrEdit";
	/** 更新群组地址 */
	public static final String CREATE_FOLDER_URL = DOMAIN + KNOWLEDGE
			+ "/CreateFolder";
	/** 分享知识地址 */
	public static final String SHARE_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/Share";
	/** 更新群组地址 */
	public static final String DELETE_KNOWLEDGE_URL = DOMAIN + KNOWLEDGE
			+ "/RemoveRepository";
	/** 删除知识库文件群组地址 */
	public static final String DELETE_KNOWLEDGE_FOLDER_URL = DOMAIN + KNOWLEDGE
			+ "/RemoveKnowledgeAndFolder";
	/** 删除知识库文件群组地址 */
	public static final String RENAME_KNOWLEDGE_FOLDER_URL = DOMAIN + KNOWLEDGE
			+ "/RenameFolder";
	/** 搜索 知识地址 */
	public static final String KNOWLEDGE_SEARCH_URL = DOMAIN + KNOWLEDGE
			+ "/Search";
	/** 更改知识状态地址 */
	public static final String KNOWLEDGE_STATUS_URL = DOMAIN + KNOWLEDGE
			+ "/ShareStatus";
	/** 删除分享知识地址 */
	public static final String KNOWLEDGE_DELETE_URL = DOMAIN + KNOWLEDGE
			+ "/ShareRemove";
	/** 移动知识地址 */
	public static final String KNOWLEDGE_MOVE_URL = DOMAIN + KNOWLEDGE
			+ "/Move";
	/** 复制知识地址 */
	public static final String KNOWLEDGE_COPY_URL = DOMAIN + KNOWLEDGE
			+ "/Copy";
	/** 通过ID获取知识地址 */
	public static final String GET_KNOWLEDGE_DETAIL_URL = DOMAIN + KNOWLEDGE
			+ "/GetRepositoryProfile";
	// /通知--------------------------------------------------------------------------------
	public static final String NOTICE = "/Notice";
	/** 获取通知列表地址 */
	public static final String NOTICE_LIST_URL = DOMAIN + NOTICE + "/Get";
	/** 私信列表地址 */
	public static final String NOTICE_UPDATE_URL = DOMAIN + NOTICE
			+ "/GetTheNumberOfNew";
	/** 删除通知地址 */
	public static final String HANDLE_GROUP_JOIN_NOTICE_URL = DOMAIN + NOTICE
			+ "/GroupResponse";
	public static final String HANDLE_COMPANY_JOIN_NOTICE_URL = DOMAIN + NOTICE
			+ "/CompanyResponse";
	/** 获取通知地址 */
	public static final String NOTICE_GET_URL = DOMAIN + NOTICE + "/Get";
	/** 删除通知地址 */
	public static final String REMOVE_NOTICE_URL = DOMAIN + NOTICE + "/Remove";
	// /私信--------------------------------------------------------------------------------
	public static final String MESSAGE = "/Message";
	/** 私信列表地址 */
	public static final String MESSAGE_LIST_URL = DOMAIN + MESSAGE + "/Get";
	/** 私信列表地址 */
	public static final String MESSAGE_TALK_LIST_URL = DOMAIN + MESSAGE
			+ "/GetTalkTo";
	/** 私信列表地址 */
	public static final String MESSAGE_HISTORY_LIST_URL = DOMAIN + MESSAGE
			+ "/GetMessagesByTarget";
	/** 私信列表地址 */
	public static final String MESSAGE_UPDATE_URL = DOMAIN + MESSAGE
			+ "/GetTheNumberOfNew";
	/** 删除信息地址 */
	public static final String MESSAGE_REMOVE_URL = DOMAIN + MESSAGE
			+ "/RemoveMessage";
	/** 创建多人会话地址 */
	public static final String CREATE_DISCUSS_URL = DOMAIN + MESSAGE
			+ "/CreateMessageGroup";
	/** 创建多人会话地址 */
	public static final String SEND_DISCUSS_MESSAGE_URL = DOMAIN + MESSAGE
			+ "/Send";
	/** 获取多人会话成员地址 */
	public static final String GET_DISCUSS_MENBER_URL = DOMAIN + MESSAGE
			+ "/getmembers";
	/** 清空对话记录地址 */
	public static final String CLEAR_DISCUSS_MESSAGE_URL = DOMAIN + MESSAGE
			+ "/RemoveDialogue";
	/** 解散对话地址 */
	public static final String REMOVE_DISCUSS_URL = DOMAIN + MESSAGE
			+ "/disband";
	/** 退出对话地址 */
	public static final String EXIT_DISCUSS_URL = DOMAIN + MESSAGE + "/exit";
	/** 获取对话地址 */
	public static final String GET_DISCUSS_LIST_URL = DOMAIN + MESSAGE
			+ "/GetMessageGroups";
	/** 获取对话地址 */
	public static final String CHANGE_DISCUSS_MENBER_URL = DOMAIN + MESSAGE
			+ "/changegroupmembers";

	// /推荐--------------------------------------------------------------------------------
	public static final String USER = "/User";
	/** 更新群组地址 */
	public static final String RECOMEND_FOLLOW_URL = DOMAIN + USER
			+ "/GetSuggestedFollows";
	/** 跟新用户当前单位地址 */
	public static final String UPDATE_USER_COMPANY_URL = DOMAIN + USER
			+ "/UpdateCurrentCompanyId";
	// /信息分类--------------------------------------------------------------------------------
	public static final String SUBJECT = "/subject";
	/** 获取根分类地址 */
	public static final String ROOT_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/GetRootSubject";
	/** 获取所有分类地址 */
	public static final String ALL_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/GetAllSubject";
	/** 获取所有分类地址 */
	public static final String GET_ROOT_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/GetRootSubject";
	/** 获取子类分类地址 */
	public static final String GET_SUB_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/ChooseSub";
	/** 关注分类地址 */
	public static final String LIKE_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/LikeSubject";
	/** 获取子类分类地址 */
	public static final String FAVORITE_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/FavorateSubject";
	/** 获取子类分类地址 */
	public static final String HOT_SUBJECT_URL = DOMAIN + SUBJECT
			+ "/GetHotSubject";

	// /第三方API--------------------------------------------------------------------------------
	public static final String API = "/Api";
	/** 获取所有分类地址 */
	public static final String QQ_AUTH_URL = DOMAIN + API + "/Callback/qq";
	public static final String CHECK_OPENID_URL = DOMAIN + API + "/IsOpenId";
	public static final String AUTH_URL = DOMAIN + API + "/Callback/";
	public static final String UN_AUTH_URL = DOMAIN + API + "/RemoveBind/";
	// /笔记--------------------------------------------------------------------------------
	public static final String NOTE = "/Note";
	/** 获取我的笔记本列表的地址 */
	public static final String MY_BOARDS_LIST_URL = DOMAIN + NOTE
			+ "/GetMyBoards";
	/** 获取分享的笔记本列表的地址 */
	public static final String SHARE_BOARDS_LIST_URL = DOMAIN + NOTE
			+ "/GetSharedForMeBoards";
	/** 获取关闭的笔记本列表的地址 */
	public static final String CLOSED_BOARDS_LIST_URL = DOMAIN + NOTE
			+ "/GetMyColosedBoards";
	/** 创建笔记本的地址 */
	public static final String CREATE_BOARDS_URL = DOMAIN + NOTE
			+ "/CreateBoard";
	/** 获取笔记列表地址 */
	public static final String GET_NOTECARD_LIST_URL = DOMAIN + NOTE
			+ "/GetCardLists";
	/** 获取笔记列表地址 */
	public static final String CREATE_NOTECARD_LIST_URL = DOMAIN + NOTE
			+ "/CreateCardList";
	/** 重命名笔记地址 */
	public static final String RENAME_BOARD_URL = DOMAIN + NOTE
			+ "/RenameBoard";
	/** 创建笔记卡片地址 */
	public static final String CREATE_CARD_URL = DOMAIN + NOTE + "/CreateCard";
	/** 获取笔记卡片详情地址 */
	public static final String GET_CARD_DETAIL_URL = DOMAIN + NOTE + "/GetCard";
	/** 获取笔记卡片详情地址 */
	public static final String REPLAY_CARD_URL = DOMAIN + NOTE + "/ReplyCard";
	/** 获取笔记卡片详情地址 */
	public static final String DELETE_CARD_REPLY_URL = DOMAIN + NOTE
			+ "/DeleteReply";
	/** 存档笔记卡片详情地址 */
	public static final String ARCHIVE_CARD_URL = DOMAIN + NOTE
			+ "/ArchiveCard";
	/** 删除笔记卡片详情地址 */
	public static final String DELETE_CARD_URL = DOMAIN + NOTE + "/DeleteCard";
	/** 删除卡片列表详情地址 */
	public static final String DELETE_CARDLIST_URL = DOMAIN + NOTE
			+ "/RemoveCardList";
	/** 归档卡片列表详情地址 */
	public static final String ARCHIVE_CARDLIST_URL = DOMAIN + NOTE
			+ "/ArchiveCardList";
	/** 获取我的笔记本情地址 */
	public static final String GET_MY_BOARDS_URL = DOMAIN + NOTE
			+ "/GetMyBoards";
	/** 复制笔记列表地址 */
	public static final String COPY_CARDLIST_URL = DOMAIN + NOTE
			+ "/CopyCardList";
	/** 移动笔记列表地址 */
	public static final String MOVE_CARDLIST_URL = DOMAIN + NOTE
			+ "/MoveCardList";
	/** 获得笔记本的笔记列表地址 */
	public static final String GET_BOARDS_CARDLIST_URL = DOMAIN + NOTE
			+ "/GetCardLists";
	/** 移动笔记本的笔记列表地址 */
	public static final String MOVE_CARDS_URL = DOMAIN + NOTE + "/MoveCards";
	/** 重命名笔记本的笔记列表地址 */
	public static final String RENAME_CARDS_URL = DOMAIN + NOTE
			+ "/RenameCardList";
	/** 移动笔记卡片地址 */
	public static final String MOVE_CARD_URL = DOMAIN + NOTE + "/MoveCard";
	/** 移动笔记卡片地址 */
	public static final String COPY_CARD_URL = DOMAIN + NOTE + "/CopyCard";
	/** 获取笔记卡片地址 */
	public static final String GET_CARDS_URL = DOMAIN + NOTE + "/GetCards";
	/** 编辑笔记卡片地址 */
	public static final String EDIT_CARDS_URL = DOMAIN + NOTE + "/EditCard";
	/** 编辑笔记卡片地址 */
	public static final String EDIT_CARD_CONTENT_URL = DOMAIN + NOTE
			+ "/editcardcontent";
	/** 获取笔记本详情地址 */
	public static final String GET_BOAARD_URL = DOMAIN + NOTE + "/GetBoard";
	/** 改变笔记本范围地址 */
	public static final String CHANGE_BOAARD_VISIT_URL = DOMAIN + NOTE
			+ "/ChangeBoardVisit";

	/** 获取已归档笔记地址 */
	public static final String GET_ARCHIVE_LIST_URL = DOMAIN + NOTE
			+ "/GetArchiveCardList";
	/** 获取已归档笔记地址 */
	public static final String RECOVERY_ARCHIVE_LIST_URL = DOMAIN + NOTE
			+ "/OpenCardList";
	/** 获取已归档笔记地址 */
	public static final String OPEN_BOARD_URL = DOMAIN + NOTE
			+ "/OpenBoard";
	/** 获取已归档笔记地址 */
	public static final String CLOSE_BOARD_URL = DOMAIN + NOTE
			+ "/CloseBoard";
	/** 获取已归档笔记地址 */
	public static final String CHANGE_BOARD_MENBER_URL = DOMAIN + NOTE
			+ "/ChangeBoardMembers";
	/** 获取已归档笔记地址 */
	public static final String CHANGE_CARD_DUE_TIME_URL = DOMAIN + NOTE
			+ "/SetCardDueDate";
	/** 获取已归档笔记地址 */
	public static final String CHANGE_CARD_MENBER_URL = DOMAIN + NOTE
			+ "/ChangeMembers";
	/** 获取已归档笔记地址 */
	public static final String COMPLETE_CARD__URL = DOMAIN + NOTE
			+ "/SetCardComplate";
	
	// /行程--------------------------------------------------------------------------------
	public static final String SCHEDULE = "/WorkSchedule";
	/** 重命名笔记地址 */
	public static final String CREATE_SCHEDULE_URL = DOMAIN + SCHEDULE
			+ "/Create";
	/** 重命名笔记地址 */
	public static final String LIST_SCHEDULE_URL = DOMAIN + SCHEDULE
			+ "/GetMySchedule";
	// /项目--------------------------------------------------------------------------------
	public static final String PROJECT = "/Project";
	/** 创建编辑项目地址 */
	public static final String CREATE_PROJECT_URL = DOMAIN + PROJECT
			+ "/CreateOrEdit";
	/** 搜索 项目地址 */
	public static final String PROJECT_SEARCH_URL = DOMAIN + PROJECT
			+ "/Search";
}
