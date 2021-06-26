package dev.bradhandy;

import com.opensymphony.workflow.FunctionProvider;
import com.opensymphony.workflow.WorkflowException;

import java.util.Map;

public class NoopFunction implements FunctionProvider {

  @Override
  public void execute(
      Map map, Map map1, com.opensymphony.module.propertyset.PropertySet propertySet)
      throws WorkflowException {

  }
}