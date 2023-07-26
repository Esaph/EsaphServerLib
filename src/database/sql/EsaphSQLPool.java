/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package database.sql;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import lawsystem.EsaphServerConfigRule;
import log.EsaphLogUtils;
import main.EsaphServer;

import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

public abstract class EsaphSQLPool implements DataBaseObtainer, EsaphServerConfigRule
{
	private EsaphLogUtils logUtilsEsaph;
	private volatile static boolean blocked = false;
	private static final String placeholder = "EsaphSQLPool: ";
	private final Timer timerRefreezeConnections = new Timer();

	private int MAX_CONN;
	private String CONNECTION;
	private String SQLPass;
	private String SQLUser;

	private final Vector<Connection> connectionPool = new Vector<Connection>();

	public EsaphSQLPool(SQLPoolBuilder.SQLPoolConfiguration sqlPoolConfiguration) throws IOException
	{
		this.MAX_CONN = sqlPoolConfiguration.getMAX_CONNECTIONS();
		this.CONNECTION = sqlPoolConfiguration.getSQL_Connection();
		this.SQLPass = sqlPoolConfiguration.getSQL_User();
		this.SQLUser = sqlPoolConfiguration.getSQL_Userpassword();
		this.logUtilsEsaph = EsaphLogUtils.LoggerSystem.authenticateEsaphSQLPool(this);
		initialize();
	}

	@Override
	public Connection borrowConnection() throws InterruptedException, SQLException {
		return getConnectionFromPool();
	}

	@Override
	public void giveBackConnection(Connection connection) throws InterruptedException, SQLException
	{
		returnConnectionToPool(connection);
	}


	private class UnfreezeConnections extends TimerTask
	{
		private static final String ALIVE_QUERY = "SELECT 1 from Users LIMIT 1";

		@Override
		public void run()
		{
			for(int counter = 0; counter < MAX_CONN; counter++)
			{
				PreparedStatement pr = null;
				try
				{
					Connection conn = getConnectionFromPool();
					pr = (PreparedStatement) conn.prepareStatement(ALIVE_QUERY);
					pr.execute();
					returnConnectionToPool(conn);
				}
				catch(Exception ec)
				{
					logUtilsEsaph.writeLog("(UnfreezeConnections()): Keeping alive failed: " + ec);
					logUtilsEsaph.writeLog("Reloading whole pool.");
					erasePool();
					break;
				}
				finally
				{
					try
					{
						if(pr != null)
						{
							pr.close();
						}
					}
					catch (Exception ec)
					{

					}
				}
				logUtilsEsaph.writeLog("all connection ok.");
			}
		}
	}

	private void erasePool()
	{
		blocked = true;
		synchronized(connectionPool)
		{
			for(int counter = 0; counter < MAX_CONN; counter++)
			{
				try
				{
					Connection conn = (Connection) this.connectionPool.firstElement();
					this.connectionPool.removeElementAt(0);
					if(conn != null && !conn.isClosed())
					{
						conn.close();
					}
				}
				catch(Exception ec)
				{
					logUtilsEsaph.writeLog("erasingPool(Exception()): " + ec);
				}
				
			}
			
			this.connectionPool.clear();
			logUtilsEsaph.writeLog("Pool was erased.");
			this.initializeConnectionPool();
			blocked = false;
			logUtilsEsaph.writeLog("Pool refreshed.");
		}
	}

	private void initialize()
	{
		initializeConnectionPool();
		this.timerRefreezeConnections.schedule(new UnfreezeConnections(), 5000, 800000); //EVERY 15-MINUTES were the connections called.
	}

	private void initializeConnectionPool()
	{
		while(!checkIfConnectionPoolIsFull())
		{
			//Adding new connection instance until the pool is full
			connectionPool.addElement(createNewConnectionForPool());
		}
		logUtilsEsaph.writeLog("Connection Pool is full, added " + this.connectionPool.size() + " connections.");
	}

	private synchronized boolean checkIfConnectionPoolIsFull()
	{
		return connectionPool.size() < MAX_CONN;
	}

	//Creating a connection
	private Connection createNewConnectionForPool()
	{
		Connection connection = null;
		try
		{
			connection = (Connection) DriverManager.getConnection(CONNECTION, SQLUser, SQLPass);
		}
		catch(Exception sqle)
		{
			System.err.println(placeholder + "Exception(createNewConnectionsForPool()): "+ sqle);
			return null;
		}

		return connection;
	}
	
	

	private Connection getConnectionFromPool() throws InterruptedException, SQLException
	{
		if(!blocked)
		{
			synchronized(connectionPool)
			{
				while(connectionPool.size() <= 0)
				{
					connectionPool.wait();
				}

				Connection con = (Connection) connectionPool.remove(0);

				if(con == null || !con.isValid(10000))
				{
					if(con != null)
					{
						con.close();
					}
					con = createNewConnectionForPool(); //Added back in finally code.
				}
				
				return con;
			}
		}
		return null;
	}
	
	
	public Connection returnConnectionToPool(Connection connection)
	{
		if(!blocked)
		{
			synchronized(connectionPool)
			{
				if(connection != null)
				{
					connectionPool.addElement(connection);
					connectionPool.notify();
				}
			}
		}
		return null;
	}
}