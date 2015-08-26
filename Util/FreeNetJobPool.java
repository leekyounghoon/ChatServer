import java.util.LinkedList;
import java.util.NoSuchElementException;

import lombok.Getter;

public class FreeNetJobPool implements Queue {
	
	private @Getter String NAME = "NetJobPool";
	private static final Object monitor = new Object();
	private @Getter LinkedList<NetJob> jobs = new LinkedList<NetJob>();
	
	//single ton pattern
	private static FreeNetJobPool instance = new FreeNetJobPool();
	private FreeNetJobPool(){}
	public static FreeNetJobPool getInstance(){
		if( instance == null)
		{
			synchronized (FreeNetJobPool.class){
				instance = new FreeNetJobPool();
			}
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
			jobs.addLast((NetJob)o);
			monitor.notify();
		}
	}
	
	@Override
	public Object pop() throws InterruptedException , NoSuchElementException{
		Object o = null;
		synchronized (monitor) {
			 
			if( jobs.isEmpty() ){
				monitor.wait();
			}
			o = jobs.removeFirst();
		}
		if ( o == null) throw new NoSuchElementException();
		return o;
	}
	
	@Override public int size(){
		return jobs.size();
	}
}
