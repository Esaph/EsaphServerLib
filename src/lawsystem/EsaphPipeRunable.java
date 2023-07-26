/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package lawsystem;


public abstract class EsaphPipeRunable<DatabaseClass> implements EsaphPipe
{
    private DatabaseClass dataBaseClass;

    public EsaphPipeRunable(DatabaseClass dataBaseClass) {
        this.dataBaseClass = dataBaseClass;
    }

    public DatabaseClass getDatabase()
    {
        return dataBaseClass;
    }
}
