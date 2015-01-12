package com.example.emperor.cryptoexample1;

import android.os.AsyncTask;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by emperor on 12/30/14.
 */
public class AsynchTaskHashInBackground  extends AsyncTask<Void, Void, String>{

    //For Standard Hashes
    private String mHashProvider = null;
    private String mHashAlgorithm = null;
    private String mTextToHash = null;
    private String mHashString = null;
    private static final String UTF_ENCODING = "UTF-8"; //Best practice for computing hashes
    private static final String NA_STRING = "N/A";

    //For verification that the system is using what we supply
    private String mSystemAlgorithm = null;
    private String mSystemProvider = null;

    private InterfaceHashStatus mInterfaceHashStatus;

    //Constructor used to get the necessary objects into this class
    // since AsyncTask by default only accepts params of the same type
    /**
     * @param interfaceHashStatus
     * @param algorithm
     * @param textToHash
     */
    public AsynchTaskHashInBackground(InterfaceHashStatus interfaceHashStatus, String algorithm, String textToHash){
        mHashAlgorithm = algorithm;
        mTextToHash = textToHash;
        mInterfaceHashStatus = interfaceHashStatus;
    }

    @Override
    protected String doInBackground(Void... params) {
        //Do the work in here
        mHashString = hashString();
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //Notify the activity that it should update the UI
        mInterfaceHashStatus.onHashComplete(mHashString, mSystemProvider, mSystemAlgorithm);
    }

    /**
     * Generates a hash of the supplied string using
     * the supplied algorithm and UTF-8 encoding.
     * @return - The hex encoded string of the hash from it's byte array
     */
    public String hashString(){
        MessageDigest messageDigest = null;
        SimpleHexEncoder hexEncoder;

        try {
            messageDigest = MessageDigest.getInstance(mHashAlgorithm);
            messageDigest.update(mTextToHash.getBytes(UTF_ENCODING));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //For the UI
        if(messageDigest.getAlgorithm() != null){
            mSystemAlgorithm = messageDigest.getAlgorithm();
            Log.d(LogTag.TAG, "hashString(): messageDigest.getAlgorithm(): " + messageDigest.getAlgorithm());
        } else {
            mSystemAlgorithm = NA_STRING;
        }

        //For the UI
        if(messageDigest.getProvider().getName() != null){
            mSystemProvider = messageDigest.getProvider().getName();
            Log.d(LogTag.TAG, "hashString(): messageDigest.getProvider(): " + messageDigest.getProvider().getName());
        } else {
            mSystemProvider = NA_STRING;
        }

        Log.d(LogTag.TAG, "hashString(): Successfully hashed the input string.");
        hexEncoder = new SimpleHexEncoder(SimpleHexEncoder.FontCase.LOWER);

        return hexEncoder.encodeHexString(messageDigest.digest()); //The hash of the text in hex encoding
    }

}//End Class
