#!/bin/bash

echo "Monitoring output directory for new files..."
echo "Press Ctrl+C to stop monitoring"
echo ""

# Monitor the output directory for changes
watch -n 1 "echo '=== Current files in output directory ==='; ls -la /Users/amitupadhyay/Developer/flink-demo/windowing/www/; echo ''; echo '=== Latest file content ==='; find /Users/amitupadhyay/Developer/flink-demo/windowing/www/ -name '*.txt' -o -name 'part-*' | head -1 | xargs cat 2>/dev/null || echo 'No finalized files yet'" 