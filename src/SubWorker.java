import lombok.Getter;
import lombok.Setter;

public class SubWorker extends Thread {

	@Getter @Setter private boolean mEnd = true;
	private BaseServer mBaseServer = null;

	private long mCurrentTime = 0;
	
//	private long mTimeout = 0;
	
	private long mPrinStatTime = 0;

	public boolean Begin(BaseServer bs) {
		mBaseServer = bs;
		this.start();
		mEnd = false;

		return true;
	}

	public void End() {
		mEnd = true;

		try {
			this.join();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void run() {

		while (!mEnd) {
			mCurrentTime = System.currentTimeMillis();

/*
			if ( mCurrentTime - mTimeout > 1000 * 10 ){
				mBaseServer.TimeoutSocket( );
				mTimeout = mCurrentTime;
			}
*/
			
			if ( mCurrentTime - mPrinStatTime > 1000  ){
				if( mBaseServer.isMPrintstate() )
					mBaseServer.PrintState();
				mPrinStatTime = mCurrentTime;
			}
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}// end of run()

}
