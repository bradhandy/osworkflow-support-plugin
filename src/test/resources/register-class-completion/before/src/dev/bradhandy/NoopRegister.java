package dev.bradhandy;

import com.opensymphony.workflow.Register;
import com.opensymphony.workflow.WorkflowContext;
import com.opensymphony.workflow.WorkflowException;
import com.opensymphony.workflow.spi.WorkflowEntry;

import java.util.Map;

public class NoopRegister implements Register {

  public Object registerVariable(WorkflowContext var1, WorkflowEntry var2, Map var3)
      throws WorkflowException {
    return null;
  }
}
