package com.study.hbase;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

public class TestBatch {

	private static Configuration conf = null;
	private static Connection conn = null;
	static{
		try {
			conf = HBaseConfiguration.create();
			conn = ConnectionFactory.createConnection(conf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void createTable(String name) throws IOException{
		Admin admin = conn.getAdmin();
		TableName tableName = TableName.valueOf(name);
		if(admin.tableExists(tableName)){
			admin.disableTable(tableName);
			admin.deleteTable(tableName);
		}
		HTableDescriptor htd = new HTableDescriptor(tableName);
		HColumnDescriptor hcd = new HColumnDescriptor("data");
		htd.addFamily(hcd);
		admin.createTable(htd);
	}
	public static void batchPut(int n) throws IOException{
		TableName tn = TableName.valueOf("test4");
		Table t = conn.getTable(tn);
		Put put = null;
		for (int i = 0; i < n; i++) {
			put = new Put(Bytes.toBytes(i));
			put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("1"), Bytes.toBytes("tom"+i));
			t.put(put);
			System.out.println("i");
		}
		t.close();
	}
	
	public static void main(String[] args) throws IOException {
		createTable("test4");
		batchPut(100000);
	}
}
