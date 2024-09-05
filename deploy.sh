#!/bin/bash

# Translated from deploy.ps1

# Pack target/*.jar and Dockerfile into tar.gz
echo "Packing target/*.jar and Dockerfile into tar.gz"
randomString=$(tr -dc A-Za-z </dev/urandom | head -c 10)
archiveName="$randomString.tar.gz"
tar -czf "$archiveName" target/*.jar Dockerfile

# Read first and second line from .env file and raise error if not found
if [ ! -f .env ]; then
    echo "Error: .env file not found"
    exit 1
fi

hostConnection=$(sed -n '1p' .env)
deployPath=$(sed -n '2p' .env)

if [ -z "$hostConnection" ] || [ -z "$deployPath" ]; then
    echo "Error: .env file is missing hostConnection or deployPath"
    exit 1
fi

# Copy archive to remote host
echo "Copying archive to remote host"
scp -P 22 "$archiveName" "$hostConnection":/tmp/"$archiveName"

# Execute remote commands:
echo "Executing deploy commands"
ssh -p 22 "$hostConnection" << EOF
    tar -xzf /tmp/$archiveName -C $deployPath --overwrite
    rm /tmp/$archiveName
    cd $deployPath
    ./deploy.sh
EOF

# Delete archive
echo "Finishing up"
rm "$archiveName"
