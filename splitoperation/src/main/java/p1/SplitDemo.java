package p1;

import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class SplitDemo {
public static void main(String[] args) throws Exception {
  // set up the stream execution environment
  final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

  // Checking input parameters
  final ParameterTool params = ParameterTool.fromArgs(args);

  // make parameters available in the web interface
  env.getConfig().setGlobalJobParameters(params);

  DataStream < String > text = env.readTextFile("/Users/amitupadhyay/Developer/flink-demo/splitoperation/oddeven");

  // String type side output for Even values
  final OutputTag < String > evenOutTag = new OutputTag < String > ("even-string-output") {};
  // Integer type side output for Odd values
  final OutputTag < Integer > oddOutTag = new OutputTag < Integer > ("odd-int-output") {};

  SingleOutputStreamOperator < Integer > mainStream = text
    .process(new ProcessFunction < String, Integer > () {
      @Override
      public void processElement(
        String value,
        Context ctx,
        Collector < Integer > out) throws Exception {

        int intVal = Integer.parseInt(value);

        // get all data in regular output as well
        out.collect(intVal);

        if (intVal % 2 == 0) {
          // emit data to side output for even output
          ctx.output(evenOutTag, String.valueOf(intVal));
        } else {
          // emit data to side output for even output
          ctx.output(oddOutTag, intVal);
        }
      }
    });

  DataStream < String > evenSideOutputStream = mainStream.getSideOutput(evenOutTag);
  DataStream < Integer > oddSideOutputStream = mainStream.getSideOutput(oddOutTag);

  evenSideOutputStream.writeAsText("/Users/amitupadhyay/Developer/flink-demo/splitoperation/even");
  oddSideOutputStream.writeAsText("/Users/amitupadhyay/Developer/flink-demo/splitoperation/odd");

  // execute program
  env.execute("ODD EVEN");
}
}