package dev.bradhandy.osworkflow.model;

import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import dev.bradhandy.osworkflow.JavaProjectTest;
import dev.bradhandy.osworkflow.OsWorkflowJavaModuleRequired;
import dev.bradhandy.testing.ModuleJdk;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JavaProjectTest
@OsWorkflowJavaModuleRequired
@ModuleJdk(languageLevel = LanguageLevel.JDK_14)
public class OsWorkflowCodeCompletionTest {

  @Test
  void givenOsWorkflowFile_whenCaretInRegisterArgumentElement_thenSuggestRegistersForCompletion(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    codeInsightTestFixture.copyDirectoryToProject("register-class-completion/before", "content");
    codeInsightTestFixture.configureFromTempProjectFile("content/workflow.xml");

    codeInsightTestFixture.complete(CompletionType.SMART);

    List<String> completionSuggestions = codeInsightTestFixture.getLookupElementStrings();
    assertThat(completionSuggestions).isNotNull().isNotEmpty();
    assertThat(completionSuggestions)
        .contains("dev.bradhandy.NoopRegister")
        .doesNotContain("dev.bradhandy.SomeOtherClass");

    codeInsightTestFixture.type('\t');
    codeInsightTestFixture.checkResultByFile("register-class-completion/after/workflow.xml");
  }
}
