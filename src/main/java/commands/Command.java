package commands;

import com.cedarsoftware.util.io.JsonWriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public abstract class Command {
    protected static final String BASEPATH =  "https://cgc-api.sbgenomics.com/v2";
    protected static final String TOKEN_KEY_NAME = "X-SBG-Auth-Token";

    public abstract void execute();

    protected String handleResponse(HttpURLConnection connection, int successStatus) throws Exception {
        int status = connection.getResponseCode();

        if (status == successStatus){
            return handleOnSuccess(connection);
        } else {
            return handleError(connection, status);
        }
    }

    protected String handleOnSuccess(HttpURLConnection connection) throws Exception {
        InputStreamReader reader = new InputStreamReader(connection.getInputStream());
        String result = parseInputStream(reader);
        return result;
    }

    protected String handleError(HttpURLConnection connection, int status) throws Exception{
        InputStreamReader streamReader = null;

        if (status > 299) {
            streamReader = new InputStreamReader(connection.getErrorStream());
        } else {
            streamReader = new InputStreamReader(connection.getInputStream());
        }

        String result = parseInputStream(streamReader);
        return result;
    }

    protected String parseInputStream(InputStreamReader inputStreamReader) throws Exception {
        BufferedReader in = new BufferedReader(inputStreamReader);
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
            content.append(System.lineSeparator());
        }
        in.close();

        return JsonWriter.formatJson(content.toString());
    }
}
