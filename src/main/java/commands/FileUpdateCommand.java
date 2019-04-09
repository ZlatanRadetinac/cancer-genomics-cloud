package commands;

import com.cedarsoftware.util.io.JsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class FileUpdateCommand extends Command {
    static final Logger logger = LoggerFactory.getLogger(FileUpdateCommand.class);

    public static HashSet<String> allowedQueryParams = new HashSet<String>(){
        {
            add("fields");
        }
    };

    private static final String ENDPOINT = "/files";

    public static final int FILE_ID_INDEX = 6;
    public static final int MIN_LENGTH = 7;

    private String token;
    private String fileId;
    private String name;
    private HashMap<String, String> metadata;
    private ArrayList<String> tags;
    private String fields;

    public FileUpdateCommand(String token, String fileId, String name, HashMap<String, String> metadata, ArrayList<String> tags) {
        this.fileId = fileId;
        this.name = name;
        this.metadata = metadata;
        this.tags = tags;
        this.token = token;
    }

    public void execute(){
        try{
            StringBuffer targetUrl = new StringBuffer(BASEPATH + ENDPOINT);
            targetUrl.append("/" + fileId);

            if (fields != null){
                targetUrl.append(fields);
            }

            URL url = new URL(targetUrl.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //HttpConnection class doesn't have a PATCH as Request Method type
            //and in order to send PATCH request this workaround has to be used.
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty(TOKEN_KEY_NAME, token);
            setRequestBody(connection);

            System.out.println(handleResponse(connection, 202));
        } catch(Exception ex){
            logger.error("Exception thrown while executing File Update command" + ex.getMessage());
        }
    }

    private void setRequestBody(HttpURLConnection connection) throws Exception {
        connection.setDoOutput(true);

        HashMap<String,Object> requestBodyProperties = new HashMap<String, Object>();

        if (name != null && !name.isEmpty()){
            requestBodyProperties.put("name", this.name);
        }
        if (metadata != null){
            requestBodyProperties.put("metadata", this.metadata);
        }
        if (tags != null && tags.size() > 0){
            requestBodyProperties.put("tags", this.tags);
        }

        String requestBody = JsonWriter.objectToJson(requestBodyProperties, new HashMap<String, Object>(){
            {
                put(JsonWriter.TYPE, false);
            }
        });

        byte[] outputInBytes = requestBody.getBytes("UTF-8");
        OutputStream os = connection.getOutputStream();
        os.write( outputInBytes );
        os.close();
    }

    public void setFields(String fields){
        this.fields = fields;
    }
}
