package hbase.newapi;


import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;

public class Demo1 {
	static Configuration conf = null;
	static Connection conn = null;
	static {
		conf = HBaseConfiguration.create();
		conf.set("hbsae.zookeeper.quorum", "hadoop:2181");
		conf.set("hbase.master", "hadoop:16010");
		try {
			conn = ConnectionFactory.createConnection(conf);
			Admin admin = conn.getAdmin();
			Table table = conn.getTable(TableName.valueOf("tablename"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
