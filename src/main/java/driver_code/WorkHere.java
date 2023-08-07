package driver_code;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.IntStream;

import javax.naming.NamingException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class WorkHere {

	public static void main(String[] args) {
		System.out.println("Hello....");
		double x = -10.46;

		System.out.println(Math.round(x));
	}

	public static void mainn(String... args) throws NamingException, IOException, InterruptedException {
		System.out.println("Hey, Dev!");

		String userName = "omsai", password = "omsai";

		FTPClient client = new FTPClient();
		client.setConnectTimeout(5000);

//		client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		client.setRemoteVerificationEnabled(false);
		client.setListHiddenFiles(true);
		client.connect("172.20.10.2", 2121);
		client.enterLocalPassiveMode();
		System.out.println("reply code : " + client.getReplyCode());

		client.login(userName, password);

		System.out.println("------------------------------------");
		System.out.print("is connected ? ");
		System.out.println(client.isConnected() + " " + client.getReplyString());
		System.out.println("--------------------------------------");
		client.changeWorkingDirectory("/Download");
		System.out.println("pwd : " + client.printWorkingDirectory());

		System.out.println("no of directories : " + client.listNames().length);
		for (FTPFile file : client.listFiles()) {
			System.out.println("file: '%s'".formatted(file.getName()));
		}

		String dir = "omsai-new-directory";

		client.mkd(dir);
		client.changeWorkingDirectory(dir);
		String content = "Hey, fuck sai";

//		client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));

		System.out.println("Files in dir %s are %d".formatted(client.printWorkingDirectory(), client.listFiles().length));

		IntStream.range(0, 10000).parallel().forEach(e -> {
			InputStream stream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
			try {
				client.storeFile("omsai-%d.txt".formatted(e), stream);
				stream.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		});
		System.out.println("Files in dir %s are %d".formatted(client.printWorkingDirectory(), client.listFiles().length));

		System.out.println("Completed....");
	}
}
