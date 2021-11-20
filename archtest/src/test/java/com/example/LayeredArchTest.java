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
                    "..usecase..")
            .layer("Domain").definedBy("..domain..")

            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Application", "Presentation")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Domain", "Application", "Presentation");

}