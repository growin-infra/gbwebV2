package gb.common.util;

import java.math.BigDecimal;

public class NumberUtil {

	/**
	 * 백분율을 계산하여 반환한다.
	 * @param tot			전체
	 * @param use		사용
	 * @param cipher	소수점반올림(자릿수)
	 * @return String
	 */
	public static double rateCal( long tot , long use , int cipher ) {
    	double rate = ( (double)use / (double)tot ) * 100;
        return round( rate , cipher );		
	}
	
	/**
	 * 반올림처리한다.
	 * @param value		값
	 * @param cipher 	소수점반올림(자릿수)
	 * @return String
	 */
	public static double round( double value , int cipher ) {
		if( Double.isNaN( value ) ) value =  0;
		BigDecimal
		bd = new BigDecimal( value );
		bd = bd.setScale( cipher , BigDecimal.ROUND_HALF_UP );
		return  bd.doubleValue();
    }

}
