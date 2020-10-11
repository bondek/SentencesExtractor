# Sentence Extractor

This is a java program which is dividing inputed text into sentences and words providing output in CSV or XML formats.

## Getting Started

To execute extractor it's required to:
* download extractor code from GitHub
* build it with Maven
* execute in command line

### Prerequisites

* JDK 8 or higher
* Maven 3.0 or higher
* Git client

### Downloading

```
git clone https://github.com/bondek/SentencesExtractor.git
```

### Building

Assuming Maven is installed and available on PATH this one can be used for building:

```
mvn clean package
```
This will execute all tests as well.

### Running

The program is written to accept data from stdin and write into stdout.

The simplest way to run is present below (we are using input from test file src/test/resources/reference-input.txt and sending output to reference-output.txt in current directory which should be project root folder)

* for XML output
```
java -jar target/SentencesExtractor-all.jar -o XML < src/test/resources/reference-input.txt > reference-output.txt
```

* for CSV output
```
java -jar target/SentencesExtractor-all.jar -o CSV < src/test/resources/reference-input.txt > reference-output.txt
```

The program has an option to provide file with special words containing end of sentence character [.?!] which shouldn't end the sentence (e.g. 'Mr.').
This can be done with -s option as follows

```
java -jar target/SentencesExtractor-all.jar -o XML -s src/main/resources/special-words.txt < src/test/resources/reference-input.txt > reference-output.txt
```

### Logging

Since program is writing its output to stdin logs should come to different place and in our case it's configured to send logs to extractor.log file by default. 

### Tests

JUnit tests are executed during build. For this purpose tests are verifying if output files have expected content.

Example test files are present in src/test/resources. Adding additional test files is the matter of modifying test parameters in test class:

```
    @Parameterized.Parameters
    public static Collection<Object[]> sourceFiles() {
        return Arrays.asList(new Object[][] {
            { "reference-input.txt", "reference-expected.csv", "reference-expected.csv"},
            { "empty-input.txt", "empty-output.csv", "empty-output.csv" }
        });
    }
```

To test if program doesn't load whole file to memory we need big input file (size > 36 MB) called large.in which is placed in project root folder.

```
java -Xmx32m -jar target/SentencesExtractor-all.jar -o XML < large.in > reference-output.txt
```

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
