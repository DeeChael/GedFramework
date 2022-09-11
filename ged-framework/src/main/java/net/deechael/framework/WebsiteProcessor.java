package net.deechael.framework;

import com.google.auto.service.AutoService;
import net.deechael.dcg.*;
import net.deechael.dcg.items.Var;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/*
 *  TODO
 *  idk how to code this!!!
 */
@AutoService(Processor.class)
public class WebsiteProcessor extends AbstractProcessor {

    private Filer filer;
    private Types types;
    private Elements elements;
    private Messager messager;

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        this.filer = environment.getFiler();
        this.types = environment.getTypeUtils();
        this.elements = environment.getElementUtils();
        this.messager = environment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> elements, RoundEnvironment environment) {
        for (TypeElement element : elements) {
            if (element.getAnnotation(Website.class) == null)
                continue;
            String mainFileName = element.getQualifiedName().toString() + "_GedFrameworkMain";
            JClass mainClass = new JClass(Level.PUBLIC, "org.gedstudio.framework.launch", mainFileName);
            mainClass.importClass(element.getQualifiedName().toString());
            mainClass.importClass(GedWebsite.class);
            JMethod mainMethod = mainClass.addMethod(Level.PUBLIC, "main", true, false, false);
            JType gedWebsiteType = JType.classType(GedWebsite.class);
            Var websiteVar = mainMethod.createVar(gedWebsiteType,
                    "website",
                    Var.constructor(gedWebsiteType,
                            Var.custom(element.getQualifiedName().toString() + ".class")
                    )
            );
            mainMethod.invokeMethod(websiteVar, "start");
            try (Writer writer = filer.createSourceFile(mainFileName).openWriter()) {
                writer.write(mainClass.getString());
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
            //filer.createResource(StandardLocation.locationFor("test.txt"),)
            break;
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new HashSet<>();
        annotations.add(Website.class.getName());
        return annotations;
    }

}
