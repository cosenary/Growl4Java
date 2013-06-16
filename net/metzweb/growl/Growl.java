package net.metzweb.growl;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Growl4Java.
 *
 * Notify with Growl using AppleScript and Java's script engine.
 * Modified and updated for Growl 2.0+ by Christian Metz.
 * Class Documentation: https://github.com/cosenary/Growl4Java
 *
 * @author Christian Metz
 * @author Tobias Södergren
 * @version 2.0
 * @license BSD http://www.opensource.org/licenses/bsd-license.php
 */
public class Growl {

  /**
   * Identifier of the latest Growl version (com.Growl.GrowlHelperApp).
   */
  private static final String GROWL_APPLICATION = "GrowlHelperApp";

  private final String applicationName;
  private String[] availableNotifications;
  private String[] enabledNotifications;
  private ScriptEngine appleScriptEngine;

  /**
   * Custom constructor.
   *
   * @param applicationName         Application name.
   * @param availableNotifications  Available notifications.
   * @param enabledNotifications    Enabled notifications.
   */
  public Growl(String applicationName, String[] availableNotifications, String[] enabledNotifications) {
    this.applicationName = applicationName;
    this.availableNotifications = availableNotifications;
    this.enabledNotifications = enabledNotifications;
  }

  /**
   * Initialize Java's AppleScript engine.
   *
   * @return Whether Growl is available.
   */
  public boolean init() {
    ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    appleScriptEngine = scriptEngineManager.getEngineByName("AppleScript");
    if (appleScriptEngine == null) {
      System.err.println("No AppleScriptEngine available.");
      return false;
    }
    if (!isGrowlEnabled()) {
      System.err.println("No Growl process was found.");
      return false;
    }
    return true;
  }

  /**
   * Add application to Growl.
   */
  public void registerApplication() {
    String script = script().add("tell application id ")
      .quote("com.Growl." + GROWL_APPLICATION)
      .nextRow("set the allNotificationsList to ")
      .array(availableNotifications)
      .nextRow("set the enabledNotificationsList to ")
      .array(enabledNotifications)
      .nextRow("register as application ")
      .quote(applicationName)
      .add(" all notifications allNotificationsList default notifications enabledNotificationsList")
      .nextRow("end tell").get();
    executeScript(script);
  }

  /**
   * Send notification.
   *
   * @param notificationName  Notificaion name.
   * @param title             Message title.
   * @param message           Message.
   */
  public void notify(String notificationName, String title, String message) {
    String script = script().add("tell application id ")
      .quote("com.Growl." + GROWL_APPLICATION)
      .nextRow("notify with name ").quote(notificationName)
      .add(" title ").quote(title)
      .add(" description ").quote(message)
      .add(" application name ").quote(applicationName)
      .nextRow("end tell").get();
    executeScript(script);
  }

  /**
   * Send notification with icon.
   *
   * @param notificationName  Notification name.
   * @param title             Message title.
   * @param message           Message.
   * @param iconPath          Absolute icon path.
   */
  public void notify(String notificationName, String title, String message, String iconPath) {
    String script = script().add(getRawImageCmd(iconPath))
      .nextRow("tell application id ")
      .quote("com.Growl." + GROWL_APPLICATION)
      .nextRow("notify with name ").quote(notificationName)
      .add(" title ").quote(title)
      .add(" description ").quote(message)
      .add(" application name ").quote(applicationName)
      .add(" image rawImage")
      .nextRow("end tell").get();
    executeScript(script);
  }

  /**
   * Generate command for converting an image to raw TIFF data.
   *
   * @param imagePath Absolute image path.
   * @return          AppleScript command.
   */
  private String getRawImageCmd(String imagePath) {
    String script = script().add("set imgfd to open for access POSIX file ").quote(imagePath)
      .nextRow("set img to read imgfd as ").quote("TIFF")
      .nextRow("close access imgfd")
      .nextRow("set rawImage to img").get();
    return script;
  }

  /**
   * Check whether Growl is available.
   *
   * @return
   */
  private boolean isGrowlEnabled() {
    String script = script().add("tell application ")
      .quote("System Events")
      .nextRow("(count of (every process whose bundle identifier is ")
      .quote("com.Growl." + GROWL_APPLICATION).add(")) > 0")
      .nextRow("end tell")
      .get();
    long count = executeScript(script, 0L);
    return count > 0;
  }

  /**
   * Execute AppleScript command.
   *
   * @param <T>           Return object.
   * @param script        AppleScript.
   * @param defaultValue  Default value.
   * @return              Script's result.
   */
  private <T> T executeScript(String script, T defaultValue) {
    try {
      return (T) appleScriptEngine.eval(script, appleScriptEngine.getContext());
    } catch (ScriptException e) {
      System.out.println(e.getMessage());
      return defaultValue;
    }
  }

  /**
   * Execute AppleScript command.
   *
   * @param script AppleScript.
   */
  private void executeScript(String script) {
    try {
      appleScriptEngine.eval(script, appleScriptEngine.getContext());
    } catch (ScriptException e) {
      System.err.println("Problem executing script.");
    }
  }

  /**
   * Init new ScriptBuilder instance.
   * Used in order to create commands.
   *
   * @return New ScriptBuilder.
   */
  private ScriptBuilder script() {
    return new ScriptBuilder();
  }

  /**
   * Local ScriptBuilder class.
   * Assembles commands to an executable script.
   */
  private class ScriptBuilder {

    StringBuilder builder = new StringBuilder();

    public ScriptBuilder add(String text) {
      builder.append(text);
      return this;
    }

    public ScriptBuilder quote(String text) {
      builder.append("\"");
      builder.append(text);
      builder.append("\"");
      return this;
    }

    public ScriptBuilder nextRow(String text) {
      builder.append("\n");
      builder.append(text);
      return this;
    }

    public String get() {
      return builder.toString();
    }

    public ScriptBuilder array(String[] array) {
      builder.append("{");
      for (int i = 0; i < array.length; i++) {
        if (i > 0) {
          builder.append(", ");
        }
        builder.append("\"");
        builder.append(array[i]);
        builder.append("\"");
      }
      
      builder.append("}");
      return this;
    }
  }

}
