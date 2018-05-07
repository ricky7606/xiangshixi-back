#!/bin/bash

if [[ -d xi-scripts ]]; then
    cd xi-scripts
fi

# SERVER=yegames.cn
SERVER=www.xiangshixi.com
if [[ $1 != '' ]]; then
    SERVER=$1
fi

echo "Server: $SERVER"


./package-war.sh $@

scp ../xi-server/target/xi-server-0.1.0.war root@${SERVER}:/opt/web/mybase/webapps/backend.war

ssh root@${SERVER} "chown jetty /opt/web/mybase/webapps/backend.war; service jetty restart;"