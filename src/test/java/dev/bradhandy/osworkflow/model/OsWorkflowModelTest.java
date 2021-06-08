package dev.bradhandy.osworkflow.model;

import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.xml.DomFileElement;
import dev.bradhandy.testing.JavaCodeInsightTestFixtureProvider;
import dev.bradhandy.testing.PluginTestDataPath;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readRegisterList;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowFileElement;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowProperty;
import static dev.bradhandy.testing.PluginUtil.runReadAction;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JavaCodeInsightTestFixtureProvider.class)
@PluginTestDataPath("build/resources/test")
public class OsWorkflowModelTest {

  @Test
  void givenOsWorkflowFile_whenOpened_thenWorkflowElementParsed(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    DomFileElement<Workflow> workflowFileElement =
        readWorkflowFileElement(workflowPsiFile, codeInsightTestFixture);
    Assertions.assertThat(workflowFileElement.getRootElement()).isNotNull();
  }

  @Test
  void givenOsWorkflowFile_whenOpened_thenWorkflowElementParsedHasMetaContent(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    WorkflowValue<?> someMetaProperty =
        readWorkflowProperty(
            workflowPsiFile,
            codeInsightTestFixture,
            WorkflowValue.withNameAndValue("someMetaProperty", "someMetaValue"));
    assertThat(someMetaProperty).isNotNull();

    WorkflowValue<?> someOtherMetaProperety =
        readWorkflowProperty(
            workflowPsiFile,
            codeInsightTestFixture,
            WorkflowValue.withNameAndValue("someOtherMetaProperty", "someOtherMetaValue"));
    assertThat(someOtherMetaProperety).isNotNull();
  }

  @Test
  void givenOsWorkflowFile_whenOpened_thenRegistersAreParsedWithArguments(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    RegisterList registerList = readRegisterList(workflowPsiFile, codeInsightTestFixture);
    assertThat(registerList).isNotNull();

    runReadAction(
        () -> {
          assertThat(registerList.getRegisters()).hasSize(3);

          Register register =
              registerList.getRegisters().stream()
                  .filter(Register.withType("some-type"))
                  .findFirst()
                  .orElse(null);
          assertThat(register).isNotNull();
          assertThat(register.getVariable().getStringValue()).isEqualTo("someVariableName");
          assertThat(register.getId().getStringValue()).isEqualTo("my-id");

          assertThat(register.getArguments())
              .hasSize(1)
              .have(
                  new Condition<>(
                      WorkflowValue.withNameAndValue("class.name", "dev.bradhandy.NoopRegister"),
                      "'class.name' argument"));
        });
  }
}
