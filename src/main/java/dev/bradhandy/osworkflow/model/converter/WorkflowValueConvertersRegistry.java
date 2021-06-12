package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiType;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.converters.values.GenericDomValueConvertersRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class WorkflowValueConvertersRegistry extends GenericDomValueConvertersRegistry {

  public WorkflowValueConvertersRegistry() {
    registerDefaultConverters();
  }

  @Override
  protected @Nullable Converter<?> getCustomConverter(Pair<PsiType, GenericDomValue> pair) {
    List<WorkflowValueConverterProvider> workflowValueConverterProviders =
        WorkflowValueConverterProvider.EP.getExtensionList();
    return workflowValueConverterProviders.stream()
        .filter(converterProvider -> converterProvider.getCondition().value(pair))
        .findFirst()
        .map(WorkflowValueConverterProvider::getConverter)
        .orElse(null);
  }
}
