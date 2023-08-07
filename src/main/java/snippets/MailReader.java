package snippets;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.event.MessageChangedEvent;
import javax.mail.event.MessageChangedListener;
import javax.mail.event.MessageCountAdapter;
import javax.mail.event.MessageCountEvent;

public class MailReader {

	public static void main(String[] args) throws MessagingException, InterruptedException {
		System.out.println("Hello... reader...");
		read();
		Thread.currentThread().join();
		System.out.println("Completed...");
	}

	public static void read() throws MessagingException {
		String host = "imap.gmail.com";
		String userName = "udaykiran0486@gmail.com";
		String password = "aqrqseqwlgxsyosn";

		Properties properties = new Properties();
//		properties.put("mail.transport.protocol", "imap");
		properties.put("mail.imap.host", host);
		properties.put("mail.imap.port", "995");
		properties.put("mail.imap.ssl.enable", true);

		Session session = Session.getDefaultInstance(properties);
		Store store = session.getStore("imaps");
		store.connect(host, userName, password);

		System.out.println("isConnected? " + store.isConnected());

		Folder inbox = store.getFolder("Inbox");
		inbox.open(Folder.READ_ONLY);


		inbox.addMessageChangedListener(new MessageChangedListener() {
			@Override
		    public void messageChanged(MessageChangedEvent e) {
				System.out.println("Changed... message");
				try {
					System.out.println("Message changed: " + e.getMessage().getSubject());
				} catch (MessagingException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
			}
		});
		inbox.addMessageCountListener(new MessageCountAdapter() {
			@Override
			public void messagesAdded(MessageCountEvent event) {
				System.out.println("Helloo, message received, " + event.getMessages().length);
				Message[] messages = event.getMessages();
				for (Message message : messages) {
					try {
						System.out.println("Added : " + message.getSubject());
						System.out.println("Content Type: " + message.getContentType());
						if (message.getContent() instanceof String) {
							String content = (String) message.getContent();
							System.out.println("Content : " + content);
						}
						System.out.println();
					} catch (MessagingException | IOException ex) {
						ex.printStackTrace();
					}
				}
			}
		});

		System.out.println("isOpen? " + inbox.isOpen() + " isSubscribed? " + inbox.isSubscribed());
		System.out.println("Listening started...");
	}
}
