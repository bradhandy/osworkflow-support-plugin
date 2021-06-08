package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

import java.util.function.Predicate;

@ImplementationOf("com.opensymphony.workflow.Register")
public interface Register extends DomElement, ArgumentContainer {

  static Predicate<Register> withType(String type) {
    return new DomElementNamePredicate<>(type);
  }

  @Attribute("type")
  @NameValue
  GenericAttributeValue<String> getType();

  @Attribute("id")
  GenericAttributeValue<String> getId();

  @Attribute("variable-name")
  GenericAttributeValue<String> getVariable();
}
