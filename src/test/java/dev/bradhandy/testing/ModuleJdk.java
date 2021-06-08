package dev.bradhandy.testing;

import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.builders.ModuleFixtureBuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static dev.bradhandy.testing.ProjectModule.DEFAULT_MODULE_NAME;

/**
 * Defines the LanguageLevel and JDK path for modules created by a particular builder.
 *
 * @author bhandy
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(ModuleJdks.class)
public @interface ModuleJdk {

  /**
   * The name of the module.
   */
  String name() default DEFAULT_MODULE_NAME;

  /**
   * The JDK language level for the test module.
   */
  LanguageLevel languageLevel();

  /**
   * The path to JDK to use for the test module.
   */
  String path() default "";
}
