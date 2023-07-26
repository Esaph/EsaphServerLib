/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package database.sql;

public class SQLPoolBuilder
{
    //private String exmaple = "jdbc:mysql://localhost/LifeCapture?useSSL=false";
    private int MAX_CONNECTIONS;
    private String SQL_User;
    private String SQL_Userpassword;
    private String SQL_Connection;

    public SQLPoolBuilder(int MAX_CONNECTIONS, String SQL_User, String SQL_Userpassword, String SQL_Connection) {
        this.MAX_CONNECTIONS = MAX_CONNECTIONS;
        this.SQL_User = SQL_User;
        this.SQL_Userpassword = SQL_Userpassword;
        this.SQL_Connection = SQL_Connection;
    }

    public SQLPoolBuilder setMAX_CONNECTIONS(int MAX_CONNECTIONS) {
        this.MAX_CONNECTIONS = MAX_CONNECTIONS;
        return this;
    }

    public SQLPoolBuilder setSQL_User(String SQL_User) {
        this.SQL_User = SQL_User;
        return this;
    }

    public SQLPoolBuilder setSQL_Userpassword(String SQL_Userpassword) {
        this.SQL_Userpassword = SQL_Userpassword;
        return this;
    }

    public SQLPoolBuilder setSQL_Connection(String SQL_Connection) {
        this.SQL_Connection = SQL_Connection;
        return this;
    }

    public SQLPoolConfiguration generateSqlPipe() throws EsaphSqlPoolException
    {
        if(MAX_CONNECTIONS <= 0 ) throw new EsaphSqlPoolException("Parameter: Max Connections must be bigger 0");
        if(SQL_User == null || SQL_User.isEmpty()) throw new EsaphSqlPoolException("Parameter: Sql Username not set");
        if(SQL_Userpassword == null || SQL_Userpassword.isEmpty()) throw new EsaphSqlPoolException("Parameter: Sql Username-password not set");
        if(SQL_Connection == null || SQL_Connection.isEmpty()) throw new EsaphSqlPoolException("Parameter: Sql Connection String not set");

        return new SQLPoolConfiguration(MAX_CONNECTIONS,
                SQL_User,
                SQL_Userpassword,
                SQL_Connection);
    }

    public class SQLPoolConfiguration
    {
        private final int MAX_CONNECTIONS;
        private final String SQL_User;
        private final String SQL_Userpassword;
        private final String SQL_Connection;

        private SQLPoolConfiguration(int MAX_CONNECTIONS, String SQL_User, String SQL_Userpassword, String SQL_Connection) {
            this.MAX_CONNECTIONS = MAX_CONNECTIONS;
            this.SQL_User = SQL_User;
            this.SQL_Userpassword = SQL_Userpassword;
            this.SQL_Connection = SQL_Connection;
        }

        public int getMAX_CONNECTIONS() {
            return MAX_CONNECTIONS;
        }

        public String getSQL_User() {
            return SQL_User;
        }

        public String getSQL_Userpassword() {
            return SQL_Userpassword;
        }

        public String getSQL_Connection() {
            return SQL_Connection;
        }
    }



    public class EsaphSqlPoolException extends Exception
    {
        public EsaphSqlPoolException() {
        }

        public EsaphSqlPoolException(String message) {
            super(message);
        }
    }
}
