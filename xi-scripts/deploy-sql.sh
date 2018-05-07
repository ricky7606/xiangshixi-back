#!/bin/bash

if [[ -d xi-scripts ]]; then
    cd xi-scripts
fi

# SERVER=yegames.cn
SERVER=www.xiangshixi.com

scp xintern.sql root@${SERVER}:/root/dist/xi-scripts/
scp initdata.sql root@${SERVER}:/root/dist/xi-scripts/

ssh root@${SERVER}
# ssh root@${SERVER} "cd /root/dist/xi-scripts; ./drop_database.sh; ./init_database.sh"