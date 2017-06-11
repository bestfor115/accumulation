package com.zyl.push.sdk.script;


public interface Executable {

	public String exec(String... objects);
	public void bindImpl(Scriptable impl);
	public int fetchAPIVersion();
	public int fetchPlatform();
	public boolean isAndroidPlatform();

}
