import java.util.LinkedList;
import java.util.NoSuchElementException;

import lombok.Getter;
import lombok.Setter;


public class JobQueue implements Queue{

	private static final String NAME = "JOB QUEUE";
	//@Getter @Setter private static final Object monitor = new Object();
	@Getter public static Object monitor = new Object();
	
	private LinkedList jobs = new LinkedList();
	
	//single ton pattern
	
	private static JobQueue instance = new JobQueue();
	private JobQueue(){}
	
	public static JobQueue getInstance(){
		if( instance == null)
		{
			synchronized (JobQueue.class){
				instance = new JobQueue();
				
			}
		}
		return instance;
	}
		
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		synchronized(monitor){
			jobs.clear();
		}
	}
	
	
	/*	
	@Override
	public synchronized void put(Object o){
		jobs.addLast(o);
		notify();
	}
	*/
	
	@Override
	public synchronized void put(Object o) {
		// TODO Auto-generated method stub
		synchronized(monitor){
			jobs.addLast(o);
			monitor.notify();
		}
	}
		
	/*
	@Override
	public synchronized Object pop() throws InterruptedException , NoSuchElementException{
		if( jobs.isEmpty() ){
			wait();
		}
		return jobs.removeFirst();

	}
	*/
	@Override
	public Object pop() throws InterruptedException, NoSuchElementException {
		// TODO Auto-generated method stub
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
	
	
	@Override
	public int size(){
		return jobs.size();
	}
	
	public void func_notify()
	{
		synchronized(monitor){
			monitor.notify();
		}
	}
	
}
