#!/bin/bash

if [[ -d xi-scripts ]]; then
    cd xi-scripts
fi

host="localhost"
if [[ "$1" != "" ]]; then
    host=$1
fi
echo "Host: $host"
cat xintern.sql initdata.sql | mysql -uroot -h $host -p