package com.accumulation.lib.utility.math;

import java.security.InvalidParameterException;

/**
 * Created by zhangyl on 2016/7/30.
 */
public class MathUtils {
    /**
     * 计算总页数
     * @param size 每页条数
     * @param total 总记录数
     * @return 总页数
     * */
    public static int calculateCount(int size,int total){
        if(size==0){
            throw new IllegalArgumentException("size can't be 0");
        }
        return total%size==0?total/size:(total/size+1);
    }
}
