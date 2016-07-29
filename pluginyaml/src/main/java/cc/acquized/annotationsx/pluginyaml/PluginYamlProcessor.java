/* Copyright (C) 2016 Acquized
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package cc.acquized.annotationsx.pluginyaml;

import cc.acquized.annotationsx.pluginyaml.annotations.Plugin;
import cc.acquized.annotationsx.pluginyaml.annotations.dependencies.Dependencies;
import cc.acquized.annotationsx.pluginyaml.annotations.dependencies.LoadBeforePlugins;
import cc.acquized.annotationsx.pluginyaml.annotations.dependencies.SoftDependencies;
import cc.acquized.annotationsx.pluginyaml.extension.PluginYamlService;
import org.bukkit.plugin.PluginLoadOrder;
import org.bukkit.plugin.java.JavaPlugin;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Automatic Processor which automaticly generates plugin.yml on Build
 * Look at the Wiki for Instructions on how to initalize this in your IDE or Build Tool
 * @author Acquized / Akkarin
 */
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes({
        "cc.acquized.annotationsx.pluginyaml.annotations.Plugin",
        "cc.acquized.annotationsx.pluginyaml.annotations.dependencies.*",
})
public class PluginYamlProcessor extends AbstractProcessor {

    private boolean mainClassLocated = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Plugin.class);
        if(annotatedElements.size() > 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Found more than one plugin class.");
            return false;
        }
        if(annotatedElements.size() <= 0) {
            return false;
        }
        if(mainClassLocated) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "The Plugin class has already been located.");
            return false;
        }

        Element mainElement = annotatedElements.iterator().next();
        mainClassLocated = true;

        if(!(mainElement instanceof TypeElement)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Element annotated with @Plugin is not a type!");
            return false;
        }

        TypeElement element = (TypeElement) mainElement;

        if(!(element.getEnclosedElements() instanceof PackageElement) && (!element.getModifiers().contains(Modifier.STATIC))) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Plugin class needs to be a top level class or a static inner class.");
            return false;
        }

        if(!processingEnv.getTypeUtils().isSubtype(element.asType(), processingEnv.getElementUtils().getTypeElement(JavaPlugin.class.getName()).asType())) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Plugin class does not extend org.bukkit.plugin.java.JavaPlugin.");
            return false;
        }

        if(!processingEnv.getTypeUtils().isSubtype(element.asType(), processingEnv.getElementUtils().getTypeElement(PluginYamlService.class.getName()).asType())) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Plugin class does not extend cc.acquized.annotationsx.pluginyaml.extension.PluginYamlService.");
        }

        Map<String, Object> plugin = new HashMap<>();

        Plugin annotation = element.getAnnotation(Plugin.class);
        plugin.put("main", element.getQualifiedName().toString());
        plugin.put("name", annotation.name());
        plugin.put("version", annotation.version());
        if(!annotation.description().isEmpty()) {
            plugin.put("description", annotation.description());
        }
        if(annotation.load() != PluginLoadOrder.POSTWORLD) {
            plugin.put("load", "startup");
        }
        if(annotation.authors().length > 0) {
            plugin.put(annotation.authors().length == 1 ? "author" : "authors", annotation.authors().length == 1 ? annotation.authors()[0] : annotation.authors());
        }
        if(!annotation.website().isEmpty()) {
            plugin.put("website", annotation.website());
        }
        if(!annotation.database()) {
            plugin.put("database", true);
        }
        if(!annotation.prefix().isEmpty()) {
            plugin.put("prefix", annotation.prefix());
        }

        Dependencies dependencies = element.getAnnotation(Dependencies.class);
        if(dependencies != null) {
            plugin.put("dependencies", dependencies.value());
        }

        LoadBeforePlugins loadBefore = element.getAnnotation(LoadBeforePlugins.class);
        if(loadBefore != null) {
            plugin.put("loadbefore", loadBefore.value());
        }

        SoftDependencies softDepend = element.getAnnotation(SoftDependencies.class);
        if(softDepend != null) {
            plugin.put("softdepend", softDepend.value());
        }

        Yaml yaml = new Yaml();
        try {
            FileObject obj = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT, "", "plugin.yml");
            try(Writer writer = obj.openWriter()) {
                writer.append("Generated by Annotations on ").append(new SimpleDateFormat("MM/DD/yyyy HH:mm:ss").format(new Date())).append("\n");
                yaml.dump(plugin, writer);
            }
            return true;
        } catch (IOException ex) {
            throw new RuntimeException("Cannot serialize Plugin.yml: " + ex.getMessage(), ex);
        }
    }

}
