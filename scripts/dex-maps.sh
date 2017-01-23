#!/bin/bash
DX='/opt/android-sdk/build-tools/23.0.2/dx'
INDIR='android/assets/Maps/'
OUTDIR='android/build/outputs/maps/'

# ${DX} --dex --output=android/assets/Maps/FlatteringShapeDex-0.3-ca7e3e2.jar android/assets/Maps/FlatteringShape-0.3-0161cc2.jar

mkdir -p ${OUTDIR}
./gradlew maps:bundle

find android/assets/Maps/ -type f -printf "--output=${OUTDIR}/%f %p\n" | xargs -n2 ${DX} --dex
