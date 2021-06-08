package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomFileDescription;

public class WorkflowFileDescription extends DomFileDescription<Workflow> {

  public WorkflowFileDescription() {
    super(Workflow.class, "workflow", "http://www.opensymphony.com/osworkflow/workflow_2_8.dtd");
  }
}
