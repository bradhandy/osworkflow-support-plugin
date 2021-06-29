package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;
import com.intellij.util.xml.SubTagList;

import java.util.Collection;

public interface Workflow extends DomElement {

  @SubTagList("meta")
  Collection<WorkflowValue<?>> getProperties();

  @SubTag("registers")
  RegisterList getRegisterList();

  @SubTag("trigger-functions")
  TriggerFunctionList getTriggerFunctionList();

  @SubTag("global-conditions")
  GlobalConditions getGlobalConditions();
}
