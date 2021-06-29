package dev.bradhandy.osworkflow.model.converter;

import com.google.common.collect.Iterables;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Pair;
import com.intellij.pom.java.LanguageLevel;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.intellij.psi.xml.XmlFile;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;
import com.intellij.util.xml.GenericDomValue;
import dev.bradhandy.osworkflow.JavaProjectTest;
import dev.bradhandy.osworkflow.OsWorkflowJavaModuleRequired;
import dev.bradhandy.osworkflow.model.Register;
import dev.bradhandy.osworkflow.model.WorkflowValue;
import dev.bradhandy.testing.ModuleJdk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Predicate;

import static dev.bradhandy.osworkflow.model.DomElementTestUtil.findArgumentsForType;
import static dev.bradhandy.osworkflow.model.DomElementTestUtil.readWorkflowProperty;
import static dev.bradhandy.testing.PluginUtil.runReadAction;
import static org.assertj.core.api.Assertions.assertThat;

@JavaProjectTest
@OsWorkflowJavaModuleRequired
@ModuleJdk(languageLevel = LanguageLevel.JDK_14)
public class ArgumentWorkflowValuePsiClassConverterProviderTest {

  private ArgumentWorkflowValuePsiClassConverterProvider converterProvider;

  @BeforeEach
  void setUpWorkflowValuePsiClassConverterProvider() {
    converterProvider = new ArgumentWorkflowValuePsiClassConverterProvider();
  }

  @Test
  void givenWorkflowValueElement_whenConvertingRegisterArgument_thenConditionSucceeds(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    List<WorkflowValue<?>> registerClassArguments =
        findArgumentsForType(
            workflowPsiFile,
            codeInsightTestFixture,
            WorkflowValue.withName("class.name"),
            Register.class,
            Register.withId("my-register-id"));
    WorkflowValue<?> registerClassArgument = Iterables.getOnlyElement(registerClassArguments);

    assertThat(converterProvider.getCondition()).isNotNull();

    Condition<Pair<PsiType, GenericDomValue>> providerCondition = converterProvider.getCondition();
    Pair<PsiType, GenericDomValue> registerArgumentPair = Pair.pair(null, registerClassArgument);
    assertThat(providerCondition.value(registerArgumentPair)).isTrue();
  }

  @Test
  void givenWorkflowValueElement_whenConvertingUnsupportedElement_thenConditionFails(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    WorkflowValue<?> metaTagWorkflowValue =
        readWorkflowProperty(
            workflowPsiFile, codeInsightTestFixture, WorkflowValue.withName("someMetaProperty"));

    assertThat(converterProvider.getCondition()).isNotNull();

    Condition<Pair<PsiType, GenericDomValue>> providerCondition = converterProvider.getCondition();
    Pair<PsiType, GenericDomValue> metaTagWorkflowValuePair = Pair.pair(null, metaTagWorkflowValue);
    assertThat(providerCondition.value(metaTagWorkflowValuePair)).isFalse();
  }

  @Test
  void givenWorkflowValueElement_whenConvertingUnsupportRegisterArgument_thenConditionFails(
      JavaCodeInsightTestFixture codeInsightTestFixture) {
    PsiFile workflowPsiFile = codeInsightTestFixture.configureByFile("parsing/before/workflow.xml");
    assertThat(workflowPsiFile).isInstanceOf(XmlFile.class);

    List<WorkflowValue<?>> unsupportedRegisterArguments =
        findArgumentsForType(
            workflowPsiFile,
            codeInsightTestFixture,
            Predicate.not(WorkflowValue.withName("class.name")),
            Register.class);

    assertThat(converterProvider.getCondition()).isNotNull();

    runReadAction(
        () -> {
          for (WorkflowValue<?> unsupportedRegisterArgument : unsupportedRegisterArguments) {
            Condition<Pair<PsiType, GenericDomValue>> providerCondition =
                converterProvider.getCondition();
            Pair<PsiType, GenericDomValue> metaTagWorkflowValuePair =
                Pair.pair(null, unsupportedRegisterArgument);
            assertThat(providerCondition.value(metaTagWorkflowValuePair)).isFalse();
          }
        });
  }

  @Test
  void givenWorkflowValuePsiConverterProvider_whenRetrievingConverter_thenShouldBeStaticInstance() {
    assertThat(converterProvider.getConverter())
        .isInstanceOf(WorkflowValuePsiClassConverter.class)
        .isSameAs(WorkflowValuePsiClassConverter.INSTANCE);
  }
}
