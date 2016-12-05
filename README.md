# C--

Project for Compiler Construction (CS534)

Implements the following [grammar](http://cs434.cs.ua.edu/spring2010/projects.htm). The C-- compiler is implemented using a hand-written recursive descent parser.

## Setup

Run the following commands to build the compiler
```
   $ > sudo apt-get install ant
   $ > code/
   $ > ant
   $ > cd ../dist/
   $ > g++ SM.cpp
```

## How To Run
Note: Input files are included in the input directory.
```
   $ > cd dist/
   $ > java -jar CMinusMinusCompiler.jar <input file here>
   $ > ./a.out output.sm
```
