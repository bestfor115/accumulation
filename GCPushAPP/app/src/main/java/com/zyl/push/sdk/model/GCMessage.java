package com.zyl.push.sdk.model;

import com.zyl.push.sdk.constant.Constant;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.HashMap;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.stream.Format;

@Root(name="message")
public class GCMessage implements Serializable{

    public static final String MESSAGE_TARGET_SYSTEM="system";
    public static final int MESSAGE_VERSION_1=1;
    private static final Serializer mSerializer = new Persister(new Format(
            "<?xml version=\"1.0\" encoding= \"UTF-8\" ?>"));
    /**
     * 
     */
    private static final long serialVersionUID = -7689710784403840470L;
    @Element(required=true)
    private Header header;
    @Element(required=true)
    private Body body;
    public Header getHeader() {
        return header;
    }
    public void setHeader(Header header) {
        this.header = header;
    }
    public Body getBody() {
        return body;
    }
    public void setBody(Body body) {
        this.body = body;
    }
    public static GCMessage cloneMessage(GCMessage old,boolean reverse){
        GCMessage message=new GCMessage();
        Header header=new Header();
        header.setTimestamp(System.currentTimeMillis());
        header.setType(old.getHeader().getType());
        header.setVersion(old.getHeader().getVersion());
        header.setReply(old.getHeader().getReply());

        if(reverse){
            header.setFrom(old.getHeader().getTo());
            header.setTo(old.getHeader().getFrom());
        }else{
            header.setTo(old.getHeader().getTo());
            header.setFrom(old.getHeader().getFrom());
        }
        message.setHeader(header);
        Body body=new Body();
        message.setBody(body);
        return message;
    }
    public static GCMessage createMessage(String from){
        return createMessage(from,MESSAGE_TARGET_SYSTEM, Constant.MessageType.TYPE_NULL);
    }
    public static GCMessage createMessage(String from,String type){
        return createMessage(from,MESSAGE_TARGET_SYSTEM,type);
    }
    public static GCMessage createMessage(String from,String to,String type){
        GCMessage message=new GCMessage();
        Header header=new Header();
        header.setTimestamp(System.currentTimeMillis());
        header.setFrom(from);
        header.setTo(to);
        header.setType(type);
        message.setHeader(header);
        Body body=new Body();
        message.setBody(body);
        return message;
    }
    public static String toXML(GCMessage obj){
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            mSerializer.write(obj,out);
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static GCMessage fromXML(String xml){
        try {
            return mSerializer.read(GCMessage.class, xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean isFromSystem(){
        return MESSAGE_TARGET_SYSTEM.equals(header.getFrom());
    }
    public String getType(){
        return header.getType();
    }
    public void putExtra(String key,String value){
        body.getExtra().put(key, value);
    }
    public String getExtra(String key){
        return body.getExtra().get(key);
    }
    public void setReply(boolean reply){
    	header.reply=reply?"1":"0";
    }
    public boolean isReply(){
    	return "1".equals(header.reply);
    }
    public static class Header implements Serializable,Cloneable{
        /**
         * 
         */
        private static final long serialVersionUID = 3126645228579846742L;
        @Attribute
        private long timestamp;
        @Attribute
        private int version=MESSAGE_VERSION_1;
        @Attribute
        private String type;
        @Attribute
        private String from;
        @Attribute
        private String to;
        @Attribute
        private String reply="0";
        public long getTimestamp() {
            return timestamp;
        }
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
        public int getVersion() {
            return version;
        }
        public void setVersion(int version) {
            this.version = version;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getFrom() {
            return from;
        }
        public void setFrom(String from) {
            this.from = from;
        }
        public String getTo() {
            return to;
        }
        public void setTo(String to) {
            this.to = to;
        }
        
        public String getReply() {
			return reply;
		}
		public void setReply(String reply) {
			this.reply = reply;
		}
		@Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
    @Root
    public static class Body implements Serializable,Cloneable{

        /**
         * 
         */
        private static final long serialVersionUID = -7952432617406054278L;
        @Attribute
        private int code=-1;
        @Attribute
        private String message="";
        @Element(required=false)
        private String content="1";
        @ElementMap(required=false)
        private HashMap<String, String> extra=new HashMap<String, String>();

        public int getCode() {
            return code;
        }
        public void setCode(int code) {
            this.code = code;
        }
        public String getMessage() {
            return message;
        }
        public void setMessage(String message) {
            this.message = message;
        }
        public String getContent() {
            return content;
        }
        public void setContent(String content) {
            this.content = content;
        }
        public HashMap<String, String> getExtra() {
            return extra;
        }
        public void setExtra(HashMap<String, String> extra) {
            this.extra = extra;
        }
        @Override
        protected Object clone() throws CloneNotSupportedException {
            return super.clone();
        }
    }
}
