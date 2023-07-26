/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package lawsystem;

import request.EsaphRequestHandler;

public interface BigBossSessionRule
{
    public boolean validateSession(EsaphRequestHandler.EsaphServerSession esaphServerSession);
    public boolean needSession(EsaphRequestHandler.EsaphServerSession esaphServerSession);
    public long getDescriptor(long UserId);
}
