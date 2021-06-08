package dev.bradhandy.osworkflow.model;

import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.SubTagList;

import java.util.Collection;

public interface RegisterList extends DomElement {

  @SubTagList("register")
  Collection<Register> getRegisters();
}
