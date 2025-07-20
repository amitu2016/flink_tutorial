package p1;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple text file sink that writes directly to visible text files
 * No .inprogress files, no extra scripts needed!
 */
public class SimpleTextFileSink implements SinkFunction<String> {
    private final String outputPath;
    private PrintWriter writer;
    
    public SimpleTextFileSink(String outputPath) {
        this.outputPath = outputPath;
    }
    
    @Override
    public void invoke(String value, Context context) throws Exception {
        // Create filename with current date
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String fileName = outputPath + "/flink_output_" + date + ".txt";
        
        // Create directory if it doesn't exist
        java.io.File dir = new java.io.File(outputPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Open file for writing (append mode)
        if (writer == null) {
            writer = new PrintWriter(new FileWriter(fileName, true));
        }
        
        // Write the data with timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        writer.println("[" + timestamp + "] " + value);
        writer.flush(); // Ensure data is written immediately
    }
} 