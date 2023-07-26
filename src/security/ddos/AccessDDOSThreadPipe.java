/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package security.ddos;

import request.EsaphRequestHandler;

public interface AccessDDOSThreadPipe
{
    void ddosprotectorAllowed(EsaphRequestHandler esaphRequestHandler);
}
