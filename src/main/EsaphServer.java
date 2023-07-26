/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package main;

import commands.EsaphPipeRequestParser;
import lawsystem.EsaphPipe;
import lawsystem.EsaphPipeRunable;
import lawsystem.EsaphServerConfigRule;
import log.EsaphLogUtils;
import request.EsaphRequestHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class EsaphServer<DatabaseClass> implements EsaphServerConfigRule
{
    // TODO: 03.11.2019 DatabaseClass must be a Singleton

    private List<EsaphPipeRunable> esaphPipesList = new ArrayList<>();
    private EsaphLogUtils esaphLogUtils;

    public void startServer() throws IOException
    {
        this.esaphLogUtils = EsaphLogUtils.LoggerSystem.authenticateEsaphServer(this);
        setup();
    }

    private void setup() throws IOException
    {
        EsaphServerConfig esaphServerConfig = obtainServerLaw(); //Getting config from extender

        esaphPipesList = getPipes(esaphServerConfig.getdataBaseClass());


        EsaphServerSocketWaiter esaphServerSocketWaiter = new EsaphServerSocketWaiter(esaphServerConfig)
        {
            @Override
            EsaphPipe onGetPipe(EsaphRequestHandler.EsaphServerSession esaphServerSession)
            {
                return findPipe(esaphServerSession);
            }

            @Override
            public boolean validateSession(EsaphRequestHandler.EsaphServerSession esaphServerSession)
            {
                return EsaphServer.this.validateSession(esaphServerSession, getDataBase());
            }

            @Override
            public boolean needSession(EsaphRequestHandler.EsaphServerSession esaphServerSession)
            {
                return EsaphServer.this.needSession(esaphServerSession, getDataBase());
            }

            @Override
            public long getDescriptor(long UserId)
            {
                return EsaphServer.this.getDescriptor(UserId);
            }

            @Override
            public EsaphServerConfig obtainServerLaw()
            {
                return EsaphServer.this.obtainServerLaw();
            }
        };
    }


    private EsaphPipe findPipe(EsaphRequestHandler.EsaphServerSession esaphServerSession)
    {
        int size = esaphPipesList.size();
        for(int counter = 0; counter < size; counter++)
        {
            EsaphPipe esaphPipe = esaphPipesList.get(counter);
            if(esaphPipe.getPipeCommand().equals(esaphServerSession.getPipe()))
            {
                esaphLogUtils.writeLog("Pipe found: " + esaphPipe.getPipeCommand());
                return esaphPipe;
            }
        }

        esaphLogUtils.writeLog("Warning, you have not declared the Pipe: " + EsaphPipeRequestParser.getCommand(esaphServerSession.getJsonData())
                + " in getPipes() method.");
        return null;
    }


    public abstract List<EsaphPipeRunable> getPipes(DatabaseClass dataBaseClass);
    public abstract DatabaseClass getDataBase();
    public abstract boolean validateSession(EsaphRequestHandler.EsaphServerSession esaphServerSession, DatabaseClass databaseClass);
    public abstract boolean needSession(EsaphRequestHandler.EsaphServerSession esaphServerSession, DatabaseClass databaseClass);
    public abstract long getDescriptor(long userId);
    //run here database operation or somethink if the user can get a session. So No madder which db you have everytime your sessionManagement.


    public class EsaphServerConfig
    {
        private DatabaseClass databaseClass;
        private int mMaxRequestLength;
        private int mRequestThreadSize;
        private int mRequestSubThreadSize;
        private String mServerName;
        private int mPort;
        private String mSSLKeystoreFilePath;
        private String mSSLTrustStoreFilePath;
        private String mSSLKeystorePassword;
        private String mSSLTrustStorePassword;

        public EsaphServerConfig(DatabaseClass databaseClass,
                                 int mMaxRequestLength,
                                 int mRequestThreadSize,
                                 int mRequestSubThreadSize,
                                 String mServerName,
                                 int mPort,
                                 String mSSLKeystoreFilePath,
                                 String mSSLTrustStoreFilePath,
                                 String mSSLKeystorePassword,
                                 String mSSLTrustStorePassword)
        {
            this.databaseClass = databaseClass;
            this.mMaxRequestLength = mMaxRequestLength;
            this.mRequestThreadSize = mRequestThreadSize;
            this.mRequestSubThreadSize = mRequestSubThreadSize;
            this.mServerName = mServerName;
            this.mPort = mPort;
            this.mSSLKeystoreFilePath = mSSLKeystoreFilePath;
            this.mSSLTrustStoreFilePath = mSSLTrustStoreFilePath;
            this.mSSLKeystorePassword = mSSLKeystorePassword;
            this.mSSLTrustStorePassword = mSSLTrustStorePassword;
        }

        public int getmMaxRequestLength() {
            return mMaxRequestLength;
        }

        public DatabaseClass getdataBaseClass() {
            return databaseClass;
        }

        public int getmRequestThreadSize() {
            return mRequestThreadSize;
        }

        public int getmRequestSubThreadSize() {
            return mRequestSubThreadSize;
        }

        public String getmServerName() {
            return mServerName;
        }

        public int getmPort() {
            return mPort;
        }

        public String getmSSLKeystoreFilePath() {
            return mSSLKeystoreFilePath;
        }

        public String getmSSLTrustStoreFilePath() {
            return mSSLTrustStoreFilePath;
        }

        public String getmSSLKeystorePassword() {
            return mSSLKeystorePassword;
        }

        public String getmSSLTrustStorePassword() {
            return mSSLTrustStorePassword;
        }
    }

    /*
    public class PipeNotFoundException<DatabaseClass extends Exception> extends Exception
    {
        public PipeNotFoundException() {
        }

        public PipeNotFoundException(String message) {
            super(message);
        }
    }*/
}