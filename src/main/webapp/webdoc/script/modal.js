function closeModal(){
	$(".modal").remove();
}
function closeModal2(){
	$(".modal2").remove();
}
//function closeModal(str){
//	console.log("closeModal str["+str+"]");
//	$(".modal").remove();
//	$("#"+str).focus();
//}

function openAlertModal(sTitle, sDescription, fnOK) {
	
	var sHtml = "";
	var title = sTitle;
	var description = sDescription;

	if (sDescription == null) {
		sDescription = "";
	}
	$("#modalDiv").remove();
	sHtml +="<div id='modalDiv' class=\"modal\">\n";
	sHtml +=	"<div class=\"modal-content\">\n";
	sHtml +=		"<div class=\"modal_inside\">\n";
	if(sTitle == "WARNING"){
		sHtml +=       		"<div class=\"modal_header_warning\">\n";
	}else if(sTitle == "ERROR"){
		sHtml +=       		"<div class=\"modal_header_error\">\n";
	}else if(sTitle == "SUCCESS"){
		sHtml +=       		"<div class=\"modal_header_success\">\n";
	}else if(sTitle == "INFORMATION"){
		sHtml +=       		"<div class=\"modal_header_success\">\n";
	}else{
		sHtml +=       		"<div class=\"modal_header_fail\">\n";
	}
	sHtml += 		           	"<div class=\"modal_header_div\">\n";
	if(sTitle == "WARNING"){
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/warning_modal_icon.png\"\n/></div>"					
	}else if(sTitle == "ERROR"){
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/fail_modal_icon.png\"\n/></div>"					
	}else if(sTitle == "SUCCESS"){
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/successs_modal_icon.png\"\n/></div>"					
	}else if(sTitle == "INFORMATION"){
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/info_modal_icon.png\"\n/></div>"					
	}else{
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/fail_modal_icon.png\"\n/></div>"					
	}
	
	sHtml +=         				"<div class=\"modal_title_div\"><p class=\"modal_title\">"+ title +"</p></div>\n";
	sHtml +=					"<img class=\"modal_x_btn\" src=\"/webdoc/images/btn/close_modal_icon.png\"  onclick=\"closeModal(this)\"/>\n";
	sHtml +=            		"</div>\n";
	sHtml +=       			"</div>\n";
	sHtml +=      			"<div class=\"modal-body\">\n";
	sHtml +=       			    "<p>\n";
	sHtml +=       			        description;
	sHtml +=       			    "</p>\n";
	sHtml +=       			"</div>\n";
	sHtml +=       			"<div class=\"modal_footer_line\"/>\n";
	sHtml +=       				"<div class=\"modal_footer\">\n";


	if(sTitle == "WARNING"){
			sHtml +=           		"<div class=\"modal_footer_warning\">\n"
			sHtml +=                     "<a href=\"#\" onclick=\"javascript:closeModal();\" >확 인</a>\n"
			sHtml +=                "</div>\n";
	}else if(sTitle == "ERROR"){
		sHtml +=           		"<div class=\"modal_footer_error\">\n"
			sHtml +=                     "<a href=\"#\" onclick=\"javascript:closeModal();\" >확 인</a>\n"
			sHtml +=                "</div>\n"
	}else if(sTitle == "SUCCESS"){
			sHtml +=           		"<div class=\"modal_footer_success\">\n"
			sHtml +=                     "<a href=\"#\" onclick=\"javascript:closeModal();\" >확 인</a>\n"
			sHtml +=                "</div>\n";
	}else if(sTitle == "INFORMATION"){
			sHtml +=           		"<div class=\"modal_footer_success\">\n"
			sHtml +=                     "<a href=\"#\" onclick=\"javascript:closeModal();\" >확 인</a>\n"
			sHtml +=                "</div>\n";
	}else{
			sHtml +=           		"<div class=\"modal_footer_fail\">\n"
			sHtml +=                     "<a href=\"#\" onclick=\"javascript:closeModal();\" >확 인</a>\n"
			sHtml +=                "</div>\n";
	}
	sHtml +=       		"</div>\n";	
	sHtml +=		"</div>\n";
	sHtml +=	"</div>\n";
	sHtml +="</div>\n";
	$("body").append(sHtml);
	$("#modal_footer_warning").click(function() {
		if (fnOK != null) {
			fnOK.call();
		}
	});
}

