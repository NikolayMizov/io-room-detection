package nl.fontys.indbe.s3cb13_io_room_usage_detection_backend.business.microsoftGraphApi;

import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.identity.ClientSecretCredential;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GetGraphServiceClient {

    @Value("${azure.client-id}")
    private String clientId;

    @Value("${azure.tenant-id}")
    private String tenantId;

    @Value("${azure.client-secret}")
    private String clientSecret;

    public GraphServiceClient getGraphServiceClient () {
        String[] scopes = new String[] {
                "https://graph.microsoft.com/.default"
        };

        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .clientSecret(clientSecret)
                .build();

        return new GraphServiceClient(credential, scopes);
    }
}
