package org.example;

import org.graalvm.polyglot.*;

public class PythonInterop {
    public static void main(String[] args) {
        try (Context context = Context.newBuilder().allowAllAccess(true).build()) {
            // Python code: define a function
            context.eval("python",
                """
            def add(a, b):
                return a + b
            """);

            // Get the function
            Value pyFunction = context.getBindings("python").getMember("add");

            // Call it with arguments
            Value result = pyFunction.execute(3, 5);
            System.out.println("Result from Python: " + result.asInt()); // prints 8
        }
    }
}
