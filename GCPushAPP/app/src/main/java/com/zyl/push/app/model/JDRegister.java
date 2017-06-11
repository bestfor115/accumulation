package com.zyl.push.app.model;

import java.io.Serializable;

public class JDRegister extends JsonResult<JDRegister.Register> {

    /**
	 * 
	 */
    private static final long serialVersionUID = 4302813852487895729L;

    public static class Register implements Serializable {
        /**
		 * 
		 */
        private static final long serialVersionUID = 1420880333956593589L;
        public String userId;
        public String deviceId;
    }
}
