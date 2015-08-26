import java.nio.ByteBuffer; 
  
public class CSLOGIN extends Obj { 
	 public USER_INFO m_UserInfo = new USER_INFO(); 
	 public boolean putbuf( ByteBuffer buf){ 
		 m_UserInfo.putbuf( buf );
		 return true; 
	 }  
	 public boolean getbuf(ByteBuffer buf){ 
		 m_UserInfo.getbuf( buf );
		 return true; 
	 }  
	 public void clear(){ 
		 m_UserInfo.clear();
	 }  
};  
