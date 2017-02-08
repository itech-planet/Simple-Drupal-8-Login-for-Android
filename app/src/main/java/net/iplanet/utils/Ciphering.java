package net.iplanet.utils;

import org.apache.commons.codec.binary.Base64;

public class Ciphering {
    public String getBase64(String s){
        byte[] encodedBytes = Base64.encodeBase64(s.getBytes());
        return new String(encodedBytes);
    }

}
