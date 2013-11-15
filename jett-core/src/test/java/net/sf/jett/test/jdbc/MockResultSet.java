package net.sf.jett.test.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.jett.test.model.Employee;

/**
 * A <code>MockResultSet</code> is a minimally implemented
 * <code>ResultSet</code> just to provide JDBC-like functionality without
 * a database.
 *
 * @author Randy Gettman
 * @since 0.6.0
 */
public class MockResultSet implements ResultSet
{
   private MockStatement myStatement;
   private Collection<Employee> myEmployees;
   private Iterator<Employee> myIterator;
   private Employee myCurrEmployee;
   private boolean wasINull;
   private int myRowNum;
   private Map<String, Integer> myColNameToColIndexMap;

   /**
    * Constructs a <code>MockResultSet</code> over the given
    * <code>Collecdtion</code>.
    * @param statement A <code>MockStatement</code>.
    * @param employees A <code>Collection</code>.
    */
   public MockResultSet(MockStatement statement, Collection<Employee> employees)
   {
      myStatement = statement;
      myEmployees = employees;
      myIterator = employees.iterator();
      myCurrEmployee = null;
      wasINull = false;
      myRowNum = 0;
      myColNameToColIndexMap = new HashMap<String, Integer>();
      myColNameToColIndexMap.put("firstName", 1);
      myColNameToColIndexMap.put("lastName", 2);
      myColNameToColIndexMap.put("salary", 3);
      myColNameToColIndexMap.put("title", 4);
      myColNameToColIndexMap.put("manager", 5);
      myColNameToColIndexMap.put("catchPhrase", 6);
      myColNameToColIndexMap.put("aManager", 7);
   }

   public boolean next() throws SQLException
   {
      if (myIterator.hasNext())
      {
         myCurrEmployee = myIterator.next();
         myRowNum++;
         return true;
      }
      return false;
   }
   public void close() throws SQLException {}
   public boolean wasNull() throws SQLException { return wasINull; }
   public String getString(int columnIndex) throws SQLException
   {
      String value;
      switch(columnIndex)
      {
      case 1:
         value = myCurrEmployee.getFirstName();
         break;
      case 2:
         value = myCurrEmployee.getLastName();
         break;
      case 3:
         value = String.valueOf(myCurrEmployee.getSalary());
         break;
      case 4:
         value = myCurrEmployee.getTitle();
         break;
      case 5:
         value = (myCurrEmployee.getManager() != null) ? myCurrEmployee.getManager().toString() : "";
         break;
      case 6:
         value = (myCurrEmployee.getCatchPhrase() != null) ? myCurrEmployee.getCatchPhrase() : "";
         break;
      case 7:
         value = String.valueOf(myCurrEmployee.isAManager());
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   public boolean getBoolean(int columnIndex) throws SQLException
   {
      boolean value;
      switch(columnIndex)
      {
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
         throw new SQLException("Field value is not boolean!");
      case 7:
         value = myCurrEmployee.isAManager();
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   public byte getByte(int columnIndex) throws SQLException
   {
      byte value;
      switch(columnIndex)
      {
      case 1:
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
         throw new SQLException("Field value is not numeric!");
      case 3:
         value = (byte) myCurrEmployee.getSalary();
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   public short getShort(int columnIndex) throws SQLException
   {
      short value;
      switch(columnIndex)
      {
      case 1:
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
         throw new SQLException("Field value is not numeric!");
      case 3:
         value = (short) myCurrEmployee.getSalary();
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   public int getInt(int columnIndex) throws SQLException
   {
      int value;
      switch(columnIndex)
      {
      case 1:
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
         throw new SQLException("Field value is not numeric!");
      case 3:
         value = (int) myCurrEmployee.getSalary();
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   public long getLong(int columnIndex) throws SQLException
   {
      long value;
      switch(columnIndex)
      {
      case 1:
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
         throw new SQLException("Field value is not numeric!");
      case 3:
         value = (long) myCurrEmployee.getSalary();
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   public float getFloat(int columnIndex) throws SQLException
   {
      float value;
      switch(columnIndex)
      {
      case 1:
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
         throw new SQLException("Field value is not numeric!");
      case 3:
         value = (float) myCurrEmployee.getSalary();
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   public double getDouble(int columnIndex) throws SQLException
   {
      double value;
      switch(columnIndex)
      {
      case 1:
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
         throw new SQLException("Field value is not numeric!");
      case 3:
         value = myCurrEmployee.getSalary();
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   @Deprecated
   public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException
   {
      BigDecimal value;
      switch(columnIndex)
      {
      case 1:
      case 2:
      case 4:
      case 5:
      case 6:
      case 7:
         throw new SQLException("Field value is not numeric!");
      case 3:
         value = new BigDecimal(myCurrEmployee.getSalary());
         value = value.setScale(scale);
         break;
      default:
         throw new SQLException("Invalid columnIndex: " + columnIndex);
      }
      return value;
   }
   public byte[] getBytes(int columnIndex) throws SQLException { return null; }
   public Date getDate(int columnIndex) throws SQLException { return null; }
   public Time getTime(int columnIndex) throws SQLException { return null; }
   public Timestamp getTimestamp(int columnIndex) throws SQLException { return null; }
   public InputStream getAsciiStream(int columnIndex) throws SQLException { return null; }

   @Deprecated
   public InputStream getUnicodeStream(int columnIndex) throws SQLException { return null; }
   public InputStream getBinaryStream(int columnIndex) throws SQLException { return null; }
   public String getString(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getString(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public boolean getBoolean(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getBoolean(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public byte getByte(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getByte(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public short getShort(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getShort(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public int getInt(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getInt(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public long getLong(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getLong(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public float getFloat(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getFloat(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public double getDouble(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getDouble(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }

   @Deprecated
   public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getBigDecimal(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public byte[] getBytes(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getBytes(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public Date getDate(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getDate(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public Time getTime(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getTime(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public Timestamp getTimestamp(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getTimestamp(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public InputStream getAsciiStream(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getAsciiStream(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   @Deprecated
   public InputStream getUnicodeStream(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getUnicodeStream(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public InputStream getBinaryStream(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getBinaryStream(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public SQLWarning getWarnings() throws SQLException { return null; }
   public void insertRow() throws SQLException {}
   public void clearWarnings() throws SQLException {}
   public String getCursorName() throws SQLException { return "mock"; }
   public ResultSetMetaData getMetaData() throws SQLException
   {
      return new MockResultSetMetaData();
   }
   public Object getObject(int columnIndex) throws SQLException
   {
      return null;
   }
   public Object getObject(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getObject(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public int findColumn(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return myColNameToColIndexMap.get(columnName);
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public Reader getCharacterStream(int columnIndex) throws SQLException { return null; }
   public Reader getCharacterStream(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getCharacterStream(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public BigDecimal getBigDecimal(int columnIndex) throws SQLException
   {
      return getBigDecimal(columnIndex, 0);
   }
   public BigDecimal getBigDecimal(String columnName) throws SQLException
   {
      if (myColNameToColIndexMap.containsKey(columnName))
         return getBigDecimal(myColNameToColIndexMap.get(columnName));
      throw new SQLException("Invalid columnName: " + columnName);
   }
   public boolean isBeforeFirst() throws SQLException { return myCurrEmployee == null; }
   public boolean isAfterLast() throws SQLException { return !myIterator.hasNext();}
   public boolean isFirst() throws SQLException { return myRowNum == 1; }
   public boolean isLast() throws SQLException { return myRowNum == myEmployees.size(); }
   public void beforeFirst() throws SQLException { throw new UnsupportedOperationException("Not implemented"); }
   public void afterLast() throws SQLException { throw new UnsupportedOperationException("Not implemented"); }
   public boolean first() throws SQLException { throw new UnsupportedOperationException("Not implemented"); }
   public boolean last() throws SQLException { throw new UnsupportedOperationException("Not implemented"); }
   public int getRow() throws SQLException { return myRowNum; }
   public boolean absolute(int row) throws SQLException { throw new UnsupportedOperationException("Not implemented"); }
   public boolean relative(int rows) throws SQLException { throw new UnsupportedOperationException("Not implemented"); }
   public boolean previous() throws SQLException { throw new UnsupportedOperationException("Not implemented"); }
   public void setFetchDirection(int direction) throws SQLException {}
   public int getFetchDirection() throws SQLException { return ResultSet.FETCH_FORWARD; }
   public void setFetchSize(int rows) throws SQLException {}
   public int getFetchSize() throws SQLException { return 0; }
   public int getType() throws SQLException { return ResultSet.TYPE_FORWARD_ONLY; }
   public int getConcurrency() throws SQLException { return ResultSet.CONCUR_READ_ONLY; }
   public boolean rowUpdated() throws SQLException { return false; }
   public boolean rowInserted() throws SQLException { return false; }
   public boolean rowDeleted() throws SQLException { return false; }
   public void updateNull(int columnIndex) throws SQLException {}
   public void updateBoolean(int columnIndex, boolean x) throws SQLException {}
   public void updateByte(int columnIndex, byte x) throws SQLException {}
   public void updateShort(int columnIndex, short x) throws SQLException {}
   public void updateInt(int columnIndex, int x) throws SQLException {}
   public void updateLong(int columnIndex, long x) throws SQLException {}
   public void updateFloat(int columnIndex, float x) throws SQLException {}
   public void updateDouble(int columnIndex, double x) throws SQLException {}
   public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {}
   public void updateString(int columnIndex, String x) throws SQLException {}
   public void updateBytes(int columnIndex, byte[] x) throws SQLException {}
   public void updateDate(int columnIndex, Date x) throws SQLException {}
   public void updateTime(int columnIndex, Time x) throws SQLException {}
   public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {}
   public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {}
   public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {}
   public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {}
   public void updateObject(int columnIndex, Object x, int scale) throws SQLException {}
   public void updateObject(int columnIndex, Object x) throws SQLException {}
   public void updateNull(String columnName) throws SQLException {}
   public void updateBoolean(String columnName, boolean x) throws SQLException {}
   public void updateByte(String columnName, byte x) throws SQLException {}
   public void updateShort(String columnName, short x) throws SQLException {}
   public void updateInt(String columnName, int x) throws SQLException {}
   public void updateLong(String columnName, long x) throws SQLException {}
   public void updateFloat(String columnName, float x) throws SQLException {}
   public void updateDouble(String columnName, double x) throws SQLException {}
   public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {}
   public void updateString(String columnName, String x) throws SQLException {}
   public void updateBytes(String columnName, byte[] x) throws SQLException {}
   public void updateDate(String columnName, Date x) throws SQLException {}
   public void updateTime(String columnName, Time x) throws SQLException {}
   public void updateTimestamp(String columnName, Timestamp x) throws SQLException {}
   public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {}
   public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {}
   public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {}
   public void updateObject(String columnName, Object x, int scale) throws SQLException {}
   public void updateObject(String columnName, Object x) throws SQLException {}
   public void updateRow() throws SQLException {}
   public void deleteRow() throws SQLException {}
   public void refreshRow() throws SQLException {}
   public void cancelRowUpdates() throws SQLException {}
   public void moveToInsertRow() throws SQLException {}
   public void moveToCurrentRow() throws SQLException {}
   public Statement getStatement() throws SQLException { return myStatement; }
   public Object getObject(int i, Map<String, Class<?>> map) throws SQLException { return getObject(i); }
   public Ref getRef(int i) throws SQLException { return null; }
   public Blob getBlob(int i) throws SQLException { return null; }
   public Clob getClob(int i) throws SQLException { return null; }
   public Array getArray(int i) throws SQLException { return null;}
   public Object getObject(String colName, Map<String, Class<?>> map) throws SQLException { return getObject(colName); }
   public Ref getRef(String colName) throws SQLException { return null; }
   public Blob getBlob(String colName) throws SQLException { return null; }
   public Clob getClob(String colName) throws SQLException { return null; }
   public Array getArray(String colName) throws SQLException { return null; }
   public Date getDate(int columnIndex, Calendar cal) throws SQLException { return null; }
   public Date getDate(String columnName, Calendar cal) throws SQLException { return null; }
   public Time getTime(int columnIndex, Calendar cal) throws SQLException { return null; }
   public Time getTime(String columnName, Calendar cal) throws SQLException { return null; }
   public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException { return getTimestamp(columnIndex); }
   public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException { return getTimestamp(columnName); }
   public URL getURL(int columnIndex) throws SQLException { return null; }
   public URL getURL(String columnName) throws SQLException { return null; }
   public void updateRef(int columnIndex, Ref x) throws SQLException {}
   public void updateRef(String columnName, Ref x) throws SQLException {}
   public void updateBlob(int columnIndex, Blob x) throws SQLException {}
   public void updateBlob(String columnName, Blob x) throws SQLException {}
   public void updateClob(int columnIndex, Clob x) throws SQLException {}
   public void updateClob(String columnName, Clob x) throws SQLException {}
   public void updateArray(int columnIndex, Array x) throws SQLException {}
   public void updateArray(String columnName, Array x) throws SQLException {}
}
