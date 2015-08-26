
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import lombok.Getter;
import lombok.Setter;


public class Acceptor extends Thread {

	//------------------  MEMBER VARIANT -----------------------//
	@Getter @Setter private boolean	mEnd = false;
			
	private BaseServer				mBaseServer = null;
	
	private String					mIP = null;
	private int						mPort = 0;
	
	private Selector				mSelector = null;
	private ServerSocketChannel 	mServerSocketChannel = null;
	private ServerSocket			mServerSocket = null;
	
	
	//------------------ BASIC FUNCTION  -----------------------//
	public boolean Begin(String ip, int port, BaseServer bs )
	{
				mIP = ip;
				mPort = port;
				mBaseServer = bs;
				
				try{
					mSelector = Selector.open(); 
					mServerSocketChannel = ServerSocketChannel.open(); 
					mServerSocketChannel.configureBlocking(false); 
					mServerSocket = mServerSocketChannel.socket();  
					//mServerSocket.setReuseAddress(true); //edit leekh13
					mServerSocket.bind(new InetSocketAddress(mIP, mPort)); // 占쌍소울옙占쏙옙트 占쏙옙占싸듸옙 占쌔쇽옙 占쌔븝옙 占싹뤄옙
					mServerSocketChannel.register(mSelector,  SelectionKey.OP_ACCEPT ); //ACCEPT占쏙옙占쏙옙 키 占쏙옙占�
				}catch( IOException e){
					e.printStackTrace();
					return false;
				}
			
				this.start();
				return true;
	}
			
			
	public void End()
	{
				mEnd = true;
				
				this.mSelector.wakeup();
				try {
					this.join();
				}catch (Exception e){
					e.printStackTrace();
				}
	}
	
	@Override
	public void run(){
				
		try{
			while ( !mEnd){  
				// 블러킹이든 비 블러킹 이든 상관 없다. 신호오면 처리
				// 하지만 스레드 종료 할때 문제 있으므로 현제는 비블러킹 처리
				mSelector.select(1);

				Iterator<SelectionKey> it = mSelector.selectedKeys().iterator();

				while (it.hasNext()) {

					SelectionKey key = (SelectionKey) it.next();
					it.remove();

					if (key.isAcceptable()) {
						SocketChannel sc = ((ServerSocketChannel) key.channel()).accept();
						mBaseServer.EventAccept(sc);
					}

				} // end of while
			}// end of while
					
		}catch( Exception e){
					e.printStackTrace();//error
		}
			
		try {
			mSelector.close();
			mServerSocketChannel.close();
		} catch (Exception e) {
			e.printStackTrace();// Error
		}
		
	} // end of run
}
