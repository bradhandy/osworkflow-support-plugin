package dev.bradhandy.testing;

import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.builders.ModuleFixtureBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Registers a module fixture builder with a HeavyIdeaTestFixture. This allows a module of this type
 * to be added to the HeaveIdeaTestFixture when being created.
 *
 * @author bhandy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ProjectModules.class)
public @interface ProjectModule {

  String DEFAULT_MODULE_NAME = "__Default_Module__";

  /**
   * The name of the module.
   */
  String name() default DEFAULT_MODULE_NAME;

  /**
   * The interface type of the builder.
   *
   * The "Raw use of parameterized class" inspection is suppressed on purpose. This is a Java 5
   * code migration inspection. Leave the parameterized type off allows the interface type to
   * be set as a value.
   *
   * @return The interface type of the {@code #builderImpl}.
   */
  Class<? extends ModuleFixtureBuilder> builderType();

  /**
   * The implementation class of the {@code #builderType}.
   *
   * @return The implementation class of the {@code #builderType}.
   */
  String builderImpl();
}
