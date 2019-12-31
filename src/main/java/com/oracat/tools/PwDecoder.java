package com.oracat.tools;
import java.util.Base64;

public class PwDecoder {
    String depassword="";
    public PwDecoder(String password)
    {
        Base64.Decoder decoder= Base64.getMimeDecoder();



        try {
            byte[] b = decoder.decode(password);
            this.depassword=new String(b);
        } catch (Exception e) {
            return ;
        }


    }

    public String getPassword()
    {
        return this.depassword;

    }
}
