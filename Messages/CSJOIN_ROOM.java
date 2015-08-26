import java.nio.ByteBuffer; 
    
public class CSJOIN_ROOM extends Obj { 
	 public int m_RoomType; 
	 public int m_RoomNumber; 
	 public boolean putbuf( ByteBuffer buf){ 
		 buf.putInt( m_RoomType );
		 buf.putInt( m_RoomNumber );
		 return true; 
	 }  
	 public boolean getbuf(ByteBuffer buf){ 
		 m_RoomType = buf.getInt();
		 m_RoomNumber = buf.getInt();
		 return true; 
	 }  
	 public void clear(){ 
		 m_RoomType = 0;
		 m_RoomNumber = 0;
	 }  
};  
