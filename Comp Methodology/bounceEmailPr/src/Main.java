import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


public class Main {
    final static Logger logger = Logger.getLogger(Main.class);


    public static void main(String[] args) {
        logger.info("Test");

        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");

        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.socketFactory.fallback", "false");


        Session session = Session.getDefaultInstance(properties);
        try {

            Store store = session.getStore("pop3s");
            store.connect("pop.gmail.com", "hwshmel1131@gmail.com",
                    "thisIsTestHw");

            // Create a Folder object and open the folder
            Folder folder = store.getFolder("inbox");
            folder.open(Folder.READ_ONLY);

            Message[] messages = folder.getMessages();
            if (messages.length != 0) {
                logger.info("Message was received");
                for (int i = 0, n = messages.length; i < n; i++) {
                    Message message = messages[i];

                    String from = InternetAddress.toString(message.getFrom());
                    String to = InternetAddress.toString(message
                            .getRecipients(Message.RecipientType.TO));
                    Message forward = new MimeMessage(session);

                    forward.setRecipients(Message.RecipientType.TO,
                            InternetAddress.parse(from));
                    forward.setSubject("Fwd: " + message.getSubject());
                    forward.setFrom(new InternetAddress(to));


                    MimeBodyPart messageBodyPart = new MimeBodyPart();

                    Multipart multipart = new MimeMultipart();

                    messageBodyPart.setContent(message, "message/rfc822");

                    multipart.addBodyPart(messageBodyPart);

                    forward.setContent(multipart);
                    forward.saveChanges();


                    Transport t = session.getTransport("smtp");
                    try {

                        t.connect("hwshmel1131@gmail.com", "thisIsTestHw");
                        t.sendMessage(forward, forward.getAllRecipients());
                        logger.info("message was sent");
                    } finally {

                        t.close();
                    }
                    folder.close(false);
                    store.close();
                }
            }
        } catch (Exception e) {
            logger.error("Somethings wrong" , e);
        }
    }
}
