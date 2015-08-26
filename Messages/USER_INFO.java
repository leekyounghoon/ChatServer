import java.nio.ByteBuffer; 
      
public class USER_INFO extends Obj { 
	 public long m_UserSN; 
	 public byte[] m_NickName = new byte[16]; 
	 public byte m_Password; 
	 public boolean putbuf( ByteBuffer buf){ 
		 buf.putLong( m_UserSN );
		 buf.put( m_NickName, 0, 16);
		 buf.put( m_Password );
		 return true; 
	 }  
	 public boolean getbuf(ByteBuffer buf){ 
		 m_UserSN = buf.getLong();
		 buf.get( m_NickName, 0, 16 );
		 buf.get();
		 return true; 
	 }  
	 public void clear(){ 
		 m_UserSN = 0;
		 m_Password = 0;
	 }  
};  
