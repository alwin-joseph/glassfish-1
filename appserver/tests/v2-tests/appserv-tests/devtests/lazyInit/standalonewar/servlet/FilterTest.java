/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package test;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.net.*;
import java.io.*;
public class FilterTest implements Filter{
    
    private ServletContext context;
    private static boolean DEFAULT_REUSE_ADDRESS;
    private static int DEFAULT_RECEIVE_BUFFER_SIZE;
    private static int DEFAULT_SEND_BUFFER_SIZE;
    private static int DEFAULT_TRAFFIC_CLASS;
    private static boolean DEFAULT_KEEP_ALIVE;
    private static boolean DEFAULT_OOB_INLINE;
    private static int DEFAULT_SO_LINGER;
    private static boolean DEFAULT_TCP_NO_DELAY;

    private static final int PORT = 7007;
    private static final String HOST = "localhost";

    private static boolean tested = false;

    
    public void destroy() {
        System.out.println("[Filter.destroy]");
    }    
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws java.io.IOException, jakarta.servlet.ServletException {
        System.out.println("[Filter.doFilter]");

        try {
        java.net.Socket socket = new java.net.Socket();
         DEFAULT_REUSE_ADDRESS = socket.getReuseAddress();
         DEFAULT_RECEIVE_BUFFER_SIZE = socket.getReceiveBufferSize();
         DEFAULT_SEND_BUFFER_SIZE = socket.getSendBufferSize();
         DEFAULT_KEEP_ALIVE = socket.getKeepAlive();
         DEFAULT_OOB_INLINE = socket.getOOBInline();
         DEFAULT_SO_LINGER = socket.getSoLinger();
         DEFAULT_TCP_NO_DELAY = socket.getTcpNoDelay();
         DEFAULT_TRAFFIC_CLASS = socket.getTrafficClass();
         socket.setReceiveBufferSize(DEFAULT_RECEIVE_BUFFER_SIZE);
         socket.setSendBufferSize(DEFAULT_SEND_BUFFER_SIZE);

         if (tested == false) {
             test();
             tested = true;
         }
       }catch (Exception e) {
         e.printStackTrace();
         throw new ServletException(e.getMessage());
       }

       System.out.println("PROCEEDING WITH THE TEST");

        ((HttpServletRequest)request).getSession().setAttribute("FILTER", "PASS");
        filterChain.doFilter(request, response);
        
    }    
    
    
    public void init(jakarta.servlet.FilterConfig filterConfig) throws jakarta.servlet.ServletException {
        System.out.println("[Filter.init]");
        context = filterConfig.getServletContext();
    }
    

    public void test() {
        InetSocketAddress isa;
        try {
            isa = new InetSocketAddress(InetAddress.getByName(HOST), PORT);
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
            throw new RuntimeException (ex);            
        }
        Socket s = new Socket();
        try {            
            s.connect(isa, 1000);
            testSocketConnection();
            InputStream is = s.getInputStream();
            OutputStream os = s.getOutputStream();
            int count = 1;
            while (true){
                int available = is.available();                
                if (available > 0 || count == 6 || count > 10) {
                    if (count == 6) {
                        try {
                            s.setSoTimeout(3000);
                            System.out.println("Blocking call...");
                            is.read();
                            throw new RuntimeException("Should have timed out");
                        } catch (java.net.SocketTimeoutException ie) {
                            System.out.println("Expected exception");
                            ie.printStackTrace();
                        }
                        os.write(count++);                        
                    }
                    int i = is.read();
                    System.out.println(i);                    
                    if (count != i && count < 11 ) throw new RuntimeException("Wrong Data, Expected : " + count + " Got: "+ i);
                    os.write(count++);                    
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }                
            }
        } catch (IOException ex) {
            if (ex instanceof java.net.SocketException) {
                System.out.println("Expected Exception");
                ex.printStackTrace();
                return; 
            } else {
                throw new RuntimeException(ex);
            }
        } finally {            
            try {
                s.close();            
            } catch (IOException ex) {
                throw new RuntimeException("Close Failed");
            }            
        }        
    }
    
