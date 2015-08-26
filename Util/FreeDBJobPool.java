import java.util.LinkedList;
import java.util.NoSuchElementException;

import lombok.Getter;


public class FreeDBJobPool {
	
	private @Getter String NAME = "DBJobPool";
	private static final Object monitor = new Object();
	private @Getter LinkedList jobs = new LinkedList();
	
	private static FreeDBJobPool instance = new FreeDBJobPool();
	private FreeDBJobPool(){}
	public static FreeDBJobPool getInstance(){
		if( instance == null)
		{
			synchronized (FreeDBJobPool.class){
				instance = new FreeDBJobPool();
			}
		}
		return instance;
	}
	
		
	public void clear(){
		synchronized(monitor){
			jobs.clear();
		}
	}
	
	public void put(Object o){
		synchronized(monitor){
			jobs.addLast(o);
			monitor.notify();
		}
	}
	
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
	
	public int size(){
		return jobs.size();
	}
}
