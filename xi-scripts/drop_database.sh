#!/bin/bash

if [[ -d xi-scripts ]]; then
    cd xi-scripts
fi

host="localhost"
if [[ "$1" != "" ]]; then
    host=$1
fi
echo "Host: $host"
echo "DROP USER 'xintern'@'localhost'; DROP DATABASE xintern;" | mysql -uroot -h $host -p
