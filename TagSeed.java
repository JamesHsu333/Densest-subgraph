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

public class TagSeed {
	public static class TagSeedMapper extends Mapper<Object, Text, Text, Text> {
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

	public static class TagSeedReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			boolean isTag = false;
			Text tag_key = new Text(key.toString().concat("*"));
			List<String> cache = new ArrayList<String>();
			for(Text value : values){
				cache.add(value.toString());
				if(value.toString().contains("%")){
					isTag = true;
				}
			}
			for(String v : cache){
				if(isTag && !v.contains("%")){
					context.write(tag_key, new Text(v));
				}else if(!isTag && !v.contains("%")){
					context.write(key, new Text(v));
				}
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Tag Seed");
		job.setJarByClass(TagSeed.class);
		job.setMapperClass(TagSeedMapper.class);
		job.setReducerClass(TagSeedReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
