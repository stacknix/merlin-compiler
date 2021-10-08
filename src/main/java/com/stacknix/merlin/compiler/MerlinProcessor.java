package com.stacknix.merlin.compiler;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import io.stacknix.merlin.db.annotations.Model;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.*;


public final class MerlinProcessor extends AbstractProcessor {

    final String GENERATED_PKG = "com.stacknix.merlin.compiler.generated";
    final String LINE = "\n";

    private Messager messager;
    private TemplateEngine tg;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        tg = new TemplateEngine();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        List<String> modelImport = new ArrayList<>();
        List<String> modelClasses = new ArrayList<>();
        List<String> modelNames = new ArrayList<>();
        Set<? extends Element> elementsToBind = roundEnvironment.getElementsAnnotatedWith(Model.class);
        for (Element element : elementsToBind) {
            String simpleName = element.getSimpleName().toString();
            String className = processingEnv.getElementUtils().getBinaryName((TypeElement) element).toString();
            Model annotation = element.getAnnotation(Model.class);
            String importImport = String.format("import %s;", className);
            String classClass = String.format("%s.class", simpleName);

            if(modelNames.contains(annotation.value())){
                messager.printMessage(Diagnostic.Kind.ERROR, "Duplicate model name found.", element);
            }
            modelNames.add(annotation.value());
            modelImport.add(importImport);
            modelClasses.add(classClass);

            for(Element e : element.getEnclosedElements()){
               // fields
            }

        }

        Map<String, Object> context = Maps.newHashMap();
        context.put("package", GENERATED_PKG);
        context.put("imports", Joiner.on(LINE).join(modelImport));
        context.put("models", Joiner.on(", ").join(modelClasses));

        List<TypeElement> types = new ImmutableList.Builder<TypeElement>()
                .addAll(ElementFilter.typesIn(annotations))
                .build();

        for (Element type : types) {
            try {
                String file = String.format("%s.%s", GENERATED_PKG, "MerlinDatabase");
                JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(file);
            try (Writer writer = sourceFile.openWriter()) {
                writer.write(tg.render(Template.MODELS_CLASS_TEMPLATED, context));
            }
            } catch (IOException ea) {
                messager.printMessage(Diagnostic.Kind.ERROR, ea.toString());
            }
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(Collections.singletonList(
                Model.class.getCanonicalName()));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private PackageElement getPackage(Element element) {
        while (element.getKind() != ElementKind.PACKAGE) {
            element = element.getEnclosingElement();
        }
        return (PackageElement) element;
    }

}