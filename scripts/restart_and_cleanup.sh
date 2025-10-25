#!/bin/bash

# Restart Docker services
echo "Restarting Docker services..."
sudo systemctl restart docker.socket docker.service
echo "Docker services restarted."

# Define ports to check
ports=(9500 9092 9093 9094 8088 8080 5432 9300 9200 7300)

# Array to store unique PIDs
declare -a pids

echo ""
echo "Checking for processes on specified ports..."

# Loop through each port and collect PIDs
for port in "${ports[@]}"; do
    echo "Checking port $port..."

    # Get PIDs for this port (exclude the header and grep process itself)
    port_pids=$(sudo lsof -ti :$port 2>/dev/null)

    if [ -n "$port_pids" ]; then
        # Add PIDs to array
        for pid in $port_pids; do
            # Check if PID is not already in array
            if [[ ! " ${pids[@]} " =~ " ${pid} " ]]; then
                pids+=($pid)
                echo "  Found process $pid on port $port"
            fi
        done
    else
        echo "  No processes found on port $port"
    fi
done

# Kill all unique PIDs
if [ ${#pids[@]} -gt 0 ]; then
    echo ""
    echo "Killing ${#pids[@]} unique process(es): ${pids[@]}"
    sudo kill -9 ${pids[@]}
    echo "Processes killed."
else
    echo ""
    echo "No processes found on any of the specified ports."
fi

echo ""
echo "Done!"