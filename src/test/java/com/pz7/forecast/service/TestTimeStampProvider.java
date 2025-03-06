package com.pz7.forecast.service;

import com.pz7.forecast.time.TimeStampProvider;

public class TestTimeStampProvider implements TimeStampProvider {
    private long currentTime = 0;

    @Override
    public long getTimeStamp() {
        return currentTime++;
    }

    public void moveTimeForwardBy(long time) {
        currentTime += time;
    }
}
