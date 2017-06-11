package com.accumulation.lib.utility.encrypt;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by zhangyl on 2016/7/28.
 */
public class EncryptUtils {

    public static final int ENCRYPT_TYPE_DES=1<<1;
    public static final int ENCRYPT_TYPE_3DES=1<<2;
    public static final int ENCRYPT_TYPE_IDEA=1<<3;
    public static final int ENCRYPT_TYPE_RSA=1<<4;
    public static final int ENCRYPT_TYPE_DSA=1<<5;
    public static final int ENCRYPT_TYPE_AES=1<<6;
    public static final int ENCRYPT_TYPE_BLOWFISH=1<<7;
    public static final int ENCRYPT_TYPE_MD5=1<<8;
    /** @hide */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true,
            value = {
                    ENCRYPT_TYPE_DES,
                    ENCRYPT_TYPE_3DES,
                    ENCRYPT_TYPE_IDEA,
                    ENCRYPT_TYPE_RSA,
                    ENCRYPT_TYPE_DSA,
                    ENCRYPT_TYPE_AES,
                    ENCRYPT_TYPE_BLOWFISH,
                    ENCRYPT_TYPE_MD5
            })
    public @interface EncryptTypes {}
    public static String encrypt(@EncryptTypes int type,Object seed, String in) {
        return null;
    }

    public static String decrypt(@EncryptTypes int type,Object seed, String in) {
        return null;
    }

    public static interface Encryptable{
        public  String encrypt(Object seed, String in);
        public String decrypt(Object seed, String in) ;
    }
}
