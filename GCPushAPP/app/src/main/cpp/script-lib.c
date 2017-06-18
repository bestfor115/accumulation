#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <android/log.h>
#include <jni.h>
#include "src/lua.h"
#include "src/lualib.h"
#include "src/lauxlib.h"
#define LOG_TAG "Script-Protocol"
#define LOGD(fmt, ...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGI(fmt, ...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGE(fmt, ...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, fmt, ##__VA_ARGS__)
#define LOGV(fmt, ...) __android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, fmt, ##__VA_ARGS__)

static JavaVM *g_vm = NULL;
static lua_State *L = NULL;
static struct {
    jclass clazz;
    jobject wo;
    jmethodID api_openApp;
    jmethodID api_toast;
    jmethodID api_sleep;
    jmethodID api_sendMessage;
    jmethodID api_callPerson;
    jmethodID api_inputString;
    jmethodID api_sendKeyEvent;
    jmethodID api_sendBackKeyEvent;
    jmethodID api_sendHomeKeyEvent;
    jmethodID api_sendMenuKeyEvent;
    jmethodID api_sendTapGesture;
    jmethodID api_sendLongPressGesture;
    jmethodID api_sendLongPressGesture_2;
    jmethodID api_sendScrollGesture;
    jmethodID api_sendScrollGesture_2;
    jmethodID api_sendFlingGesture;
    jmethodID api_sendFlingGesture_2;
    jmethodID api_sendFlingGesture_3;
    jmethodID api_sendFlingGesture_4;
    jmethodID api_sendDownGesture;
    jmethodID api_tapByText;
    jmethodID api_tapById;
    jmethodID api_setInputMethodState;
    jmethodID api_tapByImage;
} m_player;

