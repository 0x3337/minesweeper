# Minesweeper

## Build
Go to the project directory and build the project by running the command:
```
javac -sourcepath src -d out $(find . -name '*.java')
```

Then create `.jar` file by running the command:
```
jar cmvf META-INF/MANIFEST.MF Minesweeper.jar -C out . images
```

## Run
Use the following command to run the project: 
```
java -jar Minesweeper.jar
```
