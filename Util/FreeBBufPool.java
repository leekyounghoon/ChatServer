import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import lombok.Getter;


public class FreeBBufPool implements Queue{
	
	private @Getter String NAME = "ByteBufferPool";
	
	private @Getter LinkedList<ByteBuffer> jobs = new LinkedList<ByteBuffer>();
		
	private static final Object monitor = new Object();
	
	//single ton pattern
	private static FreeBBufPool instance = new FreeBBufPool();
	private FreeBBufPool(){}
	
	
	public synchronized static FreeBBufPool getInstance(){
		if( instance == null)
		{
			 instance = new FreeBBufPool();
		}
		return instance;
	}
	
		
	@Override
	public void clear(){
		synchronized(monitor){
			jobs.clear();
		}
	}

	
	@Override
	public void put(Object o){
		synchronized(monitor){
			jobs.addLast( (ByteBuffer) o );
			monitor.notify();
		}
	}
	
	@Override
	public ByteBuffer pop() throws InterruptedException , NoSuchElementException{
		ByteBuffer o = null;
		synchronized (monitor) {
			 
			if( jobs.isEmpty() ){
				monitor.wait();
			}
			o = jobs.removeFirst();
		}
		if ( o == null) throw new NoSuchElementException();
		return o;
	}
	
	@Override
	public int size(){
		return jobs.size();
	}
}
