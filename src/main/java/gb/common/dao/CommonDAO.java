package gb.common.dao;

import java.util.Map;

import org.springframework.stereotype.Repository;

@Repository("commonDAO")
public class CommonDAO extends AbstractDAO{

	@SuppressWarnings("unchecked")
	public Map<String, Object> selectFileInfo(Map<String, Object> map) throws Exception{
	    return (Map<String, Object>)selectOne("common.selectFileInfo", map);
	}
	
	public int btsSvrCnt() throws Exception {
		return (int)selectOne("common.btsSvrCnt");
	}
	
	public int tbSTTTBCnt() throws Exception {
		return (int)selectOne("common.tbSTTTBCnt");
	}
	
	public int tbUSRTBCnt() throws Exception {
		return (int)selectOne("common.tbUSRTBCnt");
	}
	
	public int tbBAKSCDTBCnt() throws Exception {
		return (int)selectOne("common.tbBAKSCDTBCnt");
	}
	
	public int tbMNGSVRTBCnt() throws Exception {
		return (int)selectOne("common.tbMNGSVRTBCnt");
	}

}
