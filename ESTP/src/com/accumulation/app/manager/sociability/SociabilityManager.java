package com.accumulation.app.manager.sociability;


public class SociabilityManager {
	
	public final static String KEY_FOR_USER_CLASS="class_user_key";
	
	private static final SociabilityManager instance=new SociabilityManager();

	public static SociabilityManager getInstance() {
		
		return instance;
	}
	
	private SociabilityManager(){
		
	}
	

}
