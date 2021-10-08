package gb.common.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class StringUtil {

	private static Runtime runtime = Runtime.getRuntime();
	static Logger log = Logger.getLogger(StringUtil.class);

	/**
	 * 조회나, 수정form에 뿌려 줄때 값이 null이면 화면에 null이라고 찍히는 것을 없애기 위해사용한다
	 * 
	 * @param obj
	 *            대상 오브젝트
	 * @return String Null이 아닌 문자열
	 */
	public static String NVL(Object obj) {
		if (obj != null && !"null".equals(obj) && !"".equals(obj))
			return obj.toString().trim();
		else
			return "";
	}

	/**
	 * 값이 null이면 화면에 default값 리턴
	 * 
	 * @param obj
	 *            대상 오브젝트
	 * @param value
	 *            Null일 경우 대치할 문자열
	 * @return String Null이 아닌 문자열
	 */
	public static String NVL(Object obj, String value) {
		if (obj != null && !"null".equals(obj) && !"".equals(obj))
			return obj.toString();
		else
			return value;
	}

	/**
	 * 값이 null이면 화면에 default값 리턴
	 * 
	 * @param obj
	 *            대상 오브젝트
	 * @param value
	 *            Null일 경우 대치할 int타입의 정수값
	 * @return int Null이 아닌 정수값
	 */
	public static int NVL(Object obj, int value) {
		if (obj != null && !"null".equals(obj) && !"".equals(obj)) {
			try {
				return Integer.parseInt(obj.toString());
			} catch (NumberFormatException e) {
				return 0;
			}
		} else
			return value;
	}

	/**
	 * 값이 null이면 화면에 default값 리턴
	 * 
	 * @param obj
	 *            대상 오브젝트
	 * @param value
	 *            Null일 경우 대치할 double타입의 실수값
	 * @return double Null이 아닌 실수값
	 */
	public static double NVL(Object obj, double value) {
		if (obj != null && !"null".equals(obj) && !"".equals(obj)) {
			try {
				return Double.parseDouble(obj.toString());
			} catch (NumberFormatException e) {
				return 0;
			}
		} else
			return value;
	}

	/**
	 * 조회나, 수정form에 뿌려 줄때 값이 0이면 default값 리턴
	 * 
	 * @param obj
	 *            대상 오브젝트
	 * @param value
	 *            대체할 default 값
	 * @return String Null이 아닌 문자열
	 */
	public static String replaceZero(Object obj, String value) {
		if (obj != null && !"null".equals(obj) && !"".equals(obj) && "0".equals(obj))
			return obj.toString().trim();
		else
			return value;
	}

	/** 문자열 추가시의 방향(왼쪽) */
	public static final int CHAR_POSITION_LEFT = 1;
	/** 문자열 추가시의 방향(오른쪽) */
	public static final int CHAR_POSITION_RIGHT = 2;

	/**
	 * 입력문자열 좌/우 에 원하는 문자를 입력갯수 만큼 추가한후 반환한다.
	 * 
	 * @param str
	 *            원본데이터
	 * @param character
	 *            추가될문자
	 * @param pos
	 *            좌(1)/우(2)
	 * @param point
	 *            갯수
	 * @return String
	 * @throws Exception
	 */
	public static String makeChar(String str, String character, int pos, int point) {

		int len = str.getBytes().length;
		if (len >= point)
			return str;

		byte bytes[] = new byte[point];
		if (pos == CHAR_POSITION_RIGHT) {
			for (int i = 0; i < bytes.length; i++) {
				if (len > i)
					bytes[i] = str.getBytes()[i];
				else {
					bytes[i] = character.getBytes()[0];
				}
			}
		} else if (pos == CHAR_POSITION_LEFT) {
			for (int i = 0; i < bytes.length; i++) {
				if ((bytes.length - len) > i) {
					bytes[i] = character.getBytes()[0];
				} else {
					bytes[i] = str.getBytes()[i - (bytes.length - len)];
				}
			}
		}
		return new String(bytes, 0, bytes.length);

	}

	/**
	 * 입력문자열 좌/우 에 원하는 문자를 입력갯수 만큼 추가한후 반환한다.
	 * 
	 * @param num
	 *            원본데이터
	 * @param character
	 *            추가될문자
	 * @param pos
	 *            좌(1)/우(2)
	 * @param point
	 *            갯수
	 * @return String
	 * @throws Exception
	 */
	public static String makeChar(int num, String character, int pos, int point) {
		return makeChar(String.valueOf(num), character, pos, point);
	}

	/**
	 * 한글 문자를 2바이트 기준으로 자른다. 짤린문자열의 마지막 문자가 한글이면 포함되지 않는다. db컬럼이 2군데 이상이어서 나누어서
	 * 넣을때 필요함 ex) String a = "a한글b몇자cd일e?"; Iterator iter = byteSubstring(a,
	 * 3).listIterator(); while (iter.hasNext()) {
	 * System.out.println(iter.next()); } 결과는 : a한, 글b, 몇, 자c, d일, e?
	 * 
	 * @param data
	 *            원본 문자열
	 * @param offset
	 *            짜를 기준 수
	 * @return offset으로 짤린 수만큼 들어있는 어래이리스트
	 */
	public static List<String> byteSubstring(String data, int offset) {

		List<String> al = new ArrayList<String>();

		if (data == null || data.equals(""))
			return al;

		String b = data;
		int totalDataLen = data.getBytes().length;
		int offset2 = offset;
		int strIndex = 0;

		// 첫글자가 한글일때 offset == 1 이면 의미가 없음
		if (offset == 1)
			return al;

		while (true) {

			String c = "";

			if (totalDataLen < offset) {
				c = new String(b.getBytes(), strIndex, totalDataLen);
				offset2 = totalDataLen;
			} else {
				c = new String(b.getBytes(), strIndex, offset);
				offset2 = offset;

			}

			offset2 = c.getBytes().length;
			// if (Character.getType(c.charAt(c.length() - 1)) ==
			// Character.OTHER_SYMBOL)
			// offset2--;

			if (offset2 == 0)
				break;

			al.add(new String(c.getBytes(), 0, offset2));

			strIndex += offset2;
			totalDataLen -= offset2;

			if (totalDataLen == 0)
				break;
		}

		return al;

	}

	/**
	 * 문자열을 입력된 바이트 만큼 잘라서 반환한다. 만약 마지막 문자열이 한글 1byte 만 리턴될경우 이문자열은 포함되지 않는다.
	 * 
	 * @param data
	 *            원본문자열
	 * @param bytes
	 *            짜를 byte length
	 * @return String
	 */
	public static String byteCut(String data, int bytes) {
		if (data == null || data.length() == 0)
			return data;
		char c;
		int data_total_length_ = 0;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < data.length(); i++) {
			c = data.charAt(i);
			data_total_length_ += String.valueOf(c).getBytes().length;
			if (bytes < data_total_length_)
				break;
			buffer.append(c);
		}
		return buffer.toString();
	}

	/**
	 * 입력된 문자열을 구분자를 기준으로 잘라서 배열을 만들어 리턴. StringTokenizer/split 과는 달리 구분자 사이의 값이
	 * 없더라도 모두 배열로 만들어 리턴한다.
	 * 
	 * @param str
	 *            원본문자열
	 * @param div
	 *            구분자
	 * @return Object[]
	 * @throws ProFrameException
	 */
	public static Object[] toArray(String str, String div) {
		List<String> list = null;
		int index = -1;

		if (str == null || div == null)
			return null;

		list = new ArrayList<String>();

		while (true) {
			index = str.indexOf(div);
			if (index != -1) {
				list.add(str.substring(0, index));

				str = str.substring(index + div.length());
			} else {
				list.add(str);
				break;
			}
		}

		return list.toArray();
	}

	public static void printMemory() {
		printMemory(null);
	}

	public static void printMemory(String msg) {
		double max_memory;
		double total_memory;
		double free_memory;

		max_memory = runtime.maxMemory() / 1024;
		total_memory = runtime.totalMemory() / 1024;
		free_memory = runtime.freeMemory() / 1024;

		if (!"".equals(NVL(msg)))
		log.debug("### max : " + NumberUtil.round(max_memory / 1024, 2) + "mb " + "### total : "
				+ NumberUtil.round(total_memory / 1024, 2) + "mb " + "### free : "
				+ NumberUtil.round(free_memory / 1024, 2) + "mb " + "### use : "
				+ NumberUtil.round((total_memory - free_memory) / 1024, 2) + "mb ");

	}

	/**
	 * 반각문자로 변경한다
	 * 
	 * @param origin
	 *            변경할 값
	 * @return String 변경된 값
	 */
	public static String toHalfChar(String origin) {
		char incode;
		char target[] = new char[origin.length()];
		for (int i = 0; i < origin.length(); i++) {
			incode = origin.charAt(i);
			int tmpcod1 = incode & 0xff00;
			int tmpcod2 = incode & 0x00ff;
			int tmpcod3 = incode & 0xffff;
			if (tmpcod3 == 0x3000) {
				target[i] = 0x20; // 전각스페이스를 반각으로...
			} else if (tmpcod1 == 0xff00) {
				// 전각영문 또는 숫자를 반각 영문또는 숫자로..
				// 한글은 그대로...
				target[i] = (char) (tmpcod2 + 0x20);
			} else
				target[i] = (char) incode;
		}
		return (new String(target));
	}

	/**
	 * 전각문자로 변경한다.
	 * 
	 * @param src
	 *            변경할 값
	 * @return String 변경된 값
	 */
	public static String toFullChar(String src) {
		if (src == null)
			return null;
		// 변환된 문자들을 쌓아놓을 StringBuffer 를 마련한다
		StringBuffer strBuf = new StringBuffer();
		char c = 0;
		int nSrcLength = src.length();
		for (int i = 0; i < nSrcLength; i++) {
			c = src.charAt(i);
			// 영문이거나 특수 문자 일경우.
			if (c >= 0x21 && c <= 0x7e) { // 공백일경우
				c += 0xfee0;
			} else if (c == 0x20) {
				c = 0x3000;
			}
			// 문자열 버퍼에 변환된 문자를 쌓는다
			strBuf.append(c);
		}
		return strBuf.toString();
	}

	public static final int MASK_JUMIN = 1;
	public static final int MASK_ACCOUNT = 2;
	public static final int MASK_CARD = 3;

	public static String toMask(int maskType, String data) {
		if ("".equals(NVL(data)))
			return "";
		String retValue = "";
		if (maskType == MASK_JUMIN) {
			retValue = data.substring(0, 6) + "*******";
		} else if (maskType == MASK_ACCOUNT) {
			int index = data.length() - 4;
			retValue = data.substring(0, index) + "****";
		} else if (maskType == MASK_CARD) {
			retValue = data.substring(0, 6) + "****" + data.substring(10);
		}
		return retValue;
	}

	/**
	 * 지정된 길이만큼 문자열의 끝을 반환한다.
	 * 
	 * @param offset
	 *            시작 index
	 * @param str
	 *            대상 문자열
	 * @return String
	 */
	public static String tailer(int offset, String str) {
		if (str == null)
			str = "";
		if (str.length() <= offset)
			return "";

		return str.substring(offset);
	}

	/**
	 * 문자열을 숫자로 변환
	 * 
	 * @param no
	 * @return
	 */
	public static int parseInt(String no) {
		int i = 0;
		if (no != null && !no.trim().equals("")) {
			i = Integer.parseInt(no);
		}
		return i;
	}

	/**
	 * 
	 * @param no
	 * @return long
	 */
	public static long parseLong(String no) {
		long l = 0;
		if (no != null && !no.trim().equals("")) {
			l = Long.parseLong(no);
		}
		return l;
	}

	/**
	 * 
	 * @param no
	 * @return long
	 */
	public static double parseDouble(String no) {
		double d = 0;
		if (no != null && !no.trim().equals("")) {
			d = Double.parseDouble(no);
		}
		return d;
	}

	/**
	 * 파일 사이즈
	 * 
	 * @param size
	 * @return int
	 */
	public static int getFileSize(String bytes) {
		int retFormat = 0;
		Double size = Double.parseDouble(bytes);

		if (bytes != "0") {
			int idx = (int) Math.floor(Math.log(size) / Math.log(1024));
			DecimalFormat df = new DecimalFormat("#,###");
			double ret = ((size / Math.pow(1024, Math.floor(idx))));
			retFormat = parseInt(df.format(ret).toString());
		} else {
			retFormat = 0;
		}

		return retFormat;

	}

	/**
	 * @comment String 숫자를 포멧에 맞게 리턴한다.
	 * @date 2008-04-01
	 * @param value
	 *            String 숫자 값
	 * @param formatString
	 *            포맷 스트링
	 * @return String 포멧에 맛게 변환된 문자열
	 */
	public static String stringToMoneyFormat(String value, String formatString) {

		DecimalFormat format = new DecimalFormat(formatString);

		if (value.split("\\-").length == 1 && value.split("\\.").length == 1) { // 양수일때
			return format.format(Long.parseLong(value));
		} else if (value.split("\\-").length == 2) { // 음수일때를 가리기 위해
			if (value.split("\\-")[0].equals("")) { // 숫자 사이에 "-" 들어갈 경우가 아닌 경우에
				return format.format(Long.parseLong(value));
			} else {
				return value;
			}

		} else {
			return value;
		}

	}

	/**
	 * @comment int 값을 숫자를 포멧에 맞게 리턴한다.
	 * @date 2008-04-01
	 * @param value
	 *            int값
	 * @param formatString
	 *            포맷 스트링
	 * @return String 포멧에 맛게 변환된 문자열
	 */
	public static String intToMoneyFormat(int value, String formatString) {

		DecimalFormat format = new DecimalFormat(formatString);

		return format.format(value);

	}

	/**
	 * @comment long 값을 숫자를 포멧에 맞게 리턴한다.
	 * @date 2008-04-01
	 * @param value
	 *            long 값
	 * @param formatString
	 *            포맷 스트링
	 * @return String 포멧에 맛게 변환된 문자열
	 */
	public static String longToMoneyFormat(long value, String formatString) {

		DecimalFormat moneyFormat = new DecimalFormat(formatString);

		return moneyFormat.format(value);
	}

	/**
	 * @comment double 값을 숫자를 포멧에 맞게 리턴한다.
	 * @date 2008-04-01
	 * @param value
	 *            double 값
	 * @param formatString
	 *            포맷 스트링
	 * @return String 포멧에 맛게 변환된 문자열
	 */
	public static String doubleToMoneyFormat(double value, String formatString) {

		DecimalFormat moneyFormat = new DecimalFormat(formatString);

		return moneyFormat.format(value);
	}

	/**
	 * @comment String 인코딩 UTF-8 변환
	 * @date 2008-04-01
	 * @param str
	 *            String 값
	 * @return String 포멧에 맛게 변환된 문자열
	 */
	public static String ko(String str) {
		if (str == null || "".equals(str))
			return "";
		try {
			return new String(str.getBytes("8859_1"), "UTF-8");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 문자열 특수문자변
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceTag(String str) {
		// str = checkNull(str);
		StringBuffer result = new StringBuffer();
		String[] replace = new String[6];
		replace[0] = "<";
		replace[1] = ">";
		replace[2] = "'";
		replace[3] = "\"";
		replace[4] = "(";
		replace[5] = ")";
		String a = "";
		for (int i = 0; i < str.length(); i++) {
			a = str.substring(i, i + 1);
			for (int j = 0; j < replace.length; j++) {
				if (a.equals(replace[j])) {
					switch (j) {
					case 0:
						a = "&lt;";
						break;
					case 1:
						a = "&gt;";
						break;
					case 2:
						a = "&#39;";
						break;
					case 3:
						a = "&quot;";
						break;
					case 4:
						a = "&#40;";
						break;
					case 5:
						a = "&#41;";
						break;
					}
				}
			}
			result.append(a);
		}
		str = result.toString();
		return str;
	}


	/**
	 * @Method Name : isNull
	 * @Date : 2013. 2. 5.
	 * @Author : LSJ
	 * @Description : 빈값인지 확인
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		return (str == null || "".equals(str.trim()));
	}

	/**
	 * @Method Name : nbsp2Empty
	 * @Date : 2013. 2. 7.
	 * @Author : pyd
	 * @Description : &nbsp;를 빈값으로 변환
	 * 
	 * @param field
	 * @return
	 */
	public static String nbspToEmpty(String str) {
		if ("&nbsp;".equals(str.trim()))
			return "";
		else
			return str;
	}

	/**
	 * @Method Name : matched
	 * @Date : 2013. 2. 7.
	 * @Author : pyd
	 * @Description : 정규식 체크
	 * 
	 * @param regex,
	 *            inputTxt
	 * @return
	 */
	public static boolean matched(String regex, String inputTxt) {
		return Pattern.matches(regex, inputTxt);
	}

	/**
	 * @Method Name : 초단위를 시분초단위로 변환
	 * @Date : 2017. 6. 2.
	 * @Author :
	 * @Description :
	 * @param String
	 * @return
	 */
	public static String secondToStr(String str) {
		String result = "";
		if (str != null && !"".equals(str)) {
			if(str.equals("0")){
				return str + "초";
			}
			double d = Double.parseDouble(str);
			if (Math.floor(d / 3600) > 0) {
				result = String.format("%.0f", (Math.floor(d / 3600))) + "시";
			}
			if (Math.floor(d % 3600 / 60) > 0) {
				if (!("").equals(result)) result += " ";
				result += " "+String.format("%.0f", (Math.floor(d % 3600 / 60))) + "분";
			}
			if ((d % 3600 % 60) > 0) {
				if (!("").equals(result)) result += " ";
				result += " "+String.format("%.0f" , (d % 3600 % 60)) + "초";
			}
		}
		return result;
	}

	/**
	 * @Method Name : 2010 을 20시 10분 변환
	 * @Date : 2017. 12. 13
	 * @Author :
	 * @Description :
	 * @param int
	 * @return
	 */
	public static String secondToStr(int str) {
		String result = "";
		if ((str / 3600) > 0) {
			result = (str / 3600) + "시";
		}
		if ((str % 3600 / 60) > 0) {
			if (!("").equals(result)) result += " ";
			result += (str % 3600 / 60) + "분";
		}
		if ((str % 3600 % 60) > 0) {
			if (!("").equals(result)) result += " ";
			result += (str % 3600 % 60) + "초";
		}
		return result;
	}
	
	/**
	 * @Method Name : 초단위를 시분초단위로 변환
	 * @Date : 2017. 12. 12.
	 * @Author :
	 * @Description :
	 * @param int
	 * @return
	 */
	public static String numberToTime(String str) {
		String result = "";
		if (str != null && !"".equals(str)) {
			String hour = str.substring(0,2); 
			String min = str.substring(2);
			
			result =  hour + "시 " + min + "분";
		}
		return result;
	}

	/**
	 * 1000단위 콤마 처리용.
	 * 
	 * @param moneyString
	 * @return
	 */
	public static String makeMoneyType(String moneyString) {
		String ls_Part1 = "";
		String ls_Part2 = "";
		if (moneyString == null || moneyString.length() == 0)
			return "0";

		// 이상한 문자가 들어온경우 원래 값 반환.
		// ex) 95억 -_-);
		try {
			Double.parseDouble(moneyString);
		} catch (NumberFormatException nfe) {
			return moneyString;
		}

		if (moneyString.indexOf(".") != -1) {
			ls_Part1 = moneyString.substring(0, moneyString.indexOf("."));
			ls_Part2 = moneyString.substring(moneyString.indexOf("."));
		} else {
			ls_Part1 = moneyString;
		}

		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();

		dfs.setGroupingSeparator(',');
		df.setGroupingSize(3);
		df.setDecimalFormatSymbols(dfs);

		return (df.format(Long.parseLong(ls_Part1))).toString() + ls_Part2;
	}

	/**
	 * int형 숫자를 지정한 포멧 문자열로 반환한다.
	 * 
	 * @param pValue
	 * @param pFormat
	 * @return
	 * @throws CException
	 */
	public static String getFormatNumber(int pValue, String pFormat) throws Exception {
		DecimalFormat a = new DecimalFormat(pFormat);
		return a.format(pValue);
	}

	/**
	 * long형 숫자를 지정한 포멧 문자열로 반환한다.
	 * 
	 * @param pValue
	 * @param pFormat
	 * @return
	 * @throws CException
	 */
	public static String getFormatNumber(long pValue, String pFormat) throws Exception {
		DecimalFormat a = new DecimalFormat(pFormat);
		return a.format(pValue);
	}

	/**
	 * Empty String여부체크
	 * 
	 * @author : gmes_admin
	 * @since : 2004. 5. 14.
	 * @param :
	 * @return :
	 * @throws :
	 * @exception :
	 */
	public static boolean isEmptyString(String val) {
		if (val == null || val.equalsIgnoreCase("") || val.length() == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * int형을 String형으로 변환.
	 * 
	 * @author : gmes_admin
	 * @since : 2004. 5. 14.
	 * @param :
	 * @return :
	 * @throws :
	 * @exception :
	 */
	public static String intTostr(int val) {
		return new Integer(val).toString();
	}
	public static String intTostr(String str) {
		return new Integer(str).toString();
	}

	/**
	 * int형을 String형으로 변환.(포맷적용)
	 * 
	 * @author : gmes_admin
	 * @since : 2004. 5. 14.
	 * @param :
	 * @return :
	 * @throws :
	 * @exception :
	 */
	public static String intTostr(int val, String format) {
		DecimalFormat form = new DecimalFormat(format);

		return form.format(new Integer(val));
	}

	/**
	 * String형을 int형으로 변환.
	 * 
	 * @author : gmes_admin
	 * @since : 2004. 5. 14.
	 * @param :
	 * @return :
	 * @throws :
	 * @exception :
	 */
	public static int strToint(String val) {
		if (isEmptyString(val)) {
			return 0;
		}

		return Integer.valueOf(val).intValue();
	}

	/**
	 * int형을 double형으로 변환.
	 * 
	 * @author : gmes_admin
	 * @since : 2004. 5. 14.
	 * @param :
	 * @return :
	 * @throws :
	 * @exception :
	 */
	public static double intTodbl(int val) {
		return new Integer(val).doubleValue();
	}

	/**
	 * long형을 String형으로 변환.
	 * 
	 * @author : gmes_admin
	 * @since : 2004. 5. 14.
	 * @param :
	 * @return :
	 * @throws :
	 * @exception :
	 */
	public static String longTostr(long val) {
		return new Long(val).toString();
	}

	// Method : String / long2str()
	// Author : gmes_admin
	// Created Date : 2004. 4. 16.
	// Description : long형을 String형으로 변환.(포맷적용)
	public static String longTostr(long val, String format) {
		DecimalFormat form = new DecimalFormat(format);

		return form.format(new Long(val));
	}

	// Method : long / str2long()
	// Author : gmes_admin
	// Created Date : 2004. 4. 16.
	// Description : String형을 long형으로 변환.
	public static long strTolong(String val) {
		if (isEmptyString(val)) {
			return 0;
		}

		return Long.parseLong(val);
	}

	// Method : String / double2string()
	// Author : gmes_admin
	// Created Date : 2004. 4. 16.
	// Description : double형을 String형으로 변환.
	public static String dblTostr(double val) {
		return new Double(val).toString();
	}

	// Method : String / dbl2str()
	// Author : gmes_admin
	// Created Date : 2004. 4. 16.
	// Description : double형을 String형으로 변환.(포맷적용)
	public static String dblTostr(double val, String format) {
		DecimalFormat form = new DecimalFormat(format);

		return form.format(new Double(val));
	}

	// Method : double / str2dbl()
	// Author : gmes_admin
	// Created Date : 2004. 4. 16.
	// Description : String형을 double형으로 변환.
	public static double strTodbl(String val) {
		if (isEmptyString(val)) {
			return 0;
		}

		return Double.parseDouble(val);
	}

	/**
	 * Replace String str에서 rep에 해당하는 String을 tok로 replace
	 * 
	 * @param str
	 *            대체될 문자를 포함한 값
	 * @param regex
	 *            대체할 문자
	 * @param replacement
	 *            rep가 대체된 문자
	 * @return rep의 값이 replacement로 바뀐 스트링값을 반환한다.
	 */
	public static String getReplaceALL(String str, String regex, String replacement) {
		String retStr = "";
		if (str == null || (str != null && str.length() < 1)) {
			return "";
		}
		if (regex == null || (regex != null && regex.length() < 1)) {
			return str;
		}

		if ((str.indexOf(regex) == -1)) {
			return str;
		}

		for (int i = 0, j = 0; (j = str.indexOf(regex, i)) > -1; i = j + regex.length()) {
			retStr += (str.substring(i, j) + replacement);
		}

		return retStr + str.substring(str.lastIndexOf(regex) + regex.length(), str.length());
	}
	
	/**
	 * yyyyMMdd 형태의 날짜를 원하는 포맷으로 변경
	 * @param yyyyMMdd str
	 * @param ex) yyyy-MM-dd
	 * @return String
	 * @throws ParseException
	 */
	public static String dateFormatStr(String str, String format) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = formatter.parse(str);
		formatter.applyPattern(format);
		return formatter.format(date);
	}
	
	public static boolean toDayCompare(String str) throws ParseException {
		boolean result = false;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = formatter.parse(str);
		
		Date to_date = new Date();
		String today = formatter.format(to_date);
		
		if (formatter.format(date).equals(today)) {
			result = true;
		}
		return result;
	}
	
	/** 
	 * yyyyMMdd 혹은 yyyyMMddHHmmss 형식의 날짜(시간) 문자열을 입력 받아 날짜는  
	 * '.', 시간은 ':'을 구분자로 삽입한 문자열을 반환한다. 
	 *  
	 * @param date  yyyyMMdd 혹은 yyyyMMddHHmmss 형식의 날짜(시간) 문자열 
	 * @param IS_TIME  시간이 포함된 문자열일 경우 시간을 제외할 지의 여부 
	 * @return 변환된 날짜 문자열 
	 */ 
	private static String dateSeparator = "-";
	private static String timeSeparator = ":";
	public static String dateConvert(String date, boolean IS_TIME) { 
		String result = null; 

		if(date.length() == 8) { 
			result = date.substring(0,4) + dateSeparator + date.substring(4,6) + dateSeparator + date.substring(6);				 
		} else if(date.length() == 14) { 
			if(IS_TIME) { 
				result = date.substring(0,4) + dateSeparator + date.substring(4,6) + dateSeparator + date.substring(6,8) 
					+ " " + date.substring(8,10) + timeSeparator + date.substring(10,12) + timeSeparator + date.substring(12); 
			} else { 
				result = date.substring(0,4) + dateSeparator + date.substring(4,6) + dateSeparator + date.substring(6,8);					 
			} 
		}  


		return result;		 
	}
	
	/**
	 * format으로 오늘 날짜 구하기
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static String getToday(String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
        Calendar c1 = Calendar.getInstance();
		return sdf.format(c1.getTime());
	}
	
	
	public static boolean isNumber(String str) {
        boolean result = false; 
        try{
            Double.parseDouble(str);
            result = true ;
        }catch(Exception e){}
        return result ;
    }
	
	/**
	 * 3자리 콤마 int
	 * @param String
	 * @return String
	 */
	public static String int_comma(String str) {
		String resut = "0";
		if (!"".equals(str)) {
			if (StringUtil.isNumber(str)) {
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(3);
				resut = nf.format(Integer.valueOf(str));
			}
		}
		return resut;
	}
	
	/**
	 * 3자리 콤마 double
	 * @param String
	 * @return String
	 */
	public static String double_comma(String str) {
		String resut = "0";
		if (!"".equals(str)) {
			if (StringUtil.isNumber(str)) {
				NumberFormat nf = NumberFormat.getInstance();
				nf.setMaximumFractionDigits(3);
				resut = nf.format(Double.parseDouble(str));
			}
		}
		return resut;
	}
	
	/**
	 * 문자제외
	 * @param String 
	 * @return String 
	 */
	public static String removeCharExceptNumber(String str) {
	    return str.replaceAll("[^0-9]", "");
	}
	
	/**
	  * 소수점 첫째 반올림
	  * @param ex
	  * @return
	  */
	public static String round10d(String str) {
		String result = null;
		if (isNumber(str)) {
			long r = (long) (Math.round(Double.parseDouble(str)*10d) /10d);			
			result = long2str(r);
		} else {
			result = str;
		}
		return result;
	}
	
	/**
	 * 반올림
	 * @param ex
	 * @return
	 */
	public static String round(String str) {
		String result = null;
		if (isNumber(str)) {
			long r = Math.round(Double.parseDouble(str));			
			result = long2str(r);
		} else {
			result = str;
		}
		return result;
	}
	
	/**
	 * 반올림
	 * @param ex
	 * @return
	 */
	public static double round(double d) {
		double result;
		if (isNumber(dbl2str(d))) {
			long r = Math.round(d);			
			result = (long) r;
		} else {
			result = d;
		}
		return result;
	}
	/**
	 * 올림
	 * @param ex
	 * @return
	 */
	public static String ceil(String str) {
		String result = null;
		if (isNumber(str)) {
			double c = Math.ceil(Double.parseDouble(str));
			result = dbl2str(c);
		} else {
			result = str;
		}
		return result;
	}
	/**
	 * 버림
	 * @param ex
	 * @return
	 */
	public static String floor(String str) {
		String result = null;
		if (isNumber(str)) {
			double f = Math.floor(Double.parseDouble(str));
			result = dbl2str(f);
		} else {
			result = str;
		}
		return result;
	}
	
	public static String dbl2str(double val) {
		return new Double(val).toString();
	}

	public static String long2str(long val) {
		return new Long(val).toString();
	}
	
	/**
	 * 날짜차이
	 */
	public static String diffOfDate(String begin, String end) throws Exception {
	    SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
	    Date beginDate = formatter.parse(begin);
	    Date endDate = formatter.parse(end);
	    long diff = endDate.getTime() - beginDate.getTime();
	    long diffDays = Math.abs(diff / (24 * 60 * 60 * 1000));
	    return long2str(diffDays);
	  }
	
	/**
	 * 날짜차이
	 */
	public static String diffOfDate(Date begin, Date end) throws Exception {
		long diff = end.getTime() - begin.getTime();
		long diffDays = Math.abs(diff / (24 * 60 * 60 * 1000));
		return long2str(diffDays);
	}

}
