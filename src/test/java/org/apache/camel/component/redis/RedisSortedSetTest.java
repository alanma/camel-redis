package org.apache.camel.component.redis;

import java.util.HashSet;
import java.util.Set;

import org.apache.camel.impl.JndiRegistry;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RedisSortedSetTest extends RedisTestSupport {
    private RedisTemplate redisTemplate;
    private ZSetOperations zSetOperations;

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);

        JndiRegistry registry = super.createRegistry();
        registry.bind("redisTemplate", redisTemplate);
        return registry;
    }

    @Before
    public void setUp() throws Exception {
        redisTemplate = mock(RedisTemplate.class);
        zSetOperations = mock(ZSetOperations.class);
        super.setUp();
    }

    @Test
    public void shouldExecuteZADD() {
        when(zSetOperations.add(anyString(), anyObject(), anyDouble())).thenReturn(false);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZADD",
                RedisConstants.KEY, "key",
                RedisConstants.VALUE, "value",
                RedisConstants.SCORE, 1.0);

        verify(zSetOperations).add("key", "value", 1.0);
        assertEquals(false, result);

    }

    @Test
    public void shouldExecuteZCARD() {
        when(zSetOperations.size(anyString())).thenReturn(2L);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZCARD",
                RedisConstants.KEY, "key");

        verify(zSetOperations).size("key");
        assertEquals(2L, result);
    }

    @Test
    public void shouldExecuteZCOUNT() {
        when(zSetOperations.count(anyString(), anyDouble(), anyDouble())).thenReturn(3L);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZCOUNT",
                RedisConstants.KEY, "key",
                RedisConstants.MIN, 1.0,
                RedisConstants.MAX, 2.0);

        verify(zSetOperations).count("key", 1.0, 2.0);
        assertEquals(3L, result);
    }

    @Test
    public void shouldExecuteZINCRBY() {
        when(zSetOperations.incrementScore(anyString(), anyString(), anyDouble())).thenReturn(3.0);
        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZINCRBY",
                RedisConstants.KEY, "key",
                RedisConstants.VALUE, "value",
                RedisConstants.INCREMENT, 2.0);

        verify(zSetOperations).incrementScore("key", "value", 2.0);
        assertEquals(3.0, result);

    }

    @Test
    public void shouldExecuteZINTERSTORE() {
        Set<String> keys = new HashSet<String>();
        keys.add("key2");
        keys.add("key3");
        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZINTERSTORE",
                RedisConstants.KEY, "key",
                RedisConstants.DESTINATION, "destination",
                RedisConstants.KEYS, keys);

        verify(zSetOperations).intersectAndStore("key", keys, "destination");
    }

    @Test
    public void shouldExecuteZRANGE() {
        Set<String> keys = new HashSet<String>();
        keys.add("key2");
        keys.add("key3");
        when(zSetOperations.range(anyString(), anyLong(), anyLong())).thenReturn(keys);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZRANGE",
                RedisConstants.KEY, "key",
                RedisConstants.START, 1,
                RedisConstants.END, 3);

        verify(zSetOperations).range("key", 1, 3);
        assertEquals(keys, result);
    }


    @Test
    public void shouldExecuteZRANGEWithScores() {
        when(zSetOperations.rangeWithScores(anyString(), anyLong(), anyLong())).thenReturn(null);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZRANGE",
                RedisConstants.KEY, "key",
                RedisConstants.WITHSCORE, true,
                RedisConstants.START, 1,
                RedisConstants.END, 3);

        verify(zSetOperations).rangeWithScores("key", 1, 3);
        assertEquals(null, result);
    }


    @Test
    public void shouldExecuteZRANGEBYSCORE() {
        Set<String> keys = new HashSet<String>();
        keys.add("key2");
        keys.add("key3");
        when(zSetOperations.rangeByScore(anyString(), anyDouble(), anyDouble())).thenReturn(keys);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZRANGEBYSCORE",
                RedisConstants.KEY, "key",
                RedisConstants.MIN, 1.0,
                RedisConstants.MAX, 2.0);

        verify(zSetOperations).rangeByScore("key", 1.0, 2.0);
        assertEquals(keys, result);
    }


    @Test
    public void shouldExecuteZRANK() {
        when(zSetOperations.rank(anyString(), anyString())).thenReturn(1L);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZRANK",
                RedisConstants.KEY, "key",
                RedisConstants.VALUE, "value");

        verify(zSetOperations).rank("key", "value");
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteZREM() {
        when(zSetOperations.remove(anyString(), anyString())).thenReturn(true);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZREM",
                RedisConstants.KEY, "key",
                RedisConstants.VALUE, "value");

        verify(zSetOperations).remove("key", "value");
        assertEquals(true, result);
    }


    @Test
    public void shouldExecuteZREMRANGEBYRANK() {
        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZREMRANGEBYRANK",
                RedisConstants.KEY, "key",
                RedisConstants.START, 1,
                RedisConstants.END, 2);

        verify(zSetOperations).removeRange("key", 1, 2);
    }

    @Test
    public void shouldExecuteZREMRANGEBYSCORE() {
        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZREMRANGEBYSCORE",
                RedisConstants.KEY, "key",
                RedisConstants.START, 1,
                RedisConstants.END, 2);

        verify(zSetOperations).removeRangeByScore("key", 1.0, 2.0);
    }


    @Test
    public void shouldExecuteZREVRANGE() {
        Set<String> keys = new HashSet<String>();
        keys.add("key2");
        keys.add("key3");
        when(zSetOperations.reverseRange(anyString(), anyLong(), anyLong())).thenReturn(keys);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZREVRANGE",
                RedisConstants.KEY, "key",
                RedisConstants.START, 1,
                RedisConstants.END, 3);

        verify(zSetOperations).reverseRange("key", 1, 3);
        assertEquals(keys, result);
    }


    @Test
    public void shouldExecuteZREVRANGEWithScores() {
        when(zSetOperations.reverseRangeWithScores(anyString(), anyLong(), anyLong())).thenReturn(null);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZREVRANGE",
                RedisConstants.KEY, "key",
                RedisConstants.WITHSCORE, true,
                RedisConstants.START, 1,
                RedisConstants.END, 3);

        verify(zSetOperations).reverseRangeWithScores("key", 1, 3);
        assertEquals(null, result);
    }


    @Test
    public void shouldExecuteZREVRANGEBYSCORE() {
        Set<String> keys = new HashSet<String>();
        keys.add("key2");
        keys.add("key3");
        when(zSetOperations.reverseRangeByScore(anyString(), anyDouble(), anyDouble())).thenReturn(keys);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZREVRANGEBYSCORE",
                RedisConstants.KEY, "key",
                RedisConstants.MIN, 1.0,
                RedisConstants.MAX, 2.0);

        verify(zSetOperations).reverseRangeByScore("key", 1.0, 2.0);
        assertEquals(keys, result);
    }


    @Test
    public void shouldExecuteZREVRANK() {
        when(zSetOperations.reverseRank(anyString(), anyString())).thenReturn(1L);

        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZREVRANK",
                RedisConstants.KEY, "key",
                RedisConstants.VALUE, "value");

        verify(zSetOperations).reverseRank("key", "value");
        assertEquals(1L, result);
    }

    @Test
    public void shouldExecuteZUNIONSTORE() {
        Set<String> keys = new HashSet<String>();
        keys.add("key2");
        keys.add("key3");
        Object result = sendHeaders(
                RedisConstants.COMMAND, "ZUNIONSTORE",
                RedisConstants.KEY, "key",
                RedisConstants.DESTINATION, "destination",
                RedisConstants.KEYS, keys);

        verify(zSetOperations).unionAndStore("key", keys, "destination");
    }
}

