/*
 * Copyright (c) 2023.
 * Julian Auguscik
 */

package lawsystem;

import data.PipeData;

public class Session extends PipeData<Session>
{
    // TODO: 16.11.2021 maybe add here @Id if using only for nitrite. 
    private long mUserId;
    private String mSession;

    public Session() {
    }

    public long getmUserId() {
        return this.mUserId;
    }

    public void setmUserId(long mUserId) {
        this.mUserId = mUserId;
    }

    public String getmSession() {
        return this.mSession;
    }

    public void setmSession(String mSession) {
        this.mSession = mSession;
    }
}
