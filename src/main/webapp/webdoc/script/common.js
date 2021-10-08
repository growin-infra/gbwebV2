function gfn_isNull(str) {
	if (str == null) return true;
	if (str == "NaN") return true;
	if (new String(str).valueOf() == "undefined") return true;    
    var chkStr = new String(str);
    if( chkStr.valueOf() == "undefined" ) return true;
    if (chkStr == null) return true;    
    if (chkStr.toString().length == 0 ) return true;   
    return false; 
}

function ComSubmit(opt_formId) {
	this.formId = gfn_isNull(opt_formId) == true ? "commonForm" : opt_formId;
	this.url = "";
	
	if(this.formId == "commonForm"){
		$("#commonForm")[0].reset();
	}
	this.setUrl = function setUrl(url){
		this.url = url;
	};
	
	this.addParam = function addParam(key, value){
		$("#"+this.formId).append($("<input type='hidden' name='"+key+"' id='"+key+"' value='"+value+"' >"));
	};
	
	this.submit = function submit(){
		var frm = $("#"+this.formId)[0];
		frm.action = this.url;
		frm.method = "post";
		frm.submit();	
	};
}

var gfv_ajaxCallback = "";
function ComAjax(opt_formId){
    this.url = "";      
    this.formId = gfn_isNull(opt_formId) == true ? "commonForm" : opt_formId;
    this.param = "";
     
    if(this.formId == "commonForm"){
        var frm = $("#commonForm");
        if(frm.length > 0){
            frm.remove();
        }
        var str = "<form id='commonForm' name='commonForm'></form>";
        $('body').append(str);
    }
     
    this.setUrl = function setUrl(url){
        this.url = url;
    };
     
    this.setCallback = function setCallback(callBack){
        fv_ajaxCallback = callBack;
    };
    
    //중복된 Function 
    this.setErrCallback = function setErrCallback(callBack){
    	fv_ajaxErrCallback = callBack;
    };
 
    this.addParam = function addParam(key,value){ 
        this.param = this.param + "&" + key + "=" + value; 
    };
   
    this.ajax = function ajax(){
        if(this.formId != "commonForm"){
            this.param += "&" + $("#" + this.formId).serialize();
        }
        $.ajax({
            url : this.url,    
            type : "POST",   
            data : this.param,
            async : false,
            success : function(data, status) {
            	loadingDiv.off();
                if(typeof(fv_ajaxCallback) == "function"){
                    fv_ajaxCallback(data);
                }
                else {
                    eval(fv_ajaxCallback + "(data);");
                }
            },
			error : function(request, status, error) {
				closeModal2();
				loadingDiv.off();
				if (request.status == 400) {
			    	var comSubmit = new ComSubmit("commonForm");
				    comSubmit.setUrl("/main");
				    comSubmit.submit();
				} else {
					openAlertModal("ERROR",error);
				}
			}
        });
    };
}


function ComAjaxRun(opt_formId){
    this.url = "";      
    this.formId = gfn_isNull(opt_formId) == true ? "commonForm" : opt_formId;
    this.param = "";
     
    if(this.formId == "commonForm"){
        var frm = $("#commonForm");
        if(frm.length > 0){
            frm.remove();
        }
        var str = "<form id='commonForm' name='commonForm'></form>";
        $('body').append(str);
    }
     
    this.setUrl = function setUrl(url){
        this.url = url;
    };
     
    this.setCallback = function setCallback(callBack){
        fv_ajaxCallback = callBack;
    };
    
    //중복된 Function 
    this.setErrCallback = function setErrCallback(callBack){
    	fv_ajaxErrCallback = callBack;
    };
 
    this.addParam = function addParam(key,value){ 
        this.param = this.param + "&" + key + "=" + value; 
    };
   
    this.ajax = function ajax(){
        if(this.formId != "commonForm"){
            this.param += "&" + $("#" + this.formId).serialize();
        }
        $.ajax({
            url : this.url,    
            type : "POST",   
            data : this.param,
            async : false,
            success : function(data, status) {
            	loadingDiv.off();
                if(typeof(fv_ajaxCallback) == "function") {
                    fv_ajaxCallback(data);
                } else {
                    eval(fv_ajaxCallback + "(data);");
                }
            },
			error : function(request, status, error) {
				loadingDiv.off();
				closeModal2();
				if (request.status == 400) {
			    	var comSubmit = new ComSubmit("commonForm");
				    comSubmit.setUrl("/main");
				    comSubmit.submit();
				} else {
					$(".modal").remove();
					bakRunModal("ERROR","백업실행 중 오류발생하였습니다.", function(){
						var comSubmit = new ComSubmit();
						comSubmit.setUrl("/set_b_run");
						comSubmit.addParam("menu_cd", "A01020206");
						comSubmit.addParam("bms_id", $("#bms_id").val());
						comSubmit.addParam("bts_id", $("#bts_id").val());
						comSubmit.submit();
					});
				}
			}
        });
    };
}

