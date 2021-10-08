package gb.common.util;

import egovframework.rte.ptl.mvc.tags.ui.pagination.AbstractPaginationRenderer;

public class CustomPaginationRenderer extends AbstractPaginationRenderer {

	public CustomPaginationRenderer() {
//		firstPageLabel = "<a href=\"#\" onclick=\"{0}({1}); return false;\"><image src=\"/easycompany/images/bt_first.gif\" border=0/></a>";
//		previousPageLabel = "<a href=\"#void\" onclick=\"{0}({1}); return false;\"><image src=\"/easycompany/images/bt_prev.gif\" border=0/></a>";
//		currentPageLabel = "<strong>{0}</strong>";
//		otherPageLabel = "<a href=\"#void\" onclick=\"{0}({1}); return false;\">{2}</a>";
//		nextPageLabel = "<a href=\"#void\" onclick=\"{0}({1}); return false;\"><image src=\"/easycompany/images/bt_next.gif\" border=0/></a>";
//		lastPageLabel = "<a href=\"#void\" onclick=\"{0}({1}); return false;\"><image src=\"/easycompany/images/bt_last.gif\" border=0/></a>";
		
		firstPageLabel = "<li class='first'><a href=\"#void\" onclick=\"{0}({1}); return false;\">처음</a></li>";
		previousPageLabel = "<li class='pre'><a href=\"#void\" onclick=\"{0}({1}); return false;\">이전</a></li>";
		currentPageLabel = "<li class='on'><a href=\"#void\">{0}</a></li>";
		otherPageLabel = "<li class='none'><a href=\"#void\" onclick=\"{0}({1}); return false;\">{2}</a></li>";
		nextPageLabel = "<li class='next'><a href=\"#void\" onclick=\"{0}({1}); return false;\">다음</a></li>";
		lastPageLabel = "<li class='last'><a href=\"#void\" onclick=\"{0}({1}); return false;\">끝</a></li>";
	}

}
