package snippets;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.function.Consumer;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mailer {

	public static Properties properties;
	public static String host = "smtp.gmail.com", port = "587";
	public static Logger logger = LogManager.getLogger(Mailer.class);

	static {
		properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.port", port);
		properties.put("mail.smtp.starttls.enable", "true");
	}

	private Session session;
	private Message message;
	private MimeMultipart multipart;

	private Mailer(String user, String password) {
		session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
		session.setDebug(false);
		message = new MimeMessage(session);
		multipart = new MimeMultipart();
	}

	public Mailer subject(String subject) {
		try {
			message.setSubject(subject);
		} catch (Exception e) {
			logger.fatal("Failed to setup subject, " + e);
		}
		return this;
	}

	public Mailer from(String from) {
		try {
			message.setFrom(new InternetAddress(from));
		} catch (MessagingException e) {
			logger.fatal("Failed to setup from, " + e);
		}
		return this;
	}

	public Mailer to(String address) {
		return setRecipient(RecipientType.TO, address);
	}

	public Mailer setRecipient(javax.mail.Message.RecipientType type, String address) {
		try {
			message.setRecipient(type, new InternetAddress(address));
		} catch (Exception e) {
			logger.fatal("Failed to setup recipient, " + e);
		}
		return this;
	}

	public Mailer text(String text) {
		try {
			message.setText(text);
		} catch (Exception e) {
			logger.error("Failed to set text as text/plain, " + e);
		}
		return this;
	}

	public Mailer body(MimeBodyPart bodyPart) {
		try {
			multipart.addBodyPart(bodyPart);
		} catch (MessagingException e) {
			logger.error("Failed to add body, " + e);
		}
		return this;
	}

	public Mailer attach(File file) {
		MimeBodyPart bodyPart = new MimeBodyPart();
		try {
			bodyPart.attachFile(file);
			bodyPart.setFileName(file.getName());
			body(bodyPart);
		} catch (IOException | MessagingException e) {
			logger.error("Failed attaching file, " + e);
		}
		return this;
	}

	public static boolean sendWithDefaultCredentials(Consumer<Mailer> task) {
		return send(task, "udaykiran0486@gmail.com", "aqrqseqwlgxsyosn");
	}

	public static boolean send(Consumer<Mailer> task, String user, String password) {
		Mailer mailer = new Mailer(user, password);
		task.accept(mailer);

		try {
			if (mailer.multipart.getCount() != 0)
				mailer.message.setContent(mailer.multipart);
		} catch (MessagingException e) {
			logger.error("Failed adding multiple file body parts of mail, " + e);
		}

		try {
			logger.info("Sending...");
			Transport.send(mailer.message);
			logger.info("Sent mail");
			return true;
		} catch (MessagingException e) {
			logger.info("Failed sending mail, " + e);
			return false;
		}
	}
}
