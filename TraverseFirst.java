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

public class TraverseFirst {
	public static class TraverseFirstMapper extends Mapper<Object, Text, Text, Text> {
		private Text from = new Text();
		private Text to = new Text();
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer st = new StringTokenizer(value.toString());
			int count = 0;
			String current;
			boolean isTag = true;
			while (st.hasMoreTokens()) {
				current = st.nextToken().toString();
				if(count == 0){
					if(!current.contains("*")){
						isTag = false;
						break;
					}
					from.set(current);
					count+=1;
				}else if(count==1){
					to.set(current);
				}else{
					break;
				}
			}
			if(isTag){
				context.write(from, to);
			}
		}
	}

	public static class TraverseFirstReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			Text trim_key = new Text(key.toString().replace("*", ""));
			for(Text value : values){
				context.write(trim_key, value);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Traverse First");
		job.setJarByClass(TraverseFirst.class);
		job.setMapperClass(TraverseFirstMapper.class);
		job.setReducerClass(TraverseFirstReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
