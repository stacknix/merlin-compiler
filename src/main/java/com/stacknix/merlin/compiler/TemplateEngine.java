package com.stacknix.merlin.compiler;

import com.hubspot.jinjava.Jinjava;

import java.util.Map;

public final class TemplateEngine {

    private final Jinjava jinjava;

    public TemplateEngine(){
        ClassLoader curClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
        this.jinjava = new Jinjava();
        Thread.currentThread().setContextClassLoader(curClassLoader);
    }

    public String render(String template, Map<String, Object> context){
        return  jinjava.render(template, context);
    }
}
