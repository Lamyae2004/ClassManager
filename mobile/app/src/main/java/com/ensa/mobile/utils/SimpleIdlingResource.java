package com.ensa.mobile.utils;

import androidx.test.espresso.IdlingResource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * IdlingResource simple pour gérer les opérations asynchrones dans les tests
 */
public class SimpleIdlingResource implements IdlingResource {

    private volatile ResourceCallback callback;
    private AtomicBoolean isIdle = new AtomicBoolean(true);

    @Override
    public String getName() {
        return SimpleIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    /**
     * Appeler quand une opération asynchrone commence
     */
    public void setIdleState(boolean idle) {
        isIdle.set(idle);
        if (idle && callback != null) {
            callback.onTransitionToIdle();
        }
    }
}