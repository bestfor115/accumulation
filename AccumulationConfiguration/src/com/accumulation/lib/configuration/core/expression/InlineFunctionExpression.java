package com.accumulation.lib.configuration.core.expression;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.accumulation.lib.configuration.core.Configuration;
import com.accumulation.lib.tool.debug.Logger;

public class InlineFunctionExpression extends Expression {
	float scale=1.0f;
	Pattern pattern = Pattern.compile("__([A-Z]+)\\((.*)\\)");
	
	private static String TAG_FUNC_SCALE="SCALE";

	public InlineFunctionExpression(Configuration conf) {
		// TODO Auto-generated constructor stub
		this.scale=conf.getScale();
	}


	@Override
	public String interpreter(String str) {
		// TODO Auto-generated method stub
		Matcher matcher = pattern.matcher(str);
		if(matcher.matches()){
			String funcName=matcher.group(1);
			String[] funcParam=matcher.group(2).trim().split(",");
			return paseFunc(funcName,funcParam);
		}else{
			return null;
		}
	}
	
	private String paseFunc(String name,String[] param){
		if(TAG_FUNC_SCALE.equals(name)){
			if(param.length==1){
				String reg =  "^(-?\\d+)(\\.\\d+)?$";
			    if(Pattern.compile(reg).matcher(param[0]).find()){
			    	return scale(Float.parseFloat(param[0]));
			    }
			}
		}
		return null;
	}
	
	private String scale(float value){
		NumberFormat format=NumberFormat.getNumberInstance() ; 
		format.setMaximumFractionDigits(5); 
		String s= format.format(value*scale); 
		Logger.e(String.format("change %s to %s", value,s));
		return s;
	}

}
