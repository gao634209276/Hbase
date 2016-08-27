package com.study.hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;

public class HBase {

	static Configuration conf = null;
	static {
		conf = HBaseConfiguration.create();
		conf.set("hbsae.zookeeper.quorum", "hadoop:2181");
		conf.set("hbase.master", "hadoop:16010");
		
	}

	@SuppressWarnings("resource")
	public static void creatTable(String tableName, String[] family) throws IOException {
		HBaseAdmin admin = new HBaseAdmin(conf);
		HTableDescriptor desc = new HTableDescriptor(tableName);
		for (int i = 0; i < family.length; i++) {
			desc.addFamily(new HColumnDescriptor(family[i]));
		}
		if (admin.tableExists(tableName)) {
			System.out.println("table Exists!");
			System.exit(0);
		} else {
			admin.createTable(desc);
			System.out.println("create table Success!");
		}
	}

	@SuppressWarnings("resource")
	public static void addData(String rowKey, String tableName, String[] column1, String[] value1, String[] column2,
			String[] value2) throws IOException {

		Put put = new Put(Bytes.toBytes(rowKey));
		HTable table = new HTable(conf, Bytes.toBytes(tableName));

		HColumnDescriptor[] columnFamilies = table.getTableDescriptor().getColumnFamilies();

		for (int i = 0; i < columnFamilies.length; i++) {
			String familyName = columnFamilies[i].getNameAsString();
			if (familyName.equals("article")) {
				for (int j = 0; j < column1.length; j++) {
					put.add(Bytes.toBytes(familyName), Bytes.toBytes(column1[j]), Bytes.toBytes(value1[j]));
					
				}
			}
			if(familyName.equals("author")){
				for(int j=0;j<column2.length;j++){
					put.add(Bytes.toBytes(familyName), Bytes.toBytes(column2[j]), Bytes.toBytes(value2[j]));
				}
			}
		}
		table.put(put);
		System.out.println("add data Success!");
	}
	
	public static void main(String[] args) throws IOException{
		
		String tableName="blog2";
		  String[] family = { "article", "author" };
	        creatTable(tableName, family);


	        String[] column1 = { "title", "content", "tag" };
	        String[] value1 = {
	                "Head First HBase",
	                "HBase is the Hadoop database. Use it when you need random, realtime read/write access to your Big Data.",
	                "Hadoop,HBase,NoSQL" };
	        String[] column2 = { "name", "nickname" };
	        String[] value2 = { "nicholas", "lee" };
	        addData("rowkey1", "blog2", column1, value1, column2, value2);
	        addData("rowkey2", "blog2", column1, value1, column2, value2);
	        addData("rowkey3", "blog2", column1, value1, column2, value2);

	}
}