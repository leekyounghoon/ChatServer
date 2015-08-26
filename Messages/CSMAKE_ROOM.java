import java.nio.ByteBuffer; 
  
public class CSMAKE_ROOM extends Obj { 
	 public int m_RoomType; 
	 public boolean putbuf( ByteBuffer buf){ 
		 buf.putInt( m_RoomType );
		 return true; 
	 }  
	 public boolean getbuf(ByteBuffer buf){ 
		 m_RoomType = buf.getInt();
		 return true; 
	 }  
	 public void clear(){ 
		 m_RoomType = 0;
	 }  
};  
