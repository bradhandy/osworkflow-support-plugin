package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ExtendClass;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

import java.util.function.Predicate;

@ExtendClass(
    value = "com.opensymphony.workflow.Register",
    allowAbstract = false,
    allowInterface = false,
    allowEnum = false,
    allowEmpty = true)
public interface Register extends DomElement, ArgumentContainer {

  static Predicate<Register> withId(String id) {
    return new DomElementNamePredicate<>(id);
  }

  @Attribute("id")
  @NameValue
  GenericAttributeValue<String> getId();

  @Attribute("type")
  GenericAttributeValue<String> getType();

  @Attribute("variable-name")
  GenericAttributeValue<String> getVariable();
}
