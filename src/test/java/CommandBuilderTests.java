import cgcli.CommandBuilder;
import commands.*;
import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;


//This class contains simple unit tests for validation of CommandBuilder class
//More sophisticated tests would require setting up IoC container and Dependency injection
//and creating Mocks of different services for testing purposes, so this is just a simple example
//of the simplest possible unit tests. - We can discuss it.

public class CommandBuilderTests{

    private static CommandBuilder builder = new CommandBuilder();

    @Test
    public void GivenUserInputWithoutCGCLIStart_WhenBuilderCalledToCreateCommand_ThenNullIsReturned() {
        String userInput = "This string doesn't begin with clcli";

        Command command = builder.Build(userInput);

        Assert.assertNull(command);
    }

    @Test
    public void GivenUserInputWithoutTokenSpecified_WhenBuilderCalledToCreateCommand_ThenNullIsReturned(){
        String userInput = "cgcli --token";

        Command command = builder.Build(userInput);

        Assert.assertNull(command);
    }

    @Test
    public void GivenUserInputWithTokenButUnknownCommand_WhenBuilderCalledToCreateCommand_ThenNullIsReturned(){
        String userInput = "cgcli --token not_relevant_in_the_test unsupported operation";

        Command command = builder.Build(userInput);

        Assert.assertNull(command);
    }

    @Test
    public void GivenCorrectUserInputForProjectListCommand_WhenBuilderCalledToCreateCommand_ThenProjectListCommandInstanceReturned(){
        String userInput = "cgcli --token not_relevant_in_test projects list";

        Command command = builder.Build(userInput);

        assertTrue(command instanceof ProjectsListCommand);
    }

    @Test
    public void GivenUserInputForProjectListCommandWithUnknownFlagForQueryParam_WhenBuilderCalledToCreateCommand_ThenNullIsReturned(){
        String userInput = "cgcli --token not_relevant_in_test projects list not_existing_vlag=random_value";

        Command command = builder.Build(userInput);

        Assert.assertNull(command);
    }

    @Test
    public void GivenUserInputForFileListCommand_WhenBuilderCalledToCreateCommand_ThenFileListInstanceReturned(){
        String userInput = "cgcli --token not_relevant_in_test files list --project not_relevant";

        Command command = builder.Build(userInput);

        assertTrue(command instanceof FileListCommand);
    }

    @Test
    public void GivenUserInutForFileListCommand_WhenProjectIsNotSpecified_ThenNullIsReturned(){
        String userInput = "cgcli --token not_relevant_in_test files list project_id";

        Command command = builder.Build(userInput);

        Assert.assertNull(command);
    }

    @Test
    public void GivenUserInputForFileStatusCommand_WhenBuilderCalledToCreateCommand_ThenFileStatusInstanceReturned(){
        String userInput = "cgcli --token not_relevant_in_test files stat --file not_relevant_file_id";

        Command command = builder.Build(userInput);

        assertTrue(command instanceof FileStatCommand);
    }

    @Test
    public void GivenUserInutForFileStatCommand_WhenFileIdIsNotSpecified_ThenNullIsReturned(){
        String userInput = "cgcli --token not_relevant_in_test files stat missing_file_id";

        Command command = builder.Build(userInput);

        Assert.assertNull(command);
    }

    @Test
    public void GivenUserInputForFileDownloadCommand_WhenBuilderCalledToCreateCommand_ThenFileDownloadInstanceReturned(){
        String userInput = "cgcli --token not_relevant_in_test files download --file not_relevant --target not_relevant";

        Command command = builder.Build(userInput);

        assertTrue(command instanceof FileDownloadCommand);
    }

    @Test
    public void GivenUserInutForFileDownloadCommand_WhenTargetIsNotSpecified_ThenNullIsReturned(){
        String userInput = "cgcli --token not_relevant_in_test files download --file not_relevant --target";

        Command command = builder.Build(userInput);

        Assert.assertNull(command);
    }

    @Test
    public void GivenUserInputForFileUpdateCommand_WhenBuilderCalledToCreateCommand_ThenFileUpdateInstanceReturned(){
        String userInput = "cgcli --token not_relevant_in_test files update --file not_relevant_file_id name=testname";

        Command command = builder.Build(userInput);

        assertTrue(command instanceof FileUpdateCommand);
    }

    @Test
    public void GivenUserInutForFileUpdateCommand_WhenFileIdIsNotSpecified_ThenNullIsReturned(){
        String userInput = "cgcli --token not_relevant_in_test files update missing_file_id";

        Command command = builder.Build(userInput);

        Assert.assertNull(command);
    }
}