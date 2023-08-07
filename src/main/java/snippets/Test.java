package snippets;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Test {

	public static void main(String[] args) throws Exception {
		System.out.println("Hello...");
		read();
//		System.out.println(filter("मेरा पेमेंट क्लियर हो चूका है मुझे सेटल में।,silencefound,माम् 28 नवंबर को शायद मैंने पेमेंट किया है ₹4200, ₹4200 पेमेंट किया हुआ है मैंने?"));
	}

	public static final Pattern AMOUNT_PATTERN = Pattern.compile("(₹?\\d+,\\d+|₹\\d+|\\b(?!2023|2022)\\d{3,20}\\b)");

	public static String filter(String text) {
		Matcher matcher = AMOUNT_PATTERN.matcher(text);
		while (matcher.find()) {
			text = text.replace(matcher.group(), "");
		}
		return text;
	}

	public static void read() throws Exception {
		String path = "C:\\Users\\uday.mekala\\Desktop\\MSIL pat and strings.xlsx";
		String text = java.nio.file.Files.readString(Path.of("C:\\Users\\uday.mekala\\Downloads\\MSIL pat and strings.txt"));

		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		int rowNo = 0;

		Scanner scanner = new Scanner(text);
		while (scanner.hasNext()) {
			String line = scanner.nextLine().trim();
			String[] words = line.split("::");

			Row row = sheet.createRow(rowNo++);
			int colNo = 0;

			for (String word : words) {
				word = word.trim();
				Cell cell = row.createCell(colNo++, CellType.STRING);
				cell.setCellValue(word);
			}
		}

		workbook.write(new FileOutputStream(new File(path), false));
		workbook.close();
		scanner.close();

		System.out.println("Completed...");
	}

}
