/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

public class SendEmailTLS {

    public static String SendEmailTLS( String mailTo,String subTxt, String msgTxt) {
        System.setProperty("client.encoding", "UTF-8");

        final String username = "info@compare.ge";
        final String password = "Gelashvili2019";

        Properties prop = new Properties();
//     Since you're using SSL, you can try to configure smtps namespace, not smtp   prop.put("mail.smtps.host", "smtp.gmail.com"); 

        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.timeout", 1000);
        prop.put("mail.smtp.starttls.enable", "true"); //TLS

        Session session = Session.getInstance(prop,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("info@compare.ge"));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(mailTo)
            //                    InternetAddress.parse("paatap@gmail.com, to_username_b@yahoo.com")
            );
            message.setHeader("Content-Type", "text/plain; charset=UTF-8");

            message.setSubject(subTxt);
            message.setSubject("მოგესალმებათ info@compare.ge /Greeting from info@compare.ge");

//           msgTxt = "compare.ge გთხოვთ გადახვიდეთ ლინკზე/please folow to link"
//                    + " http://192.168.18.22:9080/myweb1?register=" + linkmd5
//                    + "\n\n ლინკი აქტიურია 1 საათის განმავლობაში/ Link is valid 1 Hour "
//                    + "\n\n compare.ge Please do not spam my email!";


            MimeBodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setContent(msgTxt, "text/plain; charset=UTF-8");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            MimeBodyPart attachPart = new MimeBodyPart();

            String file = "/home/paatap/JAVA/apache-tomcat-8.5.30/webapps/ROOT/pdf/home.svg";
            String fileName = "/home/paatap/JAVA/apache-tomcat-8.5.30/webapps/ROOT/pdf/health.svg";
            DataSource source = new FileDataSource(file);
            attachPart.setDataHandler(new DataHandler(source));
            attachPart.setFileName(fileName);

            multipart.addBodyPart(attachPart);

            // creates multi-part
            message.setContent(multipart);
            Transport.send(message);

            System.out.println(message.getContent());
            System.out.println("Done");

            return message.getContent().toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