static JNIEnv* attach_java_thread(const char* threadName) {
    JavaVMAttachArgs args;
    jint result;
    JNIEnv*e = NULL;
    args.version = JNI_VERSION_1_4;
    args.name = (char*) threadName;
    args.group = NULL;
    if ((result = (*g_vm)->AttachCurrentThread(g_vm, &e, (void*) &args))
        != JNI_OK) {
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

void native_exec(JNIEnv *env, jobject thiz, jstring jpath) {
    LOGD("native_exec ...");
    const char *s = NULL;
    s = (*env)->GetStringUTFChars(env, jpath, NULL);
    loadALLAPI(L, s);
    (*env)->ReleaseStringUTFChars(env, jpath, s);
}

static const char* const m_class_name =
        "com/zyl/push/sdk/script/MScriptProtocol";

static JNINativeMethod gMethods[] = { { "native_exec", "(Ljava/lang/String;)V",
                                              (void*) native_exec }, };

static int registerNativeMethods(JNIEnv *env, const char* className,
                                 JNINativeMethod* gMethods, int numMethods) {
    jclass clazz;
    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    m_player.clazz = (jclass)(*env)->NewGlobalRef(env, clazz);
    if (!(m_player.api_openApp = (*env)->GetStaticMethodID(env, m_player.clazz,
                                                           "api_openApp", "(Ljava/lang/String;)V"))) {
        LOGE("no such static method : api_openApp");
        return JNI_FALSE;
    }
    if (!(m_player.api_toast = (*env)->GetStaticMethodID(env, m_player.clazz,
                                                         "api_toast", "(Ljava/lang/String;)V"))) {
        LOGE("no such static method : api_toast");
        return JNI_FALSE;
    }
    if (!(m_player.api_sleep = (*env)->GetStaticMethodID(env, m_player.clazz,
                                                         "api_sleep", "(J)V"))) {
        LOGE("no such static method : api_sleep");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendMessage = (*env)->GetStaticMethodID(env,
                                                               m_player.clazz, "api_sendMessage",
                                                               "(Ljava/lang/String;Ljava/lang/String;)I"))) {
        LOGE("no such static method : api_sendMessage");
        return JNI_FALSE;
    }
    if (!(m_player.api_callPerson = (*env)->GetStaticMethodID(env,
                                                              m_player.clazz, "api_callPerson", "(Ljava/lang/String;)I"))) {
        LOGE("no such static method : api_callPerson");
        return JNI_FALSE;
    }
    if (!(m_player.api_inputString = (*env)->GetStaticMethodID(env,
                                                               m_player.clazz, "api_inputString", "(Ljava/lang/String;)V"))) {
        LOGE("no such static method : api_inputString");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendKeyEvent = (*env)->GetStaticMethodID(env,
                                                                m_player.clazz, "api_sendKeyEvent", "(I)V"))) {
        LOGE("no such static method : api_sendKeyEvent");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendBackKeyEvent = (*env)->GetStaticMethodID(env,
                                                                    m_player.clazz, "api_sendBackKeyEvent", "()V"))) {
        LOGE("no such static method : api_sendBackKeyEvent");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendHomeKeyEvent = (*env)->GetStaticMethodID(env,
                                                                    m_player.clazz, "api_sendHomeKeyEvent", "()V"))) {
        LOGE("no such static method : api_sendHomeKeyEvent");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendMenuKeyEvent = (*env)->GetStaticMethodID(env,
                                                                    m_player.clazz, "api_sendMenuKeyEvent", "()V"))) {
        LOGE("no such static method : api_sendMenuKeyEvent");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendTapGesture = (*env)->GetStaticMethodID(env,
                                                                  m_player.clazz, "api_sendTapGesture", "(II)V"))) {
        LOGE("no such static method : api_sendTapGesture");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendLongPressGesture = (*env)->GetStaticMethodID(env,
                                                                        m_player.clazz, "api_sendLongPressGesture", "(II)V"))) {
        LOGE("no such static method : api_sendLongPressGesture");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendLongPressGesture_2 = (*env)->GetStaticMethodID(env,
                                                                          m_player.clazz, "api_sendLongPressGesture", "(IIJ)V"))) {
        LOGE("no such static method : api_sendLongPressGesture_2");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendScrollGesture = (*env)->GetStaticMethodID(env,
                                                                     m_player.clazz, "api_sendScrollGesture", "(IIII)V"))) {
        LOGE("no such static method : api_sendScrollGesture");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendScrollGesture_2 = (*env)->GetStaticMethodID(env,
                                                                       m_player.clazz, "api_sendScrollGesture", "(IIIIJ)V"))) {
        LOGE("no such static method : api_sendScrollGesture_2");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendFlingGesture = (*env)->GetStaticMethodID(env,
                                                                    m_player.clazz, "api_sendFlingGesture", "(I)V"))) {
        LOGE("no such static method : api_sendFlingGesture");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendFlingGesture_2 = (*env)->GetStaticMethodID(env,
                                                                      m_player.clazz, "api_sendFlingGesture", "(IF)V"))) {
        LOGE("no such static method : api_sendFlingGesture_2");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendFlingGesture_3 = (*env)->GetStaticMethodID(env,
                                                                      m_player.clazz, "api_sendFlingGesture", "(II)V"))) {
        LOGE("no such static method : api_sendFlingGesture_3");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendFlingGesture_4 = (*env)->GetStaticMethodID(env,
                                                                      m_player.clazz, "api_sendFlingGesture", "(IIII)V"))) {
        LOGE("no such static method : api_sendFlingGesture_4");
        return JNI_FALSE;
    }
    if (!(m_player.api_sendDownGesture = (*env)->GetStaticMethodID(env,
                                                                   m_player.clazz, "api_sendDownGesture", "(II)V"))) {
        LOGE("no such static method : api_sendDownGesture");
        return JNI_FALSE;
    }
    if (!(m_player.api_tapByText = (*env)->GetStaticMethodID(env, m_player.clazz,
                                                           "api_tapByText", "(Ljava/lang/String;)V"))) {
        LOGE("no such static method : api_openApp");
        return JNI_FALSE;
    }
    if (!(m_player.api_tapById = (*env)->GetStaticMethodID(env, m_player.clazz,
                                                           "api_tapById", "(Ljava/lang/String;)V"))) {
        LOGE("no such static method : api_openApp");
        return JNI_FALSE;
    }
    if (!(m_player.api_setInputMethodState = (*env)->GetStaticMethodID(env,
                                                                m_player.clazz, "api_setInputMethodState", "(I)V"))) {
        LOGE("no such static method : api_setInputMethodState");
        return JNI_FALSE;
    }
    if (!(m_player.api_tapByImage = (*env)->GetStaticMethodID(env, m_player.clazz,
                                                         "api_tapByImage", "(Ljava/lang/String;)V"))) {
        LOGE("no such static method : api_tapByImage");
        return JNI_FALSE;
    }
    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

int register_com_script_upgrade(JNIEnv *env) {
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
    if (register_com_script_upgrade(env) < 0) {
        LOGE("ERROR: register_com_ipanel_upgrade failed\n");
        goto bail;
    }
    g_vm = vm;

    if (NULL == (L = luaL_newstate())) {
        LOGE("luaL_newstate failed");
    }
    luaL_openlibs(L);
    luaopen_table(L);

    result = JNI_VERSION_1_4;
    bail: return result;
}

