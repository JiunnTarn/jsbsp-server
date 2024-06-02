package com.tcps.jnqrcodepay.sm;

import java.math.BigInteger;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;

public class SM2Util {
    public static String USER_ID = "31323334353637383132333435363738";
    String hdata;

    public static String[] generateKeyPair() {
        AsymmetricCipherKeyPair generateKeyPair = SM2.ecc_key_pair_generator.generateKeyPair();
        ECPoint q = ((ECPublicKeyParameters) generateKeyPair.getPublic()).getQ();
        BigInteger d = ((ECPrivateKeyParameters) generateKeyPair.getPrivate()).getD();
        ECFieldElement rawXCoord = q.getRawXCoord();
        ECFieldElement rawYCoord = q.getRawYCoord();
        String complete00 = ConversionUtil.complete00(ConversionUtil.bigIntegerTOHexForString(rawXCoord.toBigInteger()), 64);
        String complete002 = ConversionUtil.complete00(ConversionUtil.bigIntegerTOHexForString(rawYCoord.toBigInteger()), 64);
        String complete003 = ConversionUtil.complete00(ConversionUtil.bigIntegerTOHexForString(d), 64);
        return new String[]{complete00 + complete002, complete003};
    }

    public static String sm2Sign(String str, String str2) {
        byte[] hexstr2byte = ConversionUtil.hexstr2byte(str2);
        BigInteger bigInteger = SM2.ecc_n;
        ECPoint eCPoint = SM2.ecc_point_g;
        BigInteger bigInteger2 = new BigInteger(str, 16);
        FixedPointCombMultiplier fixedPointCombMultiplier = new FixedPointCombMultiplier();
        while (true) {
            BigInteger randomK = getRandomK();
            ECPoint normalize = fixedPointCombMultiplier.multiply(eCPoint, randomK).normalize();
            BigInteger mod = normalize.getAffineXCoord().toBigInteger().add(new BigInteger(1, hexstr2byte)).mod(bigInteger);
            if (!mod.equals(BigInteger.ZERO) && !mod.add(randomK).equals(bigInteger)) {
                BigInteger mod2 = bigInteger2.add(BigInteger.ONE).modInverse(bigInteger).multiply(randomK.subtract(mod.multiply(bigInteger2))).mod(bigInteger);
                if (!mod2.equals(BigInteger.ZERO)) {
                    String complete00 = ConversionUtil.complete00(ConversionUtil.bigIntegerTOHexForString(mod), 64);
                    String complete002 = ConversionUtil.complete00(ConversionUtil.bigIntegerTOHexForString(mod2), 64);
                    return complete00 + complete002;
                }
            }
        }
    }

    private static BigInteger getRandomK() {
        return new BigInteger(ConversionUtil.getUUID() + ConversionUtil.getUUID(), 16);
    }


    public static String getSM2Sign(String str, String str2, String str3, String str4) throws Exception {
        return sm2Sign(str3, SM3Digest.sm3ToHash(USER_ID, str, str2, str4));
    }
}
