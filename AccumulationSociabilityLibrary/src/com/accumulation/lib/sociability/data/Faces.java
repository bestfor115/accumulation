package com.accumulation.lib.sociability.data;

import java.io.Serializable;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


@Root(name = "div")
public class Faces implements Serializable {
	@ElementList(name = "div", inline = true)
	public List<Face> faces;
	@Attribute(required = false)
	public int width;
	@Attribute(required = false)
	public int height;

	public List<Face> getFaces() {
		return faces;
	}

	public void setFaces(List<Face> faces) {
		this.faces = faces;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
