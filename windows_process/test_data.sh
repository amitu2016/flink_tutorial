#!/bin/bash

# Test data for the Flink application
# Format: date,month,category,product,profit

echo "01-06-2018,June,Category5,Bat,12" | nc localhost 9090
sleep 1
echo "02-06-2018,June,Category4,Perfume,10" | nc localhost 9090
sleep 1
echo "03-06-2018,June,Category3,Phone,15" | nc localhost 9090
sleep 1
echo "04-06-2018,June,Category2,Laptop,25" | nc localhost 9090
sleep 1
echo "05-06-2018,June,Category1,Tablet,8" | nc localhost 9090

echo "Test data sent to localhost:9090" 