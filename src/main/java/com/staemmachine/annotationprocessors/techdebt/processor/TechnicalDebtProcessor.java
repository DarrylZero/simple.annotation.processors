package com.staemmachine.annotationprocessors.techdebt.processor;

import com.staemmachine.annotationprocessors.techdebt.annotations.TechnicalDebt;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

/**
 * {@link com.staemmachine.annotationprocessors.techdebt.processor.TechnicalDebtProcessor}
 */
@SupportedAnnotationTypes({"com.staemmachine.annotationprocessors.techdebt.annotations.TechnicalDebt"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class TechnicalDebtProcessor extends AbstractProcessor {

    private static final boolean debug = false;

    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        for (TypeElement ann : annotations) {
            debug(" ==> TypeElement ann = " + ann);
            List<? extends Element> es = ann.getEnclosedElements();
            debug(" ====> ann.getEnclosedElements() count = " + es.size());
            for (Element e : es) {
                debug(" ========> EnclosedElement: " + e);
            }

            ElementKind kind = ann.getKind();
            debug(" ====> ann.getKind() = " + kind);
            Set<? extends Element> e2s = env.getElementsAnnotatedWith(ann);

            debug(" ====> env.getElementsAnnotatedWith(ann) count = " + e2s.size());
            for (Element e2 : e2s) {
                debug(" ========> ElementsAnnotatedWith: " + e2);
                debug("           - Kind : " + e2.getKind());
                TechnicalDebt technicalDebt = e2.getAnnotation(TechnicalDebt.class);
                debug("--->>> value found " + technicalDebt.value());
                Optional<Instant> date = getDate(technicalDebt.value());
                Instant now = Instant.now();
                debug("date = " + date);
                debug("now = " + now);

                if (!date.isPresent()) {
                    messager.printMessage(Kind.ERROR,
                            "wrong value format " + technicalDebt.value());
                } else if (now.compareTo(date.get()) > 0) {
                    messager.printMessage(Kind.ERROR,
                            "deadline has passed " + technicalDebt.value());
                }

            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    private Optional<Instant> getDate(String value) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
        Date parsed;
        try {
            parsed = format.parse(value);
        } catch (ParseException e) {
            return Optional.empty();
        }
        try {
            return Optional.of(Instant.ofEpochMilli(parsed.getTime()));
        } catch (DateTimeException e) {
            return Optional.empty();
        }
    }

    private void debug(String message) {
        if (debug) {
            messager.printMessage(Kind.NOTE, message);
        }
    }


}
