package cgcli;

import commands.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.QueryParamFinder;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandBuilder {
    static Logger logger = LoggerFactory.getLogger(CommandBuilder.class);

    //Following constants are just defined as a part of a clean code and DRY rule (Don't repeat yourself).
    //Instead of changing String value on every single place where we use it, it is much easier to change
    //constant value in its definition.

    //REST API operations that are currently supported by CLI
    private static final String PROJECTS_LIST_COMMAND = "projects list";
    private static final String FILES_LIST_COMMAND = "files list";
    private static final String FILE_DETAILS_COMMAND = "files stat";
    private static final String FILE_UPDATE_COMMAND = "files update";
    private static final String FILE_DOWNLOAD_COMMAND = "files download";

    //Instead of having huge switch clause where we would check which case block will match based on the wanted operation,
    //method reference which needs to be executed will be selected from this hashmap so build method of this class will be much simplified.
    private static final HashMap<String, CommandCreatable> commandSelectionMap = new HashMap<String, CommandCreatable>(){
        {
            put(PROJECTS_LIST_COMMAND, (CommandBuilder::createProjectsListCommand));
            put(FILES_LIST_COMMAND, (CommandBuilder::createListFilesCommand));
            put(FILE_DETAILS_COMMAND, (CommandBuilder::createFileStatusCommand));
            put(FILE_UPDATE_COMMAND,(CommandBuilder::createFileUpdateCommand));
            put(FILE_DOWNLOAD_COMMAND, (CommandBuilder::createFileDownloadCommand));
        };
    };

    //Common words that commands always/often need to have. Used for validating user input
    private static final String START_COMMAND_KEYWORD = "cgcli";
    private static final String TOKEN_KEYWORD = "--token";
    private static final String PROJECT_KEYWORD = "--project";
    private static final String PARENT_KEYWORD = "--parent";
    private static final String FILE_KEYWORD = "--file";
    private static final String TARGET_KEYWORD = "--target";

    //Indexes of required words in command parts array
    private static final int CGCLI_INDEX = 0;
    private static final int TOKEN_INDEX = 1;
    private static final int OPERATION_INDEX = 3;

    public Command Build(String commandLine){
        Command command = null;
        try {
            commandLine = commandLine.trim().replaceAll("( )+", " ");
            String[] commandParts = commandLine.split(" ");

            validateCommandBase(commandParts);
            String token = getCommandToken(commandParts);
            String commandType = getCommandType(commandParts);

            if (!commandSelectionMap.containsKey(commandType)){
                throw new Exception("Unknown operation");
            } else {
                command = commandSelectionMap.get(commandType).create(commandParts, token);
            }
        } catch (Exception ex){
            logger.error("\nException thrown while parsing command line input : " + ex.getMessage());
            logger.info(CGCManager.getDocumentation());
        }
        return command;
    }

    private static void validateCommandBase(String[] commandParts) throws Exception {
        if (commandParts == null || commandParts.length < 3){
            throw new Exception("Unknown command");
        }

        if (!commandParts[CGCLI_INDEX].equals(START_COMMAND_KEYWORD)){
            throw new Exception("Command must start with " + START_COMMAND_KEYWORD);
        }

        if (!commandParts[TOKEN_INDEX].equals(TOKEN_KEYWORD)){
            throw new Exception(START_COMMAND_KEYWORD + " must be followed with --token tag and token value");
        }
    }

    private static String getCommandToken(String[] commandParts) throws Exception {
        if (commandParts.length < 3){
            throw new Exception("Command token not specified");
        }
        return commandParts[TOKEN_INDEX + 1];
    }

    private static String getCommandType(String[] commandParts) throws Exception {
        if (commandParts.length < 5){
            throw new Exception("Operation to be done not specified");
        }
        return commandParts[OPERATION_INDEX] + " " + commandParts[OPERATION_INDEX + 1];
    }


    private static Command createFileDownloadCommand(String[] commandParts, String token) throws Exception {
        validateFileDownloadCommand(commandParts);
        String fileId = commandParts[FileDownloadCommand.FILE_ID_INDEX];
        String target = commandParts[FileDownloadCommand.TARGET_INDEX];
        String fields = QueryParamFinder.generateGetRequestParams(FileDownloadCommand.allowedQueryParams, commandParts, FileDownloadCommand.MIN_LENGTH, true);
        FileDownloadCommand command = new FileDownloadCommand(token, fileId, target);
        command.setFields(fields);

        return command;
    }

    private static Command createFileUpdateCommand(String[] commandParts, String token) throws Exception {
        validateFileUpdateCommand(commandParts);
        String fileId = commandParts[FileUpdateCommand.FILE_ID_INDEX];
        String name = QueryParamFinder.findString(commandParts, "name");
        String fields = QueryParamFinder.findString(commandParts, "fields");
        HashMap<String, String> metadata = QueryParamFinder.getKeyValueMap(commandParts, "metadata");
        ArrayList<String> tags = QueryParamFinder.getArray(commandParts, "tags", ",");
        FileUpdateCommand command = new FileUpdateCommand(token, fileId, name, metadata, tags);
        command.setFields(fields);

        return command;
    }

    private static Command createFileStatusCommand(String[] commandParts, String token) throws Exception {
        validateFileStatCommand(commandParts);
        String fileId = commandParts[FileStatCommand.FILE_ID_INDEX];
        FileStatCommand command = new FileStatCommand(token, fileId);

        String queryParams = QueryParamFinder.generateGetRequestParams(FileStatCommand.allowedQueryParams, commandParts, FileStatCommand.MIN_LENGTH, true);
        if (!queryParams.equals("")){
            command.setQueryParams(queryParams);
        }

        return command;
    }

    private static Command createListFilesCommand(String[] commandParts, String token) throws Exception {
        validateFileListCommand(commandParts);
        String requiredTag = commandParts[FileListCommand.PROJECT_ID_INDEX - 1].equals(PROJECT_KEYWORD) ? "?project=" : "?parent=" ;
        String id = requiredTag + commandParts[FileListCommand.PROJECT_ID_INDEX];

        FileListCommand command = new FileListCommand(token, id);

        String queryParams = QueryParamFinder.generateGetRequestParams(FileListCommand.allowedQueryParams, commandParts, FileListCommand.MIN_LENGTH, false);
        if (!queryParams.equals("")){
            command.setQueryParams(queryParams);
        }
        return command;
    }

    private static Command createProjectsListCommand(String[] commandParts, String token) throws Exception {
        validateProjectListCommand(commandParts);
        ProjectsListCommand command = new ProjectsListCommand(token);

        String queryParams = QueryParamFinder.generateGetRequestParams(ProjectsListCommand.allowedQueryParams, commandParts, ProjectsListCommand.MIN_LENGTH, true);
        if (!queryParams.equals("")){
            command.setQueryParams(queryParams);
        }
        return command;
    }

    private static void validateProjectListCommand(String[] commandParts) throws Exception {
        if (commandParts.length < ProjectsListCommand.MIN_LENGTH){
            throw new Exception("Not enough arguments passed to Projects List command");
        }
        return;
    }

    private static void validateFileListCommand(String[] commandParts) throws Exception {
        if (commandParts.length < FileListCommand.MIN_LENGTH){
            throw new Exception("Not enough arguments passed to Files List command");
        }
        if (!commandParts[FileListCommand.PROJECT_ID_INDEX - 1].equals(PROJECT_KEYWORD) && !commandParts[FileListCommand.PROJECT_ID_INDEX -1].equals(PARENT_KEYWORD)){
            throw new Exception("File list command should have --project or --parent tag specified");
        }
    }

    private static void validateFileStatCommand(String[] commandParts) throws Exception{
        if (commandParts.length < FileStatCommand.MIN_LENGTH) {
            throw new Exception("Not enough arguments passed to file list command");
        }
        if (!commandParts[FileStatCommand.FILE_ID_INDEX - 1].equals(FILE_KEYWORD)){
            throw new Exception("File stat command should have --file tag specified");
        }
    }

    private static void validateFileUpdateCommand(String[] commandParts) throws Exception{
        if (commandParts.length <= FileUpdateCommand.MIN_LENGTH) {
            throw new Exception("Some arguments missing in file update command");
        }
        if (!commandParts[FileUpdateCommand.FILE_ID_INDEX - 1].equals(FILE_KEYWORD)){
            throw new Exception("File stat command should have --file tag specified");
        }
    }

    private static void validateFileDownloadCommand(String[] commandParts) throws Exception {
        if (commandParts.length < FileDownloadCommand.MIN_LENGTH){
            throw new Exception("Some arguments missing for File Download command");
        }
        if (!commandParts[FileDownloadCommand.FILE_ID_INDEX - 1].equals(FILE_KEYWORD)){
            throw new Exception("File Download command must be provided with --file flag");
        }
        if (!commandParts[FileDownloadCommand.TARGET_INDEX - 1].equals(TARGET_KEYWORD)){
            throw new Exception("File Donwload command must be provided with --target flag");
        }
    }
}
