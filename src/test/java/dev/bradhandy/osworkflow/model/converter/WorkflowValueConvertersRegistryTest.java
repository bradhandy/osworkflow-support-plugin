package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.intellij.testFramework.ExtensionTestUtil;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.xml.GenericDomValue;
import dev.bradhandy.osworkflow.JavaProjectTest;
import dev.bradhandy.osworkflow.model.DomElementTestUtil;
import dev.bradhandy.osworkflow.model.WorkflowValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dev.bradhandy.testing.PluginUtil.runReadAction;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@JavaProjectTest
class WorkflowValueConvertersRegistryTest {

  private WorkflowValueConvertersRegistry workflowValueConvertersRegistry;
  private Disposable testClassDisposable;

  @BeforeEach
  void setUpWorkflowValueConverterRegistry() {
    workflowValueConvertersRegistry = new WorkflowValueConvertersRegistry();
  }

  @BeforeEach
  void setUpDisposable() {
    testClassDisposable = Disposer.newDisposable("workflowValueConverterDisposable");
  }

  @AfterEach
  void resetExtensionPoints() {
    Disposer.dispose(testClassDisposable);
  }

  @Test
  void givenOneValueConverterProvider_whenConverting_thenExtensionIsFound(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiClassWorkflowValueConverterProvider workflowValueConverterProvider =
        new PsiClassWorkflowValueConverterProvider();

    ExtensionTestUtil.maskExtensions(
        WorkflowValueConverterProvider.EP,
        List.of(workflowValueConverterProvider),
        testClassDisposable);

    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    runReadAction(
        () -> {
          WorkflowValue<?> metaWithClassNameValue =
              DomElementTestUtil.readWorkflowProperty(
                  workflowPsiFile,
                  codeInsightTestFixture,
                  WorkflowValue.withName("someClassNameMetaProperty"));

          assertThat(workflowValueConvertersRegistry.getConverter(metaWithClassNameValue, null))
              .isSameAs(workflowValueConverterProvider.getConverter());
        });
  }

  @Test
  void givenMultipleValueConverterProviders_whenConverting_thenExtensionIsFound(
      JavaCodeInsightTestFixture codeInsightTestFixture,
      @Mock WorkflowValueConverterProvider mockWorkflowValueConverterProvider) {
    PsiClassWorkflowValueConverterProvider workflowValueConverterProvider =
        new PsiClassWorkflowValueConverterProvider();

    ExtensionTestUtil.maskExtensions(
        WorkflowValueConverterProvider.EP,
        List.of(mockWorkflowValueConverterProvider, workflowValueConverterProvider),
        testClassDisposable);

    doReturn((Condition<Pair<PsiType, GenericDomValue>>) (pair) -> false)
        .when(mockWorkflowValueConverterProvider)
        .getCondition();

    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    runReadAction(
        () -> {
          WorkflowValue<?> metaWithClassNameValue =
              DomElementTestUtil.readWorkflowProperty(
                  workflowPsiFile,
                  codeInsightTestFixture,
                  WorkflowValue.withName("someClassNameMetaProperty"));

          assertThat(workflowValueConvertersRegistry.getConverter(metaWithClassNameValue, null))
              .isSameAs(workflowValueConverterProvider.getConverter());
        });

    verify(mockWorkflowValueConverterProvider).getCondition();
  }

  @Test
  void givenZeroValueConverterProviders_whenConverting_thenNullIsReturned(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    ExtensionTestUtil.maskExtensions(
        WorkflowValueConverterProvider.EP, List.of(), testClassDisposable);

    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    runReadAction(
        () -> {
          WorkflowValue<?> metaWithClassNameValue =
              DomElementTestUtil.readWorkflowProperty(
                  workflowPsiFile,
                  codeInsightTestFixture,
                  WorkflowValue.withName("someClassNameMetaProperty"));

          assertThat(workflowValueConvertersRegistry.getConverter(metaWithClassNameValue, null))
              .isNull();
        });
  }
}
