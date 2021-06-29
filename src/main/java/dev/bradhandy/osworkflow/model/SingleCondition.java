package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.ExtendClass;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

import java.util.function.Predicate;

@ExtendClass(
    value = "com.opensymphony.workflow.Condition",
    allowAbstract = false,
    allowInterface = false,
    allowEnum = false,
    allowEmpty = true)
public interface SingleCondition extends DomElement, Condition, ArgumentContainer {

  static Predicate<SingleCondition> withId(String id) {
    return new DomElementNamePredicate<>(id);
  }

  @Attribute("id")
  @NameValue
  GenericAttributeValue<String> getId();

  @Attribute("name")
  GenericAttributeValue<String> getName();

  @Attribute("negate")
  GenericAttributeValue<Boolean> getNegate();
}
