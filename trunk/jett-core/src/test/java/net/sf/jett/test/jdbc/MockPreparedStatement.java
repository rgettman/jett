package net.sf.jett.test.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import net.sf.jett.test.TestUtility;
import net.sf.jett.test.model.Employee;

/**
 * <p>A <code>MockPreparedStatement</code> is a minimally implemented
 * <code>PreparedStatement</code> just to provide JDBC-like functionality
 * without a database.</p>
 *
 * <p>For these purposes, only the <code>setString</code> method will do
 * anything, and it will only switch between two possible test cases.</p>
 *
 * @author Randy Gettman
 * @since 0.6.0
 */
public class MockPreparedStatement extends MockStatement implements PreparedStatement
{
   private String myTitleSearch;

   /**
    * Constructs a <code>MockPreparedStatement</code> given with a
    * <code>MockConnection</code>.
    * @param mockConnection A <code>MockConnection</code>.
    */
   public MockPreparedStatement(MockConnection mockConnection)
   {
      super(mockConnection);
   }

   public ResultSet executeQuery() throws SQLException
   {
      List<Employee> employees = new ArrayList<Employee>(TestUtility.getEmployees());
      for (Iterator<Employee> itr = employees.iterator(); itr.hasNext(); )
      {
         Employee emp = itr.next();
         if (!emp.getTitle().equals(myTitleSearch))
            itr.remove();
      }
      return new MockResultSet(this, employees);
   }
   public int executeUpdate() throws SQLException { throw new UnsupportedOperationException("Not implemented"); }
   public void setNull(int parameterIndex, int sqlType) throws SQLException
   {
      myTitleSearch = null;
   }
   public void setBoolean(int parameterIndex, boolean x) throws SQLException {}
   public void setByte(int parameterIndex, byte x) throws SQLException {}
   public void setShort(int parameterIndex, short x) throws SQLException {}
   public void setInt(int parameterIndex, int x) throws SQLException {}
   public void setLong(int parameterIndex, long x) throws SQLException {}
   public void setFloat(int parameterIndex, float x) throws SQLException {}
   public void setDouble(int parameterIndex, double x) throws SQLException {}
   public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {}
   public void setString(int parameterIndex, String x) throws SQLException
   {
      myTitleSearch = x;
   }
   public void setBytes(int parameterIndex, byte[] x) throws SQLException {}
   public void setDate(int parameterIndex, Date x) throws SQLException {}
   public void setTime(int parameterIndex, Time x) throws SQLException {}
   public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {}
   public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {}
   public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {}
   public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {}
   public void clearParameters() throws SQLException {}
   public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException { setObject(parameterIndex, x); }
   public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException { setObject(parameterIndex, x); }
   public void setObject(int parameterIndex, Object x) throws SQLException {}
   public boolean execute() throws SQLException { return true;}
   public void addBatch() throws SQLException {}
   public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {}
   public void setRef(int i, Ref x) throws SQLException {}
   public void setBlob(int i, Blob x) throws SQLException {}
   public void setClob(int i, Clob x) throws SQLException {}
   public void setArray(int i, Array x) throws SQLException {}
   public ResultSetMetaData getMetaData() throws SQLException
   {
      return new MockResultSetMetaData();
   }
   public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {}
   public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {}
   public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {}
   public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {}
   public void setURL(int parameterIndex, URL x) throws SQLException {}
   public ParameterMetaData getParameterMetaData() throws SQLException { return null; }
}
