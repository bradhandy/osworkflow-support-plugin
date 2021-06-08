package dev.bradhandy.osworkflow.model;

import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagValue;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.reflect.DomGenericInfo;

import java.util.Optional;
import java.util.function.Predicate;

class DomElementNameValuePredicate<T extends DomElement> implements Predicate<T> {

  private final String name;
  private final String value;

  DomElementNameValuePredicate(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public boolean test(T element) {
    DomGenericInfo domGenericInfo = element.getGenericInfo();
    return domGenericInfo.isTagValueElement()
        && name.equals(domGenericInfo.getElementName(element))
        && value.equals(getValue(element));
  }

  private String getValue(DomElement element) {
    Optional<XmlTag> xmlTag = Optional.ofNullable(element.getXmlTag());
    return xmlTag.map(XmlTag::getValue).map(XmlTagValue::getTrimmedText).orElse(null);
  }
}
