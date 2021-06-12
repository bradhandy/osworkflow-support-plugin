package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.xml.impl.ConvertContextFactory;
import dev.bradhandy.osworkflow.JavaProjectTest;
import dev.bradhandy.osworkflow.OsWorkflowJavaModuleRequired;
import dev.bradhandy.osworkflow.model.WorkflowValue;
import dev.bradhandy.testing.JavaCodeInsightTestFixtureProvider;
import dev.bradhandy.testing.ModuleJdk;
import dev.bradhandy.testing.PluginTestDataPath;
import dev.bradhandy.testing.ProjectModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
              PsiClass convertedClass =
                  workflowValuePsiClassConverter.fromString(
                      metaPropertyWithClassName.getStringValue(),
                      ConvertContextFactory.createConvertContext(metaPropertyWithClassName));
              assertThat(convertedClass).isNotNull();
              assertThat(convertedClass.getQualifiedName()).isEqualTo("java.lang.Object");
            });
  }
}
