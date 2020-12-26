all: protoc-all compile-all
protoc-all: server-proto
compile-all: compile-server
server-proto: generate-proto-files compile-grpc-with-maven

generate-proto-files:
	protoc -I=./proto --java_out=./server/src/main/java ./proto/Octal.proto
compile-grpc-with-maven:
	cd ./server && mvn generate-resources && cd ..

compile-server:
	cd ./server && mvn compile && cd ..