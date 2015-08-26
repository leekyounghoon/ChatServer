import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisHelper {
	
	
	protected static final String REDIS_HOST = "192.168.10.90";
	protected static final int REDIS_PORT = 6379;
	//private final Set<Jedis> connectionList = new HashSet<Jedis>();
	@Getter @Setter
	public JedisPool jedisPool;
	
	@Getter @Setter
	public Jedis jedis;
	
	private static JedisHelper instance = new JedisHelper();
	
	public JedisHelper(){
		setJedisPool( new JedisPool( new JedisPoolConfig(), REDIS_HOST, REDIS_PORT) );
		setJedis(jedisPool.getResource()); 
	}
	
	public void destoryPool(){
		jedisPool.destroy();
	}
	
	public synchronized static JedisHelper getInstance(){
		if( instance == null)
		{
			 instance = new JedisHelper();
		}
		return instance;
	}
	
	final public Jedis getConnection()
	{
		Jedis jedis = jedisPool.getResource();
		return jedis;
	}
	
	final public void returnResource( Jedis jedis){
		this.jedisPool.returnResourceObject(jedis);
	}
}
