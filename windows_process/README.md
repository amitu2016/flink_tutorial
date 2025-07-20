# Flink Tumbling Window Processing

This Flink application processes streaming data with tumbling windows to calculate average profit per month.

## ✅ Direct Text File Creation (No Extra Scripts Needed!)

The application now creates **visible text files directly** without any `.inprogress` files or extra scripts!

### What You Get:
- ✅ **Visible text files** in macOS Finder
- ✅ **No hidden files** or `.inprogress` extensions
- ✅ **Human-readable format** with timestamps
- ✅ **No extra scripts needed**

### File Output Example:
```
[16:45:23] Month: June, Category: Category5, Product: Bat, Total Profit: 232, Count: 9
[16:45:25] Month: August, Category: Category5, Product: PC, Total Profit: 271, Count: 8
[16:45:27] Month: July, Category: Category1, Product: Television, Total Profit: 100, Count: 4
```

## How to Run

### 1. Compile and Run the Flink Application
```bash
mvn clean compile exec:java -Dexec.mainClass="p1.AverageProfitTumblingProcessing"
```

### 2. Send Test Data (in another terminal)
```bash
./test_data.sh
```

### 3. Monitor Output (in another terminal)
```bash
./monitor_output.sh
```

### 4. Check Your Text Files!
Files are created directly in `/Users/amitupadhyay/Developer/flink-demo/windowing/www/` with names like:
- `2025-07-20--16/output.txt` (organized by hour)
- `flink_output_2025-07-20.txt` (if using SimpleTextFileSink)

Or manually send data:
```bash
echo "01-06-2018,June,Category5,Bat,12" | nc localhost 9090
```

## Input Data Format
```
date,month,category,product,profit
01-06-2018,June,Category5,Bat,12
02-06-2018,June,Category4,Perfume,10
```

## Output
- **Files are written directly** to `/Users/amitupadhyay/Developer/flink-demo/windowing/www/`
- **No .inprogress files** - everything is visible immediately
- **Human-readable format** with clear labels and timestamps
- **Files are created in real-time** as data is processed

## Alternative Approaches

### Option 1: Built-in Custom Sink (Current Implementation)
- Creates files organized by hour: `2025-07-20--16/output.txt`
- Includes timestamps for each record
- Automatic directory creation

### Option 2: Simple Text File Sink
If you want even simpler file naming, you can use the `SimpleTextFileSink` class:
```java
stringOutput.addSink(new SimpleTextFileSink("/Users/amitupadhyay/Developer/flink-demo/windowing/www"));
```
This creates files like: `flink_output_2025-07-20.txt`

### Option 3: Legacy Approach (Not Recommended)
If you still want to use the old StreamingFileSink approach:
- Use the original code with `StreamingFileSink`
- Run `./finalize_files.sh` after the job finishes

## Troubleshooting
- Make sure the Flink application is running before sending data
- Check that port 9090 is available
- Files are created immediately as data is processed
- **No more .inprogress files!** Everything is visible in Finder
- **No extra scripts needed!** Files are ready to use immediately 