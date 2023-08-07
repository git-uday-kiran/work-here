package driver_code;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringJoiner;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;

import controllers.ServiceUtils;
import utils.Validator;

public class Dev {

	public static void main(String[] args) throws InvalidFormatException, IOException {
		System.out.println("Hello....");
		read();
	}

	public static void read() throws InvalidFormatException, IOException {
		String path = "C:\\Users\\uday.mekala\\Desktop\\first_emi_welcome.xlsx";
		File file = new File(path);

		XSSFWorkbook workbook = new XSSFWorkbook(file);
		Sheet sheet = workbook.getSheetAt(0);

		for (Row row : sheet) {
			//हैलो।,हैलो।,वो चोर?,हाँ जी, मोहन बोल रहा हूँ।,चाय बनाने वाला।
			String customerName = row.getCell(0).getStringCellValue();
			String value = row.getCell(1).getStringCellValue();
			String[] values = value.split(",");

			StringJoiner joiner = new StringJoiner(",");

			for (String userMessage : values) {
				boolean rs = getResult(customerName, userMessage);
				joiner.add(String.valueOf(rs));
			}
			row.createCell(2).setCellValue(joiner.toString());
		}

		File outFile = new File("C:\\Users\\uday.mekala\\Desktop\\first_emi_welcome_result.xlsx");
		outFile.delete();
		outFile.createNewFile();

		workbook.write(new FileOutputStream(outFile));
		workbook.close();
		System.out.println("Completed task...");
	}

	public static boolean getResult(String customerName, String userMessage) {
		customerName = Validator.customerName(customerName);
		JSONObject json = new JSONObject();
		return ServiceUtils.matchedCustomerName(userMessage, customerName, userMessage + customerName, json, json);
	}
}
