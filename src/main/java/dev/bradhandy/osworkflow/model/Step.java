package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;

import java.util.function.Predicate;

public interface Step extends DomElement {

  static Predicate<Step> withId(String id) {
    return new DomElementNamePredicate<>(id);
  }

  @Attribute("id")
  @NameValue
  GenericAttributeValue<String> getId();

  @Attribute("name")
  GenericAttributeValue<String> getName();
}
