package com.example.monara_backend.service;

import com.example.monara_backend.common.Notification;
import com.example.monara_backend.model.GIN;
import com.example.monara_backend.repository.GINRepo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class NotificationService implements Notification {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private final GINRepo ginRepo;

    String attachmentPath = "F:/Uni Works/Level 3/Sem 1/Group Project/Reports/";
    public String getGINName(){
        return ginRepo.nameOFNewestGIN();
    }



    @Override
    public void productAddNotification(String recipientEmail, String productName, String Category, String Quantity) throws MessagingException {

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(recipientEmail);
        helper.setSubject("Inventory Update: New Product Added ");
        helper.setText("A new Product was added to the Inventory by Inventory_Admin. See below for further Details ");
        String tableContent = "<table style='border-collapse: collapse;'>" +
                "<tr>" +
                "<th style='border: 2px solid darkblue; padding: 8px;'>Category</th>" +
                "<th style='border: 2px solid darkblue; padding: 8px;'>Product</th>" +
                "<th style='border: 2px solid darkblue; padding: 8px;'>Quantity</th>" +
                "</tr>" +
                "<tr>" +
                "<td style='border: 1px solid darkblue; padding: 8px;'>" + Category + "</td>" +
                "<td style='border: 1px solid darkblue; padding: 8px;'>" + productName + "</td>" +
                "<td style='border: 1px solid darkblue; padding: 8px;'>" + Quantity + "</td>" +
                "</tr>" +
                "</table>";
        helper.setText(tableContent, true);
        emailSender.send(message);
    }


    @Override
    public void productDeleteNotification() {

    }

    @Override
    public void GINNotification(String recipientEmail, String Path) throws MessagingException {

//        executorService.execute(() -> {
                List<GIN> gins=ginRepo.newestGIN();//Retrieving Newest GIN Data into a List
                Long invoice = gins.get(0).getInvoice_no();
                String customerName = gins.get(0).getCustomer_name();
                String mobile = gins.get(0).getContact_nu();

                String reportName=getGINName(); //here
                String path=attachmentPath+reportName+".pdf";


                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);
                helper.setTo(recipientEmail);
                helper.setSubject("New Good Issue Note - "+reportName);


                String additionalText = "Good Issue Note for,<br><br> <b>Invoice Number</b> : "+invoice+
                                                            "<br> <b>Customer Name</b> : "+customerName+
                                                            "<br> <b>Mobile</b> : "+mobile+"<br><br>" +
                                        "Additionally, below attached the <mark>Goods Issue Note (GIN)</mark> related to above goods issuance for the reference purpose.<br><br>";

                String greeting = "<b><i>This is an System Generated Email, Do not reply to this..!!!<br><br>Thank you for your attention <br> Have a nice Day.!!!</i></b>";


                String emailContent = additionalText +"\n"+ greeting;
                helper.setText(emailContent, true);


                // Attach the file
                FileSystemResource file = new FileSystemResource(new File(path)); //here (path)
                helper.addAttachment(file.getFilename(), file);

                emailSender.send(message);

//        });

    }





    @Override
    public void GRNNotification(String recipientEmail, String productName, String Category, String Quantity, String Path) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(recipientEmail);
        helper.setSubject("New Good Issue Note");

        String mainContent = "Release below Items for Invoice no : <b><u>#####</u></b> from the Inventory.";
        String additionalText = "Additionally, I have attached the <mark>Goods Receive Note (GRN)</mark> related to this goods issuance for your reference.";
        String greeting = "<b><i>Thank you for your attention..Have a nice Day.!!!</i></b>";
        String tableContent = "<table style='border-collapse: collapse;'>" +
                "<tr>" +
                "<th style='border: 2px solid darkblue; padding: 8px;'>Category</th>" +
                "<th style='border: 2px solid darkblue; padding: 8px;'>Product</th>" +
                "<th style='border: 2px solid darkblue; padding: 8px;'>Quantity</th>" +
                "</tr>" +
                "<tr>" +
                "<td style='border: 1px solid darkblue; padding: 8px;'>" + Category + "</td>" +
                "<td style='border: 1px solid darkblue; padding: 8px;'>" + productName + "</td>" +
                "<td style='border: 1px solid darkblue; padding: 8px;'>" + Quantity + "</td>" +
                "</tr>" +
                "</table>";

        String emailContent =   mainContent + "\n" +
                                tableContent + "\n"+
                                additionalText +"\n"+
                                greeting;
        helper.setText(emailContent, true);


        // Attach the file
        FileSystemResource file = new FileSystemResource(new File(attachmentPath));
        helper.addAttachment(file.getFilename(), file);

        emailSender.send(message);
    }


    @Override
    public void confirmedGRNNotification() {

    }



}
