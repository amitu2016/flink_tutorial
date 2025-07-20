package p1;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple5;

//import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AverageProfitTumblingProcessing {
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    //env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime);

    DataStream < String > data = env.socketTextStream("localhost", 9090);

    // month, product, category, profit, count
    DataStream < Tuple5 < String, String, String, Integer, Integer >> mapped = data.map(new Splitter()); // tuple  [June,Category5,Bat,12,1]
    //        [June,Category4,Perfume,10,1]
    // groupBy 'month'                                                                                           
    DataStream < Tuple5 < String, String, String, Integer, Integer >> reduced = mapped
      .keyBy(t -> t.f0)
      .window(TumblingProcessingTimeWindows.of(Time.seconds(2)))
      .reduce(new Reduce1());
    // June { [Category5,Bat,12,1] Category4,Perfume,10,1}	//rolling reduce			
    // reduced = { [Category4,Perfume,22,2] ..... }
    
    // Convert Tuple5 to String for better file output
    DataStream<String> stringOutput = reduced.map(tuple -> 
      String.format("Month: %s, Category: %s, Product: %s, Total Profit: %d, Count: %d", 
        tuple.f0, tuple.f1, tuple.f2, tuple.f3, tuple.f4));
    
    // Print to console for immediate visibility
    stringOutput.print();
    
    // Write directly to text file
    stringOutput.addSink(new TextFileSink("/Users/amitupadhyay/Developer/flink-demo/windowing/www"));

    // execute program
    env.execute("Avg Profit Per Month");
  }

  // Custom sink that writes directly to text files
  public static class TextFileSink implements SinkFunction<String> {
    private final String outputPath;
    private PrintWriter writer;
    private String currentFileName;
    
    public TextFileSink(String outputPath) {
      this.outputPath = outputPath;
    }
    
    @Override
    public void invoke(String value, Context context) throws Exception {
      // Create filename with timestamp
      String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd--HH"));
      String fileName = outputPath + "/" + timestamp + "/output.txt";
      
      // Create directory if it doesn't exist
      java.io.File dir = new java.io.File(outputPath + "/" + timestamp);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      
      // Open file for writing (append mode)
      if (writer == null || !fileName.equals(currentFileName)) {
        if (writer != null) {
          writer.close();
        }
        writer = new PrintWriter(new FileWriter(fileName, true));
        currentFileName = fileName;
      }
      
      // Write the data with timestamp
      String timestamp2 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
      writer.println("[" + timestamp2 + "] " + value);
      writer.flush(); // Ensure data is written immediately
    }
  }

  public static class Reduce1 implements ReduceFunction < Tuple5 < String, String, String, Integer, Integer >> {
    public Tuple5 < String,
    String,
    String,
    Integer,
    Integer > reduce(Tuple5 < String, String, String, Integer, Integer > current,
      Tuple5 < String, String, String, Integer, Integer > pre_result) {
      return new Tuple5 < String, String, String, Integer, Integer > (current.f0,
        current.f1, current.f2, current.f3 + pre_result.f3, current.f4 + pre_result.f4);
    }
  }
  public static class Splitter implements MapFunction < String, Tuple5 < String, String, String, Integer, Integer >> {
    public Tuple5 < String,
    String,
    String,
    Integer,
    Integer > map(String value) // 01-06-2018,June,Category5,Bat,12
    {
      String[] words = value.split(","); // words = [{01-06-2018},{June},{Category5},{Bat}.{12}
      // ignore timestamp, we don't need it for any calculations
      //Long timestamp = Long.parseLong(words[5]);
      return new Tuple5 < String, String, String, Integer, Integer > (words[1], words[2], words[3], Integer.parseInt(words[4]), 1);
    } //    June    Category5      Bat                      12 
  }
}

