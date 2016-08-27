package cn.itcast.hbase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Before;
import org.junit.Test;

public class HBaseDemo {
	Configuration conf;
	Connection conn;

	@Before
	public void init() {
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		conf.set("hbase.zookeeper.quorum", "hadoop,hadoop1,hadoop2");
		try {
			conn = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void createTable() throws IOException {

		HBaseAdmin admin = (HBaseAdmin) conn.getAdmin();
		HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("people"));
		HColumnDescriptor htd_info = new HColumnDescriptor("info");
		htd.addFamily(htd_info);
		htd.addFamily(new HColumnDescriptor("data"));
		htd_info.setMaxVersions(3);

		admin.createTable(htd);
		admin.close();

	}

	@Test
	public void testPut() throws IOException {
		HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("people"));
		HTable table = (HTable) conn.getTable(TableName.valueOf("people"));

		Put put = new Put(Bytes.toBytes("rk0001"));
		put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("name"),
				Bytes.toBytes("zhangsan"));
		put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("age"),
				Bytes.toBytes("25"));
		put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("money"),
				Bytes.toBytes("10w"));
		table.put(put);
	}

	@Test
	public void testPutAll() throws IOException {
		// HTablePool pool =

		HTable table = (HTable) conn.getTable(TableName.valueOf("people"));

		/*
		 * List<Put> puts = new ArrayList<Put>(); for(int i=1;i<= 100000; i++){
		 * Put put = new Put(Bytes.toBytes("rk"+i));
		 * put.addImmutable(Bytes.toBytes("info"),Bytes
		 * .toBytes("money"),Bytes.toBytes(""+i)); puts.add(put); }
		 */
		List<Put> puts = new ArrayList<Put>(10000);
		for (int i = 1; i <= 100001; i++) {
			Put put = new Put(Bytes.toBytes("rk" + i));
			put.addImmutable(Bytes.toBytes("info"), Bytes.toBytes("money"),
					Bytes.toBytes("" + i));
			puts.add(put);
			if (i % 10000 == 0) {
				table.put(puts);
				puts = new ArrayList<Put>(10000);
			}
		}

		table.put(puts);
		table.close();
	}

	@Test
	public void testGet() throws IOException {

		HTable table = (HTable) conn.getTable(TableName.valueOf("people"));
		Get get = new Get(Bytes.toBytes("rk9999"));
		Result result = table.get(get);
		String str = Bytes.toString(result.getValue(Bytes.toBytes("info"),
				Bytes.toBytes("money")));
		System.out.println(str);
		table.close();
	}

	@Test
	public void testScan() throws IOException {
		HTable table = (HTable) conn.getTable(TableName.valueOf("people"));
		Scan scan = new Scan(Bytes.toBytes("rk29990"), Bytes.toBytes("rk30000"));
		ResultScanner resultScaner = table.getScanner(scan);
		for (Result result : resultScaner) {
			String str = Bytes.toString(result.getValue(Bytes.toBytes("info"),
					Bytes.toBytes("money")));
			System.out.println(str);
		}
		table.close();
	}

	@Test
	public void testDelete() throws IOException {
		HTable table = (HTable) conn.getTable(TableName.valueOf("people"));
		Delete delete = new Delete(Bytes.toBytes("rk9999"));
		table.delete(delete);
		table.close();
	}

	@Test
	public void testQueryByCondition() throws IOException {
		HTable table = (HTable) conn.getTable(TableName.valueOf("people"));
		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("info"),
				Bytes.toBytes("money"), CompareOp.EQUAL, Bytes.toBytes("999"));
		Scan s = new Scan();
		s.setFilter(filter);
		ResultScanner rs = table.getScanner(s);
		for (Result result : rs) {
			System.out.println("rowkey:" + new String(result.getRow()));
		}
	}
}
