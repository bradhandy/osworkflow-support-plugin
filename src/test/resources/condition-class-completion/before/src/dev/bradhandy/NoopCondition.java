package dev.bradhandy;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.workflow.Condition;
import com.opensymphony.workflow.WorkflowException;

import java.util.Map;

public class NoopCondition implements Condition {

  @Override
  public boolean passesCondition(Map map, Map map1, PropertySet propertySet)
      throws WorkflowException {
    return false;
  }
}
