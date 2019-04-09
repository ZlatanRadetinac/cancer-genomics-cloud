package cgcli;

import commands.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class CGCManager {
    static Logger logger = LoggerFactory.getLogger(CGCManager.class);

    // Implemented as a singleton so we can be sure that only one object is reading
    // from standard input at any moment
    private static CGCManager managerInstance = null;

    // Detecting this string on standard input is used for terminating the application
    private static final String TERMINATE_APPLICATION = "cgcli terminate";

    private CGCManager(){}

    public static CGCManager getInstance(){
        if (managerInstance == null){
            managerInstance = new CGCManager();
        }
        return managerInstance;
    }

    // Calling REST API is implemented as a synchronous operation, meaning that after sending the request
    // user will be blocked from sending another request until the response from the first request is ready.
    // Intuitive and logical reason for this is that we don't want to be able to send multiple requests
    // not knowing which response is related to which request. Many CLI tools that I know are implemented
    // this way. On the other hand, there are some cli tools which use asynchronous approach
    // (for example JEST framework for system testing which runs all tests in parallel). We can discuss
    // asynchronous approach together (Command could be implemented as a thread and execute method would
    // actually be run method of a thread).

    // For implementing different kinds of requests, command pattern is used. This means that request is encapsulated
    // into a command object and CGCManager has to know nothing about what command actually does, it just needs to call
    // execute method of a command

    public void start(){
        Scanner scanner = new Scanner(System.in);
        CommandBuilder commandBuilder = new CommandBuilder();
        String line;

        while (!(line = scanner.nextLine().trim()).equals(TERMINATE_APPLICATION)){
            if (line.isEmpty()){
                continue;
            }

            Command command = commandBuilder.Build(line);
            if (command != null){
                command.execute();
            }
        }
        logger.info("Terminating the application");
    }

    public static String getDocumentation() {
        return  "Usage:\n" +
                "cgccli --token {token} {operation} [ --project {project_name} | --file {file_id} ([key-value]| --dest {destination})]\n" +
                "Where operations include : \n" +
                "projects list - Listing all of your projects\n" +
                "files list - Listing all of your projects in specified {project name}\n" +
                "file stat - Showing details about file with specified {file_id}\n" +
                "file update - Updates file with {file_id} with specified key-value pairs\n" +
                "file download - Downloads file with {file_id} to a specified destination\n\n";
    }

}
