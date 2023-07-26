/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package request;

import commands.EsaphPipeRequestParser;
import lawsystem.BigBossSessionRule;
import lawsystem.EsaphServerConfigRule;
import lawsystem.Session;
import log.EsaphLogUtils;
import main.EsaphServer;
import org.json.JSONObject;

import javax.net.ssl.SSLSocket;
import java.io.*;
import java.nio.charset.StandardCharsets;

public abstract class EsaphRequestHandler extends Thread implements EsaphServerConfigRule, BigBossSessionRule //I am the law.
{
    //Das ist die Empfangsfrau.
    private SSLSocket sslSocket;
    private EsaphLogUtils logUtilsEsaphRequestHandler;
    private EsaphRequestHandlerReadyListener esaphRequestHandlerReadyListener;
    private EsaphServer.EsaphServerConfig esaphServerConfig;

    public EsaphRequestHandler(SSLSocket sslSocket,
                               EsaphRequestHandlerReadyListener esaphRequestHandlerReadyListener) throws IOException
    {
        this.logUtilsEsaphRequestHandler = EsaphLogUtils.LoggerSystem.authenticateRequestHandler(EsaphRequestHandler.this);
        this.sslSocket = sslSocket;
        this.esaphRequestHandlerReadyListener = esaphRequestHandlerReadyListener;
        this.esaphServerConfig = obtainServerLaw();
    }

    private String readDataCarefully(BufferedReader bufferedReader, int bufferSize) throws Exception
    {
        String msg = bufferedReader.readLine();
        if(msg == null || msg.length() > bufferSize)
        {
            throw new Exception("Exception: msg " + msg + " length: " + (msg==null ? "" : msg) + ">" + bufferSize);
        }
        return msg;
    }

    public abstract EsaphLogUtils getLogger();

    @Override
    public void run()
    {
        try
        {
            logUtilsEsaphRequestHandler.writeLog("Request validate [...]");
            sslSocket.setSoTimeout(10000);
            // TODO: 23.12.2021 maybe cause the recvi error
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(this.sslSocket.getOutputStream(), StandardCharsets.UTF_8), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(this.sslSocket.getInputStream(), StandardCharsets.UTF_8));
            JSONObject jsonObject = new JSONObject(this.readDataCarefully(reader, esaphServerConfig.getmMaxRequestLength()));
            logUtilsEsaphRequestHandler.writeLog("Request validated [ok]: " + jsonObject.toString());

            //Ask the big boss.
            EsaphServerSession esaphServerSession = new EsaphServerSession(sslSocket,
                    getLogger(),
                    writer,
                    reader,
                    EsaphPipeRequestParser.getUDATA(jsonObject), //Splitting the command section and data section.
                    EsaphPipeRequestParser.getSession(jsonObject),
                    EsaphPipeRequestParser.getCommand(jsonObject));

            if(esaphServerSession.getSession() != null)
            {
                esaphServerSession.setmUserIdDescriptor(getDescriptor(esaphServerSession.getSession().getmUserId()));
            }
            else
            {
                esaphServerSession.setmUserIdDescriptor(-1);
            }

            if(needSession(esaphServerSession))
            {
                if(validateSession(esaphServerSession)) //Session is valid.
                {
                    esaphRequestHandlerReadyListener.requestValidated(esaphServerSession);
                }
            }
            else
            {
                esaphRequestHandlerReadyListener.requestValidated(esaphServerSession);
            }
        }
        catch (Exception ec)
        {
            logUtilsEsaphRequestHandler.writeLog(getClass().getName() + " failed: " + ec);
        }
        finally {
            logUtilsEsaphRequestHandler.writeLog("Request [done]");
        }
    }


    public class EsaphServerSession
    {
        private SSLSocket sslSocket;
        private EsaphLogUtils logUtilsEsaph;
        private PrintWriter writer;
        private BufferedReader reader;
        private JSONObject jsonData;
        private Session session;
        private String Pipe;
        private long mUserIdDescriptor;

        public EsaphServerSession(SSLSocket sslSocket,
                                  EsaphLogUtils logUtilsEsaph,
                                  PrintWriter writer,
                                  BufferedReader reader,
                                  JSONObject jsonData,
                                  Session session,
                                  String Pipe)
        {
            this.session = session;
            this.sslSocket = sslSocket;
            this.logUtilsEsaph = logUtilsEsaph;
            this.writer = writer;
            this.reader = reader;
            this.jsonData = jsonData;
            this.Pipe = Pipe;
        }

        public void setmUserIdDescriptor(long mUserIdDescriptor) {
            this.mUserIdDescriptor = mUserIdDescriptor;
        }

        public long getmUserIdDescriptor() {
            return mUserIdDescriptor;
        }

        public Session getSession() {
            return session;
        }

        public String getPipe() {
            return Pipe;
        }

        public SSLSocket getSslSocket() {
            return sslSocket;
        }

        public EsaphLogUtils getLogUtilsEsaph() {
            return logUtilsEsaph;
        }

        public PrintWriter getWriter() {
            return writer;
        }

        public BufferedReader getReader() {
            return reader;
        }

        public JSONObject getJsonData() {
            return jsonData;
        }
    }
}
