package com.example.ipfsdemo2;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {


   @Value("${pinata.api-key}") String pinataApiKey;
   @Value("${pinata.secret-api-key}") String pinataSecretApiKey;
   @Value("${pinata.api-url}") String apiUrl;
   @Value("${pinata.cloud-url}") String cloudUrl;


   @Override
      public String saveFile(MultipartFile file) throws IOException {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse(file.getContentType());

      RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
          .addFormDataPart("file", file.getOriginalFilename(),
              RequestBody.create(mediaType, file.getBytes()))
          .build();

      Request request = new Request.Builder()
          .url(apiUrl)
          .method("POST", body)
          .addHeader("pinata_api_key", pinataApiKey)
          .addHeader("pinata_secret_api_key", pinataSecretApiKey)
          .build();

      try (Response response = client.newCall(request).execute()) {
         if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

         String responseBody = response.body().string();
         JsonObject jsonObject = new JsonParser().parse(responseBody).getAsJsonObject();
         String ipfsHash = jsonObject.get("IpfsHash").getAsString();

         return ipfsHash;
      }
   }


   @Override
   public byte[] loadFile(String hash) {
      try {
         URL url = new URL(cloudUrl + hash);
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
         conn.setRequestMethod("GET");

         int responseCode = conn.getResponseCode();
         if (responseCode == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream().readAllBytes();
         } else {
            throw new RuntimeException("HTTP GET Request Failed with Error code : " + responseCode);
         }
      } catch (IOException ex) {
         throw new RuntimeException("Error whilst communicating with the Pinata gateway", ex);
      }
   }

}