/*******************************************************************************

"FreePastry" Peer-to-Peer Application Development Substrate

Copyright 2002-2007, Rice University. Copyright 2006-2007, Max Planck Institute 
for Software Systems.  All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

- Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.

- Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

- Neither the name of Rice  University (RICE), Max Planck Institute for Software 
Systems (MPI-SWS) nor the names of its contributors may be used to endorse or 
promote products derived from this software without specific prior written 
permission.

This software is provided by RICE, MPI-SWS and the contributors on an "as is" 
basis, without any representations or warranties of any kind, express or implied 
including, but not limited to, representations or warranties of 
non-infringement, merchantability or fitness for a particular purpose. In no 
event shall RICE, MPI-SWS or contributors be liable for any direct, indirect, 
incidental, special, exemplary, or consequential damages (including, but not 
limited to, procurement of substitute goods or services; loss of use, data, or 
profits; or business interruption) however caused and on any theory of 
liability, whether in contract, strict liability, or tort (including negligence
or otherwise) arising in any way out of the use of this software, even if 
advised of the possibility of such damage.

*******************************************************************************/ 
package rice.tutorial.remotesocket;

import java.io.IOException;
import java.nio.ByteBuffer;

import rice.p2p.commonapi.*;
import rice.p2p.commonapi.appsocket.*;

/**
 * By calling sendMyMsgDirect(NodeHandle nh), this Application opens a socket to MyApp on the requested node, and sends it's Id. 
 * 
 * @author Jeff Hoye
 */
public class MyApp implements Application {
  /**
   * The Endpoint represents the underlieing node.  By making calls on the 
   * Endpoint, it assures that the message will be delivered to a MyApp on whichever
   * node the message is intended for.
   */
  protected Endpoint endpoint;
  
  /**
   * The node we were constructed on.
   */
  protected Node node;

  /**
   * The message to receive.
   */
  ByteBuffer in;
  
  int MSG_LENGTH;
  
  public MyApp(Node node, final IdFactory factory) {
    // register the endpoint
    this.endpoint = node.buildEndpoint(this, "myinstance");
    this.node = node;
    
    // create the output message
    MSG_LENGTH = node.getLocalNodeHandle().getId().toByteArray().length;
    
    // create a buffer for the input message
    in = ByteBuffer.allocate(MSG_LENGTH);
    
    // example receiver interface
    endpoint.accept(new AppSocketReceiver() {
      /**
       * When we accept a new socket.
       */
      @Override
	public void receiveSocket(AppSocket socket) {
        // this code reuses "this" AppSocketReceiver, and registers for reading only, and a timeout of 30000. 
        socket.register(true, false, 30000, this);
        
        // it's critical to call this to be able to accept multiple times
        endpoint.accept(this);
      }    

      /**
       * Called when the socket is ready for reading or writing.
       */
      @Override
	public void receiveSelectResult(AppSocket socket, boolean canRead, boolean canWrite) {
        in.clear();
        try {
          // read from the socket into ins
          long ret = socket.read(in);    
          
          if (ret != MSG_LENGTH) {
            // if you sent any kind of long message, you would need to handle this case better
            System.out.println("Error, we only received part of a message."+ret+" from "+socket);
            return;
          }
            
          System.out.println(MyApp.this.node.getLocalNodeHandle()+" Received message from "+factory.buildId(in.array()));        
        } catch (IOException ioe) {
          ioe.printStackTrace(); 
        }
        // only need to do this if expecting more messages
//        socket.register(true, false, 3000, this);        
      }
    
      /**
       * Called if we have a problem.
       */
      @Override
	public void receiveException(AppSocket socket, Exception e) {
        e.printStackTrace();
      }    
    });
    
    // register after we have set the AppSocketReceiver
    endpoint.register();
  }

  /**
   * Getter for the node.
   */
  public Node getNode() {
    return node;
  }
  
  /**
   * Called when we receive a message.
   */
  @Override
public void deliver(Id id, Message message) {
    System.out.println(this+" received "+message);
  }

  /**
   * Called when you hear about a new neighbor.
   * Don't worry about this method for now.
   */
  @Override
public void update(NodeHandle handle, boolean joined) {
  }
  
  /**
   * Called a message travels along your path.
   * Don't worry about this method for now.
   */
  @Override
public boolean forward(RouteMessage message) {
    return true;
  }
  
  @Override
public String toString() {
    return "MyApp "+endpoint.getId();
  }
}
