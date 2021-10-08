<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<script type="text/javascript">
var calendar;
var selday;
var today;
$(document).ready(function() {
	calendar = new controller();
	calendar.init();

	function controller(target) {
		
		var that = this;   
		var m_oMonth = new Date();
		m_oMonth.setDate(1);
		
		this.init = function(e) {
			that.renderCalendar(e);
			that.initEvent(e);
		}
		
		today = new Date();
		var mm = today.getMonth()+1; //January is 0!
		var dd = today.getDate();
		var yyyy = today.getFullYear();
		if (mm < 10) mm='0'+mm;
		if (dd < 10) dd='0'+dd;
		today = String(yyyy)+String(mm)+String(dd);
		
		/* 달력 UI 생성 */
		this.renderCalendar = function(e) {
			selday = e;
			var arrTable = [];

			arrTable.push("<table class='table2'><thead>");

			var arrWeek = "일월화수목금토".split("");

			for(var i=0, len=arrWeek.length; i<len; i++) {
				var sClass = '';
				sClass += i % 7 == 0 ? 'sun' : '';
				sClass += i % 7 == 6 ? 'sat' : '';
				arrTable.push('<th class="'+sClass+'">' + arrWeek[i] + '</th>');
			}
			arrTable.push('</tr></thead>');
			arrTable.push('<tbody>');

			var oStartDt = new Date(m_oMonth.getTime());
			// 1일에서 1일의 요일을 빼면 그 주 첫번째 날이 나온다.
			oStartDt.setDate(oStartDt.getDate() - oStartDt.getDay());
			
			for(var i=0; i<100; i++) {
				if(i % 7 == 0) {
					arrTable.push('<tr>');
				}
				
				var sClass = '';
				sClass += m_oMonth.getMonth() != oStartDt.getMonth() ? 'not-this-month ' : '';
				sClass += i % 7 == 0 ? 'sun' : '';
				sClass += i % 7 == 6 ? 'sat' : '';
				
				var myy = m_oMonth.getFullYear();
				var mmm = oStartDt.getMonth()+1;
				var mdd = oStartDt.getDate();
				if (mmm < 10) mmm='0'+mmm;
				if (mdd < 10) mdd='0'+mdd;
				var initday = String(myy)+String(mmm)+String(mdd);
				
				var dataSB = "";
		        <c:forEach var="row" items="${isList}" varStatus="status">
		            if (initday == "${row.is_wrk_dt}") {
	            		dataSB = "<font size='3px' color='#ff0000'>*</font>";
		            	if (initday != selday) {
		            		dataSB = "<font size='3px' color='#C87373'>*</font>";
		            	}
		            }
		        </c:forEach>

				if( selday !=null && new String(selday).valueOf() != "undefined" ) {
					if (initday != selday) {
						arrTable.push("<td><a class='"+sClass+"' href='#void' onclick='fn_day("+initday+")'>" + oStartDt.getDate()+dataSB + "</a></td>");
					} else {
						sClass += " on";
						arrTable.push("<td><a class='"+sClass+"' href='#void' onclick='fn_day("+selday+")'>" + oStartDt.getDate()+dataSB + "</a></td>");
					}
				} else {
					if (today != initday) {
						arrTable.push("<td><a class='"+sClass+"' href='#void' onclick='fn_day("+initday+")'>" + oStartDt.getDate()+dataSB + "</a></td>");
					} else {
						selday = initday;
						sClass += " on";
						arrTable.push("<td><a class='"+sClass+"' href='#void' onclick='fn_day("+today+")'>" + oStartDt.getDate()+dataSB + "</a></td>");
					}
				}
				oStartDt.setDate(oStartDt.getDate() + 1);
				
				if(i % 7 == 6) {
					arrTable.push('</tr>');
					if(m_oMonth.getMonth() != oStartDt.getMonth()) {
						break;
					}
				}
			}
			
			arrTable.push('</tbody></table>');
			
			$('#calendar').html(arrTable.join(""));
			
			that.changeMonth();
		}
		
		/* Next, Prev 버튼 이벤트 */
		this.initEvent = function(e) {
			if(e ==null || e.valueOf() == "undefined") {
				$('#btnPrev').click(that.onPrevCalendar);
				$('#btnNext').click(that.onNextCalendar);
				$('#btnPrev12').click(that.onPrevCalendar12);
				$('#btnNext12').click(that.onNextCalendar12);
				$('#btnTodayB').click(that.onToDayB);
				$('#btnTodayR').click(that.onToDayR);
			}
		}

		/* 이전 달력 */
		this.onPrevCalendar = function(e) {
			m_oMonth.setMonth(m_oMonth.getMonth() - 1);
			that.renderCalendar(e);
		}

		/* 다음 달력 */
		this.onNextCalendar = function(e) {
			m_oMonth.setMonth(m_oMonth.getMonth() + 1);
			that.renderCalendar(e);
		}

		/* 이전 달력 */
		this.onPrevCalendar12 = function(e) {
			m_oMonth.setMonth(m_oMonth.getMonth() - 12);
			that.renderCalendar(e);
		}

		/* 다음 달력 */
		this.onNextCalendar12 = function(e) {
			m_oMonth.setMonth(m_oMonth.getMonth() + 12);
			that.renderCalendar(e);
		}

		/* 오늘 */
		this.onToDayB = function(e) {
			var comSubmit = new ComSubmit();
		    comSubmit.setUrl("/set_b_rec");
		    comSubmit.addParam("menu_cd", "A01020201");
		    comSubmit.addParam("bms_id", bms_id);
		    comSubmit.addParam("bts_id", bts_id);
		    comSubmit.submit();
		}

		/* 오늘 */
		this.onToDayR = function(e) {
			var comSubmit = new ComSubmit();
		    comSubmit.setUrl("/set_r_rec");
		    comSubmit.addParam("menu_cd", "A01020208");
		    comSubmit.addParam("bms_id", bms_id);
		    comSubmit.addParam("bts_id", bts_id);
		    comSubmit.submit();
		}

		/* 달력 이동되면 상단에 현재 년 월 다시 표시 */
		this.changeMonth = function() {
			$('#currentDate').text(that.getYearMonth(m_oMonth).substr(0,9));
		}

		/* 날짜 객체를 년 월 문자 형식으로 변환 */
		this.getYearMonth = function(oDate) {
			return oDate.getFullYear() + '년 ' + (oDate.getMonth() + 1) + '월';
		}
	}
});
function fn_day(day) {
	calendar.init(day);
	fn_log(day);
}
</script>