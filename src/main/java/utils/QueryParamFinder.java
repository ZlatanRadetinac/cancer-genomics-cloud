package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class QueryParamFinder {

    public static String generateGetRequestParams(HashSet<String> allowedParameters, String[] commandParams, int startIndex, boolean isFirst) throws Exception {
        StringBuffer suffix = new StringBuffer("");
        for (int i = startIndex; i < commandParams.length; i++){
            if (commandParams[i].split("=").length == 0){
                throw new Exception("Arguments for command must be written in form key=value");
            } else if (!allowedParameters.contains(commandParams[i].split("=")[0])) {
                throw new Exception("Disallowed query parameters are passed as a part of a command");
            } else {
                if (isFirst){
                    isFirst = false;
                    suffix.append("?");
                } else {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                suffix.append("&");
                }
                suffix.append(commandParams[i]);
            }
        }
        return suffix.toString();
    }

    public static String findString(String[] source, String paramName) throws Exception {
        int index = findParamIndex(source, paramName);
        String result = null;
        if (index != -1){
            if (index >= source.length){
                throw new Exception("Parameter value not specified");
            }
            result = source[index].split("=")[1];
        }
        return result;
    }

    public static Integer findInt(String[] source, String paramName) throws Exception {
        int index = findParamIndex(source, paramName);
        Integer result = null;
        if (index != -1){
            if (index >= source.length){
                throw new Exception("Parameter value not specified");
            }

            try {
                result = Integer.parseInt(source[index].split("=")[1]);
            } catch (ClassCastException ex){
                System.out.println("Parameter can not be casted to integer value");
            }
        }
        return result;
    }

    private static int findParamIndex(String[] source, String paramName){
        for (int i = 0; i < source.length; i++){
            if (source[i].split("=")[0].equals(paramName)){
                return i;
            }
        }
        return -1;
    }

    public static HashMap<String, String> getKeyValueMap(String[] commandParts, String paramName) {
        HashMap<String, String> metadata = new HashMap<>();
        for (int i = 0; i < commandParts.length; i++){
            if (commandParts[i].split("=")[0].split("\\.")[0].equals(paramName)){
                String key = commandParts[i].split("=")[0].split("\\.")[1];
                String value = commandParts[i].split("=")[1];
                metadata.put(key, value);
            }
        }
        return metadata;
    }

    public static ArrayList<String> getArray(String[] commandParts, String paramName, String delimiter) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < commandParts.length; i++){
            if (commandParts[i].split("=")[0].equals(paramName)){
                String[] values = commandParts[i].split("=")[1].split(delimiter);
                for (String value : values){
                    result.add(value);
                }
            }
        }
        return result;
    }
}
