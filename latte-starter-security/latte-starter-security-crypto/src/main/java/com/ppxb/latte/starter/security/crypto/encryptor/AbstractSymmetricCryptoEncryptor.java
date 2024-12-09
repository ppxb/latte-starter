package com.ppxb.latte.starter.security.crypto.encryptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import com.ppxb.latte.starter.core.constant.StringConstants;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSymmetricCryptoEncryptor implements IEncryptor {

    private static final Map<String, SymmetricCrypto> CACHE = new ConcurrentHashMap<>();

    @Override
    public String encrypt(String plaintext, String password, String publicKey) throws Exception {
        if (CharSequenceUtil.isBlank(plaintext)) {
            return plaintext;
        }
        return this.getCrypto(password).encryptHex(plaintext);
    }

    @Override
    public String decrypt(String ciphertext, String password, String privateKey) throws Exception {
        if (CharSequenceUtil.isBlank(ciphertext)) {
            return ciphertext;
        }
        return this.getCrypto(password).decryptStr(ciphertext);
    }

    protected SymmetricCrypto getCrypto(String password) {
        SymmetricAlgorithm algorithm = this.getAlgorithm();
        String key = algorithm + StringConstants.UNDERLINE + password;
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        SymmetricCrypto symmetricCrypto = new SymmetricCrypto(algorithm, password.getBytes(StandardCharsets.UTF_8));
        CACHE.put(key, symmetricCrypto);
        return symmetricCrypto;
    }

    protected abstract SymmetricAlgorithm getAlgorithm();

//    TODO: CONTINUE ENCRYPTORS
}
