package com.example;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@AnalyzeClasses(packages = "com.example")
public class LayeredArchTest {
    @ArchTest
    static final ArchRule layer_dependencies_are_respected = layeredArchitecture()
            .layer("Presentation").definedBy("..rest..")
            .layer("Application").definedBy(
                    "com.example.admin.service..",
                    "com.example.admin.common..",
                    "com.example.business.service..",
                    "com.example.business.common..",
                    "com.example.question.service..",
                    "com.example.question.common..",
                    "..usecase..")
            .layer("Domain").definedBy("..domain..")
            .layer("Infrastructure").definedBy("..infrastructure..")

            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Presentation", "Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Domain", "Application", "Presentation", "Infrastructure");

}
