package gb.common.service;

import gb.common.dao.CommonDAO;
import gb.common.util.FileUtils;
import gb.common.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
 
@Service("commonService")
@SuppressWarnings("unused")
public class CommonServiceImpl implements CommonService{
    Logger log = Logger.getLogger(this.getClass());
     
    @Resource(name="commonDAO")
    private CommonDAO commonDAO;
	
	@Resource(name = "fileUtils")
	private FileUtils fileUtils;
    
    @Override
    public Map<String, Object> selectFileInfo(Map<String, Object> map) throws Exception {
        return commonDAO.selectFileInfo(map);
    }
	
	@Override
	public Map<String, Object> lcnInfo(Map<String, Object> map) throws Exception {
		Map<String, Object> lcnInfoMap = new HashMap<String, Object>();
		int cnt = 0;
		String msg = "";
		String remainDt = "0";
		String fileUrl = map.get("fileUrl").toString();
    	if (!"".equals(fileUrl)) {
    		String fileCtxt = fileUtils.lcnReader(fileUrl);
    		if (fileCtxt != null && !"".equals(fileCtxt)) {
    			String ctArr[] = fileCtxt.split("_");
				String frm = ctArr[0];
				String svr = ctArr[1];
				String sdt = ctArr[2];
				String edt = ctArr[3];
				
				lcnInfoMap.put("svr", svr);
				lcnInfoMap.put("sdt", sdt);
				lcnInfoMap.put("edt", edt);
				
				Date today = new Date();
				Date startdate = new SimpleDateFormat("yyyy.MM.dd").parse(sdt);
				Date enddate = new SimpleDateFormat("yyyy.MM.dd").parse(edt);
				if (enddate != null) {
					remainDt = StringUtil.diffOfDate(today, enddate);
				}
				
				if (map.get("dvd") != null) {
					String dvd = map.get("dvd").toString();
					if ("svr".equals(dvd)) {
						cnt = commonDAO.btsSvrCnt();
						if (Integer.valueOf(svr) <= cnt) {
							msg = "인스턴스는 ["+svr+"]대 를 초과할 수 없습니다.\n라이선스 정보를 확인해 주십시오.";
						}
					} else if ("date".equals(dvd)) {
						if (enddate.getTime() < today.getTime()) {
							msg = "사용기간이 만료되었습니다.";
						}
					}
				}
				
				lcnInfoMap.put("remainDt", remainDt);
				
    		} else {
    			msg = "라이선스 정보가 없습니다.";
    		}
    		lcnInfoMap.put("msg", msg);
    	}
		return lcnInfoMap;
	}
	

}
