package com.staemmachine.annotationprocessors.spicollector.processor;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.staemmachine.annotationprocessors.spicollector.InterfaceToSupport;
import com.staemmachine.annotationprocessors.spicollector.Registration;
import com.staemmachine.annotationprocessors.spicollector.annotionons.Collected;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
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

    public static class MethodInfo {

        private final String packageName;
        private final String className;
        private final String methodName;

        MethodInfo(String packageName,
                String className,
                String methodName) {
            this.methodName = methodName;
            this.packageName = packageName;
            this.className = className;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("MethodInfo{");
            sb.append("packageName='").append(packageName).append('\'');
            sb.append(", className='").append(className).append('\'');
            sb.append(", methodName='").append(methodName).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }


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
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
/*
        List<String> annotatedClasses = annotations.stream()
                .flatMap(typeElement -> env.getElementsAnnotatedWith(typeElement).stream())
                .filter(element -> element.getAnnotation(Collected.class).enabled())
                .filter(TypeElement.class::isInstance)
                .map(TypeElement.class::cast)
                .map(TypeElement::getQualifiedName)
                .map(CharSequence::toString)
                .collect(Collectors.toList());
        annotatedClasses.forEach(this::debug);
        annotatedClasses.forEach(this::debug);
*/

        List<String> generatedClassNames = annotations.stream()
                .flatMap(typeElement -> env.getElementsAnnotatedWith(typeElement).stream())
                .filter(element -> nonNull(element.getAnnotation(Collected.class)))
                .filter(element -> element.getAnnotation(Collected.class).enabled())
                .filter(ExecutableElement.class::isInstance)
                .map(ExecutableElement.class::cast)
                .filter(ex -> ex.getParameters().isEmpty())
                .filter(ex -> ex.getReturnType().getKind() == TypeKind.DECLARED)
                .filter(ex -> ex.getModifiers().contains(Modifier.STATIC))
                .filter(ex -> !ex.getModifiers().contains(Modifier.ABSTRACT))
                .filter(i -> DeclaredType.class.isInstance(i.getReturnType()))
                .filter(i -> isType(DeclaredType.class.cast(i.getReturnType()).asElement().asType(),
                        InterfaceToSupport.class))
                .map(CollectedInfoProcessor::methodInfo)
                .map(CollectedInfoProcessor::createClass)
                .map(new Function<JavaFile, String>() {
                    @Override
                    public String apply(JavaFile javaFile) {
                        try {
                            javaFile.writeTo(filer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return javaFile.toJavaFileObject().getName();
                    }
                }).collect(toList());

        generatedClassNames.forEach(System.out::println);

        return true;
    }

    private static MethodInfo methodInfo(ExecutableElement method) {
        String fullClassName = method.getEnclosingElement().asType().toString();
        int lastDotIndex = fullClassName.lastIndexOf('.');
        String packageName = lastDotIndex == -1 ? "" : fullClassName.substring(0, lastDotIndex);
        String className = lastDotIndex == -1 ? fullClassName
                : fullClassName.substring(lastDotIndex + 1, fullClassName.length());
        String methodName = method.getSimpleName().toString();
        return new MethodInfo(packageName, className, methodName);
    }

    private boolean isType(TypeMirror type, Class clazz) {
        TypeMirror serializable = elementUtils.getTypeElement(clazz.getName()).asType();
        return typeUtils.isAssignable(type, serializable);
    }

    private static JavaFile createClass(MethodInfo methodInfo) {
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .build();


        MethodSpec factoryMethod = MethodSpec.methodBuilder("getInterface")
                .addModifiers(Modifier.PUBLIC)
                .returns(InterfaceToSupport.class)
                .addAnnotation(Override.class)
                .addStatement("return $N.$N()", methodInfo.getClassName(), methodInfo.getMethodName())
                .build();

        String className =
                methodInfo.getClassName() + "_" + methodInfo.getMethodName() + "_Registration";
        TypeSpec classDefinition = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(Registration.class))
                .addMethod(constructor)
                .addMethod(factoryMethod)
                .build();

        return JavaFile.builder(methodInfo.getPackageName(), classDefinition).build();
    }

    private void debug(String message) {
        if (debug) {
            messager.printMessage(Kind.NOTE, message);
        }
    }


}
