package gb.common.handler;

import java.util.HashMap;
import java.util.Map;

public class JsonMessageHandler implements MessageHandler {

	public Map<String,Object> model;
	
	public static final String RESULT_TYPE_KEY = "result";
    public static final String RETURN_TYPE_KEY = "type";
    public static final String RETURN_CODE_KEY = "code";
    public static final String RETURN_TOTALCOUNT_KEY = "totalcount"; 
    public static final String RETURN_MESSAGE_KEY = "message";
	
    public static final String RETURN_COLUMNS_KEY = "columns";
    public static final String RETURN_ROWS_KEY ="rows";
    	
    public JsonMessageHandler(){
    	this.model = new HashMap<String,Object>();
    	this.model.put(RESULT_TYPE_KEY,""); 
    	this.model.put(RETURN_TYPE_KEY,"");
    	this.model.put(RETURN_CODE_KEY,"");
    	this.model.put(RETURN_TOTALCOUNT_KEY,"");
    	this.model.put(RETURN_MESSAGE_KEY,"");
    	this.model.put(RETURN_COLUMNS_KEY,"");
    	this.model.put(RETURN_ROWS_KEY,"");
	}
    
    public void setResultType(ResultType result){
		switch(result){
			case SUCCESS : this.model.put(RESULT_TYPE_KEY,"true"); break;
			case FAILURE : this.model.put(RESULT_TYPE_KEY,"false"); break;
			default : this.model.put(RESULT_TYPE_KEY,"true"); break;
		}
	}
    
	public void setReturnType(ReturnType result){
		switch(result){
			case BOOLEAN : this.model.put(RETURN_TYPE_KEY,"boolean"); break;
			case LIST : this.model.put(RETURN_TYPE_KEY,"list"); break;
			case INFO : this.model.put(RETURN_TYPE_KEY,"info"); break;
			default : this.model.put(RETURN_TYPE_KEY,"boolean"); break;
		}
	}
	
	/**
	 *반환 코드 
	 */
	public void setReturnCode(String code){
		this.model.put(RETURN_CODE_KEY,code);
	}
	/**
	 * 메세지 
	 * @param message
	 */
	public void setReturnMessage(String message){
		this.model.put(RETURN_MESSAGE_KEY,message);
	}
	
	public void setTotalCount(int count){
		this.model.put(RETURN_TOTALCOUNT_KEY,count);
	}
	
	public void setColumns(String columns){
		this.model.put(RETURN_COLUMNS_KEY,columns);
	}
	public void setRows(Object o){
		this.model.put(RETURN_ROWS_KEY,o);
	}

	public Map<String,Object> getResultMessage(){
		return this.model;
	}
}
