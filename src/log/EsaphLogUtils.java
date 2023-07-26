/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package log;

import database.sql.EsaphSQLPool;
import lawsystem.EsaphServerConfigRule;
import main.EsaphServer;
import main.EsaphServerSocketWaiter;
import request.EsaphRequestHandler;
import security.ddos.DDOSProtector;
import storing.EsaphDiskUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class EsaphLogUtils implements EsaphServerConfigRule
{
	public static boolean logInConsole = false;
	private static final File rootPath = new File(""); // TODO: 31.10.2019 add root file
	private String InetAddress;
	private String ServerName;
	private FileWriter printer;
	private DateTimeFormatter dateTimeFormatter;

	public EsaphLogUtils(final EsaphRequestHandler.EsaphServerSession esaphServerSession) throws IOException //Tracing user session.
	{
		this.InetAddress = esaphServerSession.getSslSocket().getInetAddress().getHostAddress();
		this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss");
		this.ServerName = obtainServerLaw().getmServerName();

		if(!EsaphLogUtils.logInConsole)
		{
			this.printer = new FileWriter(EsaphDiskUtils.getUnlimitedStoringFile(rootPath.getAbsolutePath(), InetAddress),
					true);
		}
	}

	public EsaphLogUtils(String name) throws IOException //For Root Server workers
	{
		this.InetAddress = name;
		this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss");
		this.ServerName = obtainServerLaw().getmServerName();

		if(!EsaphLogUtils.logInConsole)
		{
			this.printer = new FileWriter(EsaphDiskUtils.getUnlimitedStoringFile(rootPath.getAbsolutePath(), InetAddress),
					true);
		}
	}

	public EsaphLogUtils(String name, String serverName) throws IOException //For logging without server rules.
	{
		this.InetAddress = name;
		this.dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy-HH:mm:ss");
		this.ServerName = serverName;

		if(!EsaphLogUtils.logInConsole)
		{
			this.printer = new FileWriter(EsaphDiskUtils.getUnlimitedStoringFile(rootPath.getAbsolutePath(), InetAddress),
					true);
		}
	}

	public void closeFile()
	{
		try
		{
			this.printer.close();
		}
		catch(Exception ec)
		{
			try
			{

				FileWriter writer = new FileWriter(getFatalErrorFile(), true);
				String toAppend = this.ServerName + "-" + this.dateTimeFormatter.format(LocalDateTime.now()) + "@" + this.InetAddress + ": " + ec + System.lineSeparator();
				writer.append(toAppend);
				writer.close();
			}
			catch(Exception ec2)
			{
				System.out.println(this.ServerName + "-" + this.dateTimeFormatter.format(LocalDateTime.now()) + "@" + this.InetAddress + ": 2.0 konnte datei nicht schlieﬂen:  " + ec2);
			}
		}
	}

	public void test()
	{
		try
		{
			FileWriter writer = new FileWriter(getFatalErrorFile(), true);
			String toAppend = this.ServerName + "-" + this.dateTimeFormatter.format(LocalDateTime.now()) + "@" + this.InetAddress + ": ";
			writer.append(toAppend);
			writer.close();
		}
		catch(Exception ec2)
		{
		}
	}

	private File getFatalErrorFile()
	{
        // TODO: 19.11.2019 Problem here
		return new File(URLDecoder
				.decode(EsaphLogUtils.class.getProtectionDomain()
						.getCodeSource()
						.getLocation()
						.getPath()) +
				File.pathSeparator +
				"Please report this error" + File.pathSeparator +
				this.dateTimeFormatter.format(LocalDateTime.now()) + LogPrefix.PREFIX_LOG);
	}

	public synchronized void writeLog(String log)
	{
		try
		{
			String mMessage = this.ServerName
					+ "-"
					+ this.dateTimeFormatter.format(LocalDateTime.now())
					+ "\t@\t"
					+ this.InetAddress
					+ ": "
					+ log
					+ System.lineSeparator();

			if(EsaphLogUtils.logInConsole)
			{
				System.out.println(mMessage);
			}
			else
			{
				this.printer.append(mMessage);
				this.printer.flush();
			}
		}
		catch(Exception ec)
		{
			try
			{
				File file = new File("/usr/server/bigProblem.log");
				FileWriter writer = new FileWriter(file, true);
				String toAppend = this.ServerName + "-" + this.dateTimeFormatter.format(LocalDateTime.now()) + "@" + this.InetAddress + ": " + ec + System.lineSeparator();
				writer.append(toAppend);
				writer.close();
			}
			catch(Exception ec2)
			{
				System.out.println(this.ServerName + "-" + this.dateTimeFormatter.format(LocalDateTime.now()) + "@" + this.InetAddress + ": Ja arschlecken: " + ec2 + System.lineSeparator());
			}
		}
	}

	public static class LoggerSystem
	{
		public static EsaphLogUtils authenticateDDOSProtector(DDOSProtector ddosProtector) throws IOException
		{
			return new EsaphLogUtils("DDOSProtector")
			{
				@Override
				public EsaphServer.EsaphServerConfig obtainServerLaw() {
					return ddosProtector.obtainServerLaw();
				}
			};
		}

		public static EsaphLogUtils authenticateEsaphServerSocketWaiter(EsaphServerSocketWaiter esaphServerSocketWaiter) throws IOException
		{
			return new EsaphLogUtils("EsaphServerSocketWaiter")
			{
				@Override
				public EsaphServer.EsaphServerConfig obtainServerLaw()
				{
					return esaphServerSocketWaiter.obtainServerLaw();
				}
			};
		}

		public static EsaphLogUtils authenticateRequestHandler(EsaphRequestHandler esaphRequestHandler) throws IOException
		{
			return new EsaphLogUtils("EsaphRequestHandler")
			{
				@Override
				public EsaphServer.EsaphServerConfig obtainServerLaw()
				{
					return esaphRequestHandler.obtainServerLaw();
				}
			};
		}

		public static EsaphLogUtils authenticateEsaphServer(EsaphServer esaphServer) throws IOException
		{
			return new EsaphLogUtils("EsaphServer")
			{
				@Override
				public EsaphServer.EsaphServerConfig obtainServerLaw() {
					return esaphServer.obtainServerLaw();
				}
			};
		}

		public static EsaphLogUtils authenticateEsaphSQLPool(EsaphSQLPool esaphSQLPool) throws IOException
		{
			return new EsaphLogUtils("EsaphSQLPool")
			{
				@Override
				public EsaphServer.EsaphServerConfig obtainServerLaw()
				{
					return esaphSQLPool.obtainServerLaw();
				}
			};
		}
	}
}
