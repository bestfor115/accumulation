package com.accumulation.app.data;

import java.io.Serializable;

public class ChooseFile implements Serializable{
	public String path;
	public long flagTime;
	
	public ChooseFile(){
		
	}

	public ChooseFile(String path, long flagTime) {
		super();
		this.path = path;
		this.flagTime = flagTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (flagTime ^ (flagTime >>> 32));
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		ChooseFile other = (ChooseFile) obj;
		if (flagTime != other.flagTime)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
}
