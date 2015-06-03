package org.embulk.input.jdbc.getter;

import java.sql.Types;

import org.embulk.input.jdbc.JdbcColumn;
import org.embulk.spi.PageBuilder;

public class ColumnGetterFactory
{
    private final PageBuilder to;

    public ColumnGetterFactory(PageBuilder to)
    {
        this.to = to;
    }

    public ColumnGetter newColumnGetter(JdbcColumn column)
    {
        switch(column.getSqlType()) {
        // getLong
        case Types.TINYINT:
        case Types.SMALLINT:
        case Types.INTEGER:
        case Types.BIGINT:
            return new LongColumnGetter(to);

        // getDouble
        case Types.DOUBLE:
        case Types.FLOAT:
        case Types.REAL:
            return new DoubleColumnGetter(to);

        // getBool
        case Types.BOOLEAN:
        case Types.BIT:  // JDBC BIT is boolean, unlike SQL-92
            return new BooleanColumnGetter(to);

        // getString, Clob
        case Types.CHAR:
        case Types.VARCHAR:
        case Types.LONGVARCHAR:
        case Types.CLOB:
        case Types.NCHAR:
        case Types.NVARCHAR:
        case Types.LONGNVARCHAR:
            return new StringColumnGetter(to);

        // TODO
        //// getBytes Blob
        //case Types.BINARY:
        //case Types.VARBINARY:
        //case Types.LONGVARBINARY:
        //case Types.BLOB:
        //    return new BytesColumnGetter();

        // getDate
        case Types.DATE:
            return new DateColumnGetter(to); // TODO

        // getTime
        case Types.TIME:
            return new TimeColumnGetter(to); // TODO

        // getTimestamp
        case Types.TIMESTAMP:
            return new TimestampColumnGetter(to);

        // TODO
        //// Null
        //case Types.NULL:
        //    return new NullColumnGetter();

        // getBigDecimal
        case Types.NUMERIC:
        case Types.DECIMAL:
            return new BigDecimalColumnGetter(to);

        // others
        case Types.ARRAY:  // array
        case Types.STRUCT: // map
        case Types.REF:
        case Types.DATALINK:
        case Types.SQLXML: // XML
        case Types.ROWID:
        case Types.DISTINCT:
        case Types.JAVA_OBJECT:
        case Types.OTHER:
        default:
            throw unsupportedOperationException(column);
        }
    }

    private static UnsupportedOperationException unsupportedOperationException(JdbcColumn column)
    {
        throw new UnsupportedOperationException(
                String.format("Unsupported type %s (sqlType=%d) of '%s' column. Please exclude the column from 'select:' option.",
                    column.getTypeName(), column.getSqlType(), column.getName()));
    }
}
