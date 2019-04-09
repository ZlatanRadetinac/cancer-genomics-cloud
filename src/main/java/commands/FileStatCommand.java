package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class FileStatCommand extends Command {
    static final Logger logger = LoggerFactory.getLogger(FileStatCommand.class);

    private static final String ENDPOINT = "/files";

    public static HashSet<String> allowedQueryParams = new HashSet<String>(){
        {
            add("fields");
        }
    };

    public static final int FILE_ID_INDEX = 6;
    public static final int MIN_LENGTH = 7;

    private String fileId;
    private String token;
    private String queryParams;

    public FileStatCommand(String token, String fileId) {
        this.fileId = fileId;
        this.token = token;
    }

    public void execute(){
        try{
            StringBuffer targetUrl = new StringBuffer(BASEPATH + ENDPOINT);
            targetUrl.append("/" + fileId);
            if (queryParams != null){
                targetUrl.append(queryParams);
            }

            URL url = new URL(targetUrl.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(TOKEN_KEY_NAME, token);

            System.out.println(handleResponse(connection, 200));
        } catch(Exception ex){
            logger.error("Exception while excecuting File Stat command " + ex.getMessage());
        }
    }

    public void setQueryParams(String queryParams){
        this.queryParams = queryParams;
    }
}
