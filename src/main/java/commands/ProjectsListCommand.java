package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

public class ProjectsListCommand extends Command {
    static final Logger logger = LoggerFactory.getLogger(ProjectsListCommand.class);

    public static HashSet<String> allowedQueryParams = new HashSet<String>(){
        {
            add("name");
            add("offset");
            add("limit");
            add("fields");
        }
    };

    private static final String ENDPOINT = "/projects";
    public static final int MIN_LENGTH = 5;

    private String token;
    private String queryParams;

    public ProjectsListCommand(String token){
        this.token = token;
    }

    public void execute(){
        try{
            StringBuffer targetUrl = new StringBuffer(BASEPATH + ENDPOINT);
            if (queryParams != null){
                targetUrl.append(queryParams);
            }

            URL url = new URL(targetUrl.toString());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty(TOKEN_KEY_NAME, token);

            System.out.println(handleResponse(connection, 200));
        } catch(Exception ex){
            logger.error("Exception thrown while executing projects list command " + ex.getMessage());
        }
    }

    public void setQueryParams(String queryParams){
        this.queryParams = queryParams;
    }
}
