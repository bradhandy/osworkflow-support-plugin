package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;
import com.intellij.util.xml.SubTagsList;

import java.util.List;

public interface ConditionContainer extends DomElement, Condition {

  @SubTagsList({"conditions", "condition"})
  List<Condition> getConditions();

  @SubTagList("conditions")
  List<ConditionContainer> getConditionHolders();

  @SubTagList("condition")
  List<SingleCondition> getSingleConditions();
}
