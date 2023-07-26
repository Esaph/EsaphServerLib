/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package lawsystem;

import main.EsaphServer;

public interface EsaphServerConfigRule
{
    public EsaphServer.EsaphServerConfig obtainServerLaw(); //Wie ein Gesetzt, muss von überall zugreifbar sein.
}
