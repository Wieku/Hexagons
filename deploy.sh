#!/bin/bash

./gradlew clean desktop:build desktop:copyDependencies

mkdir -p build

UPDATE_INFO=build/updateinfo-${VI_BRANCH}-${VI_VERSION}.json

echo "Generate updateinfo"

cat > ${UPDATE_INFO} <<EOF
{
    "classpath" :[
EOF

echo "Generate classpath hashes for updateinfo"
find desktop/build/dependencies -type f -exec ipfs add -nq {} \; | xargs printf "\"%s\"," | sed "s/.$//g" >> ${UPDATE_INFO}

echo "Generate launcher hash for updateinfo"
cat >> ${UPDATE_INFO} <<EOF
    ], "launcher": "$(ipfs add -nq desktop/build/libs/desktop*)"
EOF

cat >> ${UPDATE_INFO} <<EOF
}
EOF

echo "updateinfo done as ${UPDATE_INFO}"
VI_UPDATE_ID=$(ipfs add -nq ${UPDATE_INFO})

cat > build/verinfo-${VI_BRANCH}.json <<EOF
{
    "version": "${VI_VERSION}",
    "updateBaseUrl": "${VI_UPDATE_URL}",
    "updateId": "${VI_UPDATE_ID}"
}
EOF

echo "Deploy classpath dependencies to IPFS"
ipfs add -r desktop/build/dependencies

echo "Deploy Launcher IPFS"
ipfs add desktop/build/libs/desktop*

echo "Deploy updateinfo to IPFS"
ipfs add ${UPDATE_INFO}
