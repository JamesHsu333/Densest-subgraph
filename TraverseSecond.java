import java.io.*;
import java.net.URI;
import java.util.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TraverseSecond {
	public static class TraverseSecondMapper extends Mapper<Object, Text, Text, Text> {
		ArrayList<String> seed = null;
		public void setup(Context context) throws IOException, InterruptedException {
			seed = new ArrayList<String>();
			URI[] cache =context.getCacheFiles();
			if(cache != null && cache.length > 0){
				try{
					String line = "";
					FileSystem fs = FileSystem.get(context.getConfiguration());
					Path getFilePath = new Path(cache[0].toString());
					BufferedReader reader = new BufferedReader(new InputStreamReader(fs.open(getFilePath)));
					while((line = reader.readLine()) != null){
						StringTokenizer st = new StringTokenizer(line);
						int count = 0;
						while(st.hasMoreTokens()){
							String current = "";
							current = st.nextToken().toString();
							if(count == 1){
								seed.add(current);
							}
							count+=1;
						}
					}
				}catch(Exception e){
					System.out.println("Unable to read the File");
					System.exit(1);
				}
			}
		}
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			StringTokenizer st = new StringTokenizer(value.toString());
			int count = 0;
			boolean isTag = false;
			String from = "";
			String to = "";
			while (st.hasMoreTokens()) {
				if(count == 0){
					from = st.nextToken().toString();
					count+=1;
				}else if(count==1){
					to = st.nextToken().toString();
				}else{
					break;
				}
			}
			if(seed.contains(from) && seed.contains(to)){
				context.write(new Text(from), new Text(to));
			}
		}
	}

	public static class TraverseSecondReducer extends Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			for(Text value : values){
				context.write(key, value);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Traverse Second");
		job.setJarByClass(TraverseSecond.class);
		job.setMapperClass(TraverseSecondMapper.class);
		job.setReducerClass(TraverseSecondReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		try {
			job.addCacheFile(new URI("hdfs://localhost:9000/user/james/"+args[2]));
		}
		catch (Exception e) {
			System.out.println("File Not Added");
			System.exit(1);
		}
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
