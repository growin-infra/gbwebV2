package gb.common.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import gb.common.pagination.Pagination;

public class AbstractDAO {
	protected Log log = LogFactory.getLog(AbstractDAO.class);
	
	@Autowired
	private SqlSessionTemplate sqlSession;
	
	@Resource(name="commonDAO")
    private CommonDAO commonDAO;
	
	protected void printQueryId(String queryId) {
		if(log.isDebugEnabled()){
			log.debug("\t QueryId  \t:  " + queryId);
		}
	}
	
	
	/**
	 * @param queryId
	 * @param params
	 * @return
	 */
	public boolean insertB(String queryId, Object params){
		printQueryId(queryId);
		return sqlSession.insert(queryId, params) == 1 ? true:false;
	}
	
	/**
	 * @param queryId
	 * @param params
	 * @return
	 */
	public boolean updateB(String queryId, Object params){
		printQueryId(queryId);
		return sqlSession.update(queryId, params) > 0 ? true:false;
	}
	
	/**
	 * @param queryId
	 * @param params
	 * @return
	 */
	public boolean deleteB(String queryId, Object params){
		printQueryId(queryId);
		return sqlSession.delete(queryId, params) > 0 ? true:false;
	}
	
	
	
	public Object insert(String queryId, Object params){
		printQueryId(queryId);
		return sqlSession.insert(queryId, params);
	}
	
	public Object update(String queryId, Object params){
		printQueryId(queryId);
		return sqlSession.update(queryId, params);
	}
	
	public Object delete(String queryId, Object params){
		printQueryId(queryId);
		return sqlSession.delete(queryId, params);
	}
	
	public Object selectOne(String queryId){
		printQueryId(queryId);
		return sqlSession.selectOne(queryId);
	}
	
	public Object selectOne(String queryId, Object params){
		printQueryId(queryId);
		return sqlSession.selectOne(queryId, params);
	}
	
	@SuppressWarnings("rawtypes")
	public List selectList(String queryId){
		printQueryId(queryId);
		return sqlSession.selectList(queryId);
	}
	
