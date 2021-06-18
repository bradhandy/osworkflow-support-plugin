package dev.bradhandy.osworkflow.model;

import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiFile;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.CodeInsightTestFixture;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomElementVisitor;
import com.intellij.util.xml.DomFileElement;
import com.intellij.util.xml.DomManager;
import com.intellij.util.xml.DomUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

  public static <P extends ArgumentContainer> List<WorkflowValue<?>> findArgumentsForType(
      PsiFile workflowPsiFile,
      CodeInsightTestFixture codeInsightTestFixture,
      Predicate<WorkflowValue<?>> worklowPredicate,
      Class<P> parent) {
    return findArgumentsForType(
        workflowPsiFile, codeInsightTestFixture, worklowPredicate, parent, Objects::nonNull);
  }

  public static <P extends ArgumentContainer> List<WorkflowValue<?>> findArgumentsForType(
      PsiFile workflowPsiFile,
      CodeInsightTestFixture codeInsightTestFixture,
      Predicate<WorkflowValue<?>> worklowPredicate,
      Class<P> parent,
      Predicate<P> parentPredicate) {
    return runReadAction(
        (Computable<? extends List<WorkflowValue<?>>>)
            () -> {
              Workflow workflow = readWorkflowElement(workflowPsiFile, codeInsightTestFixture);
              List<WorkflowValue<?>> workflowValues = new ArrayList<>();
              workflow.acceptChildren(
                  new DomElementVisitor() {
                    @Override
                    public void visitDomElement(DomElement element) {
                      if (element instanceof WorkflowValue) {

                        // we want to skip the 'acceptChildren' call for this element since
                        // WorkflowValues do not have children. so we check these conditions with
                        // a nested "if" statement instead of rolling the condition up into the
                        // parent.
                        if (worklowPredicate.test((WorkflowValue<?>) element)
                            && parentPredicate.test(
                                DomUtil.getParentOfType(element, parent, true))) {
                          workflowValues.add((WorkflowValue<?>) element);
                        }
                      } else {
                        element.acceptChildren(this);
                      }
                    }
                  });

              return workflowValues;
            });
  }
}
