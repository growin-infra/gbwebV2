package gb.common.service;

import java.util.Map;

public interface CommonService {
	
	Map<String, Object> selectFileInfo(Map<String, Object> map) throws Exception;
	
	Map<String, Object> lcnInfo(Map<String, Object> map) throws Exception;
	
}

