package be.npl.shelfwise.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaCodeUnit;
import com.tngtech.archunit.core.domain.JavaMethodCall;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.GeneralCodingRules;
import com.tngtech.archunit.library.freeze.FreezingArchRule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

/**
 * Enforces the mechanical rules of {@code shelfwise-code/backend/CLAUDE.md} on the production code.
 * Rules that the existing code already breaks are frozen: known violations are recorded in
 * {@code src/test/resources/archunit-store} and only new ones fail.
 */
class ArchitectureTest {

    private static final String ROOT = "be.npl.shelfwise";
    private static final Pattern FEATURE_REPOSITORY = Pattern.compile("be\\.npl\\.shelfwise\\.([a-z]+)\\.repository");

    private static final JavaClasses PRODUCTION = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(ROOT);

    @Test
    @DisplayName("Constructor injection only, never @Autowired on a field")
    void noFieldInjection() {
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION.check(PRODUCTION);
    }

    @Test
    @DisplayName("java.time only, never java.util.Date or Calendar")
    void noLegacyDateTypes() {
        noClasses().should().dependOnClassesThat().areAssignableTo(Date.class)
                .orShould().dependOnClassesThat().areAssignableTo(Calendar.class)
                .check(PRODUCTION);
    }

    @Test
    @DisplayName("Never catch Exception or Throwable")
    void noCatchingBroadExceptions() {
        classes().should(notCatchAnyOf(Exception.class, Throwable.class)).check(PRODUCTION);
    }

    @Test
    @DisplayName("The business date comes from BusinessDateService, never LocalDate.now()")
    void businessDateOnlyFromBusinessDateService() {
        noClasses().that().doNotHaveSimpleName("BusinessDateService")
                .should().callMethodWhere(isNowOn(LocalDate.class).or(isNowOn(LocalDateTime.class)))
                .check(PRODUCTION);
    }

    @Test
    @DisplayName("Helpers are final, with a private constructor and only static methods")
    void helpersArePure() {
        classes().that().resideInAPackage("..helper..").and().areTopLevelClasses()
                .should().haveModifier(JavaModifier.FINAL)
                .andShould().haveOnlyPrivateConstructors()
                .check(PRODUCTION);
        methods().that().areDeclaredInClassesThat().resideInAPackage("..helper..")
                .and().areDeclaredInClassesThat().areTopLevelClasses()
                .should().beStatic()
                .check(PRODUCTION);
    }

    @Test
    @DisplayName("Helpers are no Spring beans and use no database")
    void helpersHaveNoSpringOrDatabase() {
        noClasses().that().resideInAPackage("..helper..")
                .should().beAnnotatedWith(Component.class)
                .orShould().beAnnotatedWith(Service.class)
                .orShould().beAnnotatedWith(Repository.class)
                .orShould().dependOnClassesThat().resideInAnyPackage("..repository..", "org.springframework..",
                        "jakarta.persistence..")
                .check(PRODUCTION);
    }

    @Test
    @DisplayName("Another feature's entities are read through its service, never through its repository")
    void noRepositoryOfAnotherFeature() {
        FreezingArchRule.freeze(
                classes().that().resideOutsideOfPackage(ROOT + ".seed..")
                        .should(notUseRepositoryOfAnotherFeature())
                        .as("no class should use the repository of another feature (seed loaders excepted)"))
                .check(PRODUCTION);
    }

    @Test
    @DisplayName("Controllers are thin: they call a service, never a repository")
    void controllersDoNotUseRepositories() {
        FreezingArchRule.freeze(
                noClasses().that().areAnnotatedWith(RestController.class)
                        .should().dependOnClassesThat().areAssignableTo(
                                org.springframework.data.repository.Repository.class))
                .check(PRODUCTION);
    }

    private static DescribedPredicate<JavaMethodCall> isNowOn(Class<?> owner) {
        return DescribedPredicate.describe(owner.getSimpleName() + ".now()",
                call -> call.getTargetOwner().isEquivalentTo(owner) && call.getName().equals("now"));
    }

    private static ArchCondition<JavaClass> notCatchAnyOf(Class<?>... types) {
        return new ArchCondition<>("not catch Exception or Throwable") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                for (JavaCodeUnit codeUnit : javaClass.getCodeUnits()) {
                    codeUnit.getTryCatchBlocks().forEach(block -> block.getCaughtThrowables().stream()
                            .filter(caught -> Arrays.stream(types).anyMatch(caught::isEquivalentTo))
                            .forEach(caught -> events.add(SimpleConditionEvent.violated(block,
                                    codeUnit.getFullName() + " catches " + caught.getSimpleName() + " in "
                                            + block.getSourceCodeLocation()))));
                }
            }
        };
    }

    private static ArchCondition<JavaClass> notUseRepositoryOfAnotherFeature() {
        return new ArchCondition<>("not use the repository of another feature") {
            @Override
            public void check(JavaClass javaClass, ConditionEvents events) {
                Optional<String> ownFeature = featureOf(javaClass.getPackageName());
                javaClass.getDirectDependenciesFromSelf().forEach(dependency -> {
                    Matcher target = FEATURE_REPOSITORY.matcher(dependency.getTargetClass().getPackageName());
                    if (target.matches() && !ownFeature.equals(Optional.of(target.group(1)))) {
                        events.add(SimpleConditionEvent.violated(dependency, dependency.getDescription()));
                    }
                });
            }
        };
    }

    private static Optional<String> featureOf(String packageName) {
        String rest = packageName.startsWith(ROOT + ".") ? packageName.substring(ROOT.length() + 1) : "";
        return rest.isEmpty() ? Optional.empty() : Optional.of(rest.split("\\.")[0]);
    }
}
