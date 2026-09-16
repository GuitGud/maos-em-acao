package br.org.maosemacao;
import com.tngtech.archunit.core.importer.*;
import org.junit.jupiter.api.Test;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
class ArchitectureTest {
    @Test void dependencyRule() {
        var classes=new ClassFileImporter().withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS).importPackages("br.org.maosemacao");
        noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat().resideInAnyPackage("org.springframework..","jakarta..","..application..","..infrastructure..","..web..").check(classes);
        noClasses().that().resideInAPackage("..application..").should().dependOnClassesThat().resideInAnyPackage("org.springframework..","jakarta..","..infrastructure..","..web..").check(classes);
        noClasses().that().resideInAPackage("..web..").should().dependOnClassesThat().resideInAPackage("..infrastructure.persistence..").check(classes);
    }
}
