/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package security.ddos;

import log.EsaphLogUtils;

import javax.net.ssl.SSLSocket;
import java.io.IOException;

public interface AllowedConnectionListener
{
    void onAllowedConnection(SSLSocket sslSocket, EsaphLogUtils esaphLogUtils, AccessDDOSThreadPipe accessDDOSThreadPipe) throws IOException;
}