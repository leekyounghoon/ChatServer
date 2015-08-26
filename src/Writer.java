
import java.util.NoSuchElementException;

import lombok.Getter;
import lombok.Setter;


public class Writer extends Thread {
	public static final int USERCOUNT = 5000;

	//------------------  MEMBER VARIANT -----------------------//
	@Getter @Setter private boolean					mEnd = true;
	
	//private BaseServer				mBaseServer = null;
	//public SendJobQueue				mSendJobQueue = SendJobQueue.getInstance();
	//public FreeSendJobPool			mFreeSendJobPool =  FreeSendJobPool.getInstance();
	public SendJob_Manager		mSendJob_Mgr = SendJob_Manager.getInstance(); 
	
	public int sendcount;
	//----------------- END OF MEMBER VARIANT -----------------//
	
	//------------------ BASIC FUNCTION  -----------------------//

	public boolean Begin( BaseServer bs) 
	{
		this.start();
		mEnd = false;
		
		sendcount =0;
		
		//mBaseServer = bs;
		
		/*for ( int i = 0 ; i < USERCOUNT ; ++ i ){
			SendJob Job = new SendJob();
			mFreeSendJobPool.put(Job);
		}*/
		
		return true;
	
	}
	
	public void End()
	{
		mEnd = true;
		try {
			this.join();
			
		}catch (Exception e){
			; //Error
		}
	}
	
	public void run(){
		while( !mEnd){
			SendJob Job;
			do{
				
				Job = null;
				try{
					Job = mSendJob_Mgr.popSendJob();
					
				}catch( NoSuchElementException | InterruptedException e1) {
					e1.printStackTrace();
					break;
				}
				
				//sendcount++;

				try {
					if (Job.mSC.isConnected())
						// Send = Job.mSC.write(Job.mBuffer);
						Job.mSC.write(Job.mBuffer);
					else
					{
						// 여기서 리소스는 돌려 줘야 함.
						throw new Exception("Send Error User is not connected");
						//continue;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
				finally{
					mSendJob_Mgr.returnResource(Job);
				}

				// System.out.print("\n SendCount : " + sendcount + "
				// WriteReturn : " + Send + " Sendlen : " + Job.mSize + "\n");
				
				
			}while (Job != null );
		
		}//end while
		
	}

}
