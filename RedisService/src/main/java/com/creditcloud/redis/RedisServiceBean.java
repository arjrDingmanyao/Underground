package com.creditcloud.redis;

import com.creditcloud.redis.api.RedisService;
import com.creditcloud.redis.local.ApplicationBean;
import com.google.gson.Gson;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

@Remote
@Stateless
public class RedisServiceBean
  implements RedisService
{
  private static final Logger log = LoggerFactory.getLogger(RedisServiceBean.class);
  @EJB
  ApplicationBean appBean;
  
  public void put(String key, String value)
  {
    long startTime = System.currentTimeMillis();
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      jedis.set(key, value);
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
    log.info(String.format("redis put time cost %1$dms", new Object[] { Long.valueOf(System.currentTimeMillis() - startTime) }));
  }
  
  public void put(String key, Object object)
  {
    put(key, new Gson().toJson(object));
  }
  
  public void put(String key, byte[] value)
  {
    long startTime = System.currentTimeMillis();
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      jedis.set(key.getBytes(), value);
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
    log.info(String.format("redis put time cost %1$dms", new Object[] { Long.valueOf(System.currentTimeMillis() - startTime) }));
  }
  
  public void put(String key, Serializable value)
  {
    put(key, SerializationUtils.serialize(value));
  }
  
  public String getString(String key)
  {
    long startTime = System.currentTimeMillis();
    

    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    String result;
    try
    {
      result = jedis.get(key);
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
    log.info(String.format("redis put time cost %1$dms", new Object[] { Long.valueOf(System.currentTimeMillis() - startTime) }));
    return result;
  }
  
  public byte[] getBytes(String key)
  {
    long startTime = System.currentTimeMillis();
    

    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    byte[] binary;
    try
    {
      binary = jedis.get(key.getBytes());
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
    log.info(String.format("redis put time cost %1$dms", new Object[] { Long.valueOf(System.currentTimeMillis() - startTime) }));
    return binary;
  }
  
  public Object getSerializable(String key)
  {
    byte[] binary = getBytes(key);
    if ((binary == null) || (binary.length == 0)) {
      return null;
    }
    return SerializationUtils.deserialize(binary);
  }
  
  public <T> T get(String key, Class<T> classOfT)
  {
    String json = getString(key);
    return json == null ? null : new Gson().fromJson(json, classOfT);
  }
  
  public <T> T get(String key, Type typeOfT)
  {
    String json = getString(key);
    return (T) (json == null ? null : new Gson().fromJson(json, typeOfT));
  }
  
  public boolean exist(String key)
  {
    long startTime = System.currentTimeMillis();
    

    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    boolean result;
    try
    {
      result = jedis.exists(key).booleanValue();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
    log.info(String.format("redis put time cost %1$dms", new Object[] { Long.valueOf(System.currentTimeMillis() - startTime) }));
    return result;
  }
  
  public long increment(String key)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.incr(key).longValue();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public long incrementBy(String key, long value)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.incrBy(key, value).longValue();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public long decrement(String key)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.decr(key).longValue();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public long decrementBy(String key, long value)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.decrBy(key, value).longValue();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public void subscribe(JedisPubSub listener, String... keys)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      jedis.subscribe(listener, keys);
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public void publish(String channel, String message)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      jedis.publish(channel, message);
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public void expire(String key, int second)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      jedis.expire(key, second);
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public String getSet(String key, String value)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.getSet(key, value);
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public long rpush(String key, String... values)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    long count;
    try
    {
      count = jedis.rpush(key, values).longValue();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
    return count;
  }
  
  public long lpush(String key, String... values)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    long count;
    try
    {
      count = jedis.lpush(key, values).longValue();
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
    return count;
  }
  
  public List<String> range(String key, long start, long end)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    List<String> list;
    try
    {
      list = jedis.lrange(key, start, end);
    }
    catch (Throwable localThrowable1)
    {
      localThrowable2 = localThrowable1;throw localThrowable1;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
    return list;
  }
  
  public long count(String key)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.llen(key).longValue();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public long remove(String key, long count, String value)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.lrem(key, count, key).longValue();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public long delete(String... keys)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.del(keys).longValue();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
  
  public long ttl(String key)
  {
    Jedis jedis = this.appBean.getResource();Throwable localThrowable2 = null;
    try
    {
      return jedis.ttl(key).longValue();
    }
    catch (Throwable localThrowable3)
    {
      localThrowable2 = localThrowable3;throw localThrowable3;
    }
    finally
    {
      if (jedis != null) {
        if (localThrowable2 != null) {
          try
          {
            jedis.close();
          }
          catch (Throwable x2)
          {
            localThrowable2.addSuppressed(x2);
          }
        } else {
          jedis.close();
        }
      }
    }
  }
}
