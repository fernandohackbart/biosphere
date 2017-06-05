#!/bin/bash

mkdir -p /tmp/Biosphere
rm -f /tmp/Biosphere/cel-${1}.out

cd /opt/projects/workspace/Biosphere
nohup ~/.gradle/wrapper/dists/gradle-3.4-bin/aeufj4znodijbvwfbsq3044r0/gradle-3.4/bin/gradle run >/tmp/Biosphere/cell-${1}.out 2>&1 &
