package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.xml.GenericDomValue;
import dev.bradhandy.osworkflow.model.WorkflowValue;
import dev.bradhandy.testing.JavaCodeInsightTestFixtureProvider;
import dev.bradhandy.testing.PluginTestDataPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowProperty;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(JavaCodeInsightTestFixtureProvider.class)
@PluginTestDataPath("build/resources/test")
// @todo Write negative tests when other WorkflowValue elements are added.
class WorkflowValuePsiClassConverterProviderTest {

  private WorkflowValuePsiClassConverterProvider workflowValuePsiClassConverterProvider;

  @BeforeEach
  void setUpWorkflowValuePsiClassConverterProvider() {
    workflowValuePsiClassConverterProvider = new WorkflowValuePsiClassConverterProvider();
  }

  @Test
  void givenWorkflowValueElement_whenWrappingMetaTagPassedToCondition_thenConditionSucceeds(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    WorkflowValue metaTagWorkflowValue =
        readWorkflowProperty(
            workflowPsiFile, codeInsightTestFixture, WorkflowValue.withName("someMetaProperty"));

    assertThat(workflowValuePsiClassConverterProvider.getCondition()).isNotNull();

    Condition providerCondition = workflowValuePsiClassConverterProvider.getCondition();
    Pair<PsiType, GenericDomValue> metaTagWorkflowValuePair = Pair.pair(null, metaTagWorkflowValue);
    assertThat(providerCondition.value(metaTagWorkflowValuePair)).isTrue();
  }

  @Test
  void givenWorkflowValuePsiConverterProvider_whenRetrievingConverter_thenShouldBeStaticInstance(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    assertThat(workflowValuePsiClassConverterProvider.getConverter())
        .isSameAs(WorkflowValuePsiClassConverter.INSTANCE)
        .isInstanceOf(WorkflowValuePsiClassConverter.class);
  }
}
