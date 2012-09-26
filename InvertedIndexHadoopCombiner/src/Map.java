import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

/* Author Victor Guana
 * University of Alberta
 * Department of Computing Science
 * guana@cs.ualberta.ca
*/

public class Map extends Mapper<LongWritable, Text, Text, Text>  {
	
		// Global variables accessed across parallel maps
		private String id="default";
		
		private HashMap<String, Integer> fileCounterMap =null;
	
		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			// Hashmap with the partial combiners per file read
			HashMap<String, HashMap<String, Integer>> master = new HashMap<String, HashMap<String, Integer>>();
					
	    	// Getting the file words in a single line
			String line = value.toString();
	
			// Getting the file path (case of individual page per file)
			//String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
			
			// Split the string in separate words
			StringTokenizer count = new StringTokenizer(line.toLowerCase());
				
			while (count.hasMoreTokens()) {
				String wordT = count.nextToken();
				
				if(wordT.startsWith("###")){
					id = wordT.substring(3);
				}
				else
				{
					// Map to count word occurrences per file (given id)
					fileCounterMap = master.get(id);
					
					if(fileCounterMap == null){
						//Does Not exist? create a map to count the word occurrences within a file
						HashMap<String, Integer> fileCounterMapT = new HashMap<String, Integer>();
						master.put(id, fileCounterMapT);
						
					}
					else{
						//Does Exist? start counting the words introducing a new counter per word within a file
						Integer counter = fileCounterMap.get(wordT);
						if (counter == null) {
							fileCounterMap.put(wordT, 1);
							
						} 
						else {
							Integer actual = counter+1;
							fileCounterMap.put(wordT, actual);
						}	
					}
				}
			}
			
			// Emit keys Phase
			
			//Per File counting map
			Iterator<String> itCombinerFile = master.keySet().iterator();
			while(itCombinerFile.hasNext()){
				String fileid = itCombinerFile.next();
				HashMap<String, Integer> countFile= master.get(fileid);
				
				// and per  Word counting map within a file counter
				Iterator<String> itCombiner = countFile.keySet().iterator();
				
				while(itCombiner.hasNext()){
					String word = itCombiner.next();
					Integer countWord = countFile.get(word);
					
					// Emit the KV tuple <word, <file,counter>
					String combinerTuple = fileid+"="+countWord;
					context.write(new Text(word), new Text(combinerTuple));
				}
			}
		}
	}