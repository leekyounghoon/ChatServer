import java.nio.ByteBuffer; 
  
public class SC_ALIVE extends Obj { 
	 public int m_Reserved; 
	 public boolean putbuf( ByteBuffer buf){ 
		 buf.putInt( m_Reserved );
		 return true; 
	 }  
	 public boolean getbuf(ByteBuffer buf){ 
		 m_Reserved = buf.getInt();
		 return true; 
	 }  
	 public void clear(){ 
		 m_Reserved = 0;
	 }  
};  
