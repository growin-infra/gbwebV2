package gb.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

public class test {

	public static void main(String[] args) {
		
	
		
//		System.out.println("encode2 20.7.31["+Base64Util.encode("SKhynix_1_2019.08.01_2020.07.31", 3)+"]");
//		System.out.println("encode2 21.7.31["+Base64Util.encode("SKhynix_1_2020.08.01_2021.07.31", 3)+"]");
//		System.out.println("encode2 22.7.31["+Base64Util.encode("SKhynix_1_2021.08.01_2022.07.31", 3)+"]");
//		System.out.println("encode2 22.7.31["+Base64Util.encode("LGanyang_1_2021.04.01_2022.03.31", 3)+"]");
//		
//		System.out.println("decode1["+Base64Util.decode("VlRCMGIyVlhOWEJsUmpoNFdIcEpkMDFxUlhWTlJHZDFUVVJHWmsxcVFYbE5hVFIzVG5rMGVrMVJQVDA9", 3)+"]");
//		System.out.println("decode2["+Base64Util.decode("VlRCMGIyVlhOWEJsUmpoNFdIcEpkMDFVYTNWTlJHZDFUVVJHWmsxcVFYbE5RelIzVG5rMGVrMVJQVDA9", 3)+"]");
//		System.out.println("decode3["+Base64Util.decode("VlRCMGIyVlhOWEJsUmpoNFdIcEpkMDFxUVhWTlJHZDFUVVJHWmsxcVFYbE5VelIzVG5rMGVrMVJQVDA9", 3)+"]");
		System.out.println("decode3["+Base64Util.decode("WXpJeGFHTnVVbnBaTWpsNVdsWTRlRmg2U1hkTlZHdDFUVVJSZFUxRVJtWk5ha0Y1VFVNMGQwMTVOSHBOVVQwOQ==", 3)+"]");
		
		System.out.println("SKhynix_1_2021.08.01_2022.07.31 encode["+Base64Util.encode("SKhynix_1_2021.08.01_2022.07.31", 3)+"]");
		System.out.println("SKhynix_1_2021.08.01_2022.07.31 decode["+Base64Util.decode("VlRCMGIyVlhOWEJsUmpoNFdIcEpkMDFxUlhWTlJHZDFUVVJHWmsxcVFYbE5hVFIzVG5rMGVrMVJQVDA9", 3)+"]");
		
		
	}
	
	static class Base64Util {
		
		public static String encode(String str){
			byte[] encoded = Base64.encodeBase64(str.getBytes());
			return new String(encoded);
		}
		
		public static String decode(String str){
			byte[] decoded = Base64.decodeBase64(str);
			return new String(decoded);
		}
		
		public static String encode(String str, int l){
			String strResult = str;
	    	for (int i=0; i<l; i++) {
	    		strResult = encode(strResult);
	    	}
			return strResult;
		}
		
		public static String decode(String str, int l){
			String strResult = str;
	    	for (int i=0; i<l; i++) {
	    		strResult = decode(strResult);
	    	}
			return strResult;
		}
		
	}

}
