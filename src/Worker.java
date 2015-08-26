
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;

import lombok.Getter;
import lombok.Setter;

public class Worker extends Thread {

	//private static final Consumer<? super User> User = null;
	@Getter @Setter private boolean mEnd = true;
	private BaseServer mBaseServer = null;


	//private dword skdf ;
	long mCurrentTime = 0;
	long mServerCheckTime = 0;
	
	// -----------------------------------------------------------------------
	// MEMBER FUNCTION
	// -----------------------------------------------------------------------
  
	public boolean Begin(BaseServer bs) {
		this.start();
		mEnd = false;

		mBaseServer = bs;

		mBaseServer.logger.info("Log Test OK");
		
		
		return true;
	}

	public void End() {
		mEnd = true;

		try {
			this.join();

		} catch (Exception e) {
			; // Error
		}
	}

	public void run() {
		NetJob job;
		while (!mEnd) {
			mCurrentTime = System.currentTimeMillis();
			
			do {
				
				job = null;
				try {
					job = (NetJob) mBaseServer.mJobQueue.pop();
				} catch (NoSuchElementException | InterruptedException e1) {
					e1.printStackTrace();
				}

				if (job == null) {

					try {
						Thread.sleep(0,1);
						break;
					} catch (Exception e) {
						e.printStackTrace();;
					}
				} else {
					switch (job.mState) {
					case NetJob.OP_CONNECT:
						OnConnect(job);
						break;
					case NetJob.OP_DISCONNECT:
						OnDisconnect(job);
						break;
					case NetJob.OP_RECV:

						OnRecv(job);
						break;
					}
				}

				returnNetJob(job);
				
//				// NetJob 占쏙옙占쏙옙
//				job.mBuffer.clear();
//				mBaseServer.mFreeBBufPool.put(job.mBuffer);
//				mBaseServer.mFreeNetJobPool.put(job);

			} while (job != null);

		}// end of while
	}
	
	public void returnNetJob(NetJob job)
	{
		job.mBuffer.clear();
		mBaseServer.mFreeBBufPool.put(job.mBuffer);
		mBaseServer.mFreeNetJobPool.put(job);
	}

	public void OnRecv(NetJob job) {
		//System.out.println(BaseUtil.GetTime() + " Recv - "
		//		+ job.mSC.socket().getInetAddress());

		
		try {
			job.mUser.mSC = job.mSC; 
	
			mBaseServer.mCmdStart = System.currentTimeMillis();
	
			ByteBuffer bb = job.mBuffer;
			
			//print
			/*
			byte[] m_NickName = new byte[100];
			bb.get(m_NickName, 0, bb.limit());
			String str = new String(m_NickName);
			System.out.print(str);
			bb.flip();
			*/
			
			// �씠 遺�遺꾩� �븘�슂 �뾾�쓬.
			//short len = bb.getShort();
			
			
			short msg = bb.getShort();
			int serial = bb.getInt();
			//short msg = bb.getShort();
			//int serial = bb.getInt();
			
			
			// �뿬湲곗뿉 而⑦뀗痢�
			switch (msg) {
			case Protocol.CS_LOGIN:
				H_Msglogin(job);
				break;
			default:
				break;
			}
			
	
			mBaseServer.mCmdEnd = System.currentTimeMillis();
		} catch (Exception e){
			e.printStackTrace();
		}

		//System.out.println(BaseUtil.GetTime() + " Worktime - "
		//		+ job.mSC.socket().getInetAddress() + "Time-"
		//		+ Long.toString(mBaseServer.mCmdEnd - mBaseServer.mCmdStart));

	}

