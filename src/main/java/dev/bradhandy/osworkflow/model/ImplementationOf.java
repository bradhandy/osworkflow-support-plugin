package dev.bradhandy.osworkflow.model;

/**
 * Defines the base type for an OS Workflow construct (e.g. Register, FunctionProvider, etc). This
 * configures the code completion searching to provide implementations of the correct base type.
 *
 * @author bhandy
 */
public @interface ImplementationOf {

  String value();
}
