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

public class ActivityLog {

  private static ActivityLog instance = null;

  private final List<LogListener> listeners = Collections.synchronizedList(new LinkedList<>());

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

  public void addListener(LogListener listener) {
    this.listeners.add(listener);
  }

  public void removeListener(LogListener listener) {
    this.listeners.remove(listener);
  }
  
  synchronized public static ActivityLog getInstance() {
    if (ActivityLog.instance == null)
      ActivityLog.instance = new ActivityLog();

    return ActivityLog.instance;
  }

}
