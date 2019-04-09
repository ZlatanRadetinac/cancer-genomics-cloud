package cgcli;

import commands.Command;

@FunctionalInterface
public interface CommandCreatable {
    Command create(String[] commandParts, String token) throws Exception;
}
