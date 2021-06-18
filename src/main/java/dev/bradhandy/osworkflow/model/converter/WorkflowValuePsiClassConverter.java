package dev.bradhandy.osworkflow.model.converter;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.JavaClassReferenceProvider;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.xml.ConvertContext;
import com.intellij.util.xml.Converter;
import com.intellij.util.xml.CustomReferenceConverter;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.ExtendClass;
import com.intellij.util.xml.GenericDomValue;
import com.intellij.util.xml.PsiClassConverter;
import dev.bradhandy.osworkflow.model.ArgumentContainer;
import dev.bradhandy.osworkflow.model.TypedValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static com.intellij.util.xml.PsiClassConverter.createJavaClassReferenceProvider;

class WorkflowValuePsiClassConverter extends Converter<TypedValue<PsiClass>>
    implements CustomReferenceConverter<TypedValue<PsiClass>> {

  static final WorkflowValuePsiClassConverter INSTANCE = new WorkflowValuePsiClassConverter();

  private final PsiClassConverter psiClassConverter = new PsiClassConverter();

  @Override
  public @Nullable TypedValue<PsiClass> fromString(
      @Nullable @NonNls String s, ConvertContext context) {
    return Optional.ofNullable(psiClassConverter.fromString(s, context))
        .map(TypedValue::new)
        .orElse(new TypedValue<>(null));
  }

  @Override
  public @Nullable String toString(
      @Nullable TypedValue<PsiClass> typedValue, ConvertContext context) {
    return Optional.ofNullable(typedValue)
        .map(
            psiClassTypedValue ->
                psiClassConverter.toString(typedValue.getConvertedValue(), context))
        .orElse(null);
  }

  @Override
  public PsiReference @NotNull [] createReferences(
      GenericDomValue<TypedValue<PsiClass>> psiClassValue,
      PsiElement element,
      ConvertContext context) {
    ExtendClass extendClass = findExtendClassAnnotation(psiClassValue);
    JavaClassReferenceProvider referenceProvider =
        createJavaClassReferenceProvider(
            psiClassValue,
            extendClass,
            new JavaClassReferenceProvider() {
              @Override
              public @Nullable GlobalSearchScope getScope(@NotNull Project project) {
                return context.getSearchScope();
              }
            });

    return referenceProvider.getReferencesByElement(element);
  }

  @Nullable
  private ExtendClass findExtendClassAnnotation(
      GenericDomValue<TypedValue<PsiClass>> psiClassValue) {
    ExtendClass extendClass = psiClassValue.getAnnotation(ExtendClass.class);
    if (extendClass == null) {
      ArgumentContainer parent =
          DomUtil.getParentOfType(psiClassValue, ArgumentContainer.class, true);
      if (parent != null) {
        extendClass = parent.getAnnotation(ExtendClass.class);
      }
    }

    return extendClass;
  }
}
