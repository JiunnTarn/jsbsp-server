package com.tcps.jnqrcodepay.sm;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;
import org.bouncycastle.math.ec.ECPoint;

public class SM2 {
    public static BigInteger ecc_a;
    public static BigInteger ecc_b;
    public static ECDomainParameters ecc_bc_spec;
    public static ECCurve ecc_curve;
    public static BigInteger ecc_gx;
    public static ECFieldElement ecc_gx_fieldelement;
    public static BigInteger ecc_gy;
    public static ECFieldElement ecc_gy_fieldelement;
    public static ECKeyPairGenerator ecc_key_pair_generator;
    public static BigInteger ecc_n;
    public static BigInteger ecc_p;
    public static String[] ecc_param;
    public static ECPoint ecc_point_g;
    public static String sm2_a;
    public static String sm2_b;
    public static String sm2_gx;
    public static String sm2_gy;
    public static String sm2_n;
    public static String sm2_p;

    static {
        String[] strArr = {"FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", "28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7", "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"};
        ecc_param = strArr;
        sm2_p = strArr[0];
        sm2_a = strArr[1];
        sm2_b = strArr[2];
        sm2_n = strArr[3];
        sm2_gx = strArr[4];
        sm2_gy = strArr[5];
        ecc_p = new BigInteger(sm2_p, 16);
        ecc_a = new BigInteger(sm2_a, 16);
        ecc_b = new BigInteger(sm2_b, 16);
        ecc_n = new BigInteger(sm2_n, 16);
        ecc_gx = new BigInteger(sm2_gx, 16);
        ecc_gy = new BigInteger(sm2_gy, 16);
        ecc_gx_fieldelement = new ECFieldElement.Fp(ecc_p, ecc_gx);
        ecc_gy_fieldelement = new ECFieldElement.Fp(ecc_p, ecc_gy);
        ecc_curve = new ECCurve.Fp(ecc_p, ecc_a, ecc_b);
        ecc_point_g = new ECPoint.Fp(ecc_curve, ecc_gx_fieldelement, ecc_gy_fieldelement);
        ECDomainParameters eCDomainParameters = new ECDomainParameters(ecc_curve, ecc_point_g, ecc_n);
        ecc_bc_spec = eCDomainParameters;
        ECKeyGenerationParameters eCKeyGenerationParameters = new ECKeyGenerationParameters(eCDomainParameters, new SecureRandom());
        ECKeyPairGenerator eCKeyPairGenerator = new ECKeyPairGenerator();
        ecc_key_pair_generator = eCKeyPairGenerator;
        eCKeyPairGenerator.init(eCKeyGenerationParameters);
    }
}
