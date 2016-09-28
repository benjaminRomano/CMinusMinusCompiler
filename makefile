default:
	javac -d . -cp . @sources
	mkdir org/bromano/benscript/libraries
	cp code/src/org/bromano/benscript/libraries/dictionary.lib.bs org/bromano/benscript/libraries
	jar -cvfm runner.jar code/src/runner/META-INF/MANIFEST.MF org
	jar -cvfm interpreter.jar code/src/interpreter/META-INF/MANIFEST.MF org

cat-error1:
	cat code/src/org/bromano/benscript/tests/programs/missingSemicolon.bs

run-error1:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/missingSemicolon.bs

cat-error2:
	cat code/src/org/bromano/benscript/tests/programs/invalidFunctionParameter.bs

run-error2:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/invalidFunctionParameter.bs

cat-error3:
	cat code/src/org/bromano/benscript/tests/programs/ifWithoutStatement.bs

run-error3:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/ifWithoutStatement.bs

cat-arrays:
	cat code/src/org/bromano/benscript/tests/programs/arrays.bs

run-arrays:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/arrays.bs

cat-conditionals:
	cat code/src/org/bromano/benscript/tests/programs/conditionals.bs

run-conditionals:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/conditionals.bs

cat-recursion:
	cat code/src/org/bromano/benscript/tests/programs/recursion.bs

run-recursion:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/recursion.bs

cat-iteration:
	cat code/src/org/bromano/benscript/tests/programs/iteration.bs

run-iteration:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/iteration.bs

cat-functions:
	cat code/src/org/bromano/benscript/tests/programs/functions.bs

run-functions:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/functions.bs

cat-dictionary:
	cat code/src/org/bromano/benscript/tests/programs/dictionary.bs

run-dictionary:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/dictionary.bs --libs

cat-adder:
	cat code/src/org/bromano/benscript/tests/programs/adder.bs

run-adder:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/adder.bs

cat-grad:
	cat code/src/org/bromano/benscript/tests/programs/lazy.bs

run-grad:
	java -jar runner.jar code/src/org/bromano/benscript/tests/programs/lazy.bs

run-interpreter:
	java -jar interpreter.jar

clean:
	rm -rf org
	rm -rf runner.jar
	rm -rf interpreter.jar
