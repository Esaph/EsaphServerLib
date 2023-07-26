/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package security.ddos;

import lawsystem.EsaphServerConfigRule;
import log.EsaphLogUtils;
import main.EsaphServer;
import request.EsaphRequestHandler;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public abstract class DDOSProtector implements EsaphServerConfigRule
{
    /*
     *
     * DDOS Protection from victim or bug in client.
     *
     * */

    private ThreadPoolExecutor executorMainThread;

    private EsaphLogUtils esaphLogUtils;

    public DDOSProtector() throws IOException
    {
        init();
    }

    private void init() throws IOException
    {
        EsaphServer.EsaphServerConfig esaphServerConfig = obtainServerLaw();
        this.executorMainThread = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
                esaphServerConfig.getmRequestThreadSize() + Runtime.getRuntime().availableProcessors(),
                15,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(esaphServerConfig.getmRequestThreadSize()),
                new ThreadPoolExecutor.CallerRunsPolicy());

        esaphLogUtils = EsaphLogUtils.LoggerSystem.authenticateDDOSProtector(this);
        Timer timer = new Timer();
        timer.schedule(new UnfreezeConnections(esaphLogUtils), 0, 60000);
    }

    public void canRun(ServerSocket serverSocket, //Bitte master, sie kriegen ihre logdatei mit der sie tiefer ins system eindringen können./
                                       // /Achten Sie darauf diese nicht zu verlieren. Sonst kommen sie nicht weit.
                                       int MAX_CONN_PER_MINUTE,
                                       AllowedConnectionListener allowedConnction) throws IOException
    {
        SSLSocket socketConnection = (SSLSocket) serverSocket.accept();
        socketConnection.setSoTimeout(10*1000); //Setting timeout is important, to stop communication outside

        if(connectionMap.get(socketConnection.getInetAddress().toString()) != null)
        {
            if(connectionMap.get(socketConnection.getInetAddress().toString()) >= MAX_CONN_PER_MINUTE)
            {
                esaphLogUtils.writeLog("Closing connection because max connection per minute was reached");
                socketConnection.close();
            }
            else
            {
                connectionMap.put(socketConnection.getInetAddress().toString(),  connectionMap.get(socketConnection.getInetAddress().toString()) + 1);
                esaphLogUtils.writeLog("Connection: " + socketConnection.getInetAddress());
                allowedConnction.onAllowedConnection(socketConnection, new EsaphLogUtils(socketConnection.getInetAddress().getHostName()) {
                    @Override
                    public EsaphServer.EsaphServerConfig obtainServerLaw()
                    {
                        return DDOSProtector.this.obtainServerLaw();
                    }
                }, new AccessDDOSThreadPipe() {
                    @Override
                    public void ddosprotectorAllowed(EsaphRequestHandler esaphRequestHandler)
                    {
                        submitThread(esaphRequestHandler);
                    }
                });
            }
        }
        else
        {
            connectionMap.put(socketConnection.getInetAddress().toString(), 1);
            esaphLogUtils.writeLog("Connection: " + socketConnection.getInetAddress());
            allowedConnction.onAllowedConnection(socketConnection, new EsaphLogUtils(socketConnection.getInetAddress().getHostName()) {
                @Override
                public EsaphServer.EsaphServerConfig obtainServerLaw() {
                    return DDOSProtector.this.obtainServerLaw();
                }
            }, new AccessDDOSThreadPipe() {
                @Override
                public void ddosprotectorAllowed(EsaphRequestHandler esaphRequestHandler)
                {
                    submitThread(esaphRequestHandler);
                }
            });
        }
    }

    private void submitThread(EsaphRequestHandler esaphRequestHandler)
    {
        esaphLogUtils.writeLog("Submitting task: " + executorMainThread);
        executorMainThread.submit(esaphRequestHandler);
    }

    private static final HashMap<String, Integer> connectionMap = new HashMap<String, Integer>();
    private static class UnfreezeConnections extends TimerTask
    {
        private EsaphLogUtils esaphLogUtils;

        public UnfreezeConnections(EsaphLogUtils esaphLogUtils) {
            this.esaphLogUtils = esaphLogUtils;
        }

        @Override
        public void run()
        {
            synchronized(connectionMap)
            {
                if(connectionMap.size() != 0)
                {
                    int size = connectionMap.size();
                    connectionMap.clear();

                    esaphLogUtils.writeLog("DDOS Protector "
                            + " cleared " + size);
                }
            }
        }
    }

}
