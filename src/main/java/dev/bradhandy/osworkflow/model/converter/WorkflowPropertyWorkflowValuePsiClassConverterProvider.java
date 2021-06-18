package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.GenericDomValue;
import dev.bradhandy.osworkflow.model.WorkflowValue;

public class WorkflowPropertyWorkflowValuePsiClassConverterProvider
    implements WorkflowValueConverterProvider {

  @Override
  public Converter<?> getConverter() {
    return WorkflowValuePsiClassConverter.INSTANCE;
  }

  @Override
  public Condition<Pair<PsiType, GenericDomValue>> getCondition() {
    return MetaTagWorkflowValueCondition.INSTANCE;
  }

  private static class MetaTagWorkflowValueCondition
      implements Condition<Pair<PsiType, GenericDomValue>> {

    private static final MetaTagWorkflowValueCondition INSTANCE =
        new MetaTagWorkflowValueCondition();

    @Override
    public boolean value(Pair<PsiType, GenericDomValue> workflowValueTypePair) {
      GenericDomValue<?> metaTagCandidate = workflowValueTypePair.getSecond();
      if (metaTagCandidate instanceof WorkflowValue) {
        XmlTag xmlTag = metaTagCandidate.getXmlTag();
        return xmlTag != null && "meta".equals(xmlTag.getName());
      }

      return false;
    }
  }
}
