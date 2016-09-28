# BenScript

BenScript is an interpreted programming language built using Java.

There are two executables: runner.jar and interpreter.jar to build use 

``` $ make ```





##### Variable Names

```
// Variables can start with [_a-zA-Z] and can be followed by [_0-9a-zA-Z].
// Variable names cannot match a keyword.
Valid:
_var
var1_2_3

Invalid:
1_2_3var
var
for
```

##### Primitives
Primitves work the same as in JavaScript.
``` js
// Numbers:
var a = 5;
var a = 5.5;
var a = -5.5;

// Booleans:
var a = true;
var b = false;

// String:
var a = "hello world";
var a = "\"hello world\"";
var a = "works\n";
strings can be accessed by index
var a = "hello world";
a[0] //h

// Objects:
var a = {
    a: 5,
    b: "hello world"
}

// Objects can be accesed using dot or accessors
a[b] //"hello world"
a.a //5
a.b //"hello world"

// Null
var a = null;

// Arrays
var a = [5, "hello world", null];
a[0] //5;
a[1] //"hello world"
a[2] //null

//Arrays can grow dynamically
a + 5; // [5, "hello world", null, 5];
```
##### Operators
``` js
==, !=, <, <=, >, >=, ? (null coalescing operator)
&&, ||
+, -, *, /, %, ^
+=, -=, *=, /=, %=, ^=, 
```

##### Statements
``` js
//if statement

if (a == 5) {
    console.print(5); //5
} else if (a == 6) {
    console.print(6) //6
} else {
    console.print("not 5 or 6");
}

// while loop

while (a?) {
    a = null; 
}

while (a?) {
    continue; //Obviously this is an infinite loop
}

while (a?) {
    break; //Leave loop early
}

//for loop
//Break, continue work the same as in while loops
for (var i = 0; i < arr.length; i++) {
    console.print(i, ": ", arr[i]);
}

//Functions
func f(f, g) {
    console.print(f, g);
}

// Functions with delayed evaluation of args
lfunc (f, g) {
    console.print(f,g);
}

//Anonymous functions

var a = (b) => console.log(b);

var c = (d,e) => {
    console.log(d);
    return e;
}
```

##### Comments
```js
/* 
Block comment
*/

// Single line comment
```

##### Globals
``` js

// Console object
console.println("", 2, 3); // Print functions are variadic 
console.print("", 2, 3);

a = [1,2,3]
length(a); // 3
```


##### Libraries
``` js

// Import libraries by adding the --libs arg to the runner.jar

// Dictionary
var dic = Dictionary.create();
dic.add("a", "b");
dic.put("5", "a");
dic.get("6"); //Error: Invalid Key not found
dic.getIfExists("5"); //null
```

##### Available Make file available commands:

```
cat-error1
run-error1
cat-error2
run-error2
cat-error3
run-error3
cat-arrays
run-arrays
cat-conditionals
run-conditionals
cat-recursion
run-recursion
cat-iteration
run-iteration
cat-functions
run-functions
cat-dictionary
run-dictionary
cat-adder
run-adder
cat-grad
run-grad

run-interpreter
```

##### Regenerate sources file
```
find code/src/ -path "*tests*" -prune -o -name "*.java" -print > sources
```
