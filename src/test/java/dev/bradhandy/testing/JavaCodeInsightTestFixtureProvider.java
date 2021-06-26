package dev.bradhandy.testing;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.TestApplicationManager;
import com.intellij.testFramework.TestIndexingModeSupporter;
import com.intellij.testFramework.builders.JavaModuleFixtureBuilder;
import com.intellij.testFramework.builders.ModuleFixtureBuilder;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.testFramework.fixtures.JavaIndexingModeCodeInsightTestFixture;
import com.intellij.testFramework.fixtures.JavaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.testFramework.fixtures.impl.TempDirTestFixtureImpl;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.platform.commons.support.AnnotationSupport.findAnnotation;

public class JavaCodeInsightTestFixtureProvider
    implements ParameterResolver, BeforeEachCallback, AfterEachCallback {

  private static final String DEFAULT_TEST_DATA_PATH = "build";
  private static final ProjectModule[] EMPTY_PROJECT_MODULE_ARRAY = new ProjectModule[0];
  private static final ModuleJdk[] EMPTY_MODULE_JDK_ARRAY = new ModuleJdk[0];

  private static JavaCodeInsightTestFixture codeInsightTestFixture;
  private static TempDirTestFixtureImpl tempDirTestFixture;

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return JavaCodeInsightTestFixture.class.isAssignableFrom(
        parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    if (codeInsightTestFixture == null) {
      throw new ParameterResolutionException("CodeInsightTestFixture is missing.");
    }

    return codeInsightTestFixture;
  }

  @Override
  public void afterEach(ExtensionContext context) {
    try {
      if (codeInsightTestFixture != null) {
        codeInsightTestFixture.tearDown();
      }
    } catch (Exception e) {
      context.publishReportEntry("", "Failed to teardown JavaCodeInsightTestFixture.");
    }
    TestApplicationManager.getInstance().setDataProvider(null);
    codeInsightTestFixture = null;
    tempDirTestFixture = null;
  }

  @Override
  public void beforeEach(ExtensionContext extensionContext) {
    Optional<PluginTestDataPath> pluginTestDataPathAnnotation =
        extensionContext
            .getTestClass()
            .flatMap(testClass -> findAnnotation(testClass, PluginTestDataPath.class));

    tempDirTestFixture = new TempDirTestFixtureImpl();

    String testDataPath =
        pluginTestDataPathAnnotation.map(PluginTestDataPath::value).orElse(DEFAULT_TEST_DATA_PATH);
    if (moduleFixturesAreRequired(extensionContext)) {
      codeInsightTestFixture = createHeavyCodeInsightTestFixture(testDataPath, extensionContext);
    } else {
      codeInsightTestFixture = createLightCodeInsightTestFixture(testDataPath);
    }
  }

  private JavaCodeInsightTestFixture createLightCodeInsightTestFixture(String testDataPath)
      throws ParameterResolutionException {
    IdeaProjectTestFixture projectTestFixture =
        IdeaTestFixtureFactory.getFixtureFactory()
            .createLightFixtureBuilder(new DefaultLightProjectDescriptor())
            .getFixture();

    return createCodeInsightTextFixture(testDataPath, projectTestFixture);
  }

  private JavaCodeInsightTestFixture createCodeInsightTextFixture(
      String testDataPath, IdeaProjectTestFixture projectTestFixture) {
    codeInsightTestFixture =
        JavaTestFixtureFactory.getFixtureFactory()
            .createCodeInsightFixture(projectTestFixture, tempDirTestFixture);
    codeInsightTestFixture =
        JavaIndexingModeCodeInsightTestFixture.Companion.wrapFixture(
            codeInsightTestFixture, TestIndexingModeSupporter.IndexingMode.SMART);

    codeInsightTestFixture.setTestDataPath(testDataPath);

    try {
      codeInsightTestFixture.setUp();
      return codeInsightTestFixture;
    } catch (Exception e) {
      throw new ParameterResolutionException("Could not create CodeInsightTextFixture.", e);
    }
  }

  private boolean moduleFixturesAreRequired(ExtensionContext extensionContext) {
    return getProjectModuleAnnotations(extensionContext).length > 0;
  }

  private ProjectModule[] getProjectModuleAnnotations(ExtensionContext extensionContext) {
    return extensionContext
        .getTestClass()
        .map(
            (testClass) -> {
              ProjectModules projectModulesAnnotation =
                  findAnnotation(testClass, ProjectModules.class).orElse(null);
              ProjectModule[] projectModules =
                  (projectModulesAnnotation == null)
                      ? EMPTY_PROJECT_MODULE_ARRAY
                      : projectModulesAnnotation.value();
              if (projectModules.length == 0) {
                ProjectModule projectModule =
                    findAnnotation(testClass, ProjectModule.class).orElse(null);
                if (projectModule != null) {
                  projectModules = new ProjectModule[] {projectModule};
                }
              }

              return projectModules;
            })
        .orElse(EMPTY_PROJECT_MODULE_ARRAY);
  }

  private ModuleJdk[] getModuleJdks(ExtensionContext extensionContext) {
    return extensionContext
        .getTestClass()
        .map(
            (testClass) -> {
              ModuleJdks moduleJdksAnnotation =
                  findAnnotation(testClass, ModuleJdks.class).orElse(null);
              ModuleJdk[] moduleJdks =
                  (moduleJdksAnnotation == null)
                      ? EMPTY_MODULE_JDK_ARRAY
                      : moduleJdksAnnotation.value();
              if (moduleJdks.length == 0) {
                ModuleJdk moduleJdk = findAnnotation(testClass, ModuleJdk.class).orElse(null);
                if (moduleJdk != null) {
                  moduleJdks = new ModuleJdk[] {moduleJdk};
                }
              }

              return moduleJdks;
            })
        .orElse(EMPTY_MODULE_JDK_ARRAY);
  }

  private JavaCodeInsightTestFixture createHeavyCodeInsightTestFixture(
      String testDataPath, ExtensionContext extensionContext) {
    ModuleJdk[] moduleJdks = getModuleJdks(extensionContext);
    Map<String, ModuleJdk> moduleJdkMap = Maps.uniqueIndex(List.of(moduleJdks), ModuleJdk::name);

    IdeaTestFixtureFactory fixtureFactory = IdeaTestFixtureFactory.getFixtureFactory();
    TestFixtureBuilder<IdeaProjectTestFixture> projectTestFixtureBuilder =
        fixtureFactory.createFixtureBuilder(
            extensionContext.getTestClass().map(Class::getSimpleName).orElse("NotProvided"));

    ProjectModule[] projectModules = getProjectModuleAnnotations(extensionContext);
    for (ProjectModule projectModule : projectModules) {
      fixtureFactory.registerFixtureBuilder(
          (Class<? extends ModuleFixtureBuilder<?>>) projectModule.builderType(),
          projectModule.builderImpl());
      ModuleFixtureBuilder<?> moduleFixtureBuilder =
          projectTestFixtureBuilder.addModule(projectModule.builderType());
      if (moduleJdkMap.containsKey(projectModule.name())) {
        if (!JavaModuleFixtureBuilder.class.isAssignableFrom(projectModule.builderType())) {
          throw new RuntimeException(
              String.format(
                  "Builder for module %s is not a JavaModuleFixtureBuilder implementation.",
                  projectModule.name()));
        }

        ModuleJdk moduleJdk = moduleJdkMap.get(projectModule.name());
        LanguageLevel languageLevel = moduleJdk.languageLevel();
        String jdkPath =
            Strings.isNullOrEmpty(moduleJdk.path())
                ? System.getProperty("java.home")
                : moduleJdk.path();

        ((JavaModuleFixtureBuilder<?>) moduleFixtureBuilder)
            .addJdk(jdkPath)
            .setLanguageLevel(languageLevel);
        ((JavaModuleFixtureBuilder<?>) moduleFixtureBuilder)
            .addMavenLibrary(new JavaModuleFixtureBuilder.MavenLib("opensymphony:osworkflow:2.7.0"))
            .addMavenLibrary(new JavaModuleFixtureBuilder.MavenLib("opensymphony:propertyset:1.3"));

        Path tempDirectory = Path.of(tempDirTestFixture.getTempDirPath());
        Path contentPath = tempDirectory.resolve("content");
        Path srcPath = contentPath.resolve("src");
        Path outputPath = tempDirectory.resolve("output/production");
        Path testOutputPath = tempDirectory.resolve("output/test");

        try {
          Files.createDirectories(contentPath);
          Files.createDirectories(srcPath);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        moduleFixtureBuilder.addContentRoot(contentPath.toAbsolutePath().toString());
        moduleFixtureBuilder.addSourceRoot("src");
        moduleFixtureBuilder.setOutputPath(outputPath.toAbsolutePath().toString());
        moduleFixtureBuilder.setTestOutputPath(testOutputPath.toAbsolutePath().toString());
      }
    }

    return createCodeInsightTextFixture(testDataPath, projectTestFixtureBuilder.getFixture());
  }
}
