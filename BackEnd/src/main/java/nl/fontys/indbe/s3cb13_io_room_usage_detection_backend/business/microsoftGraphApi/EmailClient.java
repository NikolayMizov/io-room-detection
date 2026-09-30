package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.transactional.SendContact;
import com.mailjet.client.transactional.SendEmailsRequest;
import com.mailjet.client.transactional.TrackOpens;
import com.mailjet.client.transactional.TransactionalEmail;
import com.mailjet.client.transactional.response.SendEmailsResponse;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.domain.MeetingRoom;
import nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.repository.entity.MeetingRoomEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailClient {
//    private final GraphServiceClient graphServiceClient;

    private final static String FROM_EMAIL_ID = "kaloyan.rakov@iodigital.com";

    @Value("${MJ_APIKEY_PUBLIC}")
    private String mjAPIKeyPublic;

    @Value("${MJ_APIKEY_PRIVATE}")
    private String mjAPIKeyPrivate;

//    public SendWarningEarlyVacatedRoom(GetGraphServiceClient getGraphClient) {
//        this.graphServiceClient = getGraphClient.getGraphServiceClient();
//    }

//    public void sendEmail(String recipientEmail, String recipientName, String senderEmail) {
//        String fromEmailId = "Testruimte1.eindhoven@iodigital.com";
//        if (senderEmail != null) {
//            fromEmailId = senderEmail;
//        }
//
//        Message message = new Message() {{
//            setSubject("Warning: you left your reserved room early without confirming it.");
//            setBody(new ItemBody() {{
//                setContentType(BodyType.Html);
//                setContent(String.format("""
//                    Dear %s,
//
//                    The system has detected you vacated your booked room earlier than the end time you declared when you booked it.
//                    Please, next time mark the room as available for others if you vacate the room early.
//
//                    Best regards,
//
//                    iO""", recipientName));
//            }});
//            setToRecipients(List.of(new Recipient() {{
//                setEmailAddress(new EmailAddress() {{
//                    setAddress(recipientEmail);
//                }});
//            }}));
//        }};
//
//        com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody sendMailPostRequestBody = new com.microsoft.graph.users.item.sendmail.SendMailPostRequestBody();
//        sendMailPostRequestBody.setMessage(message);
//        sendMailPostRequestBody.setSaveToSentItems(false);
//        graphServiceClient.users().byUserId(fromEmailId).sendMail().post(sendMailPostRequestBody);
//
//        //CC people
////        LinkedList<Recipient> ccRecipientsList = new LinkedList<Recipient>();
////        Recipient ccRecipients = new Recipient();
////        EmailAddress emailAddress1 = new EmailAddress();
////        emailAddress1.address = "def@outlook.com";
////        ccRecipients.emailAddress = emailAddress1;
////        ccRecipientsList.add(ccRecipients);
////        message.ccRecipients = ccRecipientsList;
//    }

    private MailjetClient getClient() {
        ClientOptions options = ClientOptions.builder()
                .apiKey(mjAPIKeyPublic)
                .apiSecretKey(mjAPIKeyPrivate)
                .build();
        return new MailjetClient(options);
    }

    private SendEmailsResponse sendRequest(MailjetClient client, TransactionalEmail message) {
        SendEmailsRequest request = SendEmailsRequest
            .builder()
            .message(message)
            .build();

        SendEmailsResponse response = null;
        try {
            response = request.sendWith(client);
        } catch (MailjetException e) {
            System.out.println(e.getMessage());
        }
        return response;
    }

    public void sendWarningEarlyExit(String recipientEmail, String recipientName, String senderEmail) {
        if (senderEmail == null) {
            senderEmail = FROM_EMAIL_ID;
        }

        MailjetClient client = getClient();

        TransactionalEmail message = TransactionalEmail
                .builder()
                .to(new SendContact(recipientEmail, recipientName))
                .from(new SendContact(senderEmail, "iO Digital Eindhoven"))
                .htmlPart(String.format("""
                    <p>Dear %s,</br></br>
                    
                    The system has detected you vacated your booked room earlier than the end time you declared when you booked it.
                    Please, next time mark the room as available for others if you vacate the room early.</br></br>
                    
                    Best regards,</br></br>
                    
                    iO</p>""", recipientName))
                .subject("Warning: you left your reserved room early without confirming it.")
                .trackOpens(TrackOpens.ENABLED)
                .build();

        SendEmailsResponse response = sendRequest(client, message);
    }

    public void sendWarningLateEntry(String recipientEmail, String recipientName, String senderEmail) {
        if (senderEmail == null) {
            senderEmail = FROM_EMAIL_ID;
        }

        MailjetClient client = getClient();

        TransactionalEmail message = TransactionalEmail
                .builder()
                .to(new SendContact(recipientEmail, recipientName))
                .from(new SendContact(senderEmail, "iO Digital Eindhoven"))
                .htmlPart(String.format("""
                    <p>Dear %s,</br></br>
                    
                    The system has detected you entered your booked room later than the start time you declared when you booked it.
                    Please, next time mark the room as available for others if you enter the room late.</br></br>
                    
                    Best regards,</br></br>
                    
                    iO</p>""", recipientName))
                .subject("Warning: you entered your reserved room late without confirming it.")
                .trackOpens(TrackOpens.ENABLED)
                .build();

        SendEmailsResponse response = sendRequest(client, message);
    }

    public void sendWarningAbsence(String recipientEmail, String recipientName, String senderEmail) {
        if (senderEmail == null) {
            senderEmail = FROM_EMAIL_ID;
        }

        MailjetClient client = getClient();

        TransactionalEmail message = TransactionalEmail
                .builder()
                .to(new SendContact(recipientEmail, recipientName))
                .from(new SendContact(senderEmail, "iO Digital Eindhoven"))
                .htmlPart(String.format("""
                    <p>Dear %s,</br></br>
                    
                    The system has detected you did not attend your booked room.
                    Please, next time mark the room as available for others if you are not going to attend the room.</br></br>
                    
                    Best regards,</br></br>
                    
                    iO</p>""", recipientName))
                .subject("Warning: you did not confirm you were not going to attend your reserved room.")
                .trackOpens(TrackOpens.ENABLED)
                .build();

        SendEmailsResponse response = sendRequest(client, message);
    }

    public void sendRoomRecommendations(String recipientEmail, String recipientName, String senderEmail, MeetingRoom currentRoom, List<MeetingRoomEntity> recommendedRooms) {
        if (senderEmail == null) {
            senderEmail = FROM_EMAIL_ID;
        }

        MailjetClient client = getClient();

        TransactionalEmail message = TransactionalEmail
                .builder()
                .to(new SendContact(recipientEmail, recipientName))
                .from(new SendContact(senderEmail, "iO Digital Eindhoven"))
                .htmlPart(String.format("""
                    <p>Dear %s,</br></br>
                    
                    The system has detected low attendance to your meetings in room {currentRoom}.
                    
                    Please, book a better room.</br></br>
                    Recommendations:<br>
                    {recommendedRooms}
                    
                    Best regards,</br></br>
                    
                    iO</p>""", recipientName))
                .subject("Warning: Please book a more appropriate meeting room.")
                .trackOpens(TrackOpens.ENABLED)
                .build();

        SendEmailsResponse response = sendRequest(client, message);
    }
}
