package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.Attribute;
import com.intellij.util.xml.GenericAttributeValue;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.NameValue;

import java.util.function.Predicate;

// todo implement converter
public interface WorkflowValue<T> extends GenericDomValue<TypedValue<T>> {

  static Predicate<WorkflowValue<?>> withName(String name) {
    return new DomElementNamePredicate<>(name);
  }

  static Predicate<WorkflowValue<?>> withNameAndValue(String name, String value) {
    return new DomElementNameValuePredicate<>(name, value);
  }

  @Attribute("name")
  @NameValue
  GenericAttributeValue<String> getName();
}
