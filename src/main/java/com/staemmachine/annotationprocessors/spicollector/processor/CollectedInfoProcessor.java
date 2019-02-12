package com.staemmachine.annotationprocessors.spicollector.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.staemmachine.annotationprocessors.spicollector.annotionons.Collected;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

/**
 * {@link CollectedInfoProcessor}
 * https://github.com/square/javapoet
 */
@SupportedAnnotationTypes({
        "com.staemmachine.annotationprocessors.spicollector.annotionons.Collected"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CollectedInfoProcessor extends AbstractProcessor {

    private static final boolean debug = true;

    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        //processingEnv.getElementUtils().getDocComment()
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
//        annotations.stream()
//                .flatMap(typeElement -> env.getElementsAnnotatedWith(typeElement).stream())
//                .filter(element -> element.getAnnotation(Collected.class).enabled())
//                .filter(TypeElement.class::isInstance)
//                .map(TypeElement.class::cast).map(new Function<TypeElement, Object>() {
//            @Override
//            public Object apply(TypeElement typeElement) {
//                return typeElement.;
//            }
//        })


        List<String> annotatedClasses = annotations.stream()
                .flatMap(typeElement -> env.getElementsAnnotatedWith(typeElement).stream())
                .filter(element -> element.getAnnotation(Collected.class).enabled())
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .map(TypeElement::getQualifiedName)
                .map(CharSequence::toString)
                .collect(Collectors.toList());
        annotatedClasses.forEach(this::debug);

        class R {


        }

        annotations.stream()
                .flatMap(typeElement -> env.getElementsAnnotatedWith(typeElement).stream())
                .filter(element -> element.getAnnotation(Collected.class).enabled())
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(i -> i.getParameters().isEmpty())
                .filter(i -> i.getReturnType().getKind() == TypeKind.DECLARED)
                .filter(i -> DeclaredType.class.isInstance(i.getReturnType()))
                .filter(i -> DeclaredType.class.cast(i.getReturnType()).)

                .map(
                        new Function<ExecutableElement, R>() {
                            @Override
                            public R apply(ExecutableElement executableElement) {
                                executableElement.

                                return new executableElement;
                            }
                        }).collect(Collectors.toList());
        annotatedClasses.forEach(this::debug);



        try {

            FileObject resource = filer.createResource(StandardLocation.CLASS_OUTPUT, "", "tmp.tmp");

            try(BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(resource.openOutputStream()))) {
                annotatedclasses.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        try {
                            writer.write(s);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

//
//        resources/META-INF/services/
//        javax.annotation.processing.Processor

        return true;
    }

    private static TypeSpec createClass() {
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();

        MethodSpec main = MethodSpec.methodBuilder("main")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(String[].class, "args")
                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
                .build();

        TypeSpec helloWorld = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(constructor)
                .addMethod(main)
                .build();

    }

    private void debug(String message) {
        if (debug) {
            messager.printMessage(Kind.NOTE, message);
        }
    }


}
