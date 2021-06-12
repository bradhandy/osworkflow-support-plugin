package dev.bradhandy.osworkflow.model.converter;

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
class PsiClassWorkflowValueConverterProviderTest {

  private PsiClassWorkflowValueConverterProvider psiClassWorkflowValueConverterProvider;

  @BeforeEach
  void setUpWorkflowValuePsiClassConverterProvider() {
    psiClassWorkflowValueConverterProvider = new PsiClassWorkflowValueConverterProvider();
  }

  @Test
  void givenWorkflowValueElement_whenWrappingMetaTagPassedToCondition_thenConditionSucceeds(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    WorkflowValue<?> metaTagWorkflowValue =
        readWorkflowProperty(
            workflowPsiFile, codeInsightTestFixture, WorkflowValue.withName("someMetaProperty"));

    assertThat(psiClassWorkflowValueConverterProvider.getCondition()).isNotNull();

    Condition<Pair<PsiType, GenericDomValue>> providerCondition =
        psiClassWorkflowValueConverterProvider.getCondition();
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
            Register.class);

    assertThat(registerClassArguments).hasSize(1);

    Condition<Pair<PsiType, GenericDomValue>> providerCondition =
        psiClassWorkflowValueConverterProvider.getCondition();
    Pair<PsiType, GenericDomValue> metaTagWorkflowValuePair =
        Pair.pair(null, registerClassArguments.get(0));
    assertThat(providerCondition.value(metaTagWorkflowValuePair)).isFalse();
  }

  @Test
  void givenWorkflowValuePsiConverterProvider_whenRetrievingConverter_thenShouldBeStaticInstance() {
    assertThat(psiClassWorkflowValueConverterProvider.getConverter())
        .isSameAs(WorkflowValuePsiClassConverter.INSTANCE)
        .isInstanceOf(WorkflowValuePsiClassConverter.class);
  }
}