	@SuppressWarnings("rawtypes")
	public List selectList(String queryId, Object params){
		printQueryId(queryId);
		return sqlSession.selectList(queryId,params);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map selectPagingList(String queryId, Object params) throws Exception{
		
		printQueryId(queryId);
		
	    Map<String,Object> map = (Map<String,Object>)params;
	    System.out.println("========= map ============");
	    System.out.println(map);
	    System.out.println("=========/map ============");
	    int page = 0;
	    if (map.containsKey("currentPageNo") == false || ObjectUtils.isEmpty(map.get("currentPageNo")) == true) {
	    	page = 1;
	    } else {
	    	page = Integer.parseInt(map.get("currentPageNo").toString());
	    }
	    System.out.println("page["+page+"]");
	    
//	    int start = paginationInfo.getFirstRecordIndex();
//	    /* MARIA */
//	    int end = paginationInfo.getRecordCountPerPage();
//	    map.put("START",start);
//	    map.put("END",end);
	    
//	    params = map;
	    
	    System.out.println("TABLE["+map.get("TABLE")+"]");
	    int rowCount = 0;
	    if ("STTTB".equals(map.get("TABLE"))) {
	    	rowCount = commonDAO.tbSTTTBCnt();
	    } else if ("USRTB".equals(map.get("TABLE"))) {
	    	rowCount = commonDAO.tbUSRTBCnt();
	    } else if ("BAKSCDTB".equals(map.get("TABLE"))) {
			rowCount = commonDAO.tbBAKSCDTBCnt();
		} else if ("MNGSVRTB".equals(map.get("TABLE"))) {
			rowCount = commonDAO.tbMNGSVRTBCnt();
		}
		System.out.println("rowCount["+rowCount+"]");
		
		Map<String,Object> returnMap = new HashMap<String,Object>();
//	    if(rowcnt == 0){
//	        map = new HashMap<String,Object>();
//	        map.put("TOTAL_COUNT",0);  
//	        //Pagination pagination = this.getPagination(page, 0, displayRow, pageCount);
//	        //returnMap.put("pagination", pagination);
//	    } else {
//	        rowCount = Integer.parseInt(list.get(0).get("TOTAL_COUNT").toString());
//	    }
	    
	    Pagination pagination = this.getPagination(page, rowCount, Integer.parseInt(map.get("displayRow").toString()), Integer.parseInt(map.get("pageCount").toString()));
	    returnMap.put("pagination", pagination);
	    
	    map.put("displayRow", pagination.getDisplayRow());
	    map.put("offset", pagination.getOffset());
	    
	    List<Map<String,Object>> list = sqlSession.selectList(queryId,map);
	    returnMap.put("result", list);
		
	    System.out.println("========= param ==============================");
	    System.out.println("getCurrentPage:"+pagination.getCurrentPage());
	    System.out.println("getDisplayRow:"+pagination.getDisplayRow());
	    System.out.println("getEndPage:"+pagination.getEndPage());
	    System.out.println("getLastPage:"+pagination.getLastPage());
	    System.out.println("getOffset:"+pagination.getOffset());
	    System.out.println("getPageCount:"+pagination.getPageCount());
	    System.out.println("getRowCount:"+pagination.getRowCount());
	    System.out.println("getStartPage:"+pagination.getStartPage());
	    System.out.println("========= /param =============================");
	    
		return returnMap;
	}
	
	public Pagination getPagination(int currentPage, int rowCount, int displayRow, int pageCount) {

		/*
		 * 예) 총 7페이지까지 있는데 현재 3페이지 일경우 페이징은 1 2 3 4 5 >> 출력되어야함
		 * currentPage = 3
		 * pageCount = 5 -> 한 화면에 리스트 5개씩 노출
		 * rowCount = 총 리스트 수 = 33개 가정
		 * startPage = 1
		 * endPage = 10에서 7로 변환하여 7
		 * lastPage = 7
		 * offset = 10 3페이지에 해당하는 리스트 열 시작점 -> 페이지당 5개의 리스트를 보여줄때 3페이지는 최근으로부터 16번째 리스트가 시작해야함 (화면출력 11 ~ 15번째이나 offset은 0부터 시작하기 때문에 10)
		 * 아래는 위 결과를 뽑기위한 계산식 아래 계산결과와 위의 예시가 맞으면 됨
		 */

		int startPage;
		int endPage;
		int lastPage;
		int offset;		

		//시작 페이지 = 1 ->  (3 - 1) / 5 * 5 + 1 = 1. (int형 이기때문에 2 / 5 = 0 나옴)
		startPage = (currentPage - 1) / pageCount * pageCount + 1;		

		// 현재 페이징 노출구간의 마지막 페이지 = 5 -> 1 + 5 - 1 = 5
		endPage = startPage + pageCount - 1;		

		//오프셋 = 10 -> 3페이지에 해당하는 리스트 시작위치 = (3 - 1) * 5
		offset = (currentPage - 1) * displayRow;		

		//마지막 페이지 = 7 -> (33 / 5) + 1 = 6 + 1 = 7
		lastPage = (rowCount / displayRow) + 1;		

		// 총 리스트 개수를 화면에 보여줄 개수로 mod 연산했을때 딱 맞아떨어진다면 마지막 페이지에서 -1 해줌 딱맞아 떨어지는 경우 마지막에 빈페이지가 나오기 때문
		// 예시에서는 33 % 5 = 0이 아니기 때문에 빼주지 않음
		if(rowCount % displayRow == 0){
			lastPage--;
		}		

		//현재 페이징 구간의 마지막 페이지가 실제 마지막 페이지보다 크다면 실제 마지막 페이지로 치환 시켜줌
		//실제 데이터는 7페이지까지 있으나 pageCount를 5로 설정했다면 최초 계산시 마지막 페이지는 5의 배수로 나오기 때문에 (6 ~ 10페이지) 6 7 8 9 10
		//실제 데이터가 있는 마지막 페이지로 치환하기 위함 10 -> 7 치환
		if(endPage > lastPage){
			endPage = lastPage;
		}		

		Pagination pagination = new Pagination();
		pagination.setCurrentPage(currentPage);
		pagination.setEndPage(endPage);
		pagination.setStartPage(startPage);
		pagination.setLastPage(lastPage);
        pagination.setPageCount(pageCount);
		pagination.setRowCount(rowCount);
		pagination.setDisplayRow(displayRow);
		pagination.setOffset(offset);	

		return pagination;

	}
	
	
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	public Map selectPagingList(String queryId, Object params){
//	    
//		printQueryId(queryId);
//	    
//	    Map<String,Object> map = (Map<String,Object>)params;
//	    PaginationInfo paginationInfo = null;
//	    if(map.containsKey("currentPageNo") == false || ObjectUtils.isEmpty(map.get("currentPageNo")) == true)
//	        map.put("currentPageNo","1");
//	    System.out.println("currentPageNo["+map.get("currentPageNo")+"]");
//	     
//	    paginationInfo = new PaginationInfo();
//	    paginationInfo.setCurrentPageNo(Integer.parseInt(map.get("currentPageNo").toString()));
//	    if(map.containsKey("PAGE_ROW") == false || ObjectUtils.isEmpty(map.get("PAGE_ROW")) == true){
//	        paginationInfo.setRecordCountPerPage(10);
//	    }
//	    else{
//	        paginationInfo.setRecordCountPerPage(Integer.parseInt(map.get("PAGE_ROW").toString()));
//	    }
//	    System.out.println("paginationInfo RecordCountPerPage["+paginationInfo.getRecordCountPerPage()+"]");
//	    paginationInfo.setPageSize(5);
//	    
//	    int start = paginationInfo.getFirstRecordIndex();
//	    /* MARIA */
//	    int end = paginationInfo.getRecordCountPerPage();
//	    map.put("START",start);
//	    map.put("END",end);
//	    
//	    params = map;
//	    System.out.println("params========================");
//	    System.out.println(params);
//	    System.out.println("//params======================");
//	     
//	    Map<String,Object> returnMap = new HashMap<String,Object>();
//	    List<Map<String,Object>> list = sqlSession.selectList(queryId,params);
//	     
//	    if(list.size() == 0){
//	        map = new HashMap<String,Object>();
//	        map.put("TOTAL_COUNT",0);  
//	        list.add(map);
//	         
//	        if(paginationInfo != null){
//	            paginationInfo.setTotalRecordCount(0);
//	            returnMap.put("paginationInfo", paginationInfo);
//	        }
//	    } else {
//	        if(paginationInfo != null){
//	            paginationInfo.setTotalRecordCount(Integer.parseInt(list.get(0).get("TOTAL_COUNT").toString()));
//	            returnMap.put("paginationInfo", paginationInfo);
//	        }
//	    }
//	    returnMap.put("result", list);
//	    System.out.println("page returnMap========================");
//	    System.out.println(returnMap.get("paginationInfo"));
//	    System.out.println("//page returnMap======================");
//	    return returnMap;
//	}
	
//	@SuppressWarnings("unchecked")
//	public Object selectPagingList(String queryId, Object params){
//		printQueryId(queryId);
//		Map<String,Object> map = (Map<String,Object>)params;
//		
//		String strPageIndex = (String)map.get("PAGE_INDEX");
//		String strPageRow = (String)map.get("PAGE_ROW");
//		int nPageIndex = 0;
//		int nPageRow = 20;
//		
//		if(StringUtils.isEmpty(strPageIndex) == false){
//			nPageIndex = Integer.parseInt(strPageIndex)-1;
//		}
//		if(StringUtils.isEmpty(strPageRow) == false){
//			nPageRow = Integer.parseInt(strPageRow);
//		}
//		map.put("START", (nPageIndex * nPageRow) + 1);
//		map.put("END", (nPageIndex * nPageRow) + nPageRow);
//		
//		return sqlSession.selectList(queryId, map);
//	}

}
