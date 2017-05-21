package com.accumulation.lib.sociability.data;

import java.io.Serializable;

public class Subject  implements Serializable{
	public String Id;
	public String Name;
	public String IcoPath;
	public String Description;
	public int BroadCount;
	public boolean IsCurUserLiked;
	public int FansCount;
	public String [] ParentIdList;
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
		Subject other = (Subject) obj;
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		return true;
	}
	
}
