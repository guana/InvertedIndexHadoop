import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/* Author Victor Guana
 * University of Alberta
 * Department of Computing Science
 * guana@cs.ualberta.ca
*/

public class Reduce extends Reducer<Text, Text, Text, Text> {

	protected void reduce(Text key, Iterable<Text> values, Context context)	throws IOException, InterruptedException {

		// Map to count word occurrences per file
		HashMap<String, Integer> fileCounter = new HashMap<String, Integer>();
		
		// Get all the values (files)
		for (Text value : values) {
			
			String fileCount = value.toString();
			
			// Is the word in the map?
			Integer counter = fileCounter.get(fileCount);
			
			// if not initialize the counter. Else, take the actual counter and add 1
			if (counter == null) {
				fileCounter.put(fileCount, 1);
			} 
			else {
				fileCounter.remove(fileCount);
				Integer actual = counter+1;
				fileCounter.put(fileCount, actual);
			}
		}
		// write the reduction {word, <file, counter>}
		context.write(key, new Text(fileCounter.toString()));
	}
}
    