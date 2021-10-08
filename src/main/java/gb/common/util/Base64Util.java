package gb.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Base64Util {
	
	Logger log = Logger.getLogger(this.getClass());

	public String encode(String str){
		byte[] encoded = Base64.encodeBase64(str.getBytes());
		return new String(encoded);
	}
	
	public String decode(String str){
		byte[] decoded = Base64.decodeBase64(str);
		return new String(decoded);
	}
	
	public String encode(String str, int l){
		String strResult = str;
    	for (int i=0; i<l; i++) {
    		strResult = encode(strResult);
    	}
		return strResult;
	}
	
	public String decode(String str, int l){
		String strResult = str;
    	for (int i=0; i<l; i++) {
    		strResult = decode(strResult);
    	}
		return strResult;
	}
	
}
