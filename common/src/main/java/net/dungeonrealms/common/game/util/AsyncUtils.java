package net.dungeonrealms.common.game.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Nick on 9/17/2015.
 */
public class AsyncUtils {


    /**
     * The person who made this class is officially autistic, runtime will always be null. Commons is not a fucking runnable jar file.
     * - Vawke
     */
    public static int threadCount = 0;

    /*
    We'll use this instead of new Thread().start(); every time we want
    something to be on a different thread..
     */
    public static ExecutorService pool = Executors.newFixedThreadPool(threadCount);
}
