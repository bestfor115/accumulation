
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <android/log.h>
#include <jni.h>

#include "mplayer.h"

#define LOG_TAG "VOD-Protocol"
#define LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGI(fmt, ...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGV(fmt, ...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, fmt, ##__VA_ARGS__)

static JavaVM *g_vm = NULL;

static struct {
	jclass clazz;
	void *peer;
	void *player;
	jmethodID callback;
	jobject wo;
} m_player;

static JNIEnv* attach_java_thread(const char* threadName) {
	JavaVMAttachArgs args;
	jint result;
	JNIEnv*e = NULL;
	args.version = JNI_VERSION_1_4;
	args.name = (char*) threadName;
	args.group = NULL;
	if ((result = (*g_vm)->AttachCurrentThread(g_vm, &e, (void*) &args)) != JNI_OK) {
		LOGE("NOTE: attach of thread '%s' failed\n", threadName);
		return NULL;
	}
	return e;
}

static jboolean throw_runtime_exception(JNIEnv *env, const char*msg) {
	jclass clz = NULL;
	clz = (*env)->FindClass(env, "java/lang/RuntimeException");
	if (clz != NULL) {
		(*env)->ThrowNew(env, clz, msg);
	}
	return JNI_FALSE;
}

static char* createFrequency(mplayer_media_t *t) {
	char buf[256];
	int n = 0;
	n += sprintf(buf + n, "{");
	n += sprintf(buf + n, "\"frequency\":\"%d\",", t->frequency);
	n += sprintf(buf + n, "\"symbolrate\":\"%d\",", t->symbolrate);
	n += sprintf(buf + n, "\"qam\":\"%d\",", t->qam);
	n += sprintf(buf + n, "\"service_id\":\"%d\",", t->service_id);
	n += sprintf(buf + n, "\"pmt_pid\":\"%d\"", t->pmt_pid);
	n += sprintf(buf + n, "}");
	return buf;
}

static void my_mplayer_cbf (void *player, int msg, int p1, int p2){

	LOGD("mplayer_cbf enter msg = %d", msg);
	if (player != m_player.player)
		return;
	char s[1024] = {0};

	switch (msg) {
	case MPLAYER_PREPAREPLAY_SUCCESS:	/*准备播放成功,上层应用收到此消息可以调用play*/
		break;
	case MPLAYER_CONNECT_FAILED:		/*连接服务器失败,建立会话失败或者服务器返回超时*/
		break;
	case MPLAYER_PLAY_SUCCESS:			/*播放媒体成功*/
		break;
	case MPLAYER_PLAY_FAILED:			/*播放媒体失败*/
		break;
	case MPLAYER_PROGRAM_BEGIN:			/*播放到了开始的位置*/
		break;
	case MPLAYER_PROGRAM_END:			/*播放到了结束的位置*/
		break;
	case MPLAYER_OUT_OF_RANGE:			/*请求超过时间范围*/
		break;
	case MPLAYER_RELEASE_SUCCESS:		/*关闭媒体成功*/
		m_player.player = NULL;
		break;
	case MPLAYER_RELEASE_FAIL:			/*关闭媒体失败*/
		break;
	case MPLAYER_SET_MEDIA_INFO:		/*设置媒体播放参数,p1为mplayer_media_t*/
	{
		mplayer_media_t *t = (mplayer_media_t *)p1;
    	sprintf(s, "{\"frequency\":\"%d\",\"symbolrate\":\"%d\",\"qam\":\"%d\",\"service_id\":\"%d\",\"pmt_pid\":\"%d\"}",
            t->frequency,t->symbolrate,t->qam,t->service_id,t->pmt_pid);        
	}
		break;
	default:
		return;
	}
	JNIEnv *env = attach_java_thread("mplayer-callback");
	if (env) {
		LOGD("mplayer_cbf msg=%d env=%d, clazz=%d, callback=%d, wo=%d ", msg, env, m_player.clazz, m_player.callback, m_player.wo);
		jstring js = NULL;
		if (strlen(s) > 0) {
			js =(*env)->NewStringUTF(env, s);
		}
		(*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.callback, m_player.wo, msg, p1, p2, js);
	}
}

jint native_init(JNIEnv *env, jobject thiz, jobject wo) {
	LOGD("native_init ...");
	if (m_player.peer != NULL)
		return m_player.peer;
	m_player.wo = (jobject) (*env)->NewGlobalRef(env, wo);
	m_player.peer = mplayer_init();
	return m_player.peer;
}

void native_exit(JNIEnv *env, jobject thiz, jint peer) {
	LOGD("native_exit ...");
	if (m_player.peer != peer) {
		m_player.peer= peer;
	}
	mplayer_exit(m_player.peer);
	m_player.player = NULL;
	m_player.peer = NULL;
	m_player.wo = NULL;
}

