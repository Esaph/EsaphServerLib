/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package request;

public interface EsaphRequestHandlerReadyListener
{
    void requestValidated(EsaphRequestHandler.EsaphServerSession esaphServerSession);
}
