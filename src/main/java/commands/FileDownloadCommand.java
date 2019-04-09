package commands;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class FileDownloadCommand extends Command {
    static Logger logger = LoggerFactory.getLogger(FileDownloadCommand.class);

    public static HashSet<String> allowedQueryParams = new HashSet<String>(){
        {
            add("fields");
        }
    };

    private static final String ENDPOINT = "/files/{file_id}/download_info";

    public static final int FILE_ID_INDEX = 6;
    public static final int TARGET_INDEX = 8;
    public static final int MIN_LENGTH = 9;

    private String token;
    private String fileId;
    private String target;
    private String fields = null;

    public FileDownloadCommand(String token, String fileId, String target) {
        this.fileId = fileId;
        this.target = target;
        this.token = token;
    }

    public void execute(){
        try{
            String targetUrl = BASEPATH + ENDPOINT;
            targetUrl = targetUrl.replace("{file_id}", fileId);
            if (fields != null){
                targetUrl += fields;
            }

            URL url = new URL(targetUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(TOKEN_KEY_NAME, token);

            String jsonString = handleResponse(connection, 200);
            JsonObject<String, Object> json = (JsonObject<String, Object>) JsonReader.jsonToJava(jsonString);

            if (json.containsKey("url")) {
                String downloadUrl = (String) json.get("url");
                downloadFile(downloadUrl, target);
            } else {
                System.out.println(jsonString);
            }
        } catch(Exception ex){
            logger.error("Error while executing file download command  : " + ex.getMessage());
        }
    }

    private void downloadFile(String downloadUrl, String target) throws Exception {
        URL url = new URL(downloadUrl);

        File file = new File(target);
        if (file.exists()){
            System.out.println("File with the same name already exists");
            return;
        }

        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
        System.out.println("File downloaded successfully");
    }
    public void setFields(String fields){
        this.fields = fields;
    }
}
