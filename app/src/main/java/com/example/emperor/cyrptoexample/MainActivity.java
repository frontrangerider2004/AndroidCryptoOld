package com.example.emperor.cyrptoexample;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class MainActivity extends ActionBarActivity implements InterfaceHashStatus {

    //User input and control
    private EditText editTextInput;
    private Button buttonHash;

    //Regular Hashing UI
    private TextView textViewHash;
    private TextView textViewHashAlgorithm;
    private TextView textViewHashProvider;

    //PBKDF Hashing UI
    private TextView textViewPBKDFhash;
    private TextView textviewPBKDFAlgorithm;
    private TextView textviewPBKDFprovider;
    private TextView textviewPBKDFsalt;
    private TextView textviewPBKDFsaltBitLength;
    private TextView textviewPBKDiterations;

    //For use with basic hashing
    private static final String SHA_ALGORITHM = "SHA-256"; //Minimum algorithm strength
    private static final String UTF_ENCODING = "UTF-8"; //Best practice for computing hashes
    private static final String HEX_FORMATTER = "%02x"; //Read two bytes as lower case hex
    private String mHashProvider = null;
    private String mHashAlgorithm = null;
    private String mHashString = null;

    //For use with key stretching
    private static final String PBKDF_ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final int SALT_BYTE_SIZE = 32; //Should be equal to hash byte size
    private static final int SALT_BIT_SIZE = 256; //Conversion of bytes to bits
    private static final int PBKDF_ITERATIONS = 10; //Make proportional to minimum tolerable UX delay
    private byte[] mSalt = null;
    private String mPBKDFprovider = null;
    private String mPBKDFalgorithm = null;
    private int mPBKDFiterations = 0;
    private int mSaltBitLength = 0;
    private String mPBKDFhashString = null;

    private static final String TAG = "Cyrpto";

    private Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res = getResources();

        //User inputs
        editTextInput = (EditText) findViewById(R.id.editText_input);
        buttonHash = (Button) findViewById(R.id.button_hash);

        //Plain Hashing UI
        textViewHash = (TextView) findViewById(R.id.textView_hashed);
        textViewHashAlgorithm = (TextView) findViewById(R.id.textView_hashed_algorithm);

        //PBKDF Hashing UI
        textViewPBKDFhash = (TextView) findViewById(R.id.textView_pbkdf_hashed);
        textviewPBKDFsalt = (TextView) findViewById(R.id.textView_pbkdf_algorithm);
        textviewPBKDFAlgorithm = (TextView) findViewById(R.id.textView_pbkdf_algorithm);
        textviewPBKDFprovider = (TextView) findViewById(R.id.textView_pbkdf_algorithm);
        textviewPBKDFsaltBitLength = (TextView) findViewById(R.id.textView_pbkdf_salt_bitLength);
        textviewPBKDiterations = (TextView) findViewById(R.id.textView_pbkdf_iterations);

        registerClickListeners();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handles button clicks for the "hash" button.
     */
    View.OnClickListener hashButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Hash the input string using the non-PBKDF method
            setmHashString(hashString(SHA_ALGORITHM, editTextInput.getText().toString()));

            //Hash the input string using the PBKDF method
            setmPBKDFhashString(pbkfHashString(editTextInput.getText().toString().toCharArray(),
                                                        PBKDF_ALGORITHM,
                                                        PBKDF_ITERATIONS,
                                                        SALT_BYTE_SIZE));

        }
    };

    /**
     * Sets the "hash" button click listener to the
     * cutstom listener defined above.
     */
    private void registerClickListeners(){

        buttonHash.setOnClickListener(hashButtonClickListener);
    }

    /**
     * Generates a hash of the supplied string using
     * the supplied algorithm and UTF-8 encoding.
     * @param algorithm
     * @param textToHash
     * @return
     */
    public String hashString(String algorithm, String textToHash){
        MessageDigest messageDigest = null;

        try {
            messageDigest = MessageDigest.getInstance(algorithm);

            setmHashAlgorithmr(messageDigest.getAlgorithm());
            setmHashProvider(messageDigest.getProvider().getName());

            messageDigest.update(textToHash.getBytes(UTF_ENCODING));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "hashed the input string");
        this.onHashComplete();
        return encodeHexString(messageDigest.digest()); //The hash of the text
    }

    /**
     * Gets the specified number of secure random bytes
     * using the javax security API and returns this
     * array for use as a salt in PBKD functions.
     * @param numberOfBytes
     * @return
     */
    private byte[] generateSaltBytes(int numberOfBytes){
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[numberOfBytes];
        secureRandom.nextBytes(salt);

        Log.d(TAG, "generateSaltBytes() complete.");

        setmSalt(salt);

        return salt;
    }

    /**
     * Generates a keyed, stretched, and multi-iterated
     * hash of the supplied character array using the
     * supplied algorithm and number of iterations.
     * @param password
     * @param saltBitLength
     * @param pbkfAlgorithm
     * @param iterations
     * @return
     */
    private String pbkfHashString(char[] password , String pbkfAlgorithm, int iterations, int saltBitLength){
        //Generate secure random salt
        byte[] salt = generateSaltBytes(saltBitLength);
        setmSaltBitLength(saltBitLength);

        //Setup the PBKD function
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, saltBitLength);
        setmPBKDFiterations(iterations);
        byte[] hashedBytes = null;

        try{
            //Make secret key by hashing with the PBKD function
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(PBKDF_ALGORITHM);
            if(keyFactory != null){
                Log.d(TAG, "secretKeyFactory.getProvider(): " + keyFactory.getProvider() + ", kf.getAlgorithm(): " + keyFactory.getAlgorithm());
                setmPBKDFprovider(keyFactory.getProvider().getName());
                setmPBKDFalgorithm(keyFactory.getAlgorithm().toString());
                hashedBytes = keyFactory.generateSecret(keySpec).getEncoded();
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        if(hashedBytes == null){
            Log.d(TAG, "hashedBytes = NULL");
            return null;
        }

        Log.d(TAG, "PBKF secret key generated successfully.");
        this.onPBKDFhashComplete();
        return encodeHexString(hashedBytes);
    }

    /**
     * Takes a Message Digest or any other byte
     * array and converts it into a hexidecimal
     * string using a format string for hex.
     * @param byteArray
     * @return
     */
    private String encodeHexString(byte[] byteArray){
        StringBuilder sb = new StringBuilder();

        for(byte b : byteArray){
            sb.append(String.format(HEX_FORMATTER, b));
        }

        Log.d(TAG, "Byte array encoded to HEX string.");

        return sb.toString();
    }

    public String getmHashProvider() {
        return mHashProvider;
    }

    public void setmHashProvider(String mProvider) {
        this.mHashProvider = mProvider;
    }

    public String getmHashAlgorithm() {
        return mHashAlgorithm;
    }

    public void setmHashAlgorithmr(String mHashAlgorithm) {
        this.mHashAlgorithm = mHashAlgorithm;
    }

    public byte[] getmSalt() {
        return mSalt;
    }

    private void setmSalt(byte[] mSalt) {
        this.mSalt = mSalt;
    }

    public String getmPBKDFprovider() {
        return mPBKDFprovider;
    }

    private void setmPBKDFprovider(String mPBKDFprovider) {
        this.mPBKDFprovider = mPBKDFprovider;
    }

    public String getmPBKDFalgorithm() {
        return mPBKDFalgorithm;
    }

    private void setmPBKDFalgorithm(String mPBKDFalgorithm) {
        this.mPBKDFalgorithm = mPBKDFalgorithm;
    }

    private void setmSaltBitLength(int saltBitLength){
        this.mSaltBitLength = saltBitLength;
    }

    public int getmSaltBitLength(){
        return mSaltBitLength;
    }

    private void setmPBKDFiterations(int iterations){
        this.mPBKDFiterations = iterations;
    }

    public int getmPBKDFiterations(){
        return mPBKDFiterations;
    }

    public String getmHashString() {
        return mHashString;
    }

    public void setmHashString(String mHashString) {
        this.mHashString = mHashString;
    }

    public String getmPBKDFhashString() {
        return mPBKDFhashString;
    }

    public void setmPBKDFhashString(String mPBKDFhashString) {
        this.mPBKDFhashString = mPBKDFhashString;
    }

    @Override
    public void onHashComplete() {
        //Hash the result and output to screen
        textViewHash.setText(getmHashString());
        textViewHashAlgorithm.setText(String.format(res.getString(R.string.hash_algorithm), getmHashAlgorithm()));
        textViewHashProvider.setText(String.format(res.getString(R.string.hash_provider), getmHashProvider()));
    }

    @Override
    public void onPBKDFhashComplete() {
        //Perform the PBKF hash and output to screen
        textViewPBKDFhash.setText(getmPBKDFhashString());
        textviewPBKDFsalt.setText(String.format(res.getString(R.string.pbkdf_salt), getmSalt()));
        textviewPBKDFsaltBitLength.setText(String.format(res.getString(R.string.pbkdf_salt_bitLength), getmSaltBitLength()));
        textviewPBKDFAlgorithm.setText(String.format(res.getString(R.string.pbkdf_algorithm), getmPBKDFalgorithm()));
        textviewPBKDFprovider.setText(String.format(res.getString(R.string.pbkdf_provider), getmPBKDFprovider()));
        textviewPBKDiterations.setText(String.format(res.getString(R.string.pbkdf_iterations), getmPBKDFiterations()));
    }
}//End Class
