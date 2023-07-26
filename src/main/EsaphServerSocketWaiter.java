/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package main;

import lawsystem.BigBossSessionRule;
import lawsystem.EsaphPipe;
import lawsystem.EsaphServerConfigRule;
import log.EsaphLogUtils;
import request.EsaphRequestHandler;
import request.EsaphRequestHandlerReadyListener;
import security.ddos.AccessDDOSThreadPipe;
import security.ddos.AllowedConnectionListener;
import security.ddos.DDOSProtector;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;

public abstract class EsaphServerSocketWaiter extends Thread implements EsaphServerConfigRule, BigBossSessionRule
{
    private SSLContext sslContext;
    private SSLServerSocket serverSocket;
    private EsaphServer.EsaphServerConfig esaphServerConfig;
    private EsaphLogUtils esaphLogUtils;

    public EsaphServerSocketWaiter(EsaphServer.EsaphServerConfig esaphServerConfig) throws IOException
    {
        this.esaphServerConfig = esaphServerConfig;
        this.esaphLogUtils = EsaphLogUtils.LoggerSystem.authenticateEsaphServerSocketWaiter(this);
        new Thread(this).start();
    }

    abstract EsaphPipe onGetPipe(EsaphRequestHandler.EsaphServerSession esaphServerSession);

    public void run() //Runs on Mainthread
    {
        init();
        while(true) //I think this should work.
        {
            try
            {
                ddosProtector.canRun(serverSocket, 150, new AllowedConnectionListener()
                        {
                            @Override
                            public void onAllowedConnection(SSLSocket sslSocket, EsaphLogUtils esaphLogUtils, AccessDDOSThreadPipe accessDDOSThreadPipe) throws IOException
                            {
                                EsaphRequestHandler esaphRequestHandler = new EsaphRequestHandler(sslSocket, new EsaphRequestHandlerReadyListener() {
                                    @Override
                                    public void requestValidated(EsaphRequestHandler.EsaphServerSession esaphServerSession)
                                    {
                                        new EsaphCommandCenter()
                                        {
                                            @Override
                                            public EsaphServer.EsaphServerConfig obtainServerLaw() //callback to get Config
                                            {
                                                return EsaphServerSocketWaiter.this.obtainServerLaw();
                                            }

                                            @Override
                                            public EsaphPipe onGetPipe(EsaphRequestHandler.EsaphServerSession esaphServerSession)
                                            {
                                                return EsaphServerSocketWaiter.this.onGetPipe(esaphServerSession);
                                            }
                                        }.handshake(esaphServerSession);
                                    }
                                })
                                {
                                    @Override
                                    public EsaphServer.EsaphServerConfig obtainServerLaw()
                                    {
                                        return EsaphServerSocketWaiter.this.obtainServerLaw();
                                    }

                                    @Override
                                    public boolean validateSession(EsaphServerSession esaphServerSession)
                                    {
                                        return EsaphServerSocketWaiter.this.validateSession(esaphServerSession);
                                    }

                                    @Override
                                    public boolean needSession(EsaphServerSession esaphServerSession) {
                                        return EsaphServerSocketWaiter.this.needSession(esaphServerSession);
                                    }

                                    @Override
                                    public long getDescriptor(long UserId) {
                                        return EsaphServerSocketWaiter.this.getDescriptor(UserId);
                                    }

                                    @Override
                                    public EsaphLogUtils getLogger() {
                                        return esaphLogUtils;
                                    }
                                };

                                accessDDOSThreadPipe.ddosprotectorAllowed(esaphRequestHandler);
                            }
                        });
            }
            catch(Exception ec)
            {
                this.esaphLogUtils.writeLog("EsaphServerSocketWaiter: run()" + ": " + ec);
            }
        }
    }

    private DDOSProtector ddosProtector = new DDOSProtector() {
        @Override
        public EsaphServer.EsaphServerConfig obtainServerLaw() {
            return EsaphServerSocketWaiter.this.obtainServerLaw();
        }
    };


    private void initSSLKey() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, FileNotFoundException, IOException, UnrecoverableKeyException, KeyManagementException
    {
        this.esaphLogUtils.writeLog("Setting up SSL-Encryption");

        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream(esaphServerConfig.getmSSLKeystoreFilePath()), esaphServerConfig.getmSSLKeystorePassword().toCharArray());
        this.esaphLogUtils.writeLog("SSL-Encryption Keystore VALID.");
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(keystore, esaphServerConfig.getmSSLKeystorePassword().toCharArray());

        KeyStore trustStore = KeyStore.getInstance("JKS");
        trustStore.load(new FileInputStream(esaphServerConfig.getmSSLTrustStoreFilePath()), esaphServerConfig.getmSSLTrustStorePassword().toCharArray());
        this.esaphLogUtils.writeLog("SSL-Encryption TrustStore VALID.");




        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(trustStore);

        sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManagers = tmf.getTrustManagers();
        sslContext.init(kmf.getKeyManagers(), trustManagers, null);
        this.esaphLogUtils.writeLog("SSL-Encryption OK.");
    }


    private void init()
    {
        try
        {
            this.initSSLKey();
            SSLServerSocketFactory sslServerSocketFactory = this.sslContext.getServerSocketFactory();
            this.serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(esaphServerConfig.getmPort());
            this.esaphLogUtils.writeLog("Server started: " + serverSocket.getInetAddress().getHostName() + ":" + serverSocket.getLocalPort());
        }
        catch(Exception io)
        {
            this.esaphLogUtils.writeLog("Exception(Starting server): " + io);
            System.exit(0);
        }
    }

}
