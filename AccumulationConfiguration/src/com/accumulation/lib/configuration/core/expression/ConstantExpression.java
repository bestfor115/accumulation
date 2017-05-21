package com.accumulation.lib.configuration.core.expression;

import java.util.HashMap;
import java.util.Map;

import com.accumulation.lib.configuration.core.Configuration;

public class ConstantExpression extends Expression {
	
	protected Map<String, String> mConstantMap= new HashMap<String, String>();

	@Override
	public String interpreter(String str) {
		// TODO Auto-generated method stub
		if(mConstantMap.containsKey(str)){
			return mConstantMap.get(str);
		}else{
			return null;
		}
	}
	
	public ConstantExpression(Configuration conf){
		this.mConstantMap=conf.getConstantMap();
	}

}
