package com.staemmachine.annotationprocessors.spicollector.processor;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import com.staemmachine.annotationprocessors.spicollector.InterfaceToSupport;
import com.staemmachine.annotationprocessors.spicollector.annotionons.Collected;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic.Kind;

/**
 * {@link CollectedInfoProcessor} https://github.com/square/javapoet
 */
@SupportedAnnotationTypes({
        "com.staemmachine.annotationprocessors.spicollector.annotionons.Collected"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class CollectedInfoProcessor extends AbstractProcessor {

    private static final boolean debug = true;

    private Messager messager;
    private Filer filer;
    private Types typeUtils;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);

        //processingEnv.getElementUtils().getDocComment()
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        typeUtils = processingEnv.getTypeUtils();
        Elements elementUtils = processingEnv.getElementUtils();

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

        class MethodInfo {

            private final String methodName;
            private final String className;

            MethodInfo(String methodName, String className) {
                this.methodName = methodName;
                this.className = className;
            }

            public String getMethodName() {
                return methodName;
            }

            public String getClassName() {
                return className;
            }
        }
        annotatedClasses.forEach(this::debug);

        annotations.stream()
                .flatMap(typeElement -> env.getElementsAnnotatedWith(typeElement).stream())
                .filter(element -> element.getAnnotation(Collected.class).enabled())
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(i -> i.getParameters().isEmpty())
                .filter(i -> i.getReturnType().getKind() == TypeKind.DECLARED)
                .filter(i -> DeclaredType.class.isInstance(i.getReturnType()))
                .map(i -> DeclaredType.class.cast(i.getReturnType()))
                .filter(ex -> isType(ex.asElement().asType(), InterfaceToSupport.class))
                .peek(ex -> debug(ex.asElement().getSimpleName().toString() + " is instance of " +
                        InterfaceToSupport.class.getName()))
//                .map(new Function<ExecutableElement, MethodInfo>() {
//                            @Override
//                            public MethodInfo apply(ExecutableElement executableElement) {
//                                executableElement.getEnclosingElement().asType()
//                                executableElement.getSimpleName();
//
//                                return new executableElement;
//                            }
//                        })
                .collect(Collectors.toList());


        /*try {

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
*/
//
//        resources/META-INF/services/
//        javax.annotation.processing.Processor

        return true;
    }

    private boolean isType(TypeMirror type, Class clazz) {
        TypeMirror serializable = elementUtils.getTypeElement(clazz.getName()).asType();
        return typeUtils.isAssignable(type, serializable);
    }

    private static TypeSpec createClass(
            String className,
            String methodName) {
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();

//        MethodSpec factoryMethod = MethodSpec.methodBuilder("factoryMethod")
//                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
//                .returns(void.class)
//                .addParameter(String[].class, "args")
//                .addStatement("$T.out.println($S)", System.class, "Hello, JavaPoet!")
//                .build();

        TypeSpec classDefinition = TypeSpec.classBuilder("HelloWorld")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addMethod(constructor)
//                .addMethod(factoryMethod)
                .build();

        return classDefinition;
    }

    private void debug(String message) {
        if (debug) {
            messager.printMessage(Kind.NOTE, message);
        }
    }


}
