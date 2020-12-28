all: protoc-all compile-all assembly
protoc-all: server-proto
compile-all: compile-server
server-proto: generate-proto-files compile-grpc-with-maven
assembly:
	mvn package
generate-proto-files:
	protoc -I=${OCTAL_PROTO_PATH} --java_out=./src/main/java ${OCTAL_PROTO_PATH}/Vif.proto
compile-grpc-with-maven:
	mvn generate-resources

compile-server:
	mvn compile