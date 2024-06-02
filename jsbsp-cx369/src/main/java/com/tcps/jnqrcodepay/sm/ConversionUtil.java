package com.tcps.jnqrcodepay.sm;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.UUID;

public class ConversionUtil {
    public static String complete00(String str, int i, String str2, boolean z) {
        int length = i - str.length();
        for (int i2 = 0; i2 < length; i2++) {
            str = z ? str2 + str : str + str2;
        }
        return str;
    }

    public static String complete00(String str, int i) {
        return complete00(str, i, "0", true);
    }

    public static String bigIntegerTOHexForString(BigInteger bigInteger) {
        return bigInteger.toString(16).toUpperCase();
    }

    public static byte[] hexstr2byte(String str) {
        byte[] bArr = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) Integer.parseInt(str.substring(i2, i2 + 2), 16);
        }
        return bArr;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String byte2hex(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b);
            if (hexString.length() == 1) {
                stringBuffer.append("0" + hexString);
            } else if (hexString.length() > 2) {
                stringBuffer.append(hexString.substring(hexString.length() - 2, hexString.length()));
            } else {
                stringBuffer.append(hexString);
            }
        }
        return stringBuffer.toString();
    }

    public static String convertQrCode(String str) {
        try {
            return new String(hexstr2byte(str), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getCompressPublicKey(String str, String str2) {
        String str3 = new BigInteger(str2, 16) + "";
        if (Integer.parseInt(str3.substring(str3.length() - 1, str3.length())) % 2 == 0) {
            return "02" + str;
        }
        return "03" + str;
    }
}
