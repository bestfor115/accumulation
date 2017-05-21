LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := eng

# Only compile source java files in this apk.
LOCAL_SRC_FILES := $(call all-java-files-under, src)

LOCAL_PACKAGE_NAME := SiHuaVODControl
LOCAL_CERTIFICATE := platform

LOCAL_JNI_SHARED_LIBRARIES := libsihua_protocol_jni

include $(BUILD_PACKAGE)
include $(call all-makefiles-under,$(LOCAL_PATH))