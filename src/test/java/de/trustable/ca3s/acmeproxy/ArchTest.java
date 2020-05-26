package de.trustable.ca3s.acmeproxy;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("de.trustable.ca3s.acmeproxy");

        noClasses()
            .that()
                .resideInAnyPackage("de.trustable.ca3s.acmeproxy.service..")
            .or()
                .resideInAnyPackage("de.trustable.ca3s.acmeproxy.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..de.trustable.ca3s.acmeproxy.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
