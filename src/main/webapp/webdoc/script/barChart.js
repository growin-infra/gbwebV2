function ginianBarChart(sServerName, sRateValue, sSizeValue, idCount) {
	
	
	var sHtml = "";
	var serverName = sServerName;
	var rateValue = sRateValue;
	var sizeValue = sSizeValue;
		sHtml +="<div class=\"barChartBody\">\n"
		sHtml +=    "<div class=\"barChartTopText\">\n"
	if(rateValue < 50){
		sHtml +=        "<span class=\"barChartNameValue1\">"+ serverName +"</span>\n"
	    sHtml +=        "<span class=\"barChartDataValue1\">"+ sizeValue +"</span>\n"
	}else if(rateValue >= 50 && rateValue < 80){
		sHtml +=        "<span class=\"barChartNameValue2\">"+ serverName +"</span>\n"
	    sHtml +=        "<span class=\"barChartDataValue2\">"+ sizeValue +"</span>\n"
	}else{
		sHtml +=        "<span class=\"barChartNameValue3\">"+ serverName +"</span>\n"
	    sHtml +=        "<span class=\"barChartDataValue3\">"+ sizeValue +"</span>\n"
	}
	    sHtml +=    "</div>\n"
	    sHtml +=    "<div class=\"barChart\">\n"
	if(rateValue < 50){
		sHtml +=        "<div id='id_barChart_"+idCount+"' class=\"barChartColorBlue\">\n"
	}else if(rateValue >= 50 && rateValue < 80){
		sHtml +=        "<div id='id_barChart_"+idCount+"'  class=\"barChartColorOrange\">\n"
	}else{
		sHtml +=        "<div id='id_barChart_"+idCount+"' class=\"barChartColorRed\">\n"
	}
	    sHtml +=        "<div class=\"barChartRateValue\">"+ rateValue + "%" +"</div>\n"
	    sHtml +=        "</div>\n"
	    sHtml +=        "<div class=\"barChartBg\"></div>\n"
	    sHtml +=    "</div>\n"
	    sHtml +="</div>\n"
	var id = "id_barChart_"+ idCount;
	$("#barChart").append(sHtml);
	
	if(ieVersion == true){
		$("#"+id).animate({
			width: sRateValue + "%"
		}, 500 );
	}else{
		if(rateValue < 50){
			$("#"+id).css("background" , "#0087c0");
		}else if(rateValue >= 50 && rateValue < 80){
			$("#"+id).css("background" , "#e78200");
		}else{
			$("#"+id).css("background" , "#ef3e00");
		}
		$("#"+id).css("width" , sRateValue + "%");
	}
	
}