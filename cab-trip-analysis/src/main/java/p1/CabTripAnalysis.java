package p1;

import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.typeutils.TupleTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class CabTripAnalysis {

    public static void main(String[] args) throws Exception {
        // Create Flink streaming execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Read the cab trip data from a text file
        DataStream<String> lines = env.readTextFile("/Users/amitupadhyay/Developer/flink-demo/cab+flink.txt");

        // Calculate total passengers per destination for ongoing trips
        DataStream<String> destinationPassengers = lines
            .filter(line -> line.contains("yes")) // only ongoing trips
            .map(line -> {
                String[] tokens = line.split(",");
                return Tuple2.of(tokens[6], Integer.parseInt(tokens[7])); // destination, passenger count
            })
            .returns(new TupleTypeInfo<>(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO))
            .keyBy(t -> t.f0) // group by destination
            .reduce((v1, v2) -> Tuple2.of(v1.f0, v1.f1 + v2.f1)) // sum passengers per destination
            .map(t -> "Destination: " + t.f0 + ", Passengers: " + t.f1);

        // Calculate average passengers per pickup location for ongoing trips
        DataStream<String> pickupAvg = lines
            .filter(line -> line.contains("yes"))
            .map(line -> {
                String[] tokens = line.split(",");
                return Tuple3.of(tokens[5], Integer.parseInt(tokens[7]), 1); // pickup, passengers, trip count
            })
            .returns(new TupleTypeInfo<>(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO))
            .keyBy(t -> t.f0) // group by pickup location
            .reduce((v1, v2) -> Tuple3.of(v1.f0, v1.f1 + v2.f1, v1.f2 + v2.f2)) // sum passengers & trips
            .map(t -> "Pickup: " + t.f0 + ", Avg Passengers: " + (t.f1 / t.f2));

        // Calculate average passengers per driver for ongoing trips
        DataStream<String> driverAvg = lines
            .filter(line -> line.contains("yes"))
            .map(line -> {
                String[] tokens = line.split(",");
                return Tuple3.of(tokens[3], Integer.parseInt(tokens[7]), 1); // driver, passengers, trip count
            })
            .returns(new TupleTypeInfo<>(BasicTypeInfo.STRING_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO, BasicTypeInfo.INT_TYPE_INFO))
            .keyBy(t -> t.f0) // group by driver
            .reduce((v1, v2) -> Tuple3.of(v1.f0, v1.f1 + v2.f1, v1.f2 + v2.f2)) // sum passengers & trips
            .map(t -> "Driver: " + t.f0 + ", Avg Passengers: " + (t.f1 / t.f2));

        // Write each result stream to separate output files
        destinationPassengers.writeAsText("/Users/amitupadhyay/Developer/flink-demo/output/destination_passengers.txt");
        pickupAvg.writeAsText("/Users/amitupadhyay/Developer/flink-demo/output/pickup_average.txt");
        driverAvg.writeAsText("/Users/amitupadhyay/Developer/flink-demo/output/driver_average.txt");

        // Execute the Flink streaming job
        env.execute("Cab Trip Analysis");
    }
}