jboolean native_close(JNIEnv *env, jobject thiz) {
	LOGD("native_close ...");
	if (m_player.player == NULL)
		return JNI_FALSE;
	if (mplayer_close(m_player.player) == MPLAYER_OK) {
		return JNI_TRUE;
	}
	return JNI_FALSE;
}

void native_open(JNIEnv *env, jobject thiz, int type, jstring url) {
	LOGD("native_open ...");
	if (m_player.player != NULL) {
		native_close(*env, thiz);
	}
	const char *s = NULL;
	s = (*env)->GetStringUTFChars(env, url, NULL);
	m_player.player = mplayer_open(s, type, my_mplayer_cbf);

	LOGD("native_open m_player.player is %d", m_player.player);
}

jboolean native_play(JNIEnv *env, jobject thiz, jint speed) {
	LOGD("native_play ...");
	if (m_player.player == NULL)
		return JNI_FALSE;
	return mplayer_play(m_player.player, speed) == MPLAYER_OK ? JNI_TRUE : JNI_FALSE;
}

jboolean native_stop(JNIEnv *env, jobject thiz, jint keep_last_frame) {
	LOGD("native_stop ...");
	if (m_player.player == NULL)
		return JNI_FALSE;
	return mplayer_stop(m_player.player, keep_last_frame) == MPLAYER_OK ? JNI_TRUE : JNI_FALSE;
}

jboolean native_seek(JNIEnv *env, jobject thiz, jstring seek) {
	LOGD("native_seek ...");
	if (m_player.player == NULL)
		return JNI_FALSE;
	const char *s;
	s = (*env)->GetStringUTFChars(env, seek, NULL);
	return mplayer_seek(m_player.player, s) == MPLAYER_OK ? JNI_TRUE : JNI_FALSE;
}

jboolean native_pause(JNIEnv *env, jobject thiz) {
	LOGD("native_pause ...");
	if (m_player.player == NULL)
		return JNI_FALSE;
	return mplayer_pause(m_player.player) == MPLAYER_OK ? JNI_TRUE : JNI_FALSE;
}

jstring native_get(JNIEnv *env, jobject thiz, jint prop) {
	LOGD("native_get ...");
	if (m_player.player == NULL)
		return NULL;
	char buf[64] = {0};
	if (mplayer_get_property(m_player.player, prop, buf) == MPLAYER_OK) {
		jstring js = NULL;
		js = (*env)->NewStringUTF(env, buf);
		return js;
	}
	return NULL;
}

static const char* const m_class_name = "com/ipanel/join/huawei/vod/control/MProtocol";

static JNINativeMethod gMethods[] = {
	{"native_init", 	"(Ljava/lang/ref/WeakReference;)I",	(void*)native_init },
	{"native_exit", 	"(I)V", 							(void*)native_exit },
	{"native_open", 	"(ILjava/lang/String;)V", 			(void*)native_open },
	{"native_play", 	"(I)Z", 							(void*)native_play },
	{"native_stop", 	"(I)Z", 							(void*)native_stop },
	{"native_seek", 	"(Ljava/lang/String;)Z", 			(void*)native_seek },
	{"native_pause", 	"()Z", 								(void*)native_pause },
	{"native_close", 	"()Z", 								(void*)native_close },
	{"native_get", 		"(I)Ljava/lang/String;",			(void*)native_get },
};

static int registerNativeMethods(JNIEnv *env, const char* className,
		JNINativeMethod* gMethods, int numMethods) {
	jclass clazz;
	clazz = (*env)->FindClass(env, className);
	if (clazz == NULL) {
		return JNI_FALSE;
	}
	m_player.clazz = (jclass) (*env)->NewGlobalRef(env, clazz);
	if (!(m_player.callback = (*env)->GetStaticMethodID(env, m_player.clazz,
			"native_callback", "(Ljava/lang/Object;IIILjava/lang/String;)V"))) {
		LOGE("no such static method : native_callback");
		return JNI_FALSE;
	}
	if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
		return JNI_FALSE;
	}
	return JNI_TRUE;
}

int register_com_ipanel_upgrade(JNIEnv *env){
	return registerNativeMethods(env, m_class_name, gMethods,
			sizeof(gMethods) / sizeof(gMethods[0]));
}

jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = -1;
	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed\n");
		goto bail;
	}
	assert(env != NULL);
	if (register_com_ipanel_upgrade(env) < 0) {
		LOGE("ERROR: register_com_ipanel_upgrade failed\n");
		goto bail;
	}
	g_vm = vm;

	result = JNI_VERSION_1_4;
	bail: return result;
}
