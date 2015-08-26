import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class NetJob{
	public static final byte OP_CONNECT = 1;
	public static final byte OP_DISCONNECT = 2;
	public static final byte OP_RECV = 3;
	
	public SocketChannel 	mSC=null;
	//public byte[] 			mData=null;
	public byte				mState	= 0;
	public User				mUser	= null;
	public ByteBuffer		mBuffer = null;
	
}