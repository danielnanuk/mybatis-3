package org.apache.ibatis.cache;

import org.apache.ibatis.cache.decorators.BlockingCache;
import org.apache.ibatis.cache.impl.PerpetualCache;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class BlockingCacheTest {

  @Test
  public void shouldBlockingForAround3000ms() {
    PerpetualCache perpetualCache = new PerpetualCache("id");
    final BlockingCache blockingCache = new BlockingCache(perpetualCache);
    blockingCache.setTimeout(5000); // lock timeout for 5 seconds
    Object value = blockingCache.getObject(0); // 1. get the lock
    assertNull(value);
    Thread thread = new Thread(() -> {
      long start = System.currentTimeMillis();
      assertEquals(blockingCache.getObject(0), "value"); // 2. try to acquire lock
      long end = System.currentTimeMillis();
      System.out.println("Blocked for " + (end - start) + " ms");
    });
    thread.start();
    try {
      Thread.sleep(3000);
      blockingCache.putObject(0, "value"); // release the lock
      thread.join(); // wait for the thread to terminate
    } catch (InterruptedException e) {
      e.printStackTrace();
    }


  }
}
