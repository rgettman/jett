package net.sf.jett.test.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * A <code>MockResultSetMetaData</code> is a minimally implemented
 * <code>ResultSetMetaData</code> just to provide JDBC-like functionality without
 * a database.
 *
 * @author Randy Gettman
 * @since 0.6.0
 */
public class MockResultSetMetaData implements ResultSetMetaData
{
   public int getColumnCount() throws SQLException
   {
      return 7;
   }

   public boolean isAutoIncrement(int column) throws SQLException { return true; }
   public boolean isCaseSensitive(int column) throws SQLException { return true; }
   public boolean isSearchable(int column) throws SQLException { return false; }
   public boolean isCurrency(int column) throws SQLException { return column == 3; }
   public int isNullable(int column) throws SQLException { return ResultSetMetaData.columnNullableUnknown; }
   public boolean isSigned(int column) throws SQLException { return column == 3; }
   public int getColumnDisplaySize(int column) throws SQLException { return 20; }
   public String getColumnLabel(int column) throws SQLException
   {
      String label;
      switch(column)
      {
      case 1:
         label = "firstName";
         break;
      case 2:
         label = "lastName";
         break;
      case 3:
         label = "salary";
         break;
      case 4:
         label = "title";
         break;
      case 5:
         label = "manager";
         break;
      case 6:
         label = "catchPhrase";
         break;
      case 7:
         label = "aManager";
         break;
      default:
         throw new SQLException("Invalid column index: " + column);
      }
      return label;
   }
   public String getColumnName(int column) throws SQLException
   {
      return getColumnLabel(column);
   }
   public String getSchemaName(int column) throws SQLException { return "jett"; }
   public int getPrecision(int column) throws SQLException { return 20; }
   public int getScale(int column) throws SQLException { return (column == 3) ? 2 : 0; }
   public String getTableName(int column) throws SQLException { return "employee"; }
   public String getCatalogName(int column) throws SQLException { return ""; }
   public int getColumnType(int column) throws SQLException
   {
      int colType;
      switch(column)
      {
      case 1:
         colType = Types.VARCHAR;
         break;
      case 2:
         colType = Types.VARCHAR;
         break;
      case 3:
         colType = Types.DOUBLE;
         break;
      case 4:
         colType = Types.VARCHAR;
         break;
      case 5:
         colType = Types.VARCHAR;
         break;
      case 6:
         colType = Types.VARCHAR;
         break;
      case 7:
         colType = Types.BOOLEAN;
         break;
      default:
         throw new SQLException("Invalid column index: " + column);
      }
      return colType;
   }
   public String getColumnTypeName(int column) throws SQLException
   {
      String colTypeName;
      switch(column)
      {
      case 1:
         colTypeName = "VARCHAR2";
         break;
      case 2:
         colTypeName = "VARCHAR2";
         break;
      case 3:
         colTypeName = "NUMBER(10,2)";
         break;
      case 4:
         colTypeName = "VARCHAR2";
         break;
      case 5:
         colTypeName = "VARCHAR2";
         break;
      case 6:
         colTypeName = "VARCHAR2";
         break;
      case 7:
         colTypeName = "CHAR";
         break;
      default:
         throw new SQLException("Invalid column index: " + column);
      }
      return colTypeName;
   }
   public boolean isReadOnly(int column) throws SQLException { return true; }
   public boolean isWritable(int column) throws SQLException { return false; }
   public boolean isDefinitelyWritable(int column) throws SQLException { return false; }
   public String getColumnClassName(int column) throws SQLException
   {
      String className;
      switch(column)
      {
      case 1:
         className = "java.lang.String";
         break;
      case 2:
         className = "java.lang.String";
         break;
      case 3:
         className = "java.lang.Double";
         break;
      case 4:
         className = "java.lang.String";
         break;
      case 5:
         className = "java.lang.String";
         break;
      case 6:
         className = "java.lang.String";
         break;
      case 7:
         className = "java.lang.Boolean";
         break;
      default:
         throw new SQLException("Invalid column index: " + column);
      }
      return className;
   }
}
