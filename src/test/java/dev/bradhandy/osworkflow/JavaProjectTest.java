package dev.bradhandy.osworkflow;

import dev.bradhandy.testing.JavaCodeInsightTestFixtureProvider;
import dev.bradhandy.testing.PluginTestDataPath;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@ExtendWith(JavaCodeInsightTestFixtureProvider.class)
@PluginTestDataPath("build/resources/test")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JavaProjectTest {

}
