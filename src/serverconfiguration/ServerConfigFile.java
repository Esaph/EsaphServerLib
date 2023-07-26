/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package serverconfiguration;

import java.io.File;
import java.io.IOException;

public class ServerConfigFile
{
    private static final String prefix = ".escf";
    public static File produceConfigFile(String serverConfigName) throws IOException, ConfigFileProduceException, ConfigFileExistsException
    {
        File file = new File(serverConfigName + ServerConfigFile.prefix);

        if(file.exists())
        {
            return file;
        }

        if(!file.createNewFile())
        {
            throw new ConfigFileProduceException("Es konnte keine Konfigurationsdatei angelegt werden. Bitte wenden Sie sich report@esaph.de");
        }

        return file;
    }

    public static class ConfigFileProduceException extends Exception
    {
        public ConfigFileProduceException()
        {
        }

        public ConfigFileProduceException(String message)
        {
            super(message);
        }
    }

    public static class ConfigFileExistsException extends Exception
    {
        public ConfigFileExistsException() {
        }

        public ConfigFileExistsException(String message) {
            super(message);
        }
    }

}