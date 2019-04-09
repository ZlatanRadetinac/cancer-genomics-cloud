package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class FileListCommand extends Command {
    static final Logger logger = LoggerFactory.getLogger(FileListCommand.class);

    private static final String ENDPOINT = "/files";

    public static HashSet<String> allowedQueryParams = new HashSet<String>(){
        {
            add("project");
            add("parent");
            add("name");
            add("metadata");
            add("origin.task");
            add("origin.dataset");
            add("fields");
            add("tag");
        }
    };

    public static final int PROJECT_ID_INDEX = 6;
    public static final int MIN_LENGTH = 7;

    private String projectName;
    private String token;
    private String queryParams = null;

    public FileListCommand(String token, String projectName) {
        this.projectName = projectName;
        this.token = token;
    }

    public void execute(){
        try{
            StringBuffer targetUrl = new StringBuffer(BASEPATH + ENDPOINT);
            targetUrl.append("?project=" + projectName);
            if (queryParams != null){
                targetUrl.append(queryParams);
            }

            URL url = new URL(targetUrl.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(TOKEN_KEY_NAME, token);

            System.out.println(handleResponse(connection, 200));
        } catch(Exception ex){
            logger.error("Exception while executing File List command " + ex.getMessage());
        }
    }

    public void setQueryParams(String queryParam) {
        this.queryParams = queryParam;
    }
}
