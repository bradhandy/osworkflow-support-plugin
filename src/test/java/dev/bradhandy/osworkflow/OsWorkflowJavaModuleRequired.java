package dev.bradhandy.osworkflow;

import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import dev.bradhandy.testing.ProjectModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ProjectModule(
    builderType = JavaModuleFixtureBuilder.class,
    builderImpl = "dev.bradhandy.testing.OsWorkflowJavaModuleFixtureBuilder")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OsWorkflowJavaModuleRequired {

}
