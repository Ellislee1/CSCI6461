/*
 * This file implements the system clock for the CSCI 6461 computer
 */
package com.csci6461;

/**
 * The clock class simulates the computer clock enabling the Control Unit (CU) 
 * to get clock ticks at which to execute the next instruction. It can also be 
 * used to throttle computer for debugging since timeout period is configurable.
 * 
 * @author imanuelportalatin
 */
public class Clock {
    /**
     * Storage for the timeout period in ms
     */
    private final long timeoutPeriod;
    
    /**
     * The clock's constructor sets the timeout period
     * 
     * @param time An int with timeout period in ms
     */
    public Clock(final long time) {
        /* Set the timeout period */
        this.timeoutPeriod = time;
    }
    
    /**
     * This simulates the clock tick by waiting until the timeout period 
     * expires
     * 
     * @throws InterruptedException If any thread has interrupted the current
     *                              thread
     */
    public void waitForNextTick() throws InterruptedException {
        Thread.sleep(this.timeoutPeriod);
    }
}
