package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.util.xml.converters.values.GenericDomValueConvertersRegistry;

public interface WorkflowValueConverterProvider extends GenericDomValueConvertersRegistry.Provider {

  ExtensionPointName<WorkflowValueConverterProvider> EP =
      ExtensionPointName.create("dev.bradhandy.workflow.workflowValueConverter");
}
