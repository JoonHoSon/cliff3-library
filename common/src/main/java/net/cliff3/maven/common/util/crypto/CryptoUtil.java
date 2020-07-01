package net.cliff3.maven.common.util.crypto;

import static net.cliff3.maven.common.util.crypto.CryptoUtil.Transformation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Optional;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

/**
 * CryptoUtil
 *
 * @author JoonHo Son
 * @version 1.0.0 2019-12-13 최초 작성
 * @since 1.0.0
 */
@Slf4j
public class CryptoUtil {
    public enum Transformation {
        /**
         * RSA/ECB/PKCS1Padding
         */
        RSA_ECB_PKCS1PADDING("RSA/ECB/PKCS1Padding"),

        AES_CBC_PKCS5PADDING("AES/CBC/PKCS5Padding"),

        /**
         * {@link #RSA_ECB_PKCS1PADDING} 동일
         */
        RSA(Transformation.RSA_ECB_PKCS1PADDING.getTransformation());

        @Getter
        private String transformation;

        Transformation(String transformation) {
            this.transformation = transformation;
        }
    }

    /**
     * 공개키, 비밀키
     */
    public enum LoadKeyType {
        /**
         * 공개키
         */
        PUBLIC_KEY,

        /**
         * 비밀키
         */
        PRIVATE_KEY
    }

    /**
     * 기본 인코딩/디코딩 {@link Charset}
     */
    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    /**
     * Algorithm(RSA)
     */
    private static final String ALGORITHM_RSA = "RSA";

    /**
     * 운용모드(CBC)
     */
    private static final String OPERATION_MODE_CBC = "CBC";

    /**
     * 운용모드(ECB)
     */
    private static final String OPERATION_MODE_ECB = "ECB";

    /**
     * Padding 방식(PKCSS5Padding)
     */
    private static final String PADDING_PKCS5PADDING = "PKCS5Padding";

    /**
     * Padding 방식(PKCS1Padding)
     */
    private static final String PADDING_PKCS1PADDING = "PKCS1Padding";

    /**
     * Padding 방식(없음)
     */
    private static final String PADDING_NONE = "NoPadding";

    /**
     * 기본 반복 횟수
     */
    private static final int DEFAULT_REPEAT_COUNT = 1000;

    /**
     * 128 키 사이즈
     */
    private static final int KEY_128 = 128;

    /**
     * 256 키 사이즈
     */
    private static final int KEY_256 = 256;

    /**
     * SHA256 hash 처리. 대상 문자열이 없을 경우 {@link Optional#empty()}를 반환하며 {@link NoSuchAlgorithmException}
     * 발생시 {@link CryptoException} 예외를 발생시킨다.
     *
     * @param target      Hash 처리 대상 문자열
     * @param salt        salt
     * @param repeatCount 반복 횟수
     *
     * @return Hash 처리된 결과
     * @throws CryptoException {@link NoSuchAlgorithmException} 예외 발생시
     */
    public static Optional<byte[]> makeSHA256Hash(String target, byte[] salt, int repeatCount) {
        return Optional.ofNullable(target).filter(StringUtils::isNotBlank).map(t -> {
            try {
                MessageDigest _digest = MessageDigest.getInstance("SHA-256");

                _digest.reset();

                byte[] _convertedTarget = _digest.digest(t.getBytes(UTF_8));

                if (salt != null) {
                    _digest.update(salt);

                    for (int i = 0; i < repeatCount; i++) {
                        _digest.reset();

                        _convertedTarget = _digest.digest(_convertedTarget);
                    }
                } else {
                    for (int i = 0; i < repeatCount; i++) {
                        _digest.reset();

                        _convertedTarget = _digest.digest(_convertedTarget);
                    }
                }

                return Optional.of(_convertedTarget);
            } catch (NoSuchAlgorithmException e) {
                throw new CryptoException("SHA256 암호화 오류", e);
            }
        }).orElse(Optional.empty());
    }

    /**
     * {@link #makeSHA256Hash(String, byte[], int)} 참고. 반복횟수는 {@link #DEFAULT_REPEAT_COUNT}로 설정된다.
     *
     * @param target Hash 처리 대상 문자열
     *
     * @return Hash 처리된 결과
     * @throws CryptoException {@link NoSuchAlgorithmException} 예외 발생시
     * @see #makeSHA256Hash(String, byte[], int)
     */
    public static Optional<byte[]> makeSHA256Hash(String target) {
        return makeSHA256Hash(target, generateSalt(), DEFAULT_REPEAT_COUNT);
    }

