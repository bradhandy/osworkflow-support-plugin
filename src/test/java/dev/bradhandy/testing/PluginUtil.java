package dev.bradhandy.testing;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;

/**
 * Testing support methods common to all aspects of the IntelliJ Platform.
 *
 * @author bhandy
 */
public class PluginUtil {

  /**
   * Executes the provided Computable inside of a read action within IntelliJ.
   *
   * @param computable The logic to execute within a read action.
   * @param <T> The type to return from the Computable.
   * @return The result of the provide Computable.
   */
  public static <T> T runReadAction(Computable<T> computable) {
    return ApplicationManager.getApplication().runReadAction(computable);
  }

  /**
   * Executes the provided Runnable inside of a read action within IntelliJ.
   *
   * @param runnable The logic to execute within a read action.
   */
  public static void runReadAction(Runnable runnable) {
    ApplicationManager.getApplication().runReadAction(runnable);
  }
}