int api_openAPP(lua_State* L) {
    const char * param = luaL_checkstring(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        jstring js = (*env)->NewStringUTF(env, param);
        (*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.api_openApp,
                                     js);
        (*env)->ReleaseStringUTFChars(env, js, param);
    }
    return 0;
}
int api_toast(lua_State* L) {
    const char * param = luaL_checkstring(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        jstring js = (*env)->NewStringUTF(env, param);
        (*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.api_toast,
                                     js);
    }
    return 0;
}
int api_tapByImage(lua_State* L) {
    const char * param = luaL_checkstring(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        jstring js = (*env)->NewStringUTF(env, param);
        (*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.api_tapByImage,
                                     js);
    }
    return 0;
}
int api_tapByText(lua_State* L) {
    const char * param = luaL_checkstring(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        jstring js = (*env)->NewStringUTF(env, param);
        (*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.api_tapByText,
                                     js);
        (*env)->ReleaseStringUTFChars(env, js, param);
    }
    return 0;
}
int api_tapById(lua_State* L) {
    const char * param = luaL_checkstring(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        jstring js = (*env)->NewStringUTF(env, param);
        (*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.api_tapById,
                                     js);
        (*env)->ReleaseStringUTFChars(env, js, param);
    }
    return 0;
}
int api_log(lua_State* L) {
    const char * param = luaL_checkstring(L, 1);
    LOGE("'%s'\n", param);
    return 0;
}
int api_showInputMethod(lua_State* L) {
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.api_setInputMethodState,
                                     0);
    }
    return 0;
}
int api_hideInputMethod(lua_State* L) {
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.api_setInputMethodState,
                                     1);
    }
    return 0;
}
int api_sleep(lua_State* L) {
    jlong param = luaL_checknumber(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz, m_player.api_sleep,
                                     param);
    }
    return 0;
}
int api_sendMessage(lua_State* L) {
    const char * param1 = luaL_checkstring(L, 1);
    const char * param2 = luaL_checkstring(L, 2);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        jstring js1 = (*env)->NewStringUTF(env, param1);
        jstring js2 = (*env)->NewStringUTF(env, param2);

        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendMessage, js1, js2);
        (*env)->ReleaseStringUTFChars(env, js1, param1);
        (*env)->ReleaseStringUTFChars(env, js2, param2);
    }
    return 0;
}
int api_callPerson(lua_State* L) {
    const char * param = luaL_checkstring(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        jstring js = (*env)->NewStringUTF(env, param);

        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_callPerson, js);
        (*env)->ReleaseStringUTFChars(env, js, param);
    }
    return 0;
}
int api_inputString(lua_State* L) {
    const char * param = luaL_checkstring(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        jstring js = (*env)->NewStringUTF(env, param);
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_inputString, js);
        (*env)->ReleaseStringUTFChars(env, js, param);
    }
    return 0;
}
int api_sendKeyEvent(lua_State* L) {
    jint param = luaL_checknumber(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendKeyEvent, param);
    }
    return 0;
}
int api_sendBackKeyEvent(lua_State* L) {
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendBackKeyEvent);
    }
    return 0;
}
int api_sendHomeKeyEvent(lua_State* L) {
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendHomeKeyEvent);
    }
    return 0;
}
int api_sendMenuKeyEvent(lua_State* L) {
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendMenuKeyEvent);
    }
    return 0;
}
int api_sendTapGesture(lua_State* L) {
    jint x = luaL_checknumber(L, 1);
    jint y = luaL_checknumber(L, 2);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendTapGesture, x, y);
    }
    return 0;
}
int api_sendLongPressGesture(lua_State* L) {
    jint x = luaL_checknumber(L, 1);
    jint y = luaL_checknumber(L, 2);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendLongPressGesture, x, y);
    }
    return 0;
}
int api_sendLongPressGesture_2(lua_State* L) {
    jint x = luaL_checknumber(L, 1);
    jint y = luaL_checknumber(L, 2);
    jlong time = luaL_checknumber(L, 3);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendLongPressGesture_2, x, y, time);
    }
    return 0;
}
int api_sendScrollGesture(lua_State* L) {
    jint fx = luaL_checknumber(L, 1);
    jint fy = luaL_checknumber(L, 2);
    jint tx = luaL_checknumber(L, 3);
    jint ty = luaL_checknumber(L, 4);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendScrollGesture, fx, fy, tx, ty);
    }
    return 0;
}
int api_sendScrollGesture_2(lua_State* L) {
    jint fx = luaL_checknumber(L, 1);
    jint fy = luaL_checknumber(L, 2);
    jint tx = luaL_checknumber(L, 3);
    jint ty = luaL_checknumber(L, 4);
    jlong time = luaL_checknumber(L, 5);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendScrollGesture_2, fx, fy, tx, ty, time);
    }
    return 0;
}
int api_sendFlingGesture(lua_State* L) {
    jint directionx = luaL_checknumber(L, 1);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendFlingGesture, directionx);
    }
    return 0;
}
int api_sendFlingGesture_2(lua_State* L) {
    jint directionx = luaL_checknumber(L, 1);
    jfloat locationScale = luaL_checknumber(L, 2);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendFlingGesture_2, directionx, locationScale);
    }
    return 0;
}
int api_sendFlingGesture_3(lua_State* L) {
    jint directionx = luaL_checknumber(L, 1);
    jint location = luaL_checknumber(L, 2);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendFlingGesture_3, directionx, location);
    }
    return 0;
}
int api_sendFlingGesture_4(lua_State* L) {
    jint fx = luaL_checknumber(L, 1);
    jint fy = luaL_checknumber(L, 2);
    jint tx = luaL_checknumber(L, 3);
    jint ty = luaL_checknumber(L, 4);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendFlingGesture_4, fx, fy, tx, ty);
    }
    return 0;
}
int api_sendDownGesture(lua_State* L) {
    jint x = luaL_checknumber(L, 1);
    jint y = luaL_checknumber(L, 2);
    JNIEnv *env = attach_java_thread("mplayer-callback");
    if (env) {
        (*env)->CallStaticVoidMethod(env, m_player.clazz,
                                     m_player.api_sendDownGesture, x, y);
    }
    return 0;
}
void regFunc() {
    lua_register(L, "api_openAPP", api_openAPP);
    lua_register(L, "api_toast", api_toast);
    lua_register(L, "api_sleep", api_sleep);
    lua_register(L, "api_sendMessage", api_sendMessage);
    lua_register(L, "api_callPerson", api_callPerson);
    lua_register(L, "api_inputString", api_inputString);
    lua_register(L, "api_sendKeyEvent", api_sendKeyEvent);
    lua_register(L, "api_sendBackKeyEvent", api_sendBackKeyEvent);
    lua_register(L, "api_sendHomeKeyEvent", api_sendHomeKeyEvent);
    lua_register(L, "api_sendMenuKeyEvent", api_sendMenuKeyEvent);
    lua_register(L, "api_sendTapGesture", api_sendTapGesture);
    lua_register(L, "api_sendLongPressGesture", api_sendLongPressGesture);
    lua_register(L, "api_sendLongPressGesture_2", api_sendLongPressGesture_2);
    lua_register(L, "api_sendScrollGesture", api_sendScrollGesture);
    lua_register(L, "api_sendScrollGesture_2", api_sendScrollGesture_2);
    lua_register(L, "api_sendFlingGesture", api_sendFlingGesture);
    lua_register(L, "api_sendFlingGesture_2", api_sendFlingGesture_2);
    lua_register(L, "api_sendFlingGesture_3", api_sendFlingGesture_3);
    lua_register(L, "api_sendFlingGesture_4", api_sendFlingGesture_4);
    lua_register(L, "api_sendDownGesture", api_sendDownGesture);
    lua_register(L, "api_log", api_log);
    lua_register(L, "api_tapByText", api_tapByText);
    lua_register(L, "api_tapById", api_tapById);
    lua_register(L, "api_showInputMethod", api_showInputMethod);
    lua_register(L, "api_hideInputMethod", api_hideInputMethod);
    lua_register(L, "api_tapByImage", api_tapByImage);

}

int loadALLAPI(lua_State* L, char * path) {
    LOGD("enter loadALLAPI ...\n");
    int iError = luaL_loadfile(L, path);
    if (iError) {
        LOGE("load script fail!\n");
        return iError;
    }
    iError = lua_pcall(L, 0, 0, 0);
    if (iError) {
        LOGE("execute script fail!\n");
        return iError;
    }
    regFunc();
    lua_getglobal(L, "___MAIN___");
    LOGD("exec main func");
    iError = lua_pcall(L, 0, 0, 0);
    return iError;
}
