package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTag;

public interface GlobalConditions extends DomElement {

  @SubTag("conditions")
  ConditionContainer getConditionContainer();
}
