#!/bin/bash

# echo "[package web]"
# cd ../ew-web
# ./deploy.sh $@

# if [[ $? -ne 0 ]]; then
# 	exit $?
# fi

if [[ -d xi-scripts ]]; then
    cd xi-scripts
fi

# SERVER=yegames.cn:8080
SERVER=www.xiangshixi.com

echo "[package war]"
cd ../xi-server
sed -i '' "s/localhost:8080/${SERVER}/g" ./src/main/webapp/api-doc/xi-api.yaml
sed -i '' "s/basePath: \\/api/basePath: \\/backend\\/api/g" ./src/main/webapp/api-doc/xi-api.yaml
mvn clean package -Dmaven.test.skip=true
sed -i '' "s/${SERVER}/localhost:8080/g" ./src/main/webapp/api-doc/xi-api.yaml
sed -i '' "s/basePath: \\/backend\\/api/basePath: \\/api/g" ./src/main/webapp/api-doc/xi-api.yaml