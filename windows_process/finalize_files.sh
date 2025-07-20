#!/bin/bash

OUTPUT_DIR="/Users/amitupadhyay/Developer/flink-demo/windowing/www"

echo "Looking for .inprogress files to finalize..."

# Find all .inprogress files
find "$OUTPUT_DIR" -name "*.inprogress*" | while read file; do
    echo "Processing: $file"
    
    # Get the directory and base name
    dir=$(dirname "$file")
    base_name=$(basename "$file" | sed 's/\.inprogress.*$//' | sed 's/^\.//')
    
    # Create a visible text file (without dot prefix)
    output_file="$dir/${base_name}.txt"
    
    # Copy content to new file
    cp "$file" "$output_file"
    
    echo "Created: $output_file"
    
    # Remove the .inprogress file
    rm "$file"
    echo "Removed: $file"
done

# Also clean up any remaining hidden .txt files
find "$OUTPUT_DIR" -name ".*.txt" | while read file; do
    echo "Cleaning up hidden file: $file"
    dir=$(dirname "$file")
    base_name=$(basename "$file" | sed 's/^\.//')
    visible_file="$dir/$base_name"
    
    # Copy to visible file if it doesn't exist
    if [ ! -f "$visible_file" ]; then
        cp "$file" "$visible_file"
        echo "Created visible file: $visible_file"
    fi
    
    # Remove the hidden file
    rm "$file"
    echo "Removed hidden file: $file"
done

echo "File finalization complete!"
echo "Visible text files created in: $OUTPUT_DIR" 