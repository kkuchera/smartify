package com.example.smartify.data;

import android.content.Context;

/**
 * Enables injection of mock implementations for {@link DevicesRepository} at compile time. This
 * is useful for testing, since it allows us to use a fake instance of the class to isolate the
 * dependencies and run a test hermetically.
 */
public class Injection {

    public static DevicesRepository provideDevicesRepository(Context context) {
        return new DevicesRepositoryImpl(new DevicesServiceApiImpl(context), new
                RestServiceApiImpl(context));
    }
}
