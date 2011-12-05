package runner;

import java.io.IOException;

import control.StateManager;

import android.util.Log;

import channel.Server;

public class ServerRunner extends MonitorRunner {
    protected int port;
    protected Server serverChannel;
    protected StateManager stateManager;
    
    public ServerRunner(int port) throws IOException {
	this.port = port;
	this.serverChannel = new Server(port);
    }
    
    public void startServer() {
	try {
	    Log.e("423-server", "server tries to listen");
	    this.serverChannel.listen();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public int getPort() {
        return port;
    }

    public Server getServerChannel() {
        return serverChannel;
    }
}
