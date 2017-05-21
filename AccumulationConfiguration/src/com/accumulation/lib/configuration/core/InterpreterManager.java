package com.accumulation.lib.configuration.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.accumulation.lib.configuration.core.expression.ConstantExpression;
import com.accumulation.lib.configuration.core.expression.Expression;
import com.accumulation.lib.configuration.core.expression.InlineFunctionExpression;

public class InterpreterManager {
	List<Expression> expressions=new ArrayList<>();
	private Configuration configuration;
	public String getString(String key){
		if(key == null)
			return key;
		Pattern pattern = Pattern.compile("\\{\\{(.*)\\}\\}");
		Matcher matcher = pattern.matcher(key);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String m=matcher.group(1);
			String n=m;
			for (Expression expression : expressions) {
				String tmp=expression.interpreter(n);
				if(tmp !=null){
					n=tmp;
				}
			}
		    matcher.appendReplacement(sb, n);
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	private void replaceStringRefs(View view) {
		for(Bind bd : view.getBind()){
			if(bd.value != null)
				bd.value.value = getString(bd.value.value);
		}
		for(Action action : view.getAction()){
			for(Bind bind : action.getBind()){
				if(bind.value != null)
					bind.value.value = getString(bind.value.value);
			}
		}
		for(View v : view.getView()){
			replaceStringRefs(v);
		}
	}
	
	public void work(){
		for(Screen sc : configuration.getScreen()){
			replaceStringRefs(sc.getView());
		}
		for(Style style : configuration.getStyles()){
			for(Bind bind : style.getBind()){
				if(bind.value != null)
					bind.value.value = getString(bind.value.value);
			}
		}
	}

	public static InterpreterManager createManager(Configuration conf){
		InterpreterManager manager = new InterpreterManager();
		manager.configuration=conf;
		manager.expressions.add(new ConstantExpression(conf));
		manager.expressions.add(new InlineFunctionExpression(conf));

		return manager;
	}
}
