import java.text.SimpleDateFormat;
import java.util.Date;

import redis.clients.jedis.Jedis;

public class LogWriter {
	
	private static final String KEY_SERVER_LOG = "server:log:list";
	private SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMdd HH:mm:ss SSS ");
	JedisHelper helper;
	
	public LogWriter ( JedisHelper helper){
		this.helper = helper;
	}

	// 레디스에 로그 기록 
	// @param log 저장할 로그 문자열
	// @return 저장된 후의 레이스에 저장된 로그 문자열의 길이
	
	public Long log( String log ){
		Jedis jedis = this.helper.getConnection();
		Long rtn = jedis.lpush( KEY_SERVER_LOG, sdf.format( new Date()) + log + "\n");
		helper.returnResource(jedis);
		return rtn;
	}
		
}
