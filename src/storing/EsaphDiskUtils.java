/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package storing;

import java.io.File;

public class EsaphDiskUtils
{
    public static File getStoringFile(String RootFolder, String PID) //Return a normal Storing file.
    {
        File file = new File(RootFolder + File.separator + PID);
        file.getParentFile().mkdirs();
        return file;
    }

    public static File getUnlimitedStoringFile(String RootFolder, String PID) //Split the folder and than save the file.
    {
        File file = new File(RootFolder + File.separator + getSplittedFolderPath(PID));
        file.getParentFile().mkdirs();
        return file;
    }

    private static String getSplittedFolderPath(String mainDirectory)
    {
        String DIRECTORY = mainDirectory.replace(".", "");
        StringBuilder stringBuilder = new StringBuilder();
        for(int counter = 0; counter < DIRECTORY.length(); counter++)
        {
            stringBuilder.append(DIRECTORY.substring(counter, counter+1));
            stringBuilder.append(File.separator);
        }
        stringBuilder.append(mainDirectory);
        return stringBuilder.toString();
    }
}
