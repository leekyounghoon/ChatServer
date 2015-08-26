import java.util.LinkedList;
import java.util.NoSuchElementException;

import lombok.Getter;


public class FreeUserPool implements Queue{

	private @Getter String NAME = "UserPool";
	private static final Object monitor = new Object();
	
	private @Getter LinkedList<User> jobs = new LinkedList<User>();
	
	//single ton pattern
	private static FreeUserPool instance = new FreeUserPool();

	private FreeUserPool() {}

	public static FreeUserPool getInstance() {
		if (instance == null) {
			synchronized (FreeUserPool.class) {
				instance = new FreeUserPool();

			}
		}
		return instance;
	}

	@Override
	public void clear() {
		synchronized (monitor) {
			jobs.clear();
		}
	}

	@Override
	public void put(Object o) {
		synchronized (monitor) {
			jobs.addLast((User)o);
			monitor.notify();
		}
	}

	@Override
	public Object pop() throws InterruptedException, NoSuchElementException {
		Object o = null;
		synchronized (monitor) {

			if (jobs.isEmpty()) {
				monitor.wait();
			}
			o = jobs.removeFirst();
		}
		if (o == null)
			throw new NoSuchElementException();
		return o;
	}

	@Override
	public int size() {
		return jobs.size();
	}
}
