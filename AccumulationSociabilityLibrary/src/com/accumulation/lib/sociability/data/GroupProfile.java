package com.accumulation.lib.sociability.data;

import java.io.Serializable;

import com.accumulation.lib.tool.base.CommonUtils;
/**
 * 群组的基本信息
 * */
public class GroupProfile implements Serializable,Spellable{
	public String Id;
	public String Name;
	public String HeadPicPath;
	public String CreatedId;
	public String CreatedName;
	public String Intro;
	public String Tags;
	public String CreatedTimesStr;
	public String CreatedTime;

	public int JoinedUsersCount;
	public int JoinedStatus;
	public int EntryCondition;
	public boolean IsJoined;
	public CreatedBy CreatedBy;
    
    public static class CreatedBy{
    	public String Id;
    	public String Name;
    }
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupProfile other = (GroupProfile) obj;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		return true;
	}
	@Override
	public String getSortLetters() {
		return CommonUtils.getWordSpell(Name);
	}
	
}
