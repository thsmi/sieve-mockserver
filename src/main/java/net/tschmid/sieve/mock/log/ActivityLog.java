/* 
 * The contents of this file is licensed. You may obtain a copy of
 * the license at https://github.com/thsmi/sieve/ or request it via email 
 * from the author. Do not remove or change this comment. 
 * 
 * The initial author of the code is:
 *   Thomas Schmid <schmid-thomas@gmx.net>
 */

package net.tschmid.sieve.mock.log;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Implements an simple activity log which collects log message
 * and dispatches them to listener.
 */
public class ActivityLog {

  private static ActivityLog instance = null;

  private final List<LogListener> listeners = Collections.synchronizedList(new LinkedList<>());

  /**
   * Creates a new instance. 
   * It is protected as the class is a singleton.
   */
  protected ActivityLog() {    
  }

  public void log(String message) {
    message = ""
      + "[" + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now()) + "]"
      + "[" + Thread.currentThread().getId() + "]"
      + message;

    System.out.println(message);

    for (LogListener item : this.listeners) {
      item.onLogMessage(message);
    }
  }

  /**
   * Registers a new listener which consumes log messages.
   * 
   * @param listener
   *   the listener to be registered.
   */
  public void addListener(LogListener listener) {
    this.listeners.add(listener);
  }

  /**
   * Removes the listener and stops notifying it about log messages.
   * 
   * @param listener
   *   the listener to be removed.
   */
  public void removeListener(LogListener listener) {
    this.listeners.remove(listener);
  }

  /**
   * Gets the current instance of the activity log.
   * It is a singleton.
   * 
   * @return
   *   the activity log instance.
   */
  synchronized public static ActivityLog getInstance() {
    if (ActivityLog.instance == null)
      ActivityLog.instance = new ActivityLog();

    return ActivityLog.instance;
  }

}
