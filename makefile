all: compile

compile:
	javac -d out/production/vscode/ src/*.java src/stind/*.java

run-server:
	java -cp out/production/vscode Main --server

run-client:
	java -cp out/production/vscode Main --client
