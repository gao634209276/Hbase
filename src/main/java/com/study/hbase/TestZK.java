package com.it18zhang.hbase.test;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Table;

public class TestZK {

	public static void main(String[] args) throws IOException {

		Configuration config = HBaseConfiguration.create();
		Connection conn = ConnectionFactory.createConnection(config);
		Admin admin = conn.getAdmin();

		TableName tableName = TableName.valueOf("test");
		conn.getTable(tableName);
		// create
		// admin.createTable(desc);
		// disable
		admin.disableTable(tableName);
		// drop table
		admin.deleteTable(tableName);

		Table table = (Table) conn.getTable(tableName);
		// table.get(arg0, arg1);
		// table.putAll(arg0);
		// ResultScanner rs = table.getScanner(scan);
		// for(Result r :rs ){
		// r.toString();
		// }

		admin.deleteColumn(null, null);

	}
}
