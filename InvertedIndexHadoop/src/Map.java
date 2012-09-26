/* Author Victor Guana
 * University of Alberta
 * Department of Computing Science
 * guana@cs.ualberta.ca
*/

import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class Map extends Mapper<LongWritable, Text, Text, Text>  {
	
		private String id="default";
	
	    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
	    	// Getting the file words in a single line
			String line = value.toString();
	
			// Getting the file path
			//String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
			
			// Split the string in separate words
			StringTokenizer count = new StringTokenizer(line.toLowerCase());
			
			// Ids internal pages per file
			
			
			// Emit the KV {word, file}
			while (count.hasMoreTokens()) {
				
				String wordT = count.nextToken();
				if(wordT.startsWith("###")){
					id = wordT.substring(3);
				}
				else{
					context.write(new Text(wordT), new Text(id));
				}
				
			}
		}
	}