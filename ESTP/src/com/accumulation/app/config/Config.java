package com.accumulation.app.config;

import android.os.Environment;

public class Config {
	public static final String WX_APP_ID = "wx1131b459600fe6cd";

	// 濉啓浠庣煭淇DK搴旂敤鍚庡彴娉ㄥ唽寰楀埌鐨凙PPKEY
	private static String APPKEY = "78807381cb52";
	// 濉啓浠庣煭淇DK搴旂敤鍚庡彴娉ㄥ唽寰楀埌鐨凙PPSECRET
	private static String APPSECRET = "437810c4e470b43725662db3f9bbfb4b";

	/**
	 * 鎷嶇収鍥炶皟
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;// 鎷嶇収淇敼澶村儚
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;// 鏈湴鐩稿唽淇敼澶村儚
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;// 绯荤粺瑁佸壀澶村儚

	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;// 鎷嶇収
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;// 鏈湴鍥剧墖
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;// 浣嶇疆
	public static final int REQUESTCODE_TAKE_ALT = 0x000004;// alt
	public static final int REQUESTCODE_TAKE_SPEECH = 0x000005;// alt

	public static final String EXTRA_STRING = "extra_string";
	/** 琛ㄦ儏鍖归厤姝ｅ垯琛ㄨ揪寮� */
	public static final String FACE_PATTERN = "\\[[a-zA-Z\u4E00-\u9FA5]+\\]";
	public static final String HOLD_PATTERN = "\\{#=[a-zA-Z\u4E00-\u9FA5]+=#\\}";
	public static final String EMPTY_PATTERN =  "\\(((\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+))\\)+";
	public static final String IMAGE_PATTERN = "\\[@.*@\\]";

	public static final String LINK_PATTERN = "(@|/|/|)([^(|^@]+)\\(((\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+))\\)+";
	public static final String FILE_PATTERN = "\\[file name=\"([^\"]*)\"\\s*path=\"([^\"]*)\"\\]";
	public static final String GROUP_LINK_PATTERN = "@([^\\s \\(\\)\\.]+)";
	public static final String URL_LINK_PATTERN = "(card://|http://|ftp://|https://|www){1,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*";
	public static final String TYPE_LABLE_PATTERN = "#TYPE_LABLE[0-9]+#";
	public static final String TOPIC_PATTERN = "#[a-zA-Z\u4E00-\u9FA5]+?#";

	public static final String SYMBOL_PATTERN = 	"►";

	public static final String ARTICLE_ICON_LABEL = 	"___ARTICLE___";


	/** 鍥剧墖閫夋嫨鏈�澶ф暟閲� */
	public static final int MAX_PIC_CHOOSE_COUNT = 9;
	public static final int MAX_TOPIC_PIC_CHOOSE_COUNT = 1;

	public static final int MAX_COMMENT_PIC_CHOOSE_COUNT = 3;
	/** 閫氱煡绫诲瀷 */
	public static final String NOTICE_TYPE_PERSONAL = "";
	public static final String NOTICE_TYPE_COMMON = "commonnotice";
	public static final String NOTICE_TYPE_COMMENT = "replynotice";
	public static final String NOTICE_TYPE_AT = "atnotice";
	public static final String NOTICE_TYPE_TASK = "tasknotice";
	public static final String NOTICE_TYPE_INVITE = "groupinvitenotice";
	public static final String NOTICE_TYPE_AUDIENCE = "fllowingnotice";
	public static final String NOTICE_TYPE_KNOWLEDGE = "knowledgesharenotice";

	/**
	 * 骞挎挱鐨刟ction
	 * */
	public static final String CLOSE_MENU_ACTION = "cn.cooperation.CLOSE_MENU";
	public static final String TOGGER_MENU_ACTION = "cn.cooperation.TOGGER_MENU";
	public static final String UPDATE_NOTICE_ACTION = "cn.cooperation.UPDATE_NOTICE";
	public static final String CHECK_UPDATE_ACTION = "android.intent.action.CHECK_UPDATE";
	public static final String CHECK_ALARM_ACTION = "android.intent.action.CHECK_ALARM";
	public static final String EMPHASIS_TAG = "{#SubName#}";
	public static final String GROUP_TYPE="group";
	public static final String REFRESH_BROADCAST_ACTION = "cn.cooperation.REFRESH_BROADCASTU";

	/**
	 * 鏂囦欢淇濆瓨鍦板潃閰嶇疆
	 * */
	public static final String FILE_PATH = Environment
			.getExternalStorageDirectory() + "/ESTP/";
	public static final String KNOWLEDGE_FILE_PATH = FILE_PATH + "/knowledge/";
	public static final String UPGRADE_FILE_PATH = FILE_PATH + "/upgrade/";
	/** 澶村儚缂撳瓨鍦板潃 */
	public static final String AVATAR_FILE_PATH = FILE_PATH + "/avatar/";
	/** 澶村儚缂撳瓨鍦板潃 */
	public static final String LOG_FILE_PATH = FILE_PATH + "/log/";
	/** 鍥剧墖缂撳瓨鍦板潃 */
	public static final String IMAGE_FILE_PATH = FILE_PATH + "/image/";
	/** 鍥剧墖缂撳瓨鍦板潃 */
	public static final String CACHE_FILE_PATH = FILE_PATH + "/cache/";
	public static final int NOTIFICATION_ID_MESSAGE=0x4444;
	public static final int NOTIFICATION_ID_NOTICE=0x44445;
	
