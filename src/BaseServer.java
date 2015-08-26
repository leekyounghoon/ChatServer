import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.apache.log4j.Logger;

import lombok.Getter;
import lombok.Setter;

public class BaseServer extends Thread {
	

	public static final int USERCOUNT = 1000;
	public static final int BUFFERSIZE = 4096;
	public static final int ROOMCOUNT = 1000;

	static final short CS_ALIVE = 1;

	// -----------------------------------------------------------------------
	// MEMBER VAR
	// -----------------------------------------------------------------------
	@Getter @Setter private boolean mEnd = true;

	
	// 유저 풀
	public FreeUserPool mFreeUserPool = FreeUserPool.getInstance(); 
	// 버퍼 룰 ( 유저가 접속 할때 한번 쓰고 접속을 끊을 때도 한번 또 쓴다)
	public FreeBBufPool mFreeBBufPool = FreeBBufPool.getInstance(); 
	// 유저와 버퍼를 하나 얻어와서 netjob 풀에서 하나 얻어와서 같이 쓴다.
	public FreeNetJobPool mFreeNetJobPool = FreeNetJobPool.getInstance(); 
	
	// Work 에서 NetJob을 받기 위해 쓴다.
	public JobQueue mJobQueue = JobQueue.getInstance();
		

	private Acceptor mAcceptor = new Acceptor();
	private Reader mReader = new Reader();
	//private Reader mReader1 = new Reader();
	public Writer mWriter = new Writer();
	private Worker mWorker1 = new Worker();


	private HashMap<SocketChannel, User> mSocketChannelMap = 
			new HashMap<SocketChannel, User>();
	//private Map<SocketChannel, User> mSocketChannelMap = Collections
	//		.synchronizedMap(new HashMap<SocketChannel, User>());

	//public ClientConnection mClient = new ClientConnection();
	
	private SubWorker mSubWorker = new SubWorker();
	
	// -----------------------------------------------------------------------
	// MEMBER VAR (public)
	// -----------------------------------------------------------------------

	public long mCmdStart = 0;
	public long mCmdEnd = 0;

	@Setter @Getter private static String mIP;
	@Setter @Getter private static int mPort;

	@Getter @Setter private static short mVersion_Miner;
	@Getter @Setter private static short mVersion_Major;

	@Getter @Setter private static boolean mPrintstate;
	
	public static boolean mbollogin = true;
	
	//private Object		mPerJobCntSync;
	private int			mPerJobCnt = 0;
	// -----------------------------------------------------------------------
	// BASIC MEMBER FUNCTION
	// -----------------------------------------------------------------------
	// ip = hostServerip - config

	Logger logger = Logger.getLogger(BaseServer.class);
	
	static LogWriter Jedis_logger = new LogWriter(new JedisHelper());

	public boolean Begin() {

		
		mEnd = false;
		setMPrintstate(false);

		
		//Jedis_logger.log("BASE SERVER BEGIN");
		
		
		try {
			// ////DATA BASE SETTING
			Properties p = new Properties();
			// read .ini file
			p.load(new FileInputStream("Config.ini"));

			setMIP( p.getProperty("HostServerIP") );
			setMPort( Integer.parseInt(p.getProperty("HostServerPort")) );
					
			setMVersion_Miner( Short.parseShort(p.getProperty("VERSION_MAJOR")) );
			setMVersion_Major( Short.parseShort(p.getProperty("VERSION_MINER")) );

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create Byte Buffer
		for (int i = 0; i < USERCOUNT * 2; ++i) {
			// /User Setting
			User objuser = new User();
			objuser.mBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
			objuser.mBuffer.order(ByteOrder.LITTLE_ENDIAN);
			
			mFreeUserPool.put(objuser);

			// //////////////////////// Net Job
			ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFERSIZE);

			buffer.order(ByteOrder.LITTLE_ENDIAN);

			mFreeBBufPool.put(buffer);

			NetJob job = new NetJob();
			mFreeNetJobPool.put(job);

		}
		
	
			
		
		mWriter.Begin(this);
		mWorker1.Begin(this);
		mReader.Begin(this);
		//mReader1.Begin(this);
		mAcceptor.Begin( getMIP() , getMPort(), this);
		mSubWorker.Begin( this);
		
		this.start();

		return true;
	}

