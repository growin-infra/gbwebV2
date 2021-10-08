package gb.common.handler;

import java.util.Map;

public interface MessageHandler {
	public static enum ReturnType{
		BOOLEAN,
		LIST,
		INFO
	};
	public static enum ResultType{
		SUCCESS,
		FAILURE
	}
	
	public void setResultType(ResultType result);
	
	public void setReturnType(ReturnType result);

	public void setReturnCode(String code);

	public void setReturnMessage(String message);
		
	public void setTotalCount(int count);
		
	public void setColumns(String columns);

	public void setRows(Object o);
	
	public Map<String,Object> getResultMessage();
	
}
