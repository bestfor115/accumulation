package com.accumulation.lib.sociability.im;

import com.accumulation.lib.sociability.data.MessageItem;

public interface MessageReceiver {
	public void onReceiverMessage(MessageItem message);

}
