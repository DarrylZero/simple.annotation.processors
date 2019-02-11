package com.staemmachine.annotationprocessors.spicollector.processor;

import com.staemmachine.annotationprocessors.spicollector.annotionons.Collected;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileManager.Location;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

/**
 * {@link CollectedInfoProcessor}
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


        List<String> classNames = annotations.stream()
                .flatMap(typeElement -> env.getElementsAnnotatedWith(typeElement).stream())
                .filter(element -> element.getAnnotation(Collected.class).enabled())
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .map(TypeElement::getQualifiedName)
                .map(CharSequence::toString)
                .collect(Collectors.toList());
        classNames.forEach(this::debug);



//        filer.getResource(StandardLocation.SOURCE_PATH, )
//        resources/META-INF/services/
//        javax.annotation.processing.Processor

        return true;
    }

    private void debug(String message) {
        if (debug) {
            messager.printMessage(Kind.NOTE, message);
        }
    }


}
