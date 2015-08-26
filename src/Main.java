import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.log4j.PropertyConfigurator;

import com.google.gson.Gson;

public class Main {
	
	static BaseServer bs;
	
	public static void main(String[] args) {

		// TODO Auto-generated method stub
		PropertyConfigurator.configure("log4J.properties");
		
		
		//JedisHelper jedisHelper = new JedisHelper();
		
		
		// GSON TEST
		
		/*Gson  gson = new Gson();
		USER_INFO model = new USER_INFO();
		model.m_UserSN = 100;
		model.m_CharInfo.m_CharSN = 10;
		
		System.arraycopy( "leekh".getBytes() , 0, model.m_NickName, 0, "leekh".length() );
		
		String json = gson.toJson(model);
		
		System.out.println(json);
		
		
		model.clear();
		//System.out.println( "clear SN:" + model.m_CharSN + "Type:" + model.m_CharType );
		System.out.println( "clear SN:" + model.m_UserSN + "name:" + model.m_NickName );
		
		
		model = new Gson().fromJson(json, USER_INFO.class );
		
		System.out.println( "SN:" + model.m_UserSN + "Type:" + model.m_NickName );
		
		
		String name = new String(model.m_NickName);*/
		
		// end of GSON TEST
		
		
		bs = new BaseServer();
		
		bs.Begin();
		
		cmd();
			
		bs.End();
		
	}
	
	// cmd 입력 테스트
	public static void cmd() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		ArrayList<String> cmdList = new ArrayList<String>();
		String cmd = null;

		System.out.println("MainConsol Start!!");

		while (true) {
			try {

				cmdList.clear();
				cmd = br.readLine();

				StringTokenizer st = new StringTokenizer(cmd, " ");
				while (st.hasMoreTokens()) {
					cmdList.add(st.nextToken());
					// String token = st.nextToken();
					// System.out.println(token);
				}

				String cmd0 = cmdList.get(0);

				if (cmd0.compareTo("/q") == 0 || cmd0.compareTo("/Q") == 0) {

					break;
				}

				if (cmd0.equals("state")) {
					if (cmdList.size() <= 1)
						bs.PrintState();
					else {
						String arg;
						arg = cmdList.get(1);
						if (arg.equals("0"))
							bs.setMPrintstate(false);
						else
							bs.setMPrintstate(true);
					}

				}

				System.out.println(cmd);
			} catch (Exception e) {
				;
			}
		}
	}
	
	

}

