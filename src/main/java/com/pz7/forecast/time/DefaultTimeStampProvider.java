package com.pz7.forecast.time;

public class DefaultTimeStampProvider implements TimeStampProvider {
    @Override
    public long getTimeStamp() {
        return System.currentTimeMillis();
    }
}
