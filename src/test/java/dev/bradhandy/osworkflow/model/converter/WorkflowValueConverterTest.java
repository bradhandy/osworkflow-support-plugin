package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.ExtensionTestUtil;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import dev.bradhandy.osworkflow.JavaProjectTest;
import dev.bradhandy.osworkflow.model.DomElementTestUtil;
import dev.bradhandy.osworkflow.model.WorkflowValue;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static dev.bradhandy.testing.PluginUtil.runReadAction;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@JavaProjectTest
class WorkflowValueConverterTest {

  private WorkflowValueConverter workflowValueConverter;
  private Disposable testClassDisposable;

  @BeforeEach
  void setUpWorkflowValueConverter() {
    workflowValueConverter = new WorkflowValueConverter();
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
  void givenCustomConverter_whenLookingUpConverter_thenConverterFound(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    WorkflowPropertyWorkflowValuePsiClassConverterProvider workflowValueConverterProvider =
        new WorkflowPropertyWorkflowValuePsiClassConverterProvider();

    ExtensionTestUtil.addExtensions(
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

          assertThat(workflowValueConverter.getConverter(metaWithClassNameValue))
              .isSameAs(workflowValueConverterProvider.getConverter());
        });
  }
}
