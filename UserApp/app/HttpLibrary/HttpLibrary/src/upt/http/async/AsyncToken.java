package upt.http.async;

/*
 * Copyright (c) 2013. Upresh Tikam's. All rights reserved.
 */

import java.util.UUID;

/**
 * An AsyncToken encapsulates useful data for identifying any asynchronous request and can
 * be used to identify and cancel requests that a consumer of an API no longer cares about.
 */
public class AsyncToken {
    private final String mPrefix;
    private final UUID mUUID;

    public AsyncToken(String prefix) {
        mPrefix = prefix;
        mUUID = UUID.randomUUID();
    }

    public String getPrefix() {
        return mPrefix;
    }

    public UUID getUUID() {
        return mUUID;
    }
}
