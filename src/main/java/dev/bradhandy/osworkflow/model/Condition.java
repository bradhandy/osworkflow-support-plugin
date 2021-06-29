package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;

import java.util.function.Predicate;

public interface Condition extends DomElement {

  static Predicate<Condition> withId(String id) {
    return new DomElementNamePredicate<>(id);
  }

  @Attribute("type")
  GenericAttributeValue<String> getType();
}