function ComAjaxRsrRun(opt_formId){
	this.url = "";      
	this.formId = gfn_isNull(opt_formId) == true ? "commonForm" : opt_formId;
	this.param = "";
	
	if(this.formId == "commonForm"){
		var frm = $("#commonForm");
		if(frm.length > 0){
			frm.remove();
		}
		var str = "<form id='commonForm' name='commonForm'></form>";
		$('body').append(str);
	}
	
	this.setUrl = function setUrl(url){
		this.url = url;
	};
	
	this.setCallback = function setCallback(callBack){
		fv_ajaxCallback = callBack;
	};
	
	//중복된 Function 
	this.setErrCallback = function setErrCallback(callBack){
		fv_ajaxErrCallback = callBack;
	};
	
	this.addParam = function addParam(key,value){ 
		this.param = this.param + "&" + key + "=" + value; 
	};
	
	this.ajax = function ajax(){
		if(this.formId != "commonForm"){
			this.param += "&" + $("#" + this.formId).serialize();
		}
		$.ajax({
			url : this.url,    
			type : "POST",   
			data : this.param,
			async : false,
			success : function(data, status) {
				loadingDiv.off();
				if(typeof(fv_ajaxCallback) == "function") {
					fv_ajaxCallback(data);
				} else {
					eval(fv_ajaxCallback + "(data);");
				}
			},
			error : function(request, status, error) {
				closeModal2();
				loadingDiv.off();
				if (request.status == 400) {
					var comSubmit = new ComSubmit("commonForm");
					comSubmit.setUrl("/main");
					comSubmit.submit();
				} else {
					$(".modal").remove();
					bakRunModal("ERROR","복구실행 중 오류발생하였습니다.", function(){
						var comSubmit = new ComSubmit();
						comSubmit.setUrl("/set_r_run");
						comSubmit.addParam("menu_cd", "A01020211");
						comSubmit.addParam("bms_id", $("#bms_id").val());
						comSubmit.addParam("bts_id", $("#bts_id").val());
						comSubmit.submit();
					});
				}
			}
		});
	};
}


function ComAjaxCallback(opt_formId){
	this.url = "";      
	this.formId = gfn_isNull(opt_formId) == true ? "commonForm" : opt_formId;
	this.param = "";
	
	if(this.formId == "commonForm"){
		var frm = $("#commonForm");
		if(frm.length > 0){
			frm.remove();
		}
		var str = "<form id='commonForm' name='commonForm'></form>";
		$('body').append(str);
	}
	
	this.setUrl = function setUrl(url){
		this.url = url;
	};
	
	this.setCallback = function setCallback(callBack){
		fv_ajaxCallback = callBack;
	};
	
	//중복된 Function 
	this.setErrCallback = function setErrCallback(callBack){
		fv_ajaxErrCallback = callBack;
	};
	
	this.addParam = function addParam(key,value){ 
		this.param = this.param + "&" + key + "=" + value; 
	};
	
	this.ajax = function ajax(){
		if(this.formId != "commonForm"){
			this.param += "&" + $("#" + this.formId).serialize();
		}
		$.ajax({
			url : this.url,    
			type : "POST",   
			data : this.param,
			async : false, 
			success : function(data, status) {
				if(typeof(fv_ajaxCallback) == "function"){
					fv_ajaxCallback(data);
				}
				else {
					eval(fv_ajaxCallback + "(data);");
				}
			},
			error : function(request, status, error) {
				loadingDiv.off();
			}
		});
	};
}

