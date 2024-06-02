package com.tcps.jnqrcodepay.sm;

public class SM3Digest {
    private static final int BLOCK_LENGTH = 64;
    private static final int BUFFER_LENGTH = 64;
    private static final int BYTE_LENGTH = 32;
    private byte[] V;
    private int cntBlock;
    private byte[] xBuf;
    private int xBufOff;

    public int getDigestSize() {
        return 32;
    }

    public SM3Digest() {
        this.xBuf = new byte[64];
        this.V = (byte[]) SM3.iv.clone();
        this.cntBlock = 0;
    }

    public SM3Digest(SM3Digest sM3Digest) {
        this.xBuf = new byte[64];
        this.V = (byte[]) SM3.iv.clone();
        this.cntBlock = 0;
        byte[] bArr = sM3Digest.xBuf;
        System.arraycopy(bArr, 0, this.xBuf, 0, bArr.length);
        this.xBufOff = sM3Digest.xBufOff;
        byte[] bArr2 = sM3Digest.V;
        System.arraycopy(bArr2, 0, this.V, 0, bArr2.length);
    }

    public int doFinal(byte[] bArr, int i) {
        byte[] doFinal = doFinal();
        System.arraycopy(doFinal, 0, bArr, 0, doFinal.length);
        return 32;
    }

    public void reset() {
        this.xBufOff = 0;
        this.cntBlock = 0;
        this.V = (byte[]) SM3.iv.clone();
    }

    public void update(byte[] bArr, int i, int i2) {
        int i3 = this.xBufOff;
        int i4 = 64 - i3;
        if (i4 < i2) {
            System.arraycopy(bArr, i, this.xBuf, i3, i4);
            i2 -= i4;
            i += i4;
            doUpdate();
            while (i2 > 64) {
                System.arraycopy(bArr, i, this.xBuf, 0, 64);
                i2 -= 64;
                i += 64;
                doUpdate();
            }
        }
        System.arraycopy(bArr, i, this.xBuf, this.xBufOff, i2);
        this.xBufOff += i2;
    }

    private void doUpdate() {
        byte[] bArr = new byte[64];
        for (int i = 0; i < 64; i += 64) {
            System.arraycopy(this.xBuf, i, bArr, 0, 64);
            doHash(bArr);
        }
        this.xBufOff = 0;
    }

    private void doHash(byte[] bArr) {
        byte[] CF = SM3.CF(this.V, bArr);
        byte[] bArr2 = this.V;
        System.arraycopy(CF, 0, bArr2, 0, bArr2.length);
        this.cntBlock++;
    }

    private byte[] doFinal() {
        byte[] bArr = new byte[64];
        int i = this.xBufOff;
        byte[] bArr2 = new byte[i];
        System.arraycopy(this.xBuf, 0, bArr2, 0, i);
        byte[] padding = SM3.padding(bArr2, this.cntBlock);
        for (int i2 = 0; i2 < padding.length; i2 += 64) {
            System.arraycopy(padding, i2, bArr, 0, 64);
            doHash(bArr);
        }
        return this.V;
    }

    public void update(byte b) {
        update(new byte[]{b}, 0, 1);
    }

    public static String sm3ToHash(String str, String str2, String str3, String str4) throws Exception {
        byte[] bArr = new byte[32];
        try {
            String stringBuffer = "008031323334353637383132333435363738FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E9332C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0" +
                    str2 +
                    str3;

            byte[] hexStr2byte = ConversionUtil.hexstr2byte(stringBuffer);
            SM3Digest sm3Digest = new SM3Digest();
            sm3Digest.update(hexStr2byte, 0, hexStr2byte.length);
            sm3Digest.doFinal(bArr, 0);
            String upperCase = ConversionUtil.byte2hex(bArr).toUpperCase();

            byte[] bArr2 = new byte[32];

            byte[] hexStr2byte2 = ConversionUtil.hexstr2byte(upperCase + str4);
            SM3Digest sM3Digest2 = new SM3Digest();
            sM3Digest2.update(hexStr2byte2, 0, hexStr2byte2.length);
            sM3Digest2.doFinal(bArr2, 0);
            return ConversionUtil.byte2hex(bArr2).toUpperCase();


        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
