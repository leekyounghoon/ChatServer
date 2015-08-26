import java.nio.ByteBuffer; 
      
public class SCMAKE_ROOM extends Obj { 
	 public short m_ResultCode; 
	 public int m_RoomType; 
	 public int m_RoomNumber; 
	 public boolean putbuf( ByteBuffer buf){ 
		 buf.putShort( m_ResultCode );
		 buf.putInt( m_RoomType );
		 buf.putInt( m_RoomNumber );
		 return true; 
	 }  
	 public boolean getbuf(ByteBuffer buf){ 
		 m_ResultCode = buf.getShort();
		 m_RoomType = buf.getInt();
		 m_RoomNumber = buf.getInt();
		 return true; 
	 }  
	 public void clear(){ 
		 m_ResultCode = 0;
		 m_RoomType = 0;
		 m_RoomNumber = 0;
	 }  
};  
