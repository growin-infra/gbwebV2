package gb.common.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jxls.exception.ParsePropertyException;
import net.sf.jxls.transformer.XLSTransformer;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

@Component
public class ExcelManager {
	
	Logger log = Logger.getLogger(this.getClass());

	// public ExcelManager() {}

	public String get_Filename() {
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddmmmm");
		return ft.format(new Date());
	}

	public String get_Filename(String pre) {
		return pre + get_Filename();
	}

	public void download(HttpServletRequest request,
			HttpServletResponse response, Map<String, Object> beans,
			String filename, String templateFile)
			throws ParsePropertyException, InvalidFormatException {
		String tempPath = request.getSession().getServletContext()
				.getRealPath("/WEB-INF/excel");

		try {
			InputStream is = new BufferedInputStream(new FileInputStream(
					tempPath + "/" + templateFile));
			XLSTransformer transformer = new XLSTransformer();
			Workbook resultWorkbook = transformer.transformXLS(is, beans);
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ filename + ".xlsx\"");
			OutputStream os = response.getOutputStream();
			resultWorkbook.write(os);

		} catch (IOException e) {
			e.printStackTrace();
			log.error("download IOException", e.fillInStackTrace());
		}
	}
}