function ComAjaxAsync(opt_formId){
	this.url = "";      
	this.formId = gfn_isNull(opt_formId) == true ? "commonFormAsync" : opt_formId;
	this.param = "";
	
	if(this.formId == "commonFormAsync"){
		var frm = $("#commonFormAsync");
		if(frm.length > 0){
			frm.remove();
		}
		var str = "<form id='commonFormAsync' name='commonFormAsync'></form>";
		$('body').append(str);
	}
	
	this.setUrl = function setUrl(url){
		this.url = url;
	};
	
	this.setCallback = function setCallback(callBack){
		fv_ajaxCallback = callBack;
	};
    
    this.setErrCallback = function setErrCallback(callBack){
    	fv_ajaxErrCallback = callBack;
    };
	
	this.addParam = function addParam(key,value){ 
		this.param = this.param + "&" + key + "=" + value; 
	};
	
	this.ajax = function ajax(){
		if(this.formId != "commonFormAsync"){
			this.param += "&" + $("#" + this.formId).serialize();
		}
		$.ajax({
			url : this.url,    
			type : "POST",   
			data : this.param,
			beforeSend : function() {
				loadingDiv.on();
			}, 
			complete: function () {
				loadingDiv.off();
			},
			async : true, 
			success : function(data, status) {
				loadingDiv.off();
				if(typeof(fv_ajaxCallback) == "function"){
					fv_ajaxCallback(data);
				}
				else {
					eval(fv_ajaxCallback + "(data);");
				}
			},
			error : function(request, status, error) {
				loadingDiv.off();
				if (request.status == 400) {
			    	var comSubmit = new ComSubmit("commonForm");
				    comSubmit.setUrl("/main");
				    comSubmit.submit();
				} else {
					openAlertModal("ERROR",error);
				}
			}
			,complete:function(data){
			}
		});
	};
}

function ComAjaxAsyncRun(opt_formId){
	this.url = "";      
	this.formId = gfn_isNull(opt_formId) == true ? "commonFormAsync" : opt_formId;
	this.param = "";
	
	if(this.formId == "commonFormAsync"){
		var frm = $("#commonFormAsync");
		if(frm.length > 0){
			frm.remove();
		}
		var str = "<form id='commonFormAsync' name='commonFormAsync'></form>";
		$('body').append(str);
	}
	
	this.setUrl = function setUrl(url){
		this.url = url;
	};
	
	this.setCallback = function setCallback(callBack){
		fv_ajaxCallback = callBack;
	};
    
    this.setErrCallback = function setErrCallback(callBack){
    	fv_ajaxErrCallback = callBack;
    };
	
	this.addParam = function addParam(key,value){ 
		this.param = this.param + "&" + key + "=" + value; 
	};
	
	this.ajax = function ajax(){
		if(this.formId != "commonFormAsync"){
			this.param += "&" + $("#" + this.formId).serialize();
		}
		$.ajax({
			url : this.url,    
			type : "POST",   
			data : this.param,
			beforeSend : function() {
//				loadingDiv.on();
			}, 
			complete: function () {
//				loadingDiv.off();
			},
			async : true, 
			success : function(data, status) {
//				loadingDiv.off();
				if(typeof(fv_ajaxCallback) == "function"){
					fv_ajaxCallback(data);
				}
				else {
					eval(fv_ajaxCallback + "(data);");
				}
			},
			error : function(request, status, error) {
				closeModal2();
				loadingDiv.off();
				/*
				if (request.status == 400) {
			    	var comSubmit = new ComSubmit("commonForm");
				    comSubmit.setUrl("/main");
				    comSubmit.submit();
				} else {
					openAlertModal("ERROR",error);
				}
				*/
			}
			,complete:function(data){
			}
		});
	};
}
/*
divId : 페이징 태그가 그려질 div
pageIndx : 현재 페이지 위치가 저장될 input 태그 id
recordCount : 페이지당 레코드 수
totalCount : 전체 조회 건수 
eventName : 페이징 하단의 숫자 등의 버튼이 클릭되었을 때 호출될 함수 이름
*/
var gfv_pageIndex = null;
var gfv_eventName = null;
function gfn_renderPaging(params){
	var divId = params.divId; //페이징이 그려질 div id
	gfv_pageIndex = params.pageIndex; //현재 위치가 저장될 input 태그
	var totalCount = params.totalCount; //전체 조회 건수
	var currentIndex = $("#"+params.pageIndex).val(); //현재 위치
	if($("#"+params.pageIndex).length == 0 || gfn_isNull(currentIndex) == true){
		currentIndex = 1;
	}
	
	var recordCount = params.recordCount; //페이지당 레코드 수
	if(gfn_isNull(recordCount) == true){
		recordCount = 20;
	}
	var totalIndexCount = Math.ceil(totalCount / recordCount); // 전체 인덱스 수
	gfv_eventName = params.eventName;
	
	$("#"+divId).empty();
	var preStr = "";
	var postStr = "";
	var str = "";
	
	var first = (parseInt((currentIndex-1) / 10) * 10) + 1;
	var last = (parseInt(totalIndexCount/10) == parseInt(currentIndex/10)) ? totalIndexCount%10 : 10;
	var prev = (parseInt((currentIndex-1)/10)*10) - 9 > 0 ? (parseInt((currentIndex-1)/10)*10) - 9 : 1; 
	var next = (parseInt((currentIndex-1)/10)+1) * 10 + 1 < totalIndexCount ? (parseInt((currentIndex-1)/10)+1) * 10 + 1 : totalIndexCount;
	
	if(totalIndexCount > 10){ //전체 인덱스가 10이 넘을 경우, 맨앞, 앞 태그 작성
		preStr += "<a href='#this' class='pad_5' onclick='_movePage(1)'>[<<]</a>" +
				"<a href='#this' class='pad_5' onclick='_movePage("+prev+")'>[<]</a>";
	}
	else if(totalIndexCount <=10 && totalIndexCount > 1){ //전체 인덱스가 10보다 작을경우, 맨앞 태그 작성
		preStr += "<a href='#this' class='pad_5' onclick='_movePage(1)'>[<<]</a>";
	}
	
	if(totalIndexCount > 10){ //전체 인덱스가 10이 넘을 경우, 맨뒤, 뒤 태그 작성
		postStr += "<a href='#this' class='pad_5' onclick='_movePage("+next+")'>[>]</a>" +
					"<a href='#this' class='pad_5' onclick='_movePage("+totalIndexCount+")'>[>>]</a>";
	}
	else if(totalIndexCount <=10 && totalIndexCount > 1){ //전체 인덱스가 10보다 작을경우, 맨뒤 태그 작성
		postStr += "<a href='#this' class='pad_5' onclick='_movePage("+totalIndexCount+")'>[>>]</a>";
	}
	
	for(var i=first; i<(first+last); i++){
		if(i != currentIndex){
			str += "<a href='#this' class='pad_5' onclick='_movePage("+i+")'>"+i+"</a>";
		}
		else{
			str += "<strong><a href='#this' class='pad_5' onclick='_movePage("+i+")'>"+i+"</a></strong>";
		}
	}
	$("#"+divId).append(preStr + str + postStr);
}

function _movePage(value){
	$("#"+gfv_pageIndex).val(value);
	if(typeof(gfv_eventName) == "function"){
		gfv_eventName(value);
	}
	else {
		eval(gfv_eventName + "(value);");
	}
}

/*
//퍼블리셔 추가
var winHeight = $(window).height();
var winWindh = $(window).width();
$(document).ready(function(){
	//제품 비교하기
	var scrollData = $(window).scrollTop();
	var contTopHeight = $('.common_list .cont_top').outerHeight();
	if(scrollData >= contTopHeight){
		$('.common_list .compare_box').addClass('fixed');
	}else{
		$('.common_list .compare_box').removeClass('fixed');
	}
});

$(window).scroll(function(){
	//제품 비교하기
	var scrollData = $(window).scrollTop();
	var contTopHeight = $('.common_list .cont_top').outerHeight();
	if(scrollData >= contTopHeight){
		$('.common_list .compare_box').addClass('fixed');
	}else{
		$('.common_list .compare_box').removeClass('fixed');
	}
});


//layer popup
function commonLayerOpen(thisClass){
	$('.'+thisClass).fadeIn();
}
function commonLayerClose(thisClass){
	$('.'+thisClass).fadeOut();
}
*/

/*
Map = function() {
	this.map = new Object();
};
Map.prototype = {
	put : function(key, value) {
		this.map[key] = value;
	},
	get : function(key) {
		return this.map[key];
	},
	containsKey : function(key) {
		return key in this.map;
	},
	containsValue : function(value) {
		for ( var prop in this.map) {
			if (this.map[prop] == value)
				return true;
		}
		return false;
	},
	isEmpty : function(key) {
		return (this.size() == 0);
	},
	clear : function() {
		for ( var prop in this.map) {
			delete this.map[prop];
		}
	},
	remove : function(key) {
		delete this.map[key];
	},
	keys : function() {
		var keys = new Array();
		for ( var prop in this.map) {
			keys.push(prop);
		}
		return keys;
	},
	values : function() {
		var values = new Array();
		for ( var prop in this.map) {
			values.push(this.map[prop]);
		}
		return values;
	},
	size : function() {
		var count = 0;
		for ( var prop in this.map) {
			count++;
		}
		return count;
	}
};
*/

function strToFormat(str) {
	var result;
	if (str.length == 14) {
		var date = new Date(str);
		result = date.getFullYear()+"년 ";
		result += date.getMonth()+1+"월 ";
		result += date.getDay()+"일 ";
		result += date.getHours()+"시 ";
		result += date.getMinutes()+"분 ";
		result += date.getSeconds()+"초"; 
	}
	return result;
}

/**
 * Date를 문자열로 변환한다.
 * @param value Date
 * @param outputFormat 형식 ex)YYYY.MM.DD
 */
var dateToString = function(value, outputFormat) {
    if (!value)
        return "";
 
    if(isNaN(value.getSeconds())||isNaN(value.getMinutes())
            ||isNaN(value.getHours())||isNaN(value.getDate())
            ||isNaN(value.getMonth())||isNaN(value.getFullYear()))return "";
     
    var sec = String(value.getSeconds());
    if (sec.length < 2)
        sec = "0" + sec;
     
    var min = String(value.getMinutes());
    if (min.length < 2)
        min = "0" + min;
     
    var hour = String(value.getHours());
    if (hour.length < 2)
        hour = "0" + hour;
     
    var date = String(value.getDate());
    if (date.length < 2)
        date = "0" + date;
 
    var month = String(value.getMonth() + 1);
    if (month.length < 2)
        month = "0" + month;
 
    var year = String(value.getFullYear());
    var output = "";
    var mask;
     
    var n = outputFormat != null ? outputFormat.length : 0;
    for (var i = 0; i < n; i++) {
        mask = outputFormat.charAt(i);
        if (mask == "s") {
            output += outputFormat.charAt(i + 1) == "/" && value.getSeconds() < 10 ? sec.substring(1) + "/" : sec;
            i++;
        } else if (mask == "m") {
            output += outputFormat.charAt(i + 1) == "/" && value.getMinutes() < 10 ? min.substring(1) + "/" : min;
            i++;
        } else if (mask == "h") {
            output += outputFormat.charAt(i + 1) == "/" && value.getHour() < 10 ? hour.substring(1) + "/" :  hour;
            i++;
        } else if (mask == "M") {
            output += outputFormat.charAt(i + 1) == "/" && value.getMonth() < 9 ? month.substring(1) + "/" : month;
            i++;
        } else if (mask == "D") {
            output += outputFormat.charAt(i + 1) == "/" && value.getDate() < 10 ? date.substring(1) + "/" : date;
            i++;
        } else if (mask == "Y") {
            if (outputFormat.charAt(i + 2) == "Y") {
                output += year;
                i += 3;
            } else {
                output += year.substring(2, 4);
                i++;
            }
        } else {
            output += mask;
        }
    }
    return output;
};


/**
 * 문자열을 Date로 변환.
 * @param valueString 문자열
 * @param inputFormat 형식 ex) YYYY-MM-DD hh:mm:ss
 */
