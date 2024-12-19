package com.keshaparrot.gptorganizier.service;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class JsonSecurityUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String KEY_ALIAS = "MyAppKeyAlias";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";

    private final KeyStore keyStore;

    public JsonSecurityUtils() throws Exception {
        this.keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        this.keyStore.load(null);

        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey();
        }
    }

    private void generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
        KeyGenParameterSpec keySpec = new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();

        keyGenerator.init(keySpec);
        keyGenerator.generateKey();
    }

    private SecretKey getKey() throws Exception {
        KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null);
        return keyEntry.getSecretKey();
    }

    public String encrypt(Object data) throws Exception {
        Gson gson = new Gson();
        String json = gson.toJson(data);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getKey());

        byte[] iv = cipher.getIV();
        byte[] encryptedBytes = cipher.doFinal(json.getBytes());

        byte[] encryptedDataWithIv = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, encryptedDataWithIv, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, encryptedDataWithIv, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(encryptedDataWithIv);
    }

    public <T> T decrypt(String encryptedData, Class<T> clazz) throws Exception {
        String decryptedJson = decryptData(encryptedData);
        return new Gson().fromJson(decryptedJson, clazz);
    }

    public <T> T decrypt(String encryptedData, Type type) throws Exception {
        String decryptedJson = decryptData(encryptedData);
        return new Gson().fromJson(decryptedJson, type);
    }

    private String decryptData(String encryptedData) throws Exception {
        byte[] encryptedDataWithIv = Base64.getDecoder().decode(encryptedData);

        byte[] iv = Arrays.copyOfRange(encryptedDataWithIv, 0, 12);
        byte[] encryptedBytes = Arrays.copyOfRange(encryptedDataWithIv, 12, encryptedDataWithIv.length);

        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);

        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec);
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes);
    }
}
