import java.nio.ByteBuffer;

public abstract class Obj {
	public Obj(){}
	boolean putbuf(ByteBuffer buf){return true;};
	boolean getbuf(ByteBuffer buf){return true;};
}
