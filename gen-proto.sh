#!/bin/bash
SRC_DIR=/Users/pepa/develop-funkypanda/test/proto-messages
DST_DIR=/Users/pepa/develop-funkypanda/test/src/main/java

echo $SRC_DIR
echo $DST_DIR

protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/*.proto