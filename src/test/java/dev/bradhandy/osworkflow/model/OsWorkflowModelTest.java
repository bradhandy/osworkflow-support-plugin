package dev.bradhandy.osworkflow.model;

import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.xml.DomFileElement;
import dev.bradhandy.osworkflow.JavaProjectTest;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readRegisterList;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readTriggerFunctionList;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowFileElement;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowProperty;
import static org.assertj.core.api.Assertions.assertThat;

@JavaProjectTest
class OsWorkflowModelTest {

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

    assertThat(registerList.getRegisters()).hasSize(3);

    Register register =
        registerList.getRegisters().stream()
            .filter(Register.withType("some-valid-type"))
            .findFirst()
            .orElse(null);
    assertThat(register).isNotNull();
    assertThat(register.getVariable().getStringValue()).isEqualTo("someVariableName");
    assertThat(register.getId().getStringValue()).isEqualTo("my-id");

    assertThat(register.getArguments())
        .hasSize(2)
        .haveAtMost(
            1,
            new Condition<>(
                WorkflowValue.withNameAndValue("class.name", "dev.bradhandy.NoopRegister"),
                "'class.name' argument"));
  }

  @Test
  void givenOsWorkflowFile_whenOpened_thenTriggerFunctionsAreParsedWithArguments(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    TriggerFunctionList triggerFunctionList =
        readTriggerFunctionList(workflowPsiFile, codeInsightTestFixture);
    assertThat(triggerFunctionList).isNotNull();

    assertThat(triggerFunctionList.getTriggerFunctions()).hasSize(2);
    TriggerFunction triggerFunction =
        triggerFunctionList.getTriggerFunctions().stream()
            .filter(TriggerFunction.withId("sample-trigger-function"))
            .findFirst()
            .orElse(null);
    assertThat(triggerFunction).isNotNull();

    Function function = triggerFunction.getFunction();
    assertThat(function.getId().getStringValue()).isEqualTo("my-function-id");
    assertThat(function.getType().getStringValue()).isEqualTo("some-trigger-function-type");
    assertThat(function.getName().getStringValue()).isEqualTo("my-function-name");

    assertThat(function.getArguments())
        .hasSize(2)
        .haveAtMost(
            1,
            new Condition<>(
                WorkflowValue.withNameAndValue("class.name", "dev.bradhandy.NoopRegister"),
                "'class.name' argument"));
  }
}
