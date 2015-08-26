import java.util.LinkedList;
import java.util.NoSuchElementException;

import lombok.Getter;

public class FreeRoomPool implements Queue {

	private @Getter String NAME = "FreeRoomPool";
	private static final Object monitor = new Object();
	private LinkedList jobs = new LinkedList();
	
	//singleton pattern
	private static FreeRoomPool instance = new FreeRoomPool();
	private FreeRoomPool(){}
	public static FreeRoomPool getInstance(){
		if( instance == null)
		{
			synchronized( FreeRoomPool.class ){
				instance = new FreeRoomPool();
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

	@Override
	public void put(Object o) {
		// TODO Auto-generated method stub
		synchronized(monitor){
			jobs.addLast(o);
			monitor.notify();
		}
	}

	@Override
	public Object pop() throws InterruptedException, NoSuchElementException {
		// TODO Auto-generated method stub
		Object o = null;
		synchronized (monitor) {
			 
			if( jobs.isEmpty() ){
				//monitor.wait();
				// 원래 여기서 하나 더 추가 해야 될것 같다.
			}
			o = jobs.removeFirst();
		}
		if ( o == null) throw new NoSuchElementException();
		return o;
		
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return jobs.size();
	}
	
	//private static final String NAME = "RoomPool";
	//private static final Object monitor = new Object();

	
	

}
