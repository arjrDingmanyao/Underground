package com.creditcloud.config;

import java.io.IOException;
import java.io.Reader;

public abstract interface ConfigLoader {

    public abstract Reader loadConfig(String paramString)
            throws IOException;

    public abstract long getLastModified(String paramString);
}
