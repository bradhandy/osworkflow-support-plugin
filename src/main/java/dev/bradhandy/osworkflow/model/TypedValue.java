package dev.bradhandy.osworkflow.model;

import org.jetbrains.annotations.Nullable;

public class TypedValue<T> {

  public static <T> TypedValue<T> empty() {
    return new TypedValue<T>(null);
  }

  private final T convertedValue;

  public TypedValue(@Nullable T convertedValue) {
    this.convertedValue = convertedValue;
  }

  public T getConvertedValue() {
    return convertedValue;
  }
}
