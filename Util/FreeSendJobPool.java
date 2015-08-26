import java.util.LinkedList;
import java.util.NoSuchElementException;

import lombok.Getter;

public class FreeSendJobPool implements Queue  {
	private @Getter String NAME = "FreeSendJobPool";
	
	private static final Object monitor = new Object();
	private LinkedList<SendJob> jobs = new LinkedList<SendJob>();
	
	
	//single ton pattern
	private static FreeSendJobPool instance = new FreeSendJobPool();
	private FreeSendJobPool() {}
	public static FreeSendJobPool getInstance() {
		if (instance == null) {
			synchronized (FreeSendJobPool.class) {
				instance = new FreeSendJobPool();

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
			jobs.addLast((SendJob)o);
			monitor.notify();
		}
	}

	@Override
	public Object pop() throws InterruptedException, NoSuchElementException {
		SendJob o = null;
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
