package com.accumulation.app.data;

import java.io.Serializable;

public class YLLocation  implements Serializable{
	
	public double x;
	public double y;
	public String name;
	public String description;
	public YLLocation(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}
	
	

}
