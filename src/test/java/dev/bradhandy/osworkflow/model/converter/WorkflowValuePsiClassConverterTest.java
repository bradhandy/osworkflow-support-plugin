package dev.bradhandy.osworkflow.model.converter;

import com.google.common.collect.Iterables;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.xml.impl.ConvertContextFactory;
import dev.bradhandy.osworkflow.JavaProjectTest;
import dev.bradhandy.osworkflow.OsWorkflowJavaModuleRequired;
import dev.bradhandy.osworkflow.model.Register;
import dev.bradhandy.osworkflow.model.TypedValue;
import dev.bradhandy.osworkflow.model.WorkflowValue;
import dev.bradhandy.testing.ModuleJdk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.bradhandy.osworkflow.model.DomElementTestUtil.findArgumentsForType;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowProperty;
import static org.assertj.core.api.Assertions.assertThat;

@JavaProjectTest
@OsWorkflowJavaModuleRequired
@ModuleJdk(languageLevel = LanguageLevel.JDK_14)
class WorkflowValuePsiClassConverterTest {

  private WorkflowValuePsiClassConverter workflowValuePsiClassConverter;

  @BeforeEach
  void setUpWorkflowValuePsiClassConverter() {
    workflowValuePsiClassConverter = new WorkflowValuePsiClassConverter();
  }

  @Test
  void givenOsWorkflowFile_whenMetaTagHasFullyQualifiedClassNameValue_thenConvertToPsiClass(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    WorkflowValue<?> metaPropertyWithClassName =
        readWorkflowProperty(
            workflowPsiFile,
            codeInsightTestFixture,
            WorkflowValue.withName("someClassNameMetaProperty"));

    ApplicationManager.getApplication()
        .runReadAction(
            () -> {
              TypedValue<PsiClass> typedWorkflowValue =
                  workflowValuePsiClassConverter.fromString(
                      metaPropertyWithClassName.getStringValue(),
                      ConvertContextFactory.createConvertContext(metaPropertyWithClassName));
              assertThat(typedWorkflowValue).isNotNull();
              assertThat(typedWorkflowValue.getConvertedValue()).isNotNull();
              assertThat(typedWorkflowValue.getConvertedValue().getQualifiedName())
                  .isEqualTo("java.lang.Object");
            });
  }

  @Test
  void givenOsWorkflowFile_whenRegisterClassNameIsValidRegisterType_thenConvertToPsiClass(
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
    WorkflowValue<?> registerArgument = Iterables.getOnlyElement(registerArguments);

    ApplicationManager.getApplication()
        .runReadAction(
            () -> {
              TypedValue<PsiClass> typedWorkflowValue =
                  workflowValuePsiClassConverter.fromString(
                      registerArgument.getStringValue(),
                      ConvertContextFactory.createConvertContext(registerArgument));
              assertThat(typedWorkflowValue).isNotNull();
              assertThat(typedWorkflowValue.getConvertedValue()).isNotNull();
              assertThat(typedWorkflowValue.getConvertedValue().getQualifiedName())
                  .isEqualTo("dev.bradhandy.NoopRegister");
            });
  }
}
