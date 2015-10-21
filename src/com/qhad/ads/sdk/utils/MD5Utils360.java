package com.qhad.ads.sdk.utils;

import java.security.MessageDigest;

public class MD5Utils360 {

    public MD5Utils360() {
        // TODO Auto-generated constructor stub
    }

    public static String encode(String string) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString().toUpperCase();
        } catch (Exception e) {
            return null;
        }
    }

}
