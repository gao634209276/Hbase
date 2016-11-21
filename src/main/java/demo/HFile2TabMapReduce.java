package demo;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat2;
import org.apache.hadoop.hbase.mapreduce.LoadIncrementalHFiles;
import org.apache.hadoop.hbase.mapreduce.PutSortReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.io.IOException;

/**
 * HADOOP_CLASSPATH=`${HBASE_HOME}/bin/hbase classpath`
 */
public class HFile2TabMapReduce extends Configured implements Tool {
	public static class HFile2TabMapper extends Mapper<LongWritable, Text, ImmutableBytesWritable, Put> {
		ImmutableBytesWritable rowkey = new ImmutableBytesWritable();

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

			String[] words = value.toString().split("\t");
			Put put = new Put(Bytes.toBytes(words[0]));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("name"), Bytes.toBytes(words[1]));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("age"), Bytes.toBytes(words[2]));
			rowkey.set(Bytes.toBytes(words[0]));
			context.write(rowkey, put);
		}
	}

	@Override
	public int run(String[] args) throws Exception {
		//create job
		Job job = Job.getInstance(this.getConf(), this.getClass().getSimpleName());
		//set class
		job.setJarByClass(this.getClass());
		//set path
		FileInputFormat.addInputPath(job, new Path(args[1]));
		FileOutputFormat.setOutputPath(job, new Path(args[2]));

		//set mapper
		job.setMapperClass(HFile2TabMapper.class);
		job.setMapOutputKeyClass(ImmutableBytesWritable.class);
		job.setMapOutputValueClass(Put.class);

		//set reduce
		job.setReducerClass(PutSortReducer.class);

		// set hfile output
		HTable table = new HTable(getConf(), args[0]);
		HFileOutputFormat2.configureIncrementalLoad(job, table);

		// submit job
		boolean b = job.waitForCompletion(true);
		if (!b) {
			throw new IOException("error with job !!!");
		}

		LoadIncrementalHFiles loader = new LoadIncrementalHFiles(getConf());
		// load hfile
		loader.doBulkLoad(new Path(args[2]), table);
		return 0;
	}

	public static void main(String[] args) throws Exception {
		//get configuration
		Configuration conf = HBaseConfiguration.create();

		//run job
		int status = ToolRunner.run(conf, new HFile2TabMapReduce(), args);

		//exit
		System.exit(status);

	}
}
