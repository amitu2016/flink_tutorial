package p1;

import java.sql.Timestamp;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class AverageProfitTumblingEvent {
  public static void main(String[] args) throws Exception {
    // set up the streaming execution environment
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    WatermarkStrategy < Tuple2 < Long, String >> ws =
      WatermarkStrategy
      . < Tuple2 < Long, String >> forMonotonousTimestamps()
      .withTimestampAssigner((event, timestamp) -> event.f0);

    DataStream < String > data = env.socketTextStream("localhost", 9090);

    DataStream < Tuple2 < Long, String >> sum = data.map(new MapFunction < String, Tuple2 < Long, String >> () {
        public Tuple2 < Long, String > map(String s) {
          String[] words = s.split(",");
          return new Tuple2 < Long, String > (Long.parseLong(words[0]), words[1]);
        }
      })

      .assignTimestampsAndWatermarks(ws)
      .windowAll(TumblingEventTimeWindows.of(Time.seconds(5)))
      .reduce(new ReduceFunction < Tuple2 < Long, String >> () {
        public Tuple2 < Long, String > reduce(Tuple2 < Long, String > t1, Tuple2 < Long, String > t2) {
          int num1 = Integer.parseInt(t1.f1);
          int num2 = Integer.parseInt(t2.f1);
          int sum = num1 + num2;
          Timestamp t = new Timestamp(System.currentTimeMillis());
          return new Tuple2 < Long, String > (t.getTime(), "" + sum);
        }
      });
    // Format output as human-readable string
    DataStream<String> stringOutput = sum.map(tuple -> String.format("Timestamp: %d, Sum: %s", tuple.f0, tuple.f1));
    stringOutput.addSink(new SimpleTextFileSink("/Users/amitupadhyay/Developer/flink-demo/windowing/www"));

    // execute program
    env.execute("Window");
  }
}

