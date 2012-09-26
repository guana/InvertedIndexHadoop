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
		HashMap<String, Integer> fileCounter = new HashMap<String, Integer> ();
		
		// Get all the values (files)
		for (Text value : values) {
			
			String fileCount = value.toString();
			String [] fileCountTuple = fileCount.split("=");
			
			String fileName = fileCountTuple[0];
			Integer countForFile = Integer.parseInt(fileCountTuple[1]);
						
			// Is the word in the map?
			Integer counter = fileCounter.get(fileName);
			
			// if not initialize the counter. Else, take the actual counter and add 1
			if (counter == null) {
				fileCounter.put(fileName, countForFile);
			} 
			else {
				fileCounter.remove(fileName);
				Integer actual = counter+countForFile;
				fileCounter.put(fileName, actual);
			}
		}
		// Emit the reduction {word, <file, counter>}
		context.write(key, new Text(fileCounter.toString()));
	}
}
    