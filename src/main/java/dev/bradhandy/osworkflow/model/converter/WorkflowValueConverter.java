package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.WrappingConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorkflowValueConverter extends WrappingConverter {

  @Override
  public @Nullable Converter<?> getConverter(@NotNull GenericDomValue domElement) {
    WorkflowValueConvertersRegistry workflowValueConvertersRegistry =
        ApplicationManager.getApplication().getService(WorkflowValueConvertersRegistry.class);
    return workflowValueConvertersRegistry.getConverter(domElement, null);
  }
}
