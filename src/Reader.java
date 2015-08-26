
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;


public class Reader extends Thread {
	//------------------  MEMBER VARIANT -----------------------//
			
	@Getter	@Setter
	private static Selector mReadSelector = null;

	@Getter	@Setter
	private boolean mEnd = false;

	private BaseServer mBaseServer = null;

	// ------------------ BASIC FUNCTION -----------------------//
	public Reader() {
		try {
			mReadSelector = Selector.open();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public boolean Begin(BaseServer bs) {
		mBaseServer = bs;

		this.start();
		return true;
	}

	public void End() {
		mEnd = true;
		mReadSelector.wakeup();
		try {
			this.join();
		} catch (Exception e) {
			e.printStackTrace(); // Error
		}
	}

	public void run() {

		try {
			while (!mEnd) {
				// mSelector.select(1);
				try {
					mReadSelector.select(1); // 블러킹 일 경우 소켓등록(읽기소켓)을 하지 못한다.
				} catch (Exception e) {
					e.printStackTrace();
				}

				Iterator<SelectionKey> it = mReadSelector.selectedKeys().iterator();
				while (it.hasNext()) {
					// 멀티로 스레드를 돌릴경우 이부분을 싱크 하면 될것 같다.
					SelectionKey key = (SelectionKey) it.next();
					it.remove();
					/////////////////////////////////////////////////////////////

					try {

						if (key.isReadable())
							mBaseServer.EventRead((SocketChannel) key.channel());

					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//synchronized
	public boolean UnRegist(SocketChannel sc) {
		SelectionKey key = sc.keyFor(mReadSelector);

		key.cancel();

		return true;
	}
	//synchronized
	public boolean Regist(SocketChannel sc) {
		try {
			sc.configureBlocking(false);
			// sc.socket().setSoLinger(true, 0); //leekh13
			sc.register(mReadSelector, SelectionKey.OP_READ);
		} catch (Exception e) {
			return false;
		}

		return true;
	}
						
}
