package com.tcps.jnqrcodepay.sm;

import java.math.BigInteger;

public class Util {
    public static byte[] intToBytes(int i) {
        return new byte[]{(byte) ((i >> 0) & 255), (byte) ((i >> 8) & 255), (byte) ((i >> 16) & 255), (byte) ((i >> 24) & 255)};
    }

    public static int byteToInt(byte[] bArr) {
        return ((bArr[3] & 255) << 24) | 0 | ((bArr[0] & 255) << 0) | ((bArr[1] & 255) << 8) | ((bArr[2] & 255) << 16);
    }

    public static byte[] longToBytes(long j) {
        byte[] bArr = new byte[8];
        for (int i = 0; i < 8; i++) {
            bArr[i] = (byte) (255 & (j >> (i * 8)));
        }
        return bArr;
    }

    public static byte[] byteConvert32Bytes(BigInteger bigInteger) {
        if (bigInteger == null) {
            return null;
        }
        if (bigInteger.toByteArray().length == 33) {
            byte[] bArr = new byte[32];
            System.arraycopy(bigInteger.toByteArray(), 1, bArr, 0, 32);
            return bArr;
        } else if (bigInteger.toByteArray().length == 32) {
            return bigInteger.toByteArray();
        } else {
            byte[] bArr2 = new byte[32];
            for (int i = 0; i < 32 - bigInteger.toByteArray().length; i++) {
                bArr2[i] = 0;
            }
            System.arraycopy(bigInteger.toByteArray(), 0, bArr2, 32 - bigInteger.toByteArray().length, bigInteger.toByteArray().length);
            return bArr2;
        }
    }
}
