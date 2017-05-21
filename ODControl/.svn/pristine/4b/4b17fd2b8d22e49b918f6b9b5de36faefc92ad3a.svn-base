# Copyright (C) 2010 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := libsihua_protocol_jni

LOCAL_SRC_FILES := \
src/mplayer_jni.c \
src/mplayer.c \
src/iPlayer/vod/ivod_client_main.c \
src/iPlayer/vod/ivod_client_time.c \
src/iPlayer/vod/ivod_igmp_client.c \
src/iPlayer/vod/ivod_rtsp_client.c \
src/iPlayer/vod/ivod_rtsp_ipanel.c \
src/iPlayer/vod/ivod_rtsp_isma.c \
src/iPlayer/vod/ivod_rtsp_protocol.c \
src/iPlayer/vod/ivod_rtsp_session.c \
src/iPlayer/vod/ivod_rtsp_telecom.c \
src/iPlayer/porting/ipanel_base.c \
src/iPlayer/porting/ipanel_media_processor.c \
src/iPlayer/porting/ipanel_os.c \
src/iPlayer/porting/ipanel_socket.c \
src/iPlayer/core/iplayer_main.c \
src/iPlayer/core/iplayer_vod_main.c \
src/iPlayer/base/iplayer_common.c \
src/iPlayer/base/iplayer_mem.c \
src/iPlayer/base/iplayer_player.c \
src/iPlayer/base/iplayer_qam_player.c \
src/iPlayer/base/iplayer_socket.c \
src/iPlayer/base/iplayer_ts_parse.c \
src/iPlayer/base/iplayer_ts_player.c 

LOCAL_C_INCLUDES += \
	$(LOCAL_PATH)/include \
	$(LOCAL_PATH)/src/iPlayer/include

# for native audio
# for logging
LOCAL_LDLIBS    += -llog
# for native asset manager
#LOCAL_LDLIBS    += -landroid

include $(BUILD_SHARED_LIBRARY)