	public void MakePacket_Send(SocketChannel mSC, short PacketNum, Obj object) {
		//SendJob job = mBaseServer.mWriter.mSendJob_Mgr.getSendJob() ;
		SendJob job = null;
		try {
			job = mBaseServer.mWriter.mSendJob_Mgr.getResource();
			if( job == null) 
				throw( new Exception("[MakePacket_Send] get job fail!"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		// buffer loading
		ByteBuffer buf = job.mBuffer;
		buf.putShort((short) 0);
		buf.putShort(PacketNum);
		buf.putInt(0); // serial

		// Context
		object.putbuf(buf);
		short len = (short) (buf.position());
		buf.putShort(0, len);
		buf.flip();

		// make job
		
		
		job.mSC = mSC;
		job.mSize = len;

		if( job.mSC.isConnected() )
			System.out.println("connected");
		
		// this is direct is or not
		mBaseServer.mWriter.mSendJob_Mgr.putSendJob(job);
		
	}
	

	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////// UTIL /////////////////////////////////////////////////////	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/***************************************************************************
	  * XOR �븫�샇�솕 / 蹂듯샇�솕
	  * @param code
	  * @return
	  * @throws CobraException
	  **************************************************************************/
	 public String XOR(String code)
	 {
	  //�븫�샇�솕 �궎
	  byte keyChar[] = {0x01, 0x03, 0x01, 0x05, 0x01, 0x03, 0x01, 0x01};
	  
	  //�븫�샇�솕�븷 ���긽 
	  byte codeChar[] = new byte[code.getBytes().length]; //code�쓽 臾몄옄�뿴 湲몄씠留뚰겮�쓽 諛곗뿴�쓣 留뚮뱺�떎.
	  codeChar = code.getBytes(); //code瑜� Byte�삎�쑝濡� 蹂��솚�븳�떎.
	   
	  //XOR �뿰�궛
	  for(int i=0, j=0; i< code.getBytes().length; i++)
	  {
	   codeChar[i] = (byte) (codeChar[i] ^ keyChar[j]); //code�쓽 �븳臾몄옄�� key�쓽 �븳臾몄옄瑜� ^(XOR)�뿰�궛�쓣 �븳�썑 byte�삎�쑝濡� 蹂��솚�븳�떎.
	   j = (++j < keyChar.length ? j : 0); //j�쓽 媛믪씠 key臾몄옄�뿴�쓽 湲몄씠蹂대떎 而ㅼ쭏寃쎌슦 0�쑝濡� �븘�땺寃쎌슦�뒗 j�쓽 媛믪쓣 媛뽯뒗�떎.
	  }
	 
	  return new String(codeChar) ; //byte諛곗뿴�씤 code瑜� String�쑝濡� 蹂��솚�븯�뿬 諛섑솚�븳�떎.
	 }
	 
	
	 

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////// END of UTIL /////////////////////////////////////////////////////	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// -----------------------------------------------------------------------
	// NET JOB
	// -----------------------------------------------------------------------
	public void OnConnect(NetJob job) {
		//System.out.println(BaseUtil.GetTime() + " connect - "
		//		+ job.mSC.socket().getInetAddress());

	}

	public void OnDisconnect(NetJob job) {

		// User Clear
		job.mUser.clear();
		mBaseServer.mFreeUserPool.put(job.mUser);

		//System.out.println(BaseUtil.GetTime() + " disconnect - "
		//		+ job.mSC.socket().getInetAddress());

	}
	
	/////////////// HANDLER
	public void H_Msglogin( NetJob job)
	{
			 CSLOGIN msg = new CSLOGIN();
			 msg.getbuf(job.mBuffer);
				
							  
			String newName = new String( msg.m_UserInfo.m_NickName );
			
			// Check Login User 
			// Login contents
			User user = job.mUser;
			
			user.mUserData = msg.m_UserInfo;
			
			/*
			user.mUserData.m_UserSN = msg.m_UserInfo.m_UserSN;
			System.arraycopy( msg.m_UserInfo.m_NickName, 0, 
					job.mUser.mUserData.m_NickName, 0,  msg.m_UserInfo.m_NickName.length);
			
			user.mUserData.m_CharInfo.m_CharSN = 0;
			user.mUserData.m_CharInfo.m_CharType = 1;
			*/
			//////////////
			
			SCLOGIN sendmsg = new SCLOGIN();
			sendmsg.m_ResultCode = 1;
			
			
			System.out.println( " UserSN:" + msg.m_UserInfo.m_UserSN + " Name: " + newName );
			
			
			MakePacket_Send(job.mSC, Protocol.SC_LOGIN, sendmsg);
			
			return;
	}
	
	
	
}
