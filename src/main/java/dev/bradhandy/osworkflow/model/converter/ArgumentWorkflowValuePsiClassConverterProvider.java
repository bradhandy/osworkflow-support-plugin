package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiType;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericDomValue;
import dev.bradhandy.osworkflow.model.ArgumentContainer;

public class ArgumentWorkflowValuePsiClassConverterProvider
    implements WorkflowValueConverterProvider {

  @Override
  public Converter<?> getConverter() {
    return WorkflowValuePsiClassConverter.INSTANCE;
  }

  @Override
  public Condition<Pair<PsiType, GenericDomValue>> getCondition() {
    return ArgumentContainerCondition.INSTANCE;
  }

  private static class ArgumentContainerCondition
      implements Condition<Pair<PsiType, GenericDomValue>> {

    private static final ArgumentContainerCondition INSTANCE = new ArgumentContainerCondition();

    @Override
    public boolean value(Pair<PsiType, GenericDomValue> workflowValueTypePair) {
      ArgumentContainer argumentContainer =
          DomUtil.getParentOfType(workflowValueTypePair.getSecond(), ArgumentContainer.class, true);

      return argumentContainer != null;
    }
  }
}
