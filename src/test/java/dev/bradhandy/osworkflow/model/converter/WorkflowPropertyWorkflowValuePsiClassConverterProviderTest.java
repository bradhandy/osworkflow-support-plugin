package dev.bradhandy.osworkflow.model.converter;

import com.google.common.collect.Iterables;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.xml.GenericDomValue;
import dev.bradhandy.osworkflow.JavaProjectTest;
import dev.bradhandy.osworkflow.model.Register;
import dev.bradhandy.osworkflow.model.WorkflowValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static dev.bradhandy.osworkflow.model.DomElementTestUtil.findArgumentsForType;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowProperty;
import static org.assertj.core.api.Assertions.assertThat;

@JavaProjectTest
class WorkflowPropertyWorkflowValuePsiClassConverterProviderTest {

  private WorkflowPropertyWorkflowValuePsiClassConverterProvider converterProvider;

  @BeforeEach
  void setUpWorkflowValuePsiClassConverterProvider() {
    converterProvider = new WorkflowPropertyWorkflowValuePsiClassConverterProvider();
  }

  @Test
  void givenWorkflowValueElement_whenWrappingMetaTagPassedToCondition_thenConditionSucceeds(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    WorkflowValue<?> metaTagWorkflowValue =
        readWorkflowProperty(
            workflowPsiFile, codeInsightTestFixture, WorkflowValue.withName("someMetaProperty"));

    assertThat(converterProvider.getCondition()).isNotNull();

    Condition<Pair<PsiType, GenericDomValue>> providerCondition = converterProvider.getCondition();
    Pair<PsiType, GenericDomValue> metaTagWorkflowValuePair = Pair.pair(null, metaTagWorkflowValue);
    assertThat(providerCondition.value(metaTagWorkflowValuePair)).isTrue();
  }

  @Test
  void givenWorkflowValueElement_whenConvertingUnsupportedElement_thenConditionFails(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    List<WorkflowValue<?>> registerClassArguments =
        findArgumentsForType(
            workflowPsiFile,
            codeInsightTestFixture,
            WorkflowValue.withName("class.name"),
            Register.class,
            Register.withId("my-register-id"));
    WorkflowValue<?> registerArgument = Iterables.getOnlyElement(registerClassArguments);

    assertThat(converterProvider.getCondition()).isNotNull();

    Condition<Pair<PsiType, GenericDomValue>> providerCondition = converterProvider.getCondition();
    Pair<PsiType, GenericDomValue> registerArgumentPair = Pair.pair(null, registerArgument);
    assertThat(providerCondition.value(registerArgumentPair)).isFalse();
  }

  @Test
  void givenWorkflowValuePsiConverterProvider_whenRetrievingConverter_thenShouldBeStaticInstance() {
    assertThat(converterProvider.getConverter())
        .isInstanceOf(WorkflowValuePsiClassConverter.class)
        .isSameAs(WorkflowValuePsiClassConverter.INSTANCE);
  }
}
