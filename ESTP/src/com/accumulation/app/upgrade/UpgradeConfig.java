package com.accumulation.app.upgrade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpgradeConfig implements Serializable {
	
	public int version;
	public String name;
	public String url;
	public List<String> tips=new ArrayList<String>();
}
