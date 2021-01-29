import java.io.IOException;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SeedSelection {
	public static class SeedSelectionMapper extends Mapper<Object, Text, Text, Text> {
		private Text from = new Text();
		private Text to = new Text();
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer st = new StringTokenizer(value.toString());
			int count = 0;
			while (st.hasMoreTokens()) {
				if(count == 0){
					from.set(st.nextToken().toString());
					count+=1;
				}else if(count==1){
					to.set(st.nextToken().toString());
				}else{
					break;
				}
			}
			context.write(from, to);
		}
	}

	public static class SeedSelectionReducer extends Reducer<Text, Text, Text, Text> {
		private final static Text mark = new Text("%");
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			int count = 0;
			int threshold = 4;
			for(Text value : values){
				count+=1;
			}
			if(count > threshold){
				context.write(key, mark);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Seed Selection");
		job.setJarByClass(SeedSelection.class);
		job.setMapperClass(SeedSelectionMapper.class);
		job.setReducerClass(SeedSelectionReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
