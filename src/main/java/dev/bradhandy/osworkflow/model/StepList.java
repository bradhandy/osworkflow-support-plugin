package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.List;

public interface StepList extends DomElement {

  @SubTagList("step")
  List<Step> getSteps();
}