var stringToDate = function(valueString, inputFormat) {
    if(!valueString){
        return valueString;
    }
    var mask;
    var temp;
    var dateString = "";
    var monthString = "";
    var yearString = "";
    var hourString = "";
    var miniteString = "";
    var secondString = "";
    var j = 0;
 
    var n = inputFormat.length;
    for (var i = 0; i < n; i++, j++) {
        temp = "" + valueString.charAt(j);
        mask = "" + inputFormat.charAt(i);
 
        if (mask == "M") {
            if (isNaN(Number(temp)) || temp == " ")
                j--;
            else
                monthString += temp;
        } else if (mask == "D") {
            if (isNaN(Number(temp)) || temp == " ")
                j--;
            else
                dateString += temp;
        } else if (mask == "Y") {
            yearString += temp;
        } else if (mask == "h") {
            hourString += temp;
        } else if (mask == "m") {
            miniteString += temp;
        } else if (mask == "s") {
            secondString += temp;
        } else if (!isNaN(Number(temp)) && temp != " ") {
            return null;
        }
    }
 
    temp = "" + valueString.charAt(inputFormat.length - i + j);
    if (!(temp == "") && (temp != " "))
        return null;
 
    var monthNum = Number(monthString);
    var dayNum = Number(dateString);
    var yearNum = Number(yearString);
    var hourNum = Number(hourString);
    var miniteNum = Number(miniteString);
    var secondNum = Number(secondString);
 
    if (isNaN(yearNum) || isNaN(monthNum) || isNaN(dayNum))
        return null;
 
    if (yearString.length == 2 && yearNum < 70)
        yearNum += 2000;
 
    var newDate = new Date(yearNum, monthNum - 1, dayNum);
    newDate.setHours(hourNum, miniteNum, secondNum);
 
    if (dayNum != newDate.getDate() || (monthNum - 1) != newDate.getMonth())
        return null;
 
    return newDate;
}

/**
 * 14자리 날짜를 년월일시분초로 변환
 */
var strToCustomFormat = function(v) {
	if (v.length != 14) return;
	return v.substr(0,4)+"년 "+v.substr(4,2)+"월 "+v.substr(6,2)+"일 "+v.substr(8,2)+"시 "+v.substr(10,2)+"분 "+v.substr(12,2)+"초";
}

/**
 * 초단위를 시분초로 변환
 */
var secToStr = function(seconds) {
	var result = "";
//	var pad = function(x) { return (x < 10) ? "0"+x : x; }
	var pad = function(x) { return x; }
	if ((parseInt(seconds / (60*60))) > 0) {
		result = pad(parseInt(seconds / (60*60))) + "시 ";
	}
	if ((parseInt(seconds / 60 % 60)) > 0) {
		result += pad(parseInt(seconds / 60 % 60)) + "분 ";
	}
	if ((seconds % 60) > 0) {
//		result += (Math.round(pad(seconds % 60)) < 10 ? "0"+Math.round(pad(seconds % 60)):Math.round(pad(seconds % 60))) + "초";
//		result += (Math.round(pad(seconds % 60))) + "초";
		result += ((pad(seconds % 60)).toFixed(0)) + "초";
	}
	if(seconds == "0"){
		result = "0초"
	}
	return result;

}


//숫자만 - 해당 Input 속성 삽입
//onkeypress="return fn_press(event, 'numbers');" onkeydown="fn_press_han(this);" style="ime-mode:disabled;"
function fn_press(event, type) {
	if(type == "numbers") {
	    if(event.keyCode < 48 || event.keyCode > 57) return false;
	    //onKeyDown일 경우 좌, 우, tab, backspace, delete키 허용 정의 필요
	}
}
//영어, 숫자만 - 해당 Input 속성 삽입
//onkeydown="fn_press_han(this);" style="ime-mode:disabled;"
function fn_press_han(obj) {
	//좌우 방향키, 백스페이스, 딜리트, 탭키에 대한 예외
	if (event.keyCode == 8 || event.keyCode == 9 || event.keyCode == 37 || event.keyCode == 39 || event.keyCode == 46) {
		return;
	}
	obj.value = obj.value.replace(/[\ㄱ-ㅎㅏ-ㅣ가-힣]/g, '');
}
//이메일 유효성 검사
function validateEmail(email) {
	  var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
	  return re.test(email);
}