function openModalStop(sTitle, sDescription, fnOK) {
	
	var sHtml = "";
	var title = sTitle;
	var description = sDescription;

	if (sDescription == null) {
		sDescription = "";
	}
	$("#modalDiv").remove();
	sHtml +="<div id='modalDiv' class=\"modal\">\n";
	sHtml +=	"<div class=\"modal-content\">\n";
	sHtml +=		"<div class=\"modal_inside\">\n";
	if(sTitle == "WARNING"){
		sHtml +=       		"<div class=\"modal_header_warning\">\n";
	}else if(sTitle == "ERROR"){
		sHtml +=       		"<div class=\"modal_header_error\">\n";
	}else if(sTitle == "SUCCESS"){
		sHtml +=       		"<div class=\"modal_header_success\">\n";
	}else if(sTitle == "INFORMATION"){
		sHtml +=       		"<div class=\"modal_header_success\">\n";
	}else{
		sHtml +=       		"<div class=\"modal_header_fail\">\n";
	}
	sHtml += 		           	"<div class=\"modal_header_div\">\n";
	if(sTitle == "WARNING"){
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/warning_modal_icon.png\"\n/></div>"					
	}else if(sTitle == "ERROR"){
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/fail_modal_icon.png\"\n/></div>"					
	}else if(sTitle == "SUCCESS"){
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/successs_modal_icon.png\"\n/></div>"					
	}else if(sTitle == "INFORMATION"){
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/info_modal_icon.png\"\n/></div>"					
	}else{
		sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/fail_modal_icon.png\"\n/></div>"					
	}
	
	sHtml +=         				"<div class=\"modal_title_div\"><p class=\"modal_title\">"+ title +"</p></div>\n";
	sHtml +=					"<img class=\"modal_x_btn\" src=\"/webdoc/images/btn/close_modal_icon.png\"  onclick=\"closeModal(this)\"/>\n";
	sHtml +=            		"</div>\n";
	sHtml +=       			"</div>\n";
	sHtml +=      			"<div class=\"modal-body\">\n";
	sHtml +=       			    "<p>\n";
	sHtml +=       			        description;
	sHtml +=       			    "</p>\n";
	sHtml +=       			"</div>\n";
	sHtml +=       			"<div class=\"modal_footer_line\"/>\n";
	sHtml +=       				"<div class=\"modal_footer\">\n";

	if(sTitle == "WARNING"){
			sHtml +=           		"<div class=\"modal_footer_warning\">\n"
			sHtml +=                     "<a href=\"#\" id=\"confirmModalOk\" onclick=\"javascript:continueModal(this);\" >확 인</a>\n"
			sHtml +=                "</div>\n";
	}else if(sTitle == "ERROR"){
		sHtml +=           		"<div class=\"modal_footer_error\">\n"
			sHtml +=                     "<a href=\"#\" id=\"confirmModalOk\" onclick=\"javascript:continueModal(this);\" >확 인</a>\n"
			sHtml +=                "</div>\n"
	}else if(sTitle == "SUCCESS"){
			sHtml +=           		"<div class=\"modal_footer_success\">\n"
			sHtml +=                     "<a href=\"#\" id=\"confirmModalOk\" onclick=\"javascript:continueModal(this);\" >확 인</a>\n"
			sHtml +=                "</div>\n";
	}else if(sTitle == "INFORMATION"){
			sHtml +=           		"<div class=\"modal_footer_success\">\n"
			sHtml +=                     "<a href=\"#\" id=\"confirmModalOk\" onclick=\"javascript:continueModal(this);\" >확 인</a>\n"
			sHtml +=                "</div>\n";
	}else{
			sHtml +=           		"<div class=\"modal_footer_fail\">\n"
			sHtml +=                     "<a href=\"#\" id=\"confirmModalOk\" onclick=\"javascript:continueModal(this);\" >확 인</a>\n"
			sHtml +=                "</div>\n";
	}
	sHtml +=       		"</div>\n";	
	sHtml +=		"</div>\n";
	sHtml +=	"</div>\n";
	sHtml +="</div>\n";
	$("body").append(sHtml);
	$("#confirmModalOk").click(function() {
		if (fnOK != null) {
			fnOK.call();
		}
	});
}

function continueModal(e) {
	$(e).parents('.modal').removeClass('on');
}


function openConfirmModal(sTitle, sDescription, fnOK) {
	var sHtml = "";
	var title = sTitle;
	var description = sDescription;

	if (sDescription == null) {
		sDescription = "";
	}
		
	sHtml +="<div id=\"modalDiv\" class=\"modal\">\n";
	sHtml +=	"<div class=\"modal-content\">\n";
	sHtml +=		"<div class=\"modal_inside\">\n";
	sHtml +=       		"<div class=\"modal_header_confirm\">\n";
	sHtml += 		           	"<div class=\"modal_header_div\">\n";
	sHtml +=            			"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/confirm_modal_icon.png\"\n/></div>"					
	sHtml +=         				"<div class=\"modal_title_div\"><p class=\"modal_title\">"+ title +"</p></div>\n";
	sHtml +=						"<img class=\"modal_x_btn\" src=\"/webdoc/images/btn/close_modal_icon.png\"  onclick=\"closeModal(this)\"/>\n";
	sHtml +=            		"</div>\n";
	sHtml +=       			"</div>\n";
	sHtml +=      			"<div class=\"modal-body\">\n";
	sHtml +=       			    "<p>\n";
	sHtml +=       			        description;
	sHtml +=       			    "</p>\n";
	sHtml +=       			"</div>\n";
	sHtml +=       			"<div class=\"modal_footer_line\"/>\n";
	sHtml +=       				"<div class=\"modal_footer\">\n";
	sHtml +=	          	 	"<div class=\"modal_header_div\">\n";
	sHtml += 	          			"<div class=\"modal_title_div\" id=\"modal_footer_confirm_ok\">\n"
	sHtml +=    	         	        "<a href=\"#\" id=\"confirmModalOk\" onclick=\"javascript:continueModal(this);\" >확 인</a>\n"
	sHtml +=        	   		    "</div>\n";
	sHtml +=           				"<div class=\"modal_title_div\" id=\"modal_footer_confirm_cancel\">\n"
	sHtml +=                		     "<a href=\"#\" id=\"confirmModalCancel\" onclick=\"javascript:closeModal(this);\" >취 소   </a>\n"
	sHtml +=                	"</div>\n";
	sHtml +=                "</div>\n";
	sHtml +=       		"</div>\n";	
	sHtml +=		"</div>\n";
	sHtml +=	"</div>\n";
	sHtml +="</div>\n";
	$("body").append(sHtml);
	
	$("#confirmModalOk").click(function() {
		if (fnOK != null) {
			fnOK.call();
		}
	});
}

