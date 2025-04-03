package org.example;

import org.graalvm.polyglot.*;

public class Main {
    public static void main(String[] args) {
        try (Context ctx = Context.newBuilder("python")
                .option("python.PythonPath", "packages/Lib/site-packages") // <--- Ustaw właściwą ścieżkę!
                .allowAllAccess(true)
                .build()) {

//            ctx.eval("python", "import os; os.system('graalpy -m pip install requests')");
            ctx.eval("python", "import requests; print('requests OK')");
        }
    }
}
