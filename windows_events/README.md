# Flink Tumbling Event Window - Direct Text File Output

This Flink application processes streaming data with tumbling event-time windows and writes the results **directly to visible text files**—no `.inprogress` files, no extra scripts needed!

## ✅ Direct Text File Creation
- **Visible text files** in macOS Finder (or any OS)
- **No hidden files** or `.inprogress` extensions
- **Human-readable format** with timestamps
- **No extra scripts needed**

## How to Run

### 1. Compile and Run the Flink Application
```bash
mvn clean compile exec:java -Dexec.mainClass="windows_events.AverageProfitTumblingEvent"
```

### 2. Send Test Data (in another terminal)
```bash
echo "1721480723000,123" | nc localhost 9090
echo "1721480724000,200" | nc localhost 9090
```

### 3. Check Your Text Files!
Files are created directly in:
```
/Users/amitupadhyay/Developer/flink-demo/windowing/www/
```
with names like:
```
flink_output_2025-07-20.txt
```

## Input Data Format
Each line sent to the socket should be:
```
timestamp,value
```
Example:
```
1721480723000,123
1721480724000,200
```

## Output
- **Files are written directly** to `/Users/amitupadhyay/Developer/flink-demo/windowing/www/`
- **No .inprogress files** - everything is visible immediately
- **Human-readable format** with clear labels and timestamps
- **Files are created in real-time** as data is processed

### Example Output
```
[16:45:23] Timestamp: 1721480723000, Sum: 123
[16:45:28] Timestamp: 1721480724000, Sum: 323
```

## Troubleshooting
- Make sure the Flink application is running before sending data
- Check that port 9090 is available
- Files are created immediately as data is processed
- **No more .inprogress files!** Everything is visible in Finder
- **No extra scripts needed!** Files are ready to use immediately 