package com.example.emperor.cyrptoexample;

/**
 * Created by emperor on 12/29/14.
 */
public interface InterfaceHashStatus {

    /**
     * Callback to the implementing class to
     * notify that hashing is complete.
     */
    public void onHashComplete();

    /**
     * Callback to the implementing class to
     * notify that PBKDF hashing is complete
     */
    public void onPBKDFhashComplete();

}//End Interface
