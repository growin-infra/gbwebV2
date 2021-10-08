package gb.common.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import org.apache.commons.codec.binary.Base64;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class EncryptUtil {

	static String iv = "0987654321654321";
	static String key = "1234567890123456";
	
	public static Key getAESKey() throws Exception {
	    Key keySpec;

	    iv = key.substring(0, 16);
	    byte[] keyBytes = new byte[16];
	    byte[] b = key.getBytes("UTF-8");

	    int len = b.length;
	    if (len > keyBytes.length) {
	       len = keyBytes.length;
	    }

	    System.arraycopy(b, 0, keyBytes, 0, len);
	    keySpec = new SecretKeySpec(keyBytes, "AES");

	    return keySpec;
	}

	// 암호화
	public static String enc(String str) throws Exception {
	    Key keySpec = getAESKey();
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
	    byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
	    String enStr = new String(Base64.encodeBase64(encrypted));

	    return enStr;
	}

	// 복호화
	public static String dec(String enStr) throws Exception {
	    Key keySpec = getAESKey();
	    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
	    c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes("UTF-8")));
	    byte[] byteStr = Base64.decodeBase64(enStr.getBytes("UTF-8"));
	    String decStr = new String(c.doFinal(byteStr), "UTF-8");

	    return decStr;
	}

	
	
	
	
	
	
	
	
	
	
	
	// Type1

	// MD5
	public static String getMD5(String str) {

		String rtnMD5 = "";
		try {
			// MessageDigest 인스턴스 생성
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 해쉬값 업데이트
			md.update(str.getBytes());
			// 해쉬값(다이제스트) 얻기
			byte byteData[] = md.digest();

			StringBuffer sb = new StringBuffer();

			// 출력
			for (byte byteTmp : byteData) {
				sb.append(Integer.toString((byteTmp & 0xff) + 0x100, 16).substring(1));
			}
			rtnMD5 = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			rtnMD5 = null;
		}
		return rtnMD5;
	}

	// SHA-256
	public static String getSHA256(String str) {
		String rtnSHA = "";

		try {
			MessageDigest sh = MessageDigest.getInstance("SHA-256");
			sh.update(str.getBytes());
			byte byteData[] = sh.digest();
			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			rtnSHA = sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			rtnSHA = null;
		}
		return rtnSHA;
	}

	// Type2. BigInteger를 이용하여 구현.

	public static String getEncryptMD5(String a_origin) throws UnsupportedEncodingException {
		String encryptedMD5 = "";
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("MD5");
			// md.update(a_origin.getBytes());
			md.update(a_origin.getBytes(), 0, a_origin.getBytes().length);
			byte byteData[] = md.digest();
			encryptedMD5 = new BigInteger(1, byteData).toString(16);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return encryptedMD5;
	}

	public static String getEncryptSHA256(String a_origin) {
		String encryptedSHA256 = "";
		MessageDigest md = null;

		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(a_origin.getBytes(), 0, a_origin.length());
			encryptedSHA256 = new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return encryptedSHA256;
	}

	public static String encrypt(String str) throws Exception {
		String retVal = "";
		try {
			String mstrKey = "etniisbe";
			byte[] buffer = str.getBytes();
			SecretKey key = new SecretKeySpec(mstrKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			BASE64Encoder encoder = new BASE64Encoder();
			retVal = encoder.encode(cipher.doFinal(buffer));
		} catch (Exception e) {
			throw e;
		}
		return retVal;
	}

	public static String decrypt(String str) throws Exception {
		String retVal = "";
		try {
			String mstrKey = "etniisbe";
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] buffer = decoder.decodeBuffer(str);
			SecretKey key = new SecretKeySpec(mstrKey.getBytes(), "DES");
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, key);
			retVal = new String(cipher.doFinal(buffer));
		} catch (Exception e) {
			throw e;
		}
		return retVal;
	}

}// class
