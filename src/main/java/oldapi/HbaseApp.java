package oldapi;

import java.io.IOException;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class HbaseApp {

	public static void main(String[] args) throws Exception {
		// configuration
		Configuration conf = HBaseConfiguration.create();
		//admin
		HBaseAdmin admin = new HBaseAdmin(conf);
		//table
		TableName name = TableName.valueOf("test3");
		//descrptor
		HTableDescriptor tdesc = new HTableDescriptor(name);
		//coulumn family desc
		HColumnDescriptor hcd = new HColumnDescriptor("data");
		tdesc.addFamily(hcd);
		admin.createTable(tdesc);
		System.out.println("create table over");
	}
	@SuppressWarnings("deprecation")
	public void get() throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		// configuration
		Configuration conf = HBaseConfiguration.create();
		// admin
		HBaseAdmin admin = new HBaseAdmin(conf);
		// table
		HTable table = new HTable(conf, "test3");
		Get get = new Get(Bytes.toBytes("test3"));
		get.addColumn(Bytes.toBytes("data"), Bytes.toBytes("1"));
		get.addColumn(Bytes.toBytes("data"), Bytes.toBytes("2"));
		get.addColumn(Bytes.toBytes("data"), Bytes.toBytes("3"));
		Result result = table.get(get);
		List<KeyValue> kv = result.getColumn(Bytes.toBytes("data"),
				Bytes.toBytes("1"));
		System.out.println("data:1" + Bytes.toBytes(kv.toString()));

		table.close();
		System.out.println("put data over!");

	}
	@SuppressWarnings("deprecation")
	public void put() throws MasterNotRunningException, ZooKeeperConnectionException, IOException{
		// configuration
		Configuration conf = HBaseConfiguration.create();
		// admin
		HBaseAdmin admin = new HBaseAdmin(conf);
		// table
		//TableName table = TableName.valueOf("test3");
		HTable table = new HTable(conf,"test3");
		Put put = new Put(Bytes.toBytes("row1"));
		put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("1"), Bytes.toBytes("hello"));
		put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("2"), Bytes.toBytes("hadoop"));
		put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("3"), Bytes.toBytes("hbase"));
		table.put(put);
		table.close();
		System.out.println("put data over!");
		
	}
}
