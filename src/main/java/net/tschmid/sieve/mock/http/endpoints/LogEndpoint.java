package net.tschmid.sieve.mock.http.endpoints;

import net.tschmid.sieve.mock.http.HttpRequest;
import net.tschmid.sieve.mock.http.HttpResponse;
import net.tschmid.sieve.mock.http.WebSocket;
import net.tschmid.sieve.mock.log.LogListener;
import net.tschmid.sieve.mock.log.ActivityLog;

/**
 * The logger endpoint it gets upgraded into a websocket.
 */
public class LogEndpoint implements Endpoint {

  @Override
  public boolean canHandle(final HttpRequest request) {
    if (!request.getMethod().equals("GET"))
      return false;

    if (request.getPath().equals("/log"))
      return true;

    return false;
  }

  @Override
  public void handle(final HttpRequest request, final HttpResponse response) throws Exception {

    final WebSocket socket = new WebSocket(request, response);
    socket.upgrade();

    final LogListener listener = (String msg) -> { 
      try {
        socket.send(msg);
      } catch (Exception e){
        // We can not do anything in case we fail...
      }      
    };

    try {
      ActivityLog.getInstance().addListener(listener);
      //socket.send("Hello World");
      socket.read();
      socket.read();
    } finally {
      ActivityLog.getInstance().removeListener(listener);
    }

  
    // socket.send("\r\n");
    /*WebSocketMessage message = socket.read();

    System.out.println(new String(message.getData());

    socket.send(data);*/
    
    // TODO Auto-generated method stub

  }
  
}