	public void End() {
		try {
			mEnd = true;
			
			//다른 스레드들 종료 신호
			mSubWorker.setMEnd(true);
			mAcceptor.setMEnd(true);
			mReader.setMEnd(true);
			mWorker1.setMEnd(true);
			mWriter.setMEnd(true);
			
			// 대기중일수 있는 스레드 깨움
			//mJobQueue.getMonitor().notify();
			//mWriter.mSendJobQueue.notify();
			
			// 스레드들이 하던 일을 잠시동안 기다림
			//mSubWorker.join(3000);
			//mAcceptor.join(3000);
			//mReader.join(3000);
			//mWorker1.join(1000);
			//mWriter.join(1000);
			
			// 혹시 중단 중인 스레드가 있으면 작업 중단 ( worker writer )
			if( mSubWorker.isAlive() ){
				mSubWorker.interrupt();
			}
			if( mAcceptor.isAlive() ){
				mAcceptor.interrupt();
			}
			if( mReader.isAlive() ){
				mReader.interrupt();
			}
			if( mWorker1.isAlive() ){
				mWorker1.interrupt();
			}
			if( mWriter.isAlive() ){
				mWriter.interrupt();
			}
			
			System.out.println("All Thread Die of BaseServer");
			
		} catch (Exception e) {
		   e.printStackTrace(); // Error
		}
	}

	@Override
	public void run() {
		
		while (!mEnd) {
			try {
				
				// you have to wait for other thread.
				Thread.sleep(1000);  
				
			} catch (Exception e) {
				e.printStackTrace();;
			}
		}    
	}
	
	
	 public void PrintState()
	 {
		String str = new String();
		str += "scCnt:" + mSocketChannelMap.size();
		str += "/" + mFreeUserPool.size();
		str += " BufPool:" + mFreeBBufPool.size();
		str += " NetJob:" + mJobQueue.size();
		str += "/" + mFreeNetJobPool.size();
		
		str += " PerJobCnt" + mPerJobCnt;
		 
		 mPerJobCnt = 0;
		 
		 System.out.println(str);
	 }
	

	// ///////////////////////////////// Event Handler //////////////////
	// Accept, call by Acceptor
	public void EventAccept(SocketChannel sc) {
		
		boolean bRegist = mReader.Regist(sc);
		if( bRegist == false)
			return;
		
		// AllocateBuffer
		User objuser = null;

		try {
			objuser = (User) mFreeUserPool.pop();
			
			if (objuser == null) {
				sc.close();
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}

		//long worktime = System.currentTimeMillis();
		// System.out.println("[EventAccept] get objuser !!" +
		// Long.toString(worktime ));

		synchronized (mSocketChannelMap) {

			mSocketChannelMap.put(sc, objuser);
			//objuser.mSC = sc;
			objuser.mCurrentTick = System.currentTimeMillis();
		}
		
	}

	// Disconnect, call by Reader
	public void EventDisconnect(SocketChannel sc) {
		User objuser = null;
		synchronized (mSocketChannelMap) {
			if (mSocketChannelMap.containsKey(sc) == false) {
				// System.out.print("[EventDisconnect] mSocketChannelMap.containsKey(sc)==false");
				return;
			} else {
				objuser = mSocketChannelMap.get(sc);

				ByteBuffer buffer = null;
				try {
					buffer = (ByteBuffer) mFreeBBufPool.pop();

				} catch (NoSuchElementException | InterruptedException e1) {
					e1.printStackTrace();
					return;
				}

				buffer.put(objuser.mBuffer);
				buffer.flip();

				NetJob job = null;
				try {
					job = (NetJob) mFreeNetJobPool.pop();
				} catch (NoSuchElementException | InterruptedException e1) {
					e1.printStackTrace();
					return;
				}

				job.mSC = sc;
				job.mState = NetJob.OP_DISCONNECT;
				job.mUser = objuser;
				job.mBuffer = buffer;

				mSocketChannelMap.remove(sc);

				try {
					sc.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

				mJobQueue.put(job);

			}
		}
	}

	
	

	// Read, call by Reader
	public void EventRead(SocketChannel sc) {
		// read user buffer
		User objuser = null;
		synchronized (mSocketChannelMap) {
			objuser = mSocketChannelMap.get(sc);
			
		}
		
		synchronized (sc) {
			// read
			try {
							
				int r = sc.read(objuser.mBuffer);
				
				if (r == -1) {
					EventDisconnect(sc);
					return;
				} else {
					objuser.mCurrentTick = System.currentTimeMillis();
					// System.out.print("\n[Readbyte]" + r +
					// sc.socket().getInetAddress() + "  " );
				}
			} catch (Exception e) {
				// e.printStackTrace(); sometime user socket disconnect from server
				// when already to login to user.
				//e.printStackTrace();
				EventDisconnect(sc);
				return;
			}
	
			chkPacketHeader(sc, objuser);
		}
	}
	
	
	//print
	//byte[] Temp = new byte[100];
	//objuser.mBuffer.get(Temp, 0, objuser.mBuffer.limit());
	//String strTemp = new String(Temp);
	//System.out.print(strTemp);
	//objuser.mBuffer.flip();
	// cut off header
	public void chkPacketHeader(SocketChannel sc, User objuser) {
 
		short len = 0;
		int sliceLimit = 0;
		
		objuser.mBuffer.flip(); // current position zero
		
		NetJob job = null;
		ByteBuffer buffer = null;
	
		//  길이 헤더 비교 
		while (objuser.mBuffer.remaining() > 2) {
			
			// 버퍼를 자르기 위한 준비
			len = objuser.mBuffer.getShort();
			
			if( len <= 0)
			{
				break;
			}
			
			if (objuser.mBuffer.remaining() < (len-2) || (len-2) > BUFFERSIZE ) {
				// 여기서 Warning 표시
				break;
			}
			
			////////////// 복사할 data 준비 //////////////////////
			try {
				buffer = (ByteBuffer) mFreeBBufPool.pop();
			} catch (NoSuchElementException | InterruptedException e1) {
				e1.printStackTrace();
				return;
			}
			buffer.clear();
			
			//objuser.mBuffer.order(ByteOrder.LITTLE_ENDIAN);
			// 준비 끝 버퍼 자르기 시작
						
			int oldlimit = objuser.mBuffer.limit();
			sliceLimit += len;
			objuser.mBuffer.limit(sliceLimit);
			
			buffer = objuser.mBuffer.slice();
			buffer.order(ByteOrder.LITTLE_ENDIAN); // slice 한 다음에는 byteorder이 바뀜
			
			//원상 복귀(다음 패킷 읽을 준비)
			objuser.mBuffer.limit(oldlimit);
			objuser.mBuffer.position( sliceLimit );
			
			try {
				job = (NetJob) mFreeNetJobPool.pop();
			} catch (NoSuchElementException | InterruptedException e1) {
				e1.printStackTrace();
				return;
			}
			job.mSC = sc;
			job.mState = NetJob.OP_RECV;
			job.mUser = objuser;
			job.mBuffer = buffer;
			
			
			mJobQueue.put(job);
			
			mPerJobCnt++;
			
		}
		objuser.mBuffer.compact();
		
	}


	
	public void TimeoutSocket( ){
		User objuser = null;
		java.util.Iterator<SocketChannel> it = null;
		//Set keySet = mSocketChannelMap.keySet();

		
		List<SocketChannel> mSocketChannelList = new ArrayList<SocketChannel>();
		
		
		
		synchronized(mSocketChannelMap) {

			try{	
					it = mSocketChannelMap.keySet().iterator();
					//it = keySet.iterator();
				
					while( it.hasNext() )
					{
							SocketChannel sc = it.next();
							
							objuser = mSocketChannelMap.get(sc);
							if( objuser == null)
							{
								break;
							}
							else // timeout
							{
								long CurrentTick = System.currentTimeMillis();
								if( CurrentTick - objuser.mCurrentTick > 1000 * 10 )
								{
									
									try{
											mSocketChannelList.add(sc);
																				
										} catch (Exception e) {
											
											e.printStackTrace();
										}
									
									//BEFORE
									/*
									try{
											mReader.UnRegist(sc);
											EventDisconnect(sc);
											sc.socket().close();
											//objuser.mSC.socket().close();
									} catch (Exception e) {
										
										e.printStackTrace();
									}
									*/	
							   }
								
							}
							
					}
					
					// 여기서 작성된 리스트가 있으면 지운다.
					try{
						int Size =  mSocketChannelList.size();
						for( int Count = 0 ; Count < Size ; Count++ )
						{
							SocketChannel sc = mSocketChannelList.get(Count);
							if( sc != null)
							{
									try{
										mReader.UnRegist(sc);
										EventDisconnect(sc);
										sc.socket().close();

									} catch (Exception e) {
											
											e.printStackTrace();
									}
							}
							
						}
					
					}
					catch( Exception e) {
						e.printStackTrace();
					}
					
				
			}catch (Exception e){
					e.printStackTrace();
			}
		}
	}
	
	

}