    /**
     * {@link #makeSHA256Hash(String, byte[], int)} 참고. 반복횟수는 {@link #DEFAULT_REPEAT_COUNT}로 설정된다.
     *
     * @param target Hash 처리 대상 문자열
     * @param salt   salt
     *
     * @return Hash 처리된 결과
     * @throws CryptoException {@link NoSuchAlgorithmException} 예외 발생시
     * @see #makeSHA256Hash(String, byte[], int)
     */
    public static Optional<byte[]> makeSHA256Hash(String target, byte[] salt) {
        return makeSHA256Hash(target, salt, DEFAULT_REPEAT_COUNT);
    }

    /**
     * AES128 암호화. {@link #encryptAES(String, String, int, byte[])} 참고.
     *
     * @param target 대상 문자열
     * @param secret 암호화키
     *
     * @return 처리 결과
     * @throws CryptoException 암호화 처리 중 발생
     */
    public static Optional<AESCrypto> encryptAES128(String target, String secret) {
        return encryptAES(target, secret, KEY_128, generateSalt());
    }

    /**
     * AES128 암호화. {@link #encryptAES(String, String, int, byte[])} 참고.
     *
     * @param target 대상 문자열
     * @param secret 암호화키
     * @param salt   salt
     *
     * @return 처리 결과
     * @throws CryptoException 암호화 처리 중 발생
     */
    public static Optional<AESCrypto> encryptAES128(String target, String secret, byte[] salt) {
        return encryptAES(target, secret, KEY_128, salt);
    }

    /**
     * AES256 암호화. {@link #encryptAES(String, String, int, byte[])} 참고.
     *
     * @param target 대상 문자열
     * @param secret 암호화키
     *
     * @return 처리 결과
     * @throws CryptoException 암호화 처리 중 발생
     */
    public static Optional<AESCrypto> encryptAES256(String target, String secret) {
        return encryptAES(target, secret, KEY_256, generateSalt());
    }

    /**
     * AES256 암호화. {@link #encryptAES(String, String, int, byte[])} 참고.
     *
     * @param target 대상 문자열
     * @param secret 암호화키
     * @param salt   salt
     *
     * @return 처리 결과
     * @throws CryptoException 암화화 처리 중 발생
     */
    public static Optional<AESCrypto> encryptAES256(String target, String secret, byte[] salt) {
        return encryptAES(target, secret, KEY_256, salt);
    }

    /**
     * {@link Base64} 클래스를 이용하여 인코딩된 문자열에 대한 AES128 복호화 처리.
     * {@link #decryptAES(byte[], String, byte[], byte[], int)} 참고
     *
     * @param encodedByBase64 복호화 대상
     * @param secret          암호화키
     * @param ivKey           initial vector
     * @param salt            salt
     *
     * @return 처리 결과
     * @throws CryptoException 복호화 처리시
     */
    public static Optional<byte[]> decryptAES128ByBase64(String encodedByBase64,
                                                         String secret,
                                                         byte[] ivKey,
                                                         byte[] salt) {
        return decryptAES(Base64.decodeBase64(encodedByBase64), secret, ivKey, salt, KEY_128);
    }

    /**
     * {@link #decryptAES(byte[], String, byte[], byte[], int)} 참고
     *
     * @param target 복호화 대상
     * @param secret 암호화키
     * @param ivKey  initial vector
     * @param salt   salt
     *
     * @return 처리 결과
     * @throws CryptoException 복호화 처리시
     */
    public static Optional<byte[]> decryptAES128(byte[] target, String secret, byte[] ivKey, byte[] salt) {
        return decryptAES(target, secret, ivKey, salt, KEY_128);
    }

    /**
     * {@link #decryptAES(byte[], String, byte[], byte[], int)} 참고
     *
     * @param target 복호화 대상
     * @param secret 암호화키
     * @param ivKey  initial vector
     * @param salt   salt
     *
     * @return 처리 결과
     * @throws CryptoException 복호화 처리시
     */
    public static Optional<byte[]> decryptAES256(String target, String secret, byte[] ivKey, byte[] salt) {
        return decryptAES(target.getBytes(UTF_8), secret, ivKey, salt, KEY_256);
    }

    /**
     * {@link Base64} 클래스를 이용하여 인코딩된 문자열에 대한 AES256 복호화 처리.
     * {@link #decryptAES(byte[], String, byte[], byte[], int)} 참고
     *
     * @param encodedByBase64 복호화 대상
     * @param secret          암호화키
     * @param ivKey           initial vector
     * @param salt            salt
     *
     * @return 처리 결과
     * @throws CryptoException 복호화 처리시
     */
    public static Optional<byte[]> decryptAES256ByBase64(String encodedByBase64,
                                                         String secret,
                                                         byte[] ivKey,
                                                         byte[] salt) {
        return decryptAES(Base64.decodeBase64(encodedByBase64), secret, ivKey, salt, KEY_256);
    }

    /**
     * {@link #decryptAES(byte[], String, byte[], byte[], int)} 참고
     *
     * @param target 복호화 대상
     * @param secret 암호화키
     * @param ivKey  initial vector
     * @param salt   salt
     *
     * @return 처리 결과
     * @throws CryptoException 복호화 처리시
     */
    public static Optional<byte[]> decryptAES256(byte[] target, String secret, byte[] ivKey, byte[] salt) {
        return decryptAES(target, secret, ivKey, salt, KEY_256);
    }

    /**
     * RSA key pair 생성
     *
     * @return {@link KeyPair}
     */
    public static KeyPair generateRSAKeyPair() {
        try {
            final int keySize = 2048;
            SecureRandom _random = new SecureRandom();
            KeyPairGenerator _generator = KeyPairGenerator.getInstance(ALGORITHM_RSA);

            _generator.initialize(keySize, _random);

            return _generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("키쌍 생성 실패", e);
        }
    }

    /**
     * RSA 암호화 처리
     *
     * @param target    암호화 대상
     * @param publicKey 공개키
     *
     * @return 암호화 결과
     * @throws CryptoException 암호화 처리시
     * @see NoSuchAlgorithmException
     * @see NoSuchPaddingException
     * @see InvalidKeySpecException
     * @see InvalidKeyException
     * @see IllegalBlockSizeException
     * @see BadPaddingException
     */
    public static Optional<byte[]> encryptRSA(byte[] target, byte[] publicKey) {
        return Optional.ofNullable(target).map(t -> {
            try {
                Cipher _cipher = Cipher.getInstance(RSA.getTransformation());

                _cipher.init(Cipher.ENCRYPT_MODE, loadKey(publicKey, LoadKeyType.PUBLIC_KEY));

                return Optional.of(_cipher.doFinal(target));
            } catch (Throwable e) {
                throw new CryptoException("RSA 암호화 오류", e);
            }
        }).orElse(Optional.empty());
    }

    /**
     * {@link #encryptRSA(byte[], byte[])} 참고
     *
     * @param target        암호화 대상
     * @param makeHexString 공개/비공개 키의 계수, 지수 및 {@link Base64#encodeBase64URLSafeString(byte[])} 생성 여부
     *
     * @return 암호화 결과
     * @see RSAKeySet
     * @see #generateRSAKeyPair()
     */
    public static Optional<RSAKeySet> encryptRSA(byte[] target, boolean makeHexString) {
        KeyPair _keyPair = generateRSAKeyPair();
        Optional<byte[]> _encrypted = encryptRSA(target, _keyPair.getPublic().getEncoded());

        if (_encrypted.isPresent()) {
            RSAKeySet _result = new RSAKeySet(_keyPair.getPublic(), _keyPair.getPrivate(), true);

            _result.setEncryptedValue(_encrypted.get());
            return Optional.of(_result);
        } else {
            return Optional.empty();
        }
    }

    /**
     * RSA 복호화 처리
     *
     * @param target     복호화 대상
     * @param privateKey 비밀키
     *
     * @return 복호화 결과
     * @throws CryptoException 복호화 실패시
     * @see NoSuchAlgorithmException
     * @see NoSuchPaddingException
     * @see InvalidKeySpecException
     * @see InvalidKeyException
     * @see IllegalBlockSizeException
     * @see BadPaddingException
     */
    public static Optional<byte[]> decryptRSA(byte[] target, byte[] privateKey) {
        return Optional.ofNullable(target).map(t -> {
            try {
                Cipher _cipher = Cipher.getInstance(RSA.getTransformation());
                Key _privateKey = loadKey(privateKey, LoadKeyType.PRIVATE_KEY);

                _cipher.init(Cipher.DECRYPT_MODE, _privateKey);

                return Optional.of(_cipher.doFinal(target));
            } catch (Throwable e) {
                throw new CryptoException("RSA 복호화 실패", e);
            }
        }).orElse(Optional.empty());
    }

    /**
     * {@link #decryptRSA(byte[], byte[])} 참고
     *
     * @param target 복호화 대상
     * @param keySet 비밀키 정보를 포함하는 {@link RSAKeySet}
     *
     * @return 복호화 결과
     * @throws CryptoException 복호화 실패시
     */
    public static Optional<byte[]> decryptRSA(byte[] target, RSAKeySet keySet) {
        return decryptRSA(target, keySet.getPrivateKey().getEncoded());
    }

    /**
     * 임의의  salt 생성
     *
     * @return salt
     */
    public static byte[] generateSalt() {
        SecureRandom _random = new SecureRandom();
        byte[] _bytes = new byte[30];

        _random.nextBytes(_bytes);

        return _bytes;
    }

    //------------------------------------------------------------------------------------------------------------------
    // private function
    //------------------------------------------------------------------------------------------------------------------

    /**
     * AES 암호화 처리. 인자로 지정된 keySize 기반으로 암호화 처리를 하며, 해당 결과는 {@link AESCrypto} 인스턴스로 반환한다.
     * 대상 문자열이 존재하지 않을 경우 {@link Optional#empty()}를 반환한다.
     *
     * @param target  대상 문자열
     * @param secret  암호화키
     * @param keySize 키 사이즈
     * @param salt    salt
     *
     * @return 처리 결과
     * @throws CryptoException 암호화 처리 중 발생
     * @see AESCrypto
     * @see java.io.UnsupportedEncodingException
     * @see NoSuchAlgorithmException
     * @see java.security.spec.InvalidKeySpecException
     * @see javax.crypto.NoSuchPaddingException
     * @see java.security.InvalidKeyException
     * @see java.security.spec.InvalidParameterSpecException
     * @see javax.crypto.BadPaddingException
     * @see javax.crypto.IllegalBlockSizeException
     */
    private static Optional<AESCrypto> encryptAES(String target, String secret, int keySize, byte[] salt) {
        return Optional.ofNullable(target).filter(StringUtils::isNotBlank).map(t -> {
            try {
                SecretKeyFactory _factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                KeySpec _keySpec = new PBEKeySpec(secret.toCharArray(),
                                                  salt,
                                                  DEFAULT_REPEAT_COUNT,
                                                  keySize);
                SecretKey _secretKey = _factory.generateSecret(_keySpec);
                SecretKeySpec _secretKeySpec = new SecretKeySpec(_secretKey.getEncoded(), "AES");
                Cipher _cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING.getTransformation());

                _cipher.init(Cipher.ENCRYPT_MODE, _secretKeySpec);

                AlgorithmParameters _parameters = _cipher.getParameters();
                byte[] _ivBytes = _parameters.getParameterSpec(IvParameterSpec.class).getIV();
                byte[] encryptedBytes = _cipher.doFinal(target.getBytes(UTF_8));

                return Optional.of(new AESCrypto(salt, encryptedBytes, _ivBytes));
            } catch (Throwable e) {
                throw new CryptoException("AES암호화 오류", e);
            }
        }).orElse(Optional.empty());
    }

    /**
     * AES 복호화 처리. 복호화 결과를 {@code byte[]} 형태로 반환한다.
     *
     * @param target  복호화 대상
     * @param secret  암호화키
     * @param ivBytes initial vector
     * @param salt    salt
     * @param keySize 키 사이즈
     *
     * @return 처리 결과
     * @throws CryptoException 복호화 처리시
     * @see NoSuchAlgorithmException
     * @see InvalidKeySpecException
     * @see NoSuchPaddingException
     * @see InvalidKeyException
     * @see java.security.InvalidAlgorithmParameterException
     */
    private static Optional<byte[]> decryptAES(byte[] target, String secret, byte[] ivBytes, byte[] salt, int keySize) {
        return Optional.ofNullable(target).map(t -> {
            try {
                SecretKeyFactory _factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
                PBEKeySpec _spec = new PBEKeySpec(secret.toCharArray(), salt, DEFAULT_REPEAT_COUNT, keySize);
                SecretKey _secretKey = _factory.generateSecret(_spec);
                SecretKeySpec _secretKeySpec = new SecretKeySpec(_secretKey.getEncoded(), "AES");
                Cipher _cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING.getTransformation());

                _cipher.init(Cipher.DECRYPT_MODE, _secretKeySpec, new IvParameterSpec(ivBytes));

                return Optional.of(_cipher.doFinal(target));
            } catch (Throwable e) {
                throw new CryptoException("AES복호화 오류", e);
            }
        }).orElseGet(Optional::empty);
    }

    /**
     * 주어진 바이트 배열을 종류({@link LoadKeyType})에 따라 해당 키로 변환하여 반환
     *
     * @param key  키 바이트 배열
     * @param type {@link LoadKeyType}
     *
     * @return {@link Key}
     * @throws NoSuchAlgorithmException 허용되지 않는 알로리즘 지정시
     * @throws InvalidKeySpecException  Key spec 지정 오류
     */
    private static Key loadKey(byte[] key, LoadKeyType type) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory _keyFactory = KeyFactory.getInstance(ALGORITHM_RSA);
        EncodedKeySpec _encodedKeySpec;

        if (LoadKeyType.PUBLIC_KEY == type) {
            _encodedKeySpec = new X509EncodedKeySpec(key);

            return _keyFactory.generatePublic(_encodedKeySpec);
        } else {
            _encodedKeySpec = new PKCS8EncodedKeySpec(key);

            return _keyFactory.generatePrivate(_encodedKeySpec);
        }
    }
}
