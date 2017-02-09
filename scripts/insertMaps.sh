#!/bin/bash
CTR=$1
MAPID=$2

for f in $(find maps -mindepth 1 -maxdepth 1 -type d -printf "%f\n" | sort); do
    if [ "$f" != "build" ]; then
        printf "(${MAPID}, '$(./scripts/hashMap.sh maps/${f}/src ${CTR})'), --${f}\n"
        ((CTR++))
        ((MAPID++))
    fi
done
