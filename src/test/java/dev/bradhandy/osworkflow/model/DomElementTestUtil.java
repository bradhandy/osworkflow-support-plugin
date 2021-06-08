package dev.bradhandy.osworkflow.model;

import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;

import java.util.function.Predicate;

import static dev.bradhandy.testing.PluginUtil.runReadAction;
import static org.assertj.core.api.Assertions.assertThat;

public final class DomElementTestUtil {

  private DomElementTestUtil() {
    // DO NOT instantiate.
  }

  public static DomFileElement<Workflow> readWorkflowFileElement(
      PsiFile workflowPsiFile, CodeInsightTestFixture codeInsightTestFixture) {
    return runReadAction(
        (Computable<? extends DomFileElement<Workflow>>)
            () -> {
              DomFileElement<Workflow> workflowDomFileElement =
                  DomManager.getDomManager(codeInsightTestFixture.getProject())
                      .getFileElement((XmlFile) workflowPsiFile, Workflow.class);
              assertThat(workflowDomFileElement).isNotNull();

              return workflowDomFileElement;
            });
  }

  public static Workflow readWorkflowElement(
      PsiFile workflowPsiFile, CodeInsightTestFixture codeInsightTestFixture) {
    return runReadAction(
        (Computable<? extends Workflow>)
            () -> {
              DomFileElement<Workflow> workflowDomFileElement =
                  readWorkflowFileElement(workflowPsiFile, codeInsightTestFixture);

              Workflow workflow = workflowDomFileElement.getRootElement();
              assertThat(workflow).isNotNull();

              return workflow;
            });
  }

  public static WorkflowValue<?> readWorkflowProperty(
      PsiFile workflowPsiFile,
      CodeInsightTestFixture codeInsightTestFixture,
      Predicate<WorkflowValue<?>> workflowValuePredicate) {
    return runReadAction(
        (Computable<? extends WorkflowValue<?>>)
            () -> {
              Workflow workflow = readWorkflowElement(workflowPsiFile, codeInsightTestFixture);
              return workflow.getProperties().stream()
                  .filter(workflowValuePredicate)
                  .findFirst()
                  .orElse(null);
            });
  }

  public static RegisterList readRegisterList(
      PsiFile workflowPsiFile, CodeInsightTestFixture codeInsightTestFixture) {
    return runReadAction(
        (Computable<? extends RegisterList>)
            () -> {
              Workflow workflow = readWorkflowElement(workflowPsiFile, codeInsightTestFixture);
              return workflow.getRegisterList();
            });
  }
}
