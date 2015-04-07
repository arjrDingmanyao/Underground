package com.creditcloud.redis.local;

import com.creditcloud.client.api.ClientService;
import com.creditcloud.common.bean.AbstractClientApplicationBean;
import com.creditcloud.config.ClientConfig;
import com.creditcloud.config.RedisConfig;
import com.creditcloud.config.RedisPoolConfig;
import com.creditcloud.config.SentinelConfig;
import com.creditcloud.config.SentinelServer;
import com.creditcloud.config.SentinelServerPool;
import com.creditcloud.config.api.ConfigManager;
import com.creditcloud.model.enums.misc.CacheType;
import com.creditcloud.model.exception.ClientCodeNotMatchException;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

@Singleton
@Startup
@LocalBean
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
public class ApplicationBean
  extends AbstractClientApplicationBean
{
  private static final Logger log = LoggerFactory.getLogger(ApplicationBean.class);
  @EJB
  ConfigManager configManager;
  @EJB
  ClientService clientService;
  JedisSentinelPool commonSentinelPool;
  JedisSentinelPool loanRelateSentinelPool;
  JedisPool redisPool;
  
  @PostConstruct
  void init()
  {
    log.info("ApplicationBean in RedisService initializing");
    ClientConfig clientConfig = this.configManager.getClientConfig();
    this.client = this.clientService.getClient(clientConfig.getCode());
    if (clientConfig.getClientFeatures().isEnableServiceRedis())
    {
      RedisConfig redisConfig = this.configManager.getRedisConfig();
      RedisPoolConfig pool = redisConfig.getPoolConfig();
      JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
      jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
      jedisPoolConfig.setMaxTotal(pool.getMaxActive());
      jedisPoolConfig.setMaxWaitMillis(pool.getMaxWaitMillis());
      this.redisPool = new JedisPool(jedisPoolConfig, redisConfig.getHost(), redisConfig.getPort());
      log.info("redis pool in RedisService initialized.[clientCode={}]", getClientCode());
    }
    if (clientConfig.getClientFeatures().isEnableServiceSentinel())
    {
      SentinelConfig sentinelConfig = this.configManager.getSentinelConfig();
      this.commonSentinelPool = initPool(sentinelConfig.getCommon());
      this.loanRelateSentinelPool = initPool(sentinelConfig.getLoan());
      log.info("sentinel pool in RedisService initialized.[clientCode={}]", getClientCode());
    }
    log.info("ApplicationBean in RedisService initialized.[clientCode={}]", getClientCode());
  }
  
  @PreDestroy
  void destroy()
  {
    if (this.redisPool != null) {
      this.redisPool.destroy();
    }
    if (this.commonSentinelPool != null) {
      this.commonSentinelPool.destroy();
    }
    if (this.loanRelateSentinelPool != null) {
      this.loanRelateSentinelPool.destroy();
    }
  }
  
  public void checkClientCode(String clientCode)
  {
    if (!isValid(clientCode))
    {
      String cause = String.format("The incoming clientcode do not match the local client.[incoming=%s][local=%s]", new Object[] { clientCode, getClientCode() });
      log.warn(cause);
      throw new ClientCodeNotMatchException(cause);
    }
  }
  
  public ClientConfig getClientConfig()
  {
    return this.configManager.getClientConfig();
  }
  
  public Jedis getResource()
  {
    return this.redisPool.getResource();
  }
  
  public Jedis getResource(CacheType type)
  {
    if (type == CacheType.COMMON) {
      return this.commonSentinelPool.getResource();
    }
    if (type == CacheType.LOANRELATE) {
      return this.loanRelateSentinelPool.getResource();
    }
    return null;
  }
  
  private JedisSentinelPool initPool(SentinelServerPool config)
  {
    RedisPoolConfig pool = config.getPoolConfig();
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxIdle(pool.getMaxIdle());
    jedisPoolConfig.setMaxTotal(pool.getMaxActive());
    jedisPoolConfig.setMaxWaitMillis(pool.getMaxWaitMillis());
    
    Set<String> sentinels = new HashSet();
    for (SentinelServer server : config.getServer()) {
      sentinels.add(new HostAndPort(server.getHost(), server.getPort()).toString());
    }
    log.info("sentinels={}", sentinels);
    return new JedisSentinelPool(config.getMasterName(), sentinels, jedisPoolConfig);
  }
}