	public static final String BOARD_URL_FLAG="http://www.apingtai.cn/Note/Board";
	
	/**鏈姞鍏ョ兢缁�*/
	public static final int GROUP_STATE_UNJOIN=0;
	/**寰呭鏍镐腑缇ょ粍*/
	public static final int GROUP_STATE_JOINING=1;
	/**宸插姞鍏ョ兢缁�*/
	public static final int GROUP_STATE_JOINED=2;
	
	public static final String QRCODE_SCAN_USER_TAG="/apingtai:user/";
	public static final String QRCODE_SCAN_TAG="/apingtai:";


	public static final String[] FACE_CONFIG =  {
        "微笑", "face/14.gif",
        "撇嘴", "face/1.gif",
        "色", "face/2.gif",
        "发呆", "face/3.gif",
        "得意", "face/4.gif",
        "流泪", "face/5.gif",
        "害羞", "face/6.gif",
        "闭嘴", "face/7.gif",
        "睡", "face/8.gif",
        "大哭", "face/9.gif",
        "尴尬", "face/10.gif",
        "发怒", "face/11.gif",
        "调皮", "face/12.gif",
        "呲牙", "face/13.gif",
        "惊讶", "face/0.gif",
        "难过", "face/15.gif",
        "酷", "face/16.gif",
        "冷汗", "face/96.gif",
        "抓狂", "face/18.gif",
        "吐", "face/19.gif",
        "偷笑", "face/20.gif",
        "可爱", "face/21.gif",
        "白眼", "face/22.gif",
        "傲慢", "face/23.gif",
        "饥饿", "face/24.gif",
        "困", "face/25.gif",
        "惊恐", "face/26.gif",
        "流汗", "face/27.gif",
        "憨笑", "face/28.gif",
        "大兵", "face/29.gif",
        "奋斗", "face/30.gif",
        "咒骂", "face/31.gif",
        "疑问", "face/32.gif",
        "嘘", "face/33.gif",
        "晕", "face/34.gif",
        "折磨", "face/35.gif",
        "衰", "face/36.gif",
        "骷髅", "face/37.gif",
        "敲打", "face/38.gif",
        "再见", "face/39.gif",
        "擦汗", "face/97.gif",
        "抠鼻", "face/98.gif",
        "鼓掌", "face/99.gif",
        "糗大了", "face/100.gif",
        "坏笑", "face/101.gif",
        "左哼哼", "face/102.gif",
        "右哼哼", "face/103.gif",
        "哈欠", "face/104.gif",
        "鄙视", "face/105.gif",
        "委屈", "face/106.gif",
        "快哭了", "face/107.gif",
        "阴险", "face/108.gif",
        "亲亲", "face/109.gif",
        "吓", "face/110.gif",
        "可怜", "face/111.gif",
        "菜刀", "face/112.gif",
        "西瓜", "face/89.gif",
        "啤酒", "face/113.gif",
        "篮球", "face/114.gif",
        "乒乓", "face/115.gif",
        "咖啡", "face/60.gif",
        "饭", "face/61.gif",
        "猪头", "face/46.gif",
        "玫瑰", "face/63.gif",
        "凋谢", "face/64.gif",
        "示爱", "face/116.gif",
        "爱心", "face/66.gif",
        "心碎", "face/67.gif",
        "蛋糕", "face/53.gif",
        "闪电", "face/54.gif",
        "炸弹", "face/55.gif",
        "刀", "face/56.gif",
        "足球", "face/57.gif",
        "瓢虫", "face/117.gif",
        "便便", "face/59.gif",
        "月亮", "face/75.gif",
        "太阳", "face/74.gif",
        "礼物", "face/69.gif",
        "拥抱", "face/49.gif",
        "强", "face/76.gif",
        "弱", "face/77.gif",
        "握手", "face/78.gif",
        "胜利", "face/79.gif",
        "抱拳", "face/118.gif",
        "勾引", "face/119.gif",
        "拳头", "face/120.gif",
        "差劲", "face/121.gif",
        "爱你", "face/122.gif",
        "NO", "face/123.gif",
        "OK", "face/124.gif",
        "爱情", "face/42.gif",
        "飞吻", "face/85.gif",
        "跳跳", "face/43.gif",
        "发抖", "face/41.gif",
        "怄火", "face/86.gif",
        "转圈", "face/125.gif",
        "磕头", "face/126.gif",
        "回头", "face/127.gif",
        "跳绳", "face/128.gif",
        "挥手", "face/129.gif",
        "激动", "face/130.gif",
        "街舞", "face/131.gif",
        "献吻", "face/132.gif",
        "左太极", "face/133.gif",
        "右太极", "face/134.gif"
    };

}
