package com.ug.eon.android.tv.infoserver;

/**
 * Created by milan.adamovic on 5/16/2018.
 */
public interface InfoServiceCallback<T> {
    void onResponse(T response);
}