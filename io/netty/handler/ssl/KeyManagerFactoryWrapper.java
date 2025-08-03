/*
 * Decompiled with CFR 0.152.
 */
package io.netty.handler.ssl;

import io.netty.handler.ssl.util.SimpleKeyManagerFactory;
import io.netty.util.internal.ObjectUtil;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.ManagerFactoryParameters;

final class KeyManagerFactoryWrapper
extends SimpleKeyManagerFactory {
    private final KeyManager km;

    KeyManagerFactoryWrapper(KeyManager km) {
        this.km = ObjectUtil.checkNotNull(km, "km");
    }

    @Override
    protected void engineInit(KeyStore keyStore, char[] var2) throws Exception {
    }

    @Override
    protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws Exception {
    }

    @Override
    protected KeyManager[] engineGetKeyManagers() {
        return new KeyManager[]{this.km};
    }
}

