package net.cliff3.maven.common.util.crypto;

import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;

/**
 * RSAKeySet
 *
 * @author JoonHo Son
 * @since 0.1.0
 */
public class RSAKeySet {
    /**
     * Public key
     */
    @Getter
    private Key publicKey;

    /**
     * {@link Base64#encodeBase64URLSafeString(byte[])} 처리된 {@link #publicKey} 문자열
     */
    @Getter
    private String publicKeyString;

    /**
     * Private key
     */
    @Getter
    private Key privateKey;

    /**
     * {@link Base64#encodeBase64URLSafeString(byte[])} 처리된 {@link #privateKey} 문자열
     */
    @Getter
    private String privateKeyString;

    /**
     * 암호화 처리된 결과
     */
    @Getter
    @Setter
    private byte[] encryptedValue;

    /**
     * 공개키의 계수(modulus)
     */
    @Getter
    private String publicKeyModulus;

    /**
     * 공개키의 지수(exponent)
     */
    @Getter
    private String publicKeyExponent;

    /**
     * 비밀키의 계수(modulus)
     */
    @Getter
    private String privateKeyModulus;

    /**
     * 비밀키의 지수(exponent)
     */
    @Getter
    private String privateKeyExponent;

    /**
     * 공개/비공개 키의 계수(modulus) 및 지수(exponent)의 문자열 처리 여부. 해당 값이 <strong>true</strong>일 경우
     * 16진수의 공개/비공개 키의 계수 및 지수를 생성한다
     *
     * @see #publicKeyModulus
     * @see #publicKeyExponent
     * @see #privateKeyModulus
     * @see #privateKeyExponent
     */
    @Getter
    private boolean makeKeyToHexString;

    /**
     * Constructor
     *
     * @param publicKeyModulus   16진수로 구성된 공개키 계수
     * @param publicKeyExponent  16진수로 구성된 공개키 지수
     * @param privateKeyModulus  16진수로 구성된 비밀키 계수
     * @param privateKeyExponent 16진수로 구성된 비밀키 지수
     */
    public RSAKeySet(String publicKeyModulus,
                     String publicKeyExponent,
                     String privateKeyModulus,
                     String privateKeyExponent) {
        this.publicKeyModulus = publicKeyModulus;
        this.publicKeyExponent = publicKeyExponent;
        this.privateKeyModulus = privateKeyModulus;
        this.privateKeyExponent = privateKeyExponent;

        stringToKey();
    }

    /**
     * Constructor
     *
     * @param publicKey          공개키
     * @param privateKey         비밀키
     * @param makeKeyToHexString 공개/비공개 키의 계수, 지수 및 {@link Base64#encodeBase64URLSafeString(byte[])} 문자열 생성 여부
     */
    public RSAKeySet(Key publicKey, Key privateKey, boolean makeKeyToHexString) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
        this.makeKeyToHexString = makeKeyToHexString;

        if (makeKeyToHexString) {
            keyToHexString();
        }
    }

    /**
     * 생성자를 통해 전달된 공개/비공개키의 계수(modulus) 및 지수(exponent) 정보를 이용하여
     * 실제 public/private key를 생성한다.
     *
     * @throws CryptoException 복원 실패
     * @see NoSuchAlgorithmException
     * @see InvalidKeySpecException
     */
    private void stringToKey() {
        try {
            KeyFactory _keyFactory = KeyFactory.getInstance("RSA");

            BigInteger _publicKeyModulus = new BigInteger(publicKeyModulus, 16);
            BigInteger _publicKeyExponent = new BigInteger(publicKeyExponent, 16);
            RSAPublicKeySpec _rsaPublicKeySpec = new RSAPublicKeySpec(_publicKeyModulus, _publicKeyExponent);

            this.publicKey = _keyFactory.generatePublic(_rsaPublicKeySpec);

            BigInteger _privateKeyModulus = new BigInteger(privateKeyModulus, 16);
            BigInteger _privateKeyExponent = new BigInteger(privateKeyExponent, 16);
            RSAPrivateKeySpec _rsaPrivateCrtKeySpec = new RSAPrivateKeySpec(_privateKeyModulus, _privateKeyExponent);

            this.privateKey = _keyFactory.generatePrivate(_rsaPrivateCrtKeySpec);
        } catch (Throwable e) {
            throw new CryptoException("계수 및 지수로부터 키 생성 실패", e);
        }
    }

    /**
     * 생성자를 통해 전달된 공개/비공개키를 계수(modulus), 지수(exponent) 및 {@link Base64#encodeBase64URLSafeString(byte[])}
     * 처리하여 할당한다.
     *
     * @throws CryptoException 계수 및 지수 생성 실패
     * @see NoSuchAlgorithmException
     * @see InvalidKeySpecException
     */
    private void keyToHexString() {
        try {
            KeyFactory _keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec _rsaPublicKeySpec = _keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);

            publicKeyModulus = _rsaPublicKeySpec.getModulus().toString(16);
            publicKeyExponent = _rsaPublicKeySpec.getPublicExponent().toString(16);
            publicKeyString = Base64.encodeBase64URLSafeString(publicKey.getEncoded());

            RSAPrivateKeySpec _rsaPrivateKeySpec = _keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec.class);

            privateKeyModulus = _rsaPrivateKeySpec.getModulus().toString(16);
            privateKeyExponent = _rsaPrivateKeySpec.getPrivateExponent().toString(16);
            privateKeyString = Base64.encodeBase64URLSafeString(privateKey.getEncoded());
        } catch (Throwable e) {
            throw new CryptoException("계수 및 지수 생성 실패", e);
        }
    }
}
