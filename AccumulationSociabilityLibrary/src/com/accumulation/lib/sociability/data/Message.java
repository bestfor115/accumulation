package com.accumulation.lib.sociability.data;

import java.io.Serializable;
import java.util.List;

/**
 * к╫пе
 * */
public class Message extends MessageItem implements Serializable {

	public List<ChildrenItem> Children;

	public class ChildrenItem extends MessageItem implements Serializable {

	}
}
