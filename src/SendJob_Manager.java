import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import lombok.Getter;
import lombok.Setter;

public class SendJob_Manager {
	
	private static int BUFFER_COUNT = 1000;
	private static int BUFFER_SIZE = 2048;
	 
	// 유저와 버퍼를 하나 얻어와서 netjob 풀에서 하나 얻어와서 같이 쓴다.
	// POOL 
	private static final Object poolmonitor = new Object();
	private static LinkedList<SendJob> mSendJobPool = new LinkedList<SendJob>();
	
	
	//QUEUE
	private static final Object monitor = new Object();
	private static LinkedList<SendJob> jobs = new LinkedList<SendJob>();
	
	//singleton
	private static SendJob_Manager instance = new  SendJob_Manager( BUFFER_COUNT, BUFFER_SIZE);
	
	public synchronized static SendJob_Manager getInstance(){
		if( instance == null)
		{
			instance = new SendJob_Manager( BUFFER_COUNT, BUFFER_SIZE);
		}
		return instance;

	}
	////////////////
	
	// Crater
	SendJob_Manager( int Count , int BufferSize)
	{
		//카운트 만큼 버퍼를 생성한다.
		try{
			for (int i = 0; i < Count * 2; ++i) {
				ByteBuffer buffer = ByteBuffer.allocateDirect(BufferSize);
				buffer.order(ByteOrder.LITTLE_ENDIAN);
				
				SendJob job = new SendJob();
				job.mBuffer = buffer;
				
				mSendJobPool.addLast(job);
			}
		}catch( Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	public void returnResource( SendJob job ){
		try{
			synchronized (poolmonitor) {
				mSendJobPool.addLast(job);
				poolmonitor.notify();
			}
		}catch ( Exception e){
			e.printStackTrace();
		}
	}
	
	public SendJob getResource(){
		SendJob job= null;
		try{
			synchronized (poolmonitor) {

				if (mSendJobPool.isEmpty()) {
					poolmonitor.wait();
				}
				job = mSendJobPool.removeFirst();
			}
			
			if (job == null)
				throw new Exception("NO Elemenet");
			
		}catch (Exception e){
			e.printStackTrace();
		}
		
		return job;
	}

	public void putSendJob( SendJob job){
		synchronized (monitor) {
			jobs.addLast(job);
			monitor.notify();
		}
	}
	
	public SendJob popSendJob() throws InterruptedException, NoSuchElementException{
		SendJob job = null;
		synchronized (monitor) {

			if (jobs.isEmpty()) {
				monitor.wait();
			}
			job = jobs.removeFirst();
		}
		if (job == null)
			throw new NoSuchElementException();
		return job;
		
	}
	
}
