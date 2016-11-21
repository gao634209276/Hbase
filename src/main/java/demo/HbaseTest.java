package demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.IOUtils;

import java.io.IOException;

public class HbaseTest {
	public static void main(String[] args) throws IOException {
		// createTab();
		//putData();
		//getDate();
		//testNameSpace();
		// scanData();
		testFilter();
	}

	public static void testFilter() {
		HTable table = null;
		try {
			table = getTable();
			Scan scan = new Scan();
			// create filter
			Filter filter = null;
			filter = new PrefixFilter(Bytes.toBytes("rk"));
			filter = new PageFilter(3); //分页

			//create hbase comparator
			ByteArrayComparable comp = null;
			comp = new SubstringComparator("lisi");
			filter = new SingleColumnValueExcludeFilter(
					Bytes.toBytes("info"),
					Bytes.toBytes("name"),
					CompareFilter.CompareOp.EQUAL,
					comp);

			// set filter
			scan.setFilter(filter);
			ResultScanner rs = table.getScanner(scan);
			for (Result result : rs) {
				printResult(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (table != null) {
				IOUtils.closeStream(table);
			}
		}
	}

	public static void scanData() {
		HTable table = null;
		try {
			table = getTable();
			//create Scan instance
			Scan scan = new Scan();
			scan.setStartRow(Bytes.toBytes("rk0001"));
			scan.setStopRow(Bytes.toBytes("rk0003"));
			scan.addFamily(Bytes.toBytes("info"));
			//set cache
			scan.setCacheBlocks(false);//是否缓存本地
			scan.setBatch(2);//面向列的缓存,每次返回列有2行
			scan.setCaching(2);//面向行的缓存,10行数据读取5次.默认-1,通过底层rpc调用
			//set permission
			//scan.setACL(perms);

			//get scanner result
			ResultScanner rs = table.getScanner(scan);
			for (Result result : rs) {
				printResult(result);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (table != null) {
				IOUtils.closeStream(table);
			}

		}
	}

	public static void printResult(Result rs) {
		for (Cell cell : rs.rawCells()) {
			System.out.println(Bytes.toString(CellUtil.cloneRow(cell)) + " : "
					+ Bytes.toString(CellUtil.cloneFamily(cell)) + " : "
					+ Bytes.toString(CellUtil.cloneQualifier(cell)) + " : "
					+ Bytes.toString(CellUtil.cloneValue(cell)));
		}
	}

	public static void testNameSpace() throws IOException {

		Configuration conf = HBaseConfiguration.create();
		HBaseAdmin admin = new HBaseAdmin(conf);
		//create namespace
		NamespaceDescriptor desc = NamespaceDescriptor.create("ns1").build();
		admin.createNamespace(desc);
		//close resource
		admin.close();
	}

	public static void getDate() throws IOException {
		HTable table = getTable();
		Get get = new Get(Bytes.toBytes("rk0001"));

		//get data
		Result result = table.get(get);
		//byte[] value = result.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"));
		//System.out.println(new String(value));
		for (Cell cell : result.rawCells()) {
			//System.out.println(new String(cell.getFamily()) + " : " + new String(cell.getQualifier()) + " : " + new String(cell.getValue()));
			System.out.println(new String(CellUtil.cloneFamily(cell)));
			System.out.println(Bytes.toString(CellUtil.cloneQualifier(cell)));
			System.out.println(Bytes.toString(CellUtil.cloneValue(cell)));
		}
	}

	public static void putData() throws IOException {
		//get Table
		HTable table = getTable();
		//put Table
		Put put = new Put(Bytes.toBytes("rk0001"));
		put.add(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes("lisi"));
		table.put(put);
		//close resource
		table.close();

	}

	public static HTable getTable() throws IOException {
		Configuration conf = HBaseConfiguration.create();
		HTable table = new HTable(conf, Bytes.toBytes("t1"));
		return table;
	}

	public static void createTab() throws IOException {
		// create a default configuration
		Configuration conf = HBaseConfiguration.create();
		// create a hbase admin
		HBaseAdmin admin = new HBaseAdmin(conf);
		boolean b = admin.tableExists(Bytes.toBytes("t1"));
		if (b) {
			admin.disableTable(Bytes.toBytes("t1"));
			admin.deleteTable("t1");
		}
		HTableDescriptor table = new HTableDescriptor(TableName.valueOf("t1"));
		table.addFamily(new HColumnDescriptor(Bytes.toBytes("info")));
		table.addFamily(new HColumnDescriptor(Bytes.toBytes("secret")));
		//create table
		admin.createTable(table);
		admin.close();
	}
}