    private void testSocketConnection() {
        try {
            Socket s1 = new Socket(InetAddress.getByName(HOST), PORT);
            System.out.println("Local :" + s1.getLocalSocketAddress());
            s1.close();
            Socket s2 = new Socket(InetAddress.getByName(HOST), PORT, false);
            System.out.println("Local:" + s2.getLocalSocketAddress());
            s2.close();
            Socket s3 = new Socket(InetAddress.getByName(HOST), PORT, InetAddress.getByName(HOST), 8600);
            System.out.println("Remote:" + s3.getRemoteSocketAddress());
            s3.close();
            Socket s4 = new Socket(Proxy.NO_PROXY);
            s4.connect(new InetSocketAddress(HOST, PORT));
            System.out.println("Remote: " + s4.getRemoteSocketAddress());
            s4.close();
            Socket s5 = new Socket(HOST, PORT);
            System.out.println("Local:" + s5.getLocalSocketAddress());
            s5.close();
            Socket s6 = new Socket();
            s6.bind(null);
            System.out.println("Local:" + s6.getLocalSocketAddress());
            s6.close();
            try {
                Socket s7 = new Socket(HOST, PORT + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Socket s = new Socket();
            int rsize = s.getReceiveBufferSize();            
            s.setReceiveBufferSize(1000);
            s.connect(new InetSocketAddress(InetAddress.getByName(HOST), PORT));            
            if (s.getChannel() != null ) throw new RuntimeException ("Channel not null");
            if (s.getInetAddress().equals(InetAddress.getByName(HOST)) == false ) throw new RuntimeException("Not connected to local address");
            boolean b = s.getKeepAlive();            
            s.setKeepAlive(! b);
            if (s.getKeepAlive() == b) throw new RuntimeException("Keep alive is not set");
            if (s.getLocalAddress().equals(InetAddress.getByName(HOST)) == false ) throw new RuntimeException("Not connected to local address");            
            System.out.println("Port : " + s.getLocalPort());
            System.out.println("Port : " + s.getLocalSocketAddress());            
            System.out.println("Port : " + s.getPort());              
            b = s.getOOBInline();
            s.setOOBInline(! b);
            if (s.getOOBInline() == b) throw new RuntimeException("OOB not set");
            s.setPerformancePreferences(1000, 100, 100);
            
            int bs = s.getReceiveBufferSize();
            if (bs == rsize ) throw new RuntimeException("RBS not set : " + bs);
            b = s.getReuseAddress();
            s.setReuseAddress(!b);
            if (s.getReuseAddress() == b) throw new RuntimeException("ReuseADDR not set");
            int i = s.getSoLinger();
            if (i != -1 ) {
                s.setSoLinger(false, 0);
                if (s.getSoLinger() != -1) throw new RuntimeException("Linger not set");
            } else {
                s.setSoLinger(true, 1000);
                if (s.getSoLinger() != 1000) throw new RuntimeException("Linger not set");
            }
            s.setSoTimeout(1000);
            if (s.getSoTimeout() != 1000 ) throw new RuntimeException("TIMEOUT not SET");
            
            b = s.getTcpNoDelay();
            s.setTcpNoDelay(! b);
           
            if (s.getTcpNoDelay() == b) throw new RuntimeException("TCP NO DELAT not SET");
            i = s.getTrafficClass();     
           
            
            int j = i +5;
            if (j > 250) j = i -5;
            int k = s.getTrafficClass();
            s.setTrafficClass(j);
            int tc = s.getTrafficClass();
            if (tc == k) throw new RuntimeException("TC not set: " + tc);       
            
            s.setSendBufferSize(1000);
            s.getSendBufferSize();
        } catch (RuntimeException e){
            throw e;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unwanted exceprtion");      
        
        }       
           
    }
}
