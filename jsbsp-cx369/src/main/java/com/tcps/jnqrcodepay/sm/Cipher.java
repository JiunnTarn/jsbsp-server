package com.tcps.jnqrcodepay.sm;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECPoint;

public class Cipher {
    private int ct = 1;
    private byte[] key = new byte[32];
    private byte keyOff = 0;
    private ECPoint p2;
    private SM3Digest sm3c3;
    private SM3Digest sm3keybase;

    private void Reset() {
        this.sm3keybase = new SM3Digest();
        this.sm3c3 = new SM3Digest();
        byte[] byteConvert32Bytes = Util.byteConvert32Bytes(this.p2.getRawXCoord().toBigInteger());
        this.sm3keybase.update(byteConvert32Bytes, 0, byteConvert32Bytes.length);
        this.sm3c3.update(byteConvert32Bytes, 0, byteConvert32Bytes.length);
        byte[] byteConvert32Bytes2 = Util.byteConvert32Bytes(this.p2.getRawYCoord().toBigInteger());
        this.sm3keybase.update(byteConvert32Bytes2, 0, byteConvert32Bytes2.length);
        this.ct = 1;
        NextKey();
    }

    private void NextKey() {
        SM3Digest sM3Digest = new SM3Digest(this.sm3keybase);
        sM3Digest.update((byte) ((this.ct >> 24) & 255));
        sM3Digest.update((byte) ((this.ct >> 16) & 255));
        sM3Digest.update((byte) ((this.ct >> 8) & 255));
        sM3Digest.update((byte) (this.ct & 255));
        sM3Digest.doFinal(this.key, 0);
        this.keyOff = (byte) 0;
        this.ct++;
    }

    public ECPoint Init_enc(ECPoint eCPoint) {
        AsymmetricCipherKeyPair generateKeyPair = SM2.ecc_key_pair_generator.generateKeyPair();
        BigInteger d = ((ECPrivateKeyParameters) generateKeyPair.getPrivate()).getD();
        ECPoint q = ((ECPublicKeyParameters) generateKeyPair.getPublic()).getQ();
        this.p2 = eCPoint.multiply(d);
        Reset();
        return q;
    }

    public void Encrypt(byte[] bArr) {
        this.sm3c3.update(bArr, 0, bArr.length);
        for (int i = 0; i < bArr.length; i++) {
            if (this.keyOff == this.key.length) {
                NextKey();
            }
            byte b = bArr[i];
            byte[] bArr2 = this.key;
            byte b2 = this.keyOff;
            this.keyOff = (byte) (b2 + 1);
            bArr[i] = (byte) (b ^ bArr2[b2]);
        }
    }

    public void Init_dec(BigInteger bigInteger, ECPoint eCPoint) {
        this.p2 = eCPoint.multiply(bigInteger);
        Reset();
    }

    public void Decrypt(byte[] bArr) {
        for (int i = 0; i < bArr.length; i++) {
            if (this.keyOff == this.key.length) {
                NextKey();
            }
            byte b = bArr[i];
            byte[] bArr2 = this.key;
            byte b2 = this.keyOff;
            this.keyOff = (byte) (b2 + 1);
            bArr[i] = (byte) (b ^ bArr2[b2]);
        }
        this.sm3c3.update(bArr, 0, bArr.length);
    }

    public void Dofinal(byte[] bArr) {
        byte[] byteConvert32Bytes = Util.byteConvert32Bytes(this.p2.getRawYCoord().toBigInteger());
        this.sm3c3.update(byteConvert32Bytes, 0, byteConvert32Bytes.length);
        this.sm3c3.doFinal(bArr, 0);
        Reset();
    }
}
