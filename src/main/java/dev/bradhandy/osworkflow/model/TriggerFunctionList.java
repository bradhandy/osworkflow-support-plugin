package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface TriggerFunctionList extends DomElement {

  @SubTagList("trigger-function")
  List<TriggerFunction> getTriggerFunctions();
}
