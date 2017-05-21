package com.accumulation.lib.sociability.data;

import java.io.Serializable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name = "a")
public class Face implements Serializable {
	@Attribute(required = false)
	public String path;
	@Attribute(required = false)
	public String title;


	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "[" + title + "]";
	}

	public Face(String title, String path) {
		super();
		this.path = path;
		this.title = title;
	}
	
	
}