function bakRunModal(sTitle, sDescription, fnOK) {
	var sHtml = "";
	var title = sTitle;
	var description = sDescription;
	
	if (sDescription == null) {
		sDescription = "";
	}
	
	sHtml +="<div id=\"modalDiv\" class=\"modal\">\n";
	sHtml +=	"<div class=\"modal-content\">\n";
	sHtml +=		"<div class=\"modal_inside\">\n";
	sHtml +=       		"<div class=\"modal_header_confirm\">\n";
	sHtml += 		           	"<div class=\"modal_header_div\">\n";
	sHtml +=            		"<div class=\"modal_title_div\"><img class=\"modal_header_icon\" src=\"/webdoc/images/btn/fail_modal_icon.png\"\n/></div>"
	sHtml +=         				"<div class=\"modal_title_div\"><p class=\"modal_title\">"+ title +"</p></div>\n";
	sHtml +=						"<img class=\"modal_x_btn\" src=\"/webdoc/images/btn/close_modal_icon.png\"  onclick=\"closeModal(this)\"/>\n";
	sHtml +=            		"</div>\n";
	sHtml +=       			"</div>\n";
	sHtml +=      			"<div class=\"modal-body\">\n";
	sHtml +=       			    "<p>\n";
	sHtml +=       			        description;
	sHtml +=       			    "</p>\n";
	sHtml +=       			"</div>\n";
	sHtml +=       			"<div class=\"modal_footer_line\"/>\n";
	sHtml +=       				"<div class=\"modal_footer\">\n";
	sHtml +=	          	 	"<div class=\"modal_header_div\">\n";
	sHtml +=           			"<div class=\"modal_footer_error\">\n"
	sHtml +=    	        		"<a href=\"#\" id=\"confirmModalOk\" onclick=\"javascript:continueModal(this);\" >확 인</a>\n"
	sHtml +=                	"</div>\n"
	sHtml +=                "</div>\n";
	sHtml +=       		"</div>\n";	
	sHtml +=		"</div>\n";
	sHtml +=	"</div>\n";
	sHtml +="</div>\n";
	$("body").append(sHtml);
	
	$("#confirmModalOk").click(function() {
		if (fnOK != null) {
			fnOK.call();
		}
	});
	
}


function bakAndRestoreModal(sTitle, sDescription, fnOK) {
	var sHtml = "";
	if (sTitle == "BACKUP"){
		var title = "백업중입니다"
	}else{
		var title = "복구중입니다"
	}
	var description = sDescription;
	
	if (sDescription == null) {
		sDescription = "";
	}
	
	sHtml +="<div id=\"modalDiv\" class=\"modal2\">\n";
	sHtml +=	"<div class=\"modal-content2\">\n";
	sHtml +=		"<div class=\"modal_inside2\">\n";
	sHtml +=       		"<div class=\"modal_header_confirm2\">\n";
	sHtml += 		           	"<div class=\"modal_header_div2\">\n";
	sHtml +=         				"<div class=\"modal_title_div\"><p class=\"modal_title2\">"+ title +"</p></div>\n";
	sHtml +=            		    "<div class=\"modal_title_div\"><img class=\"modal_header_icon2\" src=\"/webdoc/images/loading/bak_and_restore.gif\"\n/></div>"
	sHtml +=            		"</div>\n";
	sHtml += 		           	"<div class=\"modal_header_div2\">\n";
	sHtml +=         				"<div class=\"modal_title_div\"><p class=\"modal_title3\">"+ description +"</p></div>\n";
	sHtml +=            		"</div>\n";
    sHtml +=       	    "</div>\n";
	sHtml +=		"</div>\n";
	sHtml +=	"</div>\n";
	sHtml +="</div>\n";
	$("body").append(sHtml);
	
	$("#confirmModalOk").click(function() {
		if (fnOK != null) {
			fnOK.call();
		}
	});
	
}

