package com.staemmachine.annotationprocessors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.staemmachine.annotationprocessors.techdebt.annotations.TechnicalDebt;
import com.staemmachine.annotationprocessors.techdebt.processor.TechnicalDebtProcessor;
import org.junit.jupiter.api.Test;

class NamesTest {

    @Test
    void testNames() {
        assertClassName("com.staemmachine.annotationprocessors.techdebt.annotations.TechnicalDebt",
                TechnicalDebt.class);
        assertClassName(
                "com.staemmachine.annotationprocessors.techdebt.processor.TechnicalDebtProcessor",
                TechnicalDebtProcessor.class);
    }

    private static void assertClassName(String name, Class clazz) {
        assertEquals(name, clazz.getName());
    }

}