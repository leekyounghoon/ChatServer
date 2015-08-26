
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class User {

	public SocketChannel  mSC = null;
	// 泥섏쓬 �뜲�씠�꽣瑜� 諛쏅뒗 踰꾪띁( 嫄대뱶由ъ� 留먭쾬)
	public ByteBuffer	  mBuffer = null;
	// timeout 
	public long			  mCurrentTick = 0;
/////////////////// DATA /////////////////////////////
	public USER_INFO 	  mUserData = new USER_INFO();
	
////////////////// FUNCTION //////////////////////////	
	public void clear(){
		mSC = null;
		mBuffer.clear();
		mCurrentTick = 0;
		mUserData.clear();
	}
}
