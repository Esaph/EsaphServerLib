/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package main;

import lawsystem.EsaphPipe;
import lawsystem.EsaphServerConfigRule;
import request.EsaphRequestHandler;

public abstract class EsaphCommandCenter implements EsaphServerConfigRule
{
    public void handshake(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        EsaphCommandCenter.this.onGetPipe(esaphServerSession).transfusion(esaphServerSession); //Run command.
        // TODO: 03.11.2019 Warning, some exceptions are getting lost somewhere.
    }

    public abstract EsaphPipe onGetPipe(EsaphRequestHandler.EsaphServerSession esaphServerSession);
}