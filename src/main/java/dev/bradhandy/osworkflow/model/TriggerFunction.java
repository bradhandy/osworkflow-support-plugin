package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.NameValue;
import com.intellij.util.xml.SubTag;

import java.util.function.Predicate;

public interface TriggerFunction extends DomElement {

  static Predicate<TriggerFunction> withId(String id) {
    return new DomElementNamePredicate<>(id);
  }

  @Attribute
  @NameValue
  GenericAttributeValue<String> getId();

  @SubTag("function")
  Function getFunction();
}
