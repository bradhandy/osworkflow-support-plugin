package dev.bradhandy.osworkflow.model;

import com.google.common.collect.Iterables;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.ProcessingContext;
import com.intellij.util.xml.impl.GenericValueReferenceProvider;
import dev.bradhandy.osworkflow.JavaProjectTest;
import dev.bradhandy.osworkflow.OsWorkflowJavaModuleRequired;
import dev.bradhandy.testing.ModuleJdk;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static dev.bradhandy.osworkflow.model.DomElementTestUtil.findArgumentsForType;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowProperty;
import static org.assertj.core.api.Assertions.assertThat;

@JavaProjectTest
@OsWorkflowJavaModuleRequired
@ModuleJdk(languageLevel = LanguageLevel.JDK_14)
class OsWorkflowModelReferencesTest {

  @Test
  void givenOsWorkflowFile_whenOpened_thenMetaTagWithClassNameHasReferences(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    Predicate<WorkflowValue<?>> targetWorkflowProperty =
        WorkflowValue.withName("someClassNameMetaProperty");
    WorkflowValue<?> classNameProperty =
        readWorkflowProperty(workflowPsiFile, codeInsightTestFixture, targetWorkflowProperty);
    assertThat(classNameProperty).isNotNull();

    ApplicationManager.getApplication()
        .runReadAction(
            () -> {
              assertThat(classNameProperty.getXmlElement()).isNotNull();

              GenericValueReferenceProvider referenceProvider = new GenericValueReferenceProvider();
              PsiReference[] references =
                  referenceProvider.getReferencesByElement(
                      classNameProperty.getXmlElement(), new ProcessingContext());

              assertThat(references).isNotEmpty();
            });
  }

  @Test
  void givenOsWorkflowFile_whenRegisterClassNameIsValidRegisterType_thenReferencesAvailable(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    codeInsightTestFixture.copyDirectoryToProject("parsing/before", "");

    PsiFile workflowPsiFile = codeInsightTestFixture.configureFromTempProjectFile("workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    List<WorkflowValue<?>> registerArguments =
        findArgumentsForType(
            workflowPsiFile,
            codeInsightTestFixture,
            WorkflowValue.withName("class.name"),
            Register.class,
            Register.withType("some-valid-type"));
    WorkflowValue<PsiClass> registerArgument =
        (WorkflowValue<PsiClass>) Iterables.getOnlyElement(registerArguments);

    ApplicationManager.getApplication()
        .runReadAction(
            () -> {
              GenericValueReferenceProvider referenceProvider = new GenericValueReferenceProvider();
              PsiReference[] references =
                  referenceProvider.getReferencesByElement(
                      registerArgument.getXmlElement(), new ProcessingContext());
              assertThat(references).isNotNull().isNotEmpty();
            });
  }
}
