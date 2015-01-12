package com.example.emperor.cryptoexample1;

import android.os.AsyncTask;

/**
 * Created by emperor on 12/31/14.
 */
public class AsynchTaskPBKDFinBackground extends AsyncTask<Void, Void, String> {

    private static final String HEX_FORMATTER = "%02x"; //Read two bytes as lower case hex

    //For PBKDF Hashes
    private byte[] mSalt = null;
    private String mPBKDFprovider = null;
    private String mPBKDFalgorithm = null;
    private int mPBKDFiterations = 0;
    private int mSaltBitLength = 0;
    private String mPBKDFhashString = null;

    //Constructor used to get the necessary objects into this class
    // since AsyncTask by default only accepts params of the same type

    /**
     *
     * @param password
     * @param pbkdfAlgorithm
     * @param iterations
     * @param saltBitLength
     */
    public AsynchTaskPBKDFinBackground(char[] password, String pbkdfAlgorithm, int iterations, int saltBitLength){

    }

    @Override
    protected String doInBackground(Void... params) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

}//End Class
