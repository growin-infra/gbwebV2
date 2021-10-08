/* 해당 사이트에만 적용 됨 */


/* 팝업 열고 닫기 */
function layerShow(){
	$(".layerPop").css({top:"0"});
}
function layerClose(){
	$(".layerPop").css({top:"-9999999px"});
}

/* 팝업 열고 닫기2 */
function layerShow2(){
	$(".layerPop2").css({top:"0"});
}
function layerClose2(){
	$(".layerPop2").css({top:"-9999999px"});
}



//대현 170516 백업서버 메뉴 클릭
$(document).ready(function(){

	var	tit = $('.server>li>a');       //1deps
	var sub = $('.server>li>ul>li>a'); //2deps
	
	var list_t = $('.tit_list'); //폼1
	var list_s = $('.sub_list'); //폼2
	
	var none = {display:'none'};
	var block = {display:'block'};
	
	$('.server>li>ul').css({display:'none'}); // 2deps 닫고 시작.
	
	tit.on('dblclick', function(){    // 1deps 클릭했을 때
		$('.change_text').text('관리서버 등록');  //폼 위의 제목 변경
		$(this).next().toggle(200);               // 메뉴 토글
		$('.server_list').css({display:'none'});
		 tit.removeClass('on');
		 $(this).addClass('on');
		 
		 if($(this).hasClass('on') == false){
			 list_t.css(block);
			 list_s.css(none);
		 }else{
			 list_t.css(block);
			 list_s.css(none);
		 }
	});
	
	sub.on('click', function(){     // 2deps 클릭했을 때
		$('.change_text').text('대상서버 등록'), //폼 위의 제목 변경
		$('.server_list').css({display:'none'});
		sub.removeClass('on');
		$(this).addClass('on');
		
		if($(this).hasClass('on') == true){
			list_s.css(block);
			if($(this).parent().parent().parent().siblings().children().hasClass('on') == true){
				list_t.css(none);
			}else{
				list_s.css(block);
				list_t.css(none);
			}
		}else{
			list_s.css(block);
		}
	});

});

//Souce DB > Backup Source Type 셀렉트 선택시 
//$(document).ready(function(){
//	$(".backup_select").change(function(){
//		var idx = $(".backup_select option:selected").index();		
//		$(".sourceType").removeClass("on").eq(idx).addClass("on");		
//	});
//});




