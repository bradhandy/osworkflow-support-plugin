package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.Collection;
import java.util.List;

public interface ArgumentContainer extends DomElement {

  @SubTagList("arg")
  Collection<WorkflowValue<?>> getArguments();
}
