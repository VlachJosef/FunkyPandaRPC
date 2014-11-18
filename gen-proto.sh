#!/bin/bash

# Deprecated, generating classes from proto files is done by sbt-protobuf plugin
SRC_DIR=/Users/pepa/develop-funkypanda/funkypanda-rpc/src/main/protobuf
DST_DIR=/Users/pepa/develop-funkypanda/funkypanda-rpc/src/main/generated/java

echo $SRC_DIR
echo $DST_DIR

protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/*.proto