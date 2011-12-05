package runner.matrix;

import java.io.IOException;
import java.io.OptionalDataException;
import java.net.UnknownHostException;

import control.state.ClientStateHandler;
import control.transfer.ClientTransferHandler;

import channel.Client;

import matrix.Matrix;
import runner.ClientRunner;
import task.matrix.MatrixJobQueue;
import task.matrix.MatrixResults;
import task.matrix.MatrixWorker;
import android.util.Log;

public class MatrixClientRunner extends ClientRunner {
    private MatrixWorker worker;
    private MatrixResults results;
    private Matrix matrix1;
    private Matrix matrix2;
    private MatrixJobQueue jobQueue;

    public MatrixClientRunner(String serverIP, int serverPort) {
	super(serverIP, serverPort); // Including connecting to server
	Log.e("423-client", "connected to the server " + serverIP + ":"
		+ serverPort);

	this.results = new MatrixResults();
	this.worker = new MatrixWorker(0, this.hwMonitor, this.results);
    }

    public void run() {
	getMatrixFromServer();
	Log.e("423-client", "got the matrix from server");
	initJobQueue();
	Log.e("423-client", "initialized job queue");
	initClientStateHandler();
	Log.e("423-client", "initialized client state handler");
	initTransferHandler();
	Log.e("423-client", "initialized client transfer handler");
	processWork();
	Log.e("423-client", "finished working");
	sendResultsToServer();
	Log.e("423-client", "sent work back to the server");
    }

    private void getMatrixFromServer() {
	try {

	    this.matrix1 = (Matrix) this.getChannel().getObject();
	    Log.e("423-client", "got matrix1");

	    this.matrix2 = (Matrix) this.getChannel().getObject();
	    Log.e("423-client", "got matrix2");

	} catch (OptionalDataException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (ClassNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void initJobQueue() {
	jobQueue = MatrixJobQueue.generateJobQueueWithMatrixs(matrix1, matrix2);
    }

    public void initClientStateHandler() {
	try {
	    stateChannel = new Client(serverIP, serverPort + 1);
	} catch (UnknownHostException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	clientStateHandler = new ClientStateHandler(jobQueue, hwMonitor, stateChannel);
    }
    
    public void initTransferHandler() {
	transferHandler = new ClientTransferHandler(jobQueue, channel);
    }

    public void processWork() {
	this.worker.processJobsWithThrottling(jobQueue);
    }

    private void sendResultsToServer() {
	try {
	    this.getChannel().sendObject(this.results);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
