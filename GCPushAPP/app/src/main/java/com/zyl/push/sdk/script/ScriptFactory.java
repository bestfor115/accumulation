package com.zyl.push.sdk.script;


public class ScriptFactory {

    private static final String CLASS_PATH = "com.zyl.push.sdk.script.ScriptImpl";
    private static Scriptable mScriptable;

    public static synchronized Scriptable getScriptable() {
        if (mScriptable == null) {
            mScriptable = createScriptable();
        }
        return mScriptable;
    }

    private ScriptFactory() {

    }

    private static Scriptable createScriptable() {
        try {
            Class c = Class.forName(CLASS_PATH);
            return (Scriptable) c.newInstance();
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        throw new IllegalStateException("can't find a valid scriptable impl");
    }
}
