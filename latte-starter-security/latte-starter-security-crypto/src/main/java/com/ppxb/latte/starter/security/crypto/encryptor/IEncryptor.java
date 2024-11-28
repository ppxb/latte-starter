package com.ppxb.latte.starter.security.crypto.encryptor;

public interface IEncryptor {

    String encrypt(String plaintext, String password, String publicKey) throws Exception;

    String decrypt(String ciphertext, String password, String privateKey) throws Exception;
}
