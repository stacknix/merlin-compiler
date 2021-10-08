package com.stacknix.merlin.compiler;

public class Template {

    public static final String MODELS_CLASS_TEMPLATED = "package {{package}};\n" +
            "\n" +
            "\n" +
            "import android.content.Context;\n" +
            "\n" +
            "import java.util.Arrays;\n" +
            "import java.util.List;\n" +
            "\n" +
            "import io.stacknix.merlin.db.Merlin;\n" +
            "import io.stacknix.merlin.db.MerlinObject;\n" +
            "\n" +
            "{{imports}}\n" +
            "\n" +
            "public class MerlinDatabase {\n" +
            "\n" +
            "    public final List<Class<? extends MerlinObject>> DATA = Arrays.asList({{models}});\n" +
            "\n" +
            "    public List<Class<? extends MerlinObject>> getModels(){\n" +
            "        return DATA;\n" +
            "    }\n" +
            "\n" +
            "    private void connect(Context context){\n" +
            "        Merlin.connect(context, getModels());\n" +
            "    }\n" +
            "\n" +
            "    public static void init(Context context){\n" +
            "        new MerlinDatabase().connect(context);\n" +
            "    }\n" +
            "\n" +
            "}\n";
}
