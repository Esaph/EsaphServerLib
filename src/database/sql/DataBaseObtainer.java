/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package database.sql;

import com.mysql.jdbc.Connection;

import java.sql.SQLException;

public interface DataBaseObtainer
{
	public Connection borrowConnection() throws InterruptedException, SQLException;
	public void giveBackConnection(Connection connection) throws InterruptedException, SQLException;
}
