package net.tschmid.sieve.mock.http.endpoints;

import net.tschmid.sieve.mock.http.HttpRequest;
import net.tschmid.sieve.mock.http.HttpResponse;
import net.tschmid.sieve.mock.http.WebSocket;
import net.tschmid.sieve.mock.log.LogListener;
import net.tschmid.sieve.mock.log.ActivityLog;

public class LogEndpoint implements Endpoint {

  @Override
  public boolean canHandle(HttpRequest request) {
    if (request.getMethod().equals("GET") != true)
      return false;

    if (request.getPath().equals("/log"))
      return true;

    return false;
  }

  @Override
  public void handle(HttpRequest request, HttpResponse response) throws Exception {

    WebSocket socket = new WebSocket(request, response);
    socket.upgrade();

    LogListener listener = (String msg) -> { 
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
