package com.tencent.qcloud.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by bradyxiao on 2017/7/21.
 * author bradyxiao
 */
public class QCDigestUtils {
    private static final int STREAM_BUFFER_LENGTH = 1024;

    private static byte[] digest(MessageDigest digest, InputStream data) throws IOException {
        return updateDigest(digest, data).digest();
    }

    public static MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException var2) {
            throw new IllegalArgumentException(var2);
        }
    }

    public static MessageDigest getMd2Digest() {
        return getDigest("MD2");
    }

    public static MessageDigest getMd5Digest() {
        return getDigest("MD5");
    }

    public static MessageDigest getSha1Digest() {
        return getDigest("SHA-1");
    }

    public static MessageDigest getSha256Digest() {
        return getDigest("SHA-256");
    }

    public static MessageDigest getSha384Digest() {
        return getDigest("SHA-384");
    }

    public static MessageDigest getSha512Digest() {
        return getDigest("SHA-512");
    }

    public static byte[] md2(byte[] data) {
        return getMd2Digest().digest(data);
    }

    public static byte[] md2(InputStream data) throws IOException {
        return digest(getMd2Digest(), data);
    }

    public static byte[] md2(String data) {
        return md2(QCStringUtils.getBytesUTF8(data));
    }

    public static String md2Hex(byte[] data) {
        return QCHexUtils.encodeHexString(md2(data));
    }

    public static String md2Hex(InputStream data) throws IOException {
        return QCHexUtils.encodeHexString(md2(data));
    }

    public static String md2Hex(String data) {
        return QCHexUtils.encodeHexString(md2(data));
    }

    public static byte[] md5(byte[] data) {
        return getMd5Digest().digest(data);
    }

    public static byte[] md5(InputStream data) throws IOException {
        return digest(getMd5Digest(), data);
    }

    public static byte[] md5(String data) {
        return md5(QCStringUtils.getBytesUTF8(data));
    }

    public static String md5Hex(byte[] data) {
        return QCHexUtils.encodeHexString(md5(data));
    }

    public static String md5Hex(InputStream data) throws IOException {
        return QCHexUtils.encodeHexString(md5(data));
    }

    public static String md5Hex(String data) {
        return QCHexUtils.encodeHexString(md5(data));
    }

    public static byte[] sha1(byte[] data) {
        return getSha1Digest().digest(data);
    }

    public static byte[] sha1(InputStream data) throws IOException {
        return digest(getSha1Digest(), data);
    }

    public static byte[] sha1(String data) {
        return sha1(QCStringUtils.getBytesUTF8(data));
    }

    public static String sha1Hex(byte[] data) {
        return QCHexUtils.encodeHexString(sha1(data));
    }

    public static String sha1Hex(InputStream data) throws IOException {
        return QCHexUtils.encodeHexString(sha1(data));
    }

    public static String sha1Hex(String data) {
        return QCHexUtils.encodeHexString(sha1(data));
    }

    public static byte[] sha256(byte[] data) {
        return getSha256Digest().digest(data);
    }

    public static byte[] sha256(InputStream data) throws IOException {
        return digest(getSha256Digest(), data);
    }

    public static byte[] sha256(String data) {
        return sha256(QCStringUtils.getBytesUTF8(data));
    }

    public static String sha256Hex(byte[] data) {
        return QCHexUtils.encodeHexString(sha256(data));
    }

    public static String sha256Hex(InputStream data) throws IOException {
        return QCHexUtils.encodeHexString(sha256(data));
    }

    public static String sha256Hex(String data) {
        return QCHexUtils.encodeHexString(sha256(data));
    }

    public static byte[] sha384(byte[] data) {
        return getSha384Digest().digest(data);
    }

    public static byte[] sha384(InputStream data) throws IOException {
        return digest(getSha384Digest(), data);
    }

    public static byte[] sha384(String data) {
        return sha384(QCStringUtils.getBytesUTF8(data));
    }

    public static String sha384Hex(byte[] data) {
        return QCHexUtils.encodeHexString(sha384(data));
    }

    public static String sha384Hex(InputStream data) throws IOException {
        return QCHexUtils.encodeHexString(sha384(data));
    }

    public static String sha384Hex(String data) {
        return QCHexUtils.encodeHexString(sha384(data));
    }

    public static byte[] sha512(byte[] data) {
        return getSha512Digest().digest(data);
    }

    public static byte[] sha512(InputStream data) throws IOException {
        return digest(getSha512Digest(), data);
    }

    public static byte[] sha512(String data) {
        return sha512(QCStringUtils.getBytesUTF8(data));
    }

    public static String sha512Hex(byte[] data) {
        return QCHexUtils.encodeHexString(sha512(data));
    }

    public static String sha512Hex(InputStream data) throws IOException {
        return QCHexUtils.encodeHexString(sha512(data));
    }

    public static String sha512Hex(String data) {
        return QCHexUtils.encodeHexString(sha512(data));
    }

    public static MessageDigest updateDigest(MessageDigest messageDigest, byte[] valueToDigest) {
        messageDigest.update(valueToDigest);
        return messageDigest;
    }

    public static MessageDigest updateDigest(MessageDigest digest, InputStream data) throws IOException {
        byte[] buffer = new byte[1024];

        for(int read = data.read(buffer, 0, 1024); read > -1; read = data.read(buffer, 0, 1024)) {
            digest.update(buffer, 0, read);
        }

        return digest;
    }

    public static MessageDigest updateDigest(MessageDigest messageDigest, String valueToDigest) {
        messageDigest.update(QCStringUtils.getBytesUTF8(valueToDigest));
        return messageDigest;
    }
}
