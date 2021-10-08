package gb.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("unused")
public class CryptUtil {
	
    private static Key key = null;


	private static final int BUFFER_SIZE = 8192;
	private static final String separator = System.getProperty("file.separator");
//    private static String DEFAULT_KEY = Thread.currentThread().getContextClassLoader().getResource("config"+separator+"key"+separator+Constants.DEFAULT_KEY).getFile();
    private static String DEFAULT_KEY = null;
    
    /**
     * 비밀키 파일 생성 메소드
     * 
     * @return 비밀키 파일
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    public static File makeKey() throws IOException, NoSuchAlgorithmException {
        return makeKey(DEFAULT_KEY);
    }

    public static File makeKey(String filename) throws IOException, NoSuchAlgorithmException {
        File file = new File(filename);
        KeyGenerator generator = KeyGenerator.getInstance("DES");
        generator.init(new SecureRandom());
        Key key = generator.generateKey();
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
        out.writeObject(key);
        out.close();
        return file;
    }

    /**
     * 지정된 비밀키를 가지고 오는 메서드
     * 
     * @return 비밀키
     * @throws Exception
     */
    private static Key getKey() throws Exception {
        if (key != null) {
            return key;
        } else {
            return getKey(DEFAULT_KEY);
        }
    }

    private static Key getKey(String filename) throws Exception {
        if (key == null) {
            File file = new File(filename);
            if (!file.exists()) {
                file = makeKey();
            }
            if (file.exists()) {
                ObjectInputStream in =
                    new ObjectInputStream(new FileInputStream(filename));
                key = (Key) in.readObject();
                in.close();
            } else {
                throw new Exception("암호키 객체를 생성할 수 없습니다.");
            }
        }
        return key;
    }

    /**
     * 문자열 대칭 암호화
     * 
     * @param strVal 암호화할 문자열
     * @return 암호화된 문자열
     * @throws Exception
     */
    public static String encrypt(String strVal) throws Exception {
        if (strVal == null || strVal.length() == 0)
            return "";
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getKey());
        String amalgam = strVal;

        byte[] inputBytes1 = amalgam.getBytes("UTF-8");
        byte[] outputBytes1 = cipher.doFinal(inputBytes1);
        BASE64Encoder encoder = new BASE64Encoder();
        String outputStr1 = encoder.encode(outputBytes1);
        return outputStr1;
    }
    
    /**
     * 문자열 대칭 암호화
     * 
     * @param strVal 암호화할 문자열
     * @return 암호화된 문자열
     * @throws Exception
     */
    public static String encrypt(String strVal, int cnt) throws Exception {
    	if (strVal == null || strVal.length() == 0)
    		return "";
    	Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
    	cipher.init(Cipher.ENCRYPT_MODE, getKey());
    	String amalgam = strVal;
    	
    	byte[] inputBytes1 = amalgam.getBytes("UTF-8");
    	byte[] outputBytes1 = cipher.doFinal(inputBytes1);
    	BASE64Encoder encoder = new BASE64Encoder();
    	String outputStr1 = encoder.encode(outputBytes1);
    	for (int i=0; i<cnt; i++) {
    		outputStr1 = encrypt(outputStr1);
    	}
    	return outputStr1;
    }

    /**
     * 문자열 대칭 복호화
     * 
     * @param strVal 복호화할 문자열
     * @return 복호화된 문자열
     * @throws Exception
     */
    public static String decrypt(String strVal) throws Exception {
        if (strVal == null || strVal.length() == 0)
            return "";
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, getKey());
        BASE64Decoder decoder = new BASE64Decoder();

        byte[] inputBytes1 = decoder.decodeBuffer(strVal);
        byte[] outputBytes2 = cipher.doFinal(inputBytes1);

        String strResult = new String(outputBytes2, "UTF-8");
        return strResult;
    }
    
    /**
     * 문자열 대칭 복호화
     * 
     * @param strVal 복호화할 문자열
     * @return 복호화된 문자열
     * @throws Exception
     */
    public static String decrypt(String strVal, int cnt) throws Exception {
    	String strResult = strVal;
    	for (int i=0; i<cnt; i++) {
    		strResult = decrypt(strResult);
    	}
    	return strResult;
    }
}
