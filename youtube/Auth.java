/*
 * Adapted from Google Inc, and Jeremy Walker.
 * (https://github.com/youtube/api-samples/blob/master/java/src/main/java/com/google/api/services/samples/youtube/cmdline/data/UploadVideo.java)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package youtube;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.FileDataStoreFactory;

import java.io.*;
import java.util.List;

/**
 * Shared class used by every sample. Contains methods for authorizing a user and caching credentials.
 */
public class Auth {
    public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final String CREDENTIALS_DIRECTORY = ".oauth-credentials";


    public static String getCredDirectory() {
        return CREDENTIALS_DIRECTORY;
    }

    /**
     * Authorizes the installed application to access user's protected data.
     *
     * @param scopes              list of scopes needed to run YouTube upload.
     * @param credentialDatastore name of the credential datastore to cache OAuth tokens
     */
    public static Credential authorize(List<String> scopes, String credentialDatastore) throws IOException {
        FileReader fileReader = new FileReader("src/client_secret.json");

        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, fileReader);

        // Checks that the defaults have been replaced (Default = "Enter X here").
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            System.out.println(
                    "Enter Client ID and Secret from https://console.developers.google.com/project/_/apiui/credential "
                            + "into src/main/resources/client_secrets.json");
            System.exit(1);
        }

        // This creates the credentials datastore at ~/.oauth-credentials/${credentialDatastore}
        FileDataStoreFactory fileDataStoreFactory = new FileDataStoreFactory(new File(System.getProperty("user.home") + "/" + CREDENTIALS_DIRECTORY));
        DataStore<StoredCredential> datastore = fileDataStoreFactory.getDataStore(credentialDatastore);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, scopes).setCredentialDataStore(datastore)
                .build();

        // Build the local server and bind it to port 8080
        LocalServerReceiver localReceiver = new LocalServerReceiver.Builder().setPort(8080).build();

        // Authorize.
        return new AuthorizationCodeInstalledApp(flow, localReceiver).authorize("user");
    }
}