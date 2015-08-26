import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

public class SendJob {

	public static final int BUFFERSIZE = 4096;
	
	public SocketChannel mSC = null;
	public short			 mSize = 0;
	public ByteBuffer    mBuffer = null;
	
	SendJob()
	{
		mBuffer = ByteBuffer.allocateDirect(BUFFERSIZE);
		mBuffer.order(ByteOrder.LITTLE_ENDIAN);
	}
	
	public void clear()
	{
		mSC = null;
		mSize = 0;
		mBuffer.clear();
	}
	
}
