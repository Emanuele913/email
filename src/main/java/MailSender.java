import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;

public class MailSender {

    @Autowired
    GenerateInfo generateInfo;

    String smtpHost;

    String smtpPort = "465";

    String subject;

    String authUsername;

    String authPassword;

    boolean hasAuth = false;

    boolean debug = false;

    List<InternetAddress> emailTos;

    List<InternetAddress> emailCcs;

    List<InternetAddress> emailBccs;

    String emailFrom;

    String senderName;

    Boolean isHtml = false;

    Boolean isAttach = false;

    String messageText;

    List<String> attachs;

    private static String[] arguments;

    private static final int SUCCESS = 0;

    private static final int FAILURE = 1;

    public static String retrieveArgument(int index) {
        if (arguments.length > index) {
            if (arguments[index] != null && !arguments[index].isEmpty()) {
                return arguments[index];
            }
        }
        System.exit(FAILURE);
        return null;
    }

    public void sendMail() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.auth", String.valueOf(hasAuth));
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.ssl.enable", "true");

        Session session = Session.getDefaultInstance(props);
        if (hasAuth) {
            session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(authUsername, authPassword);
                }
            });
        } else {
            session = Session.getInstance(props);
        }

        session.setDebug(debug);

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom, senderName));
            if (emailTos != null)
                message.setRecipients(RecipientType.TO, emailTos.toArray(new InternetAddress[emailTos.size()]));
            if (emailCcs != null)
                message.setRecipients(RecipientType.CC, emailCcs.toArray(new InternetAddress[emailCcs.size()]));
            if (emailBccs != null)
                message.setRecipients(RecipientType.BCC, emailBccs.toArray(new InternetAddress[emailBccs.size()]));

            message.setSubject(subject);

            if (isHtml) {
                message.setContent(messageText, "text/html");
            } else {
                /// Poichè deve essere allegata una tabella prevediamo che il contenuto
                /// sia solo in formato HTML
                message.setText("Not valid Message");
            }

            if (isAttach){
                Multipart multipart = new MimeMultipart();
                try {
                    /// Per ogni attach presente in lista andiamo a creare un nuovo Oggetto da allegare alla mail
                    for( String attach : attachs) {
                        MimeBodyPart attachmentPart = new MimeBodyPart();
                        File f = new File(attach);
                        System.out.println(f.getName());
                        attachmentPart.attachFile(f);
                        multipart.addBodyPart(attachmentPart);
                    }
                    // Set HTML mail to send
                    MimeBodyPart htmlPart = new MimeBodyPart();
                    htmlPart.setContent( messageText, "text/html; charset=utf-8" ); //<<<<< Rappresentazione in HTML
                    multipart.addBodyPart(htmlPart);
                    message.setContent(multipart);
                } catch (IOException e) {

                    e.printStackTrace();

                }


            }


            Transport.send(message);
            System.out.println("Email sent successfully");
            System.exit(SUCCESS);

        } catch (MessagingException e) {
            System.err.println(e.getMessage());
            System.exit(FAILURE);
        } catch (UnsupportedEncodingException e) {
            System.err.println(e.getMessage());
            System.exit(FAILURE);
        }
    }

    public static void main(String[] args) {

        if (args == null || args.length == 0) {
            System.err.println("No Argument Specified");
            System.exit(FAILURE);
        }

        arguments = args;

        final MailSender mailSender = new MailSender();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-host":
                    mailSender.smtpHost = retrieveArgument(i + 1);
                    break;
                case "-port":
                    mailSender.smtpPort = retrieveArgument(i + 1);
                    break;
                case "-from":
                    String[] sender = retrieveArgument(i + 1).split(":");
                    mailSender.emailFrom = sender[0];
                    if (sender.length == 2) {
                        mailSender.senderName = sender[1];
                    } else {
                        mailSender.senderName = sender[0];
                    }
                    break;
                case "-subject":
                    mailSender.subject = retrieveArgument(i + 1);
                    break;
                case "-auth":
                    String[] creds = retrieveArgument(i + 1).split(":");
                    mailSender.hasAuth = true;
                    mailSender.authUsername = creds[0];
                    mailSender.authPassword = creds[1];
                    break;
                    /// Omettiamo il case -message poichè caricato dal GetInfo dell'oggetto GenerateIndo
                    /*
                case "-message":
                    String message = retrieveArgument(i + 1);

                    break;*/
                case "-H":
                    mailSender.isHtml = true;
                    break;
                case "-debug":
                    mailSender.debug = true;
                    break;
                case "-to":
                    String[] tos = retrieveArgument(i + 1).split(":");
                    mailSender.emailTos = new ArrayList<>();
                    for (String email : tos) {
                        try {
                            mailSender.emailTos.add(new InternetAddress(email));
                        } catch (AddressException e) {
                            System.err.println(String.format("Invalid email: %s", email));
                        }
                    }
                    break;
                case "-bcc":
                    String[] bccs = retrieveArgument(i + 1).split(":");
                    mailSender.emailBccs = new ArrayList<>();
                    for (String email : bccs) {
                        try {
                            mailSender.emailBccs.add(new InternetAddress(email));
                        } catch (AddressException e) {
                            System.err.println(String.format("Invalid email: %s", email));
                        }
                    }
                    break;
                case "-cc":
                    String[] ccs = retrieveArgument(i + 1).split(":");
                    mailSender.emailCcs = new ArrayList<>();
                    for (String email : ccs) {
                        try {
                            mailSender.emailCcs.add(new InternetAddress(email));
                        } catch (AddressException e) {
                            System.err.println(String.format("Invalid email: %s", email));
                        }
                    }
                    break;
                    /// I percorsi sono separati dal delimitatore ','
                case "-A":
                    mailSender.isAttach = true;
                    String [] attachments = retrieveArgument(i + 1).split(";");
                    mailSender.attachs = new ArrayList<>();
                    for (String attach : attachments){
                        try {
                            mailSender.attachs.add(new String(attach));
                        }catch (Exception e) {
                            System.err.println(String.format("Invalid File Name: %s", attach));
                        }
                    }
                    break;
                default:
                    if (args[i].contains("-"))
                        System.err.println(String.format("Invalid Argument %s", args[i]));
                    break;
            }
        }


        mailSender.messageText = mailSender.generateInfo.getInfo().toString();
        mailSender.sendMail();
    }
}
