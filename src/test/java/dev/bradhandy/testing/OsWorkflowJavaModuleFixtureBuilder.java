package dev.bradhandy.testing;

import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.ModuleFixture;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.testFramework.fixtures.impl.JavaModuleFixtureBuilderImpl;
import com.intellij.testFramework.fixtures.impl.ModuleFixtureImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Custom implementation of JavaModuleFixtureBuilderImpl. This is so we don't have to depend on
 * inner classes from IntelliJ test components.
 *
 * @author bhandy
 */
public class OsWorkflowJavaModuleFixtureBuilder
    extends JavaModuleFixtureBuilderImpl<ModuleFixture> {

  public OsWorkflowJavaModuleFixtureBuilder(
      @NotNull TestFixtureBuilder<? extends IdeaProjectTestFixture> fixtureBuilder) {
    super(fixtureBuilder);
  }

  @Override
  protected @NotNull ModuleFixture instantiateFixture() {
    return new ModuleFixtureImpl(this);
  }
}
