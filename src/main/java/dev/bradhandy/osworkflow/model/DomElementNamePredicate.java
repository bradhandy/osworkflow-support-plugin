package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.reflect.DomGenericInfo;

import java.util.function.Predicate;

class DomElementNamePredicate<T extends DomElement> implements Predicate<T> {

  private final String name;

  DomElementNamePredicate(String name) {
    this.name = name;
  }

  @Override
  public boolean test(T element) {
    if (element == null) {
      return false;
    }

    DomGenericInfo domGenericInfo = element.getGenericInfo();
    return name.equals(domGenericInfo.getElementName(element));
  }
}
