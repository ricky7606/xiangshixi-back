#!/bin/bash

if [[ -d xi-scripts ]]; then
    cd xi-scripts
fi

./package-war.sh

if [[ $? -ne 0 ]]; then
	exit $?
fi

rm -rf ../dist

mkdir ../dist
mkdir ../dist/xi-scripts

cp ./* ../dist/xi-scripts/
cp ../xi-server/target/xi-server-0.1.0.war ../dist/