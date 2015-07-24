package com.facebook.presto.connector.jmx;

import com.facebook.presto.spi.ConnectorSession;
import com.facebook.presto.spi.SchemaTableName;
import com.facebook.presto.spi.type.BigintType;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.Test;

import java.util.List;

import static com.facebook.presto.spi.type.TimeZoneKey.UTC_KEY;
import static com.facebook.presto.spi.type.VarcharType.VARCHAR;
import static java.lang.management.ManagementFactory.getPlatformMBeanServer;
import static java.util.Locale.ENGLISH;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class TestJmxMetadata
{
    private static final ConnectorSession SESSION = new ConnectorSession("user", UTC_KEY, ENGLISH, System.currentTimeMillis(), null);
    private static final String RUNTIME_OBJECT = "java.lang:type=Runtime";
    private static final SchemaTableName RUNTIME_TABLE = new SchemaTableName("jmx", RUNTIME_OBJECT.toLowerCase(ENGLISH));

    private final JmxMetadata metadata = new JmxMetadata("test", getPlatformMBeanServer());

    @Test
    public void testListSchemas()
            throws Exception
    {
        assertEquals(metadata.listSchemaNames(SESSION), ImmutableList.of("jmx"));
    }

    @Test
    public void testListTables()
    {
        assertTrue(metadata.listTables(SESSION, "jmx").contains(RUNTIME_TABLE));
    }

    @Test
    public void testGetTableHandle()
            throws Exception
    {
        JmxTableHandle handle = metadata.getTableHandle(SESSION, RUNTIME_TABLE);
        assertEquals(handle.getConnectorId(), "test");
        assertEquals(handle.getObjectName(), RUNTIME_OBJECT);

        List<JmxColumnHandle> columns = handle.getColumns();
        assertTrue(columns.contains(new JmxColumnHandle("test", "node", VARCHAR)));
        assertTrue(columns.contains(new JmxColumnHandle("test", "Name", VARCHAR)));
        assertTrue(columns.contains(new JmxColumnHandle("test", "StartTime", BigintType.BIGINT)));
    }
}
