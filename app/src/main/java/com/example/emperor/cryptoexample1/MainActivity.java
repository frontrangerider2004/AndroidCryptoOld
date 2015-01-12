package com.example.emperor.cryptoexample1;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;

import com.example.emperor.cyrptoexample.R;

public class MainActivity extends ActionBarActivity implements InterfaceHashStatus{

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
    private TextView textviewPBKDiterations;
    private TextView textviewPBKDFprovider;
    private TextView textviewPBKDFsalt;
    private TextView textviewPBKDFsaltBitLength;

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
    private static final String NA_STRING = "N/A";
    private byte[] mSalt = null;
    private String mPBKDFprovider = null;
    private String mPBKDFalgorithm = null;
    private int mPBKDFiterations = 0;
    private int mSaltBitLength = 0;
    private String mPBKDFhashString = null;

    private Resources mResources;

    private Context mContext;

    private InterfaceHashStatus interfaceHashStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResources = getResources();
        mContext = getApplicationContext();
        interfaceHashStatus = this;

        //User inputs
        editTextInput = (EditText) findViewById(R.id.editText_input);
        buttonHash = (Button) findViewById(R.id.button_hash);

        //Plain Hashing UI
        textViewHash = (TextView) findViewById(R.id.textView_hashed);
        textViewHashAlgorithm = (TextView) findViewById(R.id.textView_hashed_algorithm);
        textViewHashProvider = (TextView) findViewById(R.id.textView_hashed_provider);

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
            //TODO Hash the string normally
            new AsynchTaskHashInBackground(interfaceHashStatus, SHA_ALGORITHM, editTextInput.getText().toString()).execute();

            //TODO hash the string with the PBKDF methods

            //Hide the keyboard so we can see the screen
            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    };

    /**
     * Sets the "hash" button click listener to the
     * cutstom listener defined above.
     */
    private void registerClickListeners(){

        buttonHash.setOnClickListener(hashButtonClickListener);
    }

//    /**
//     * Generates a hash of the supplied string using
//     * the supplied algorithm and UTF-8 encoding.
//     * @param algorithm
//     * @param textToHash
//     * @return
//     */
//    public String hashString(String algorithm, String textToHash){
//        MessageDigest messageDigest = null;
//
//        try {
//            messageDigest = MessageDigest.getInstance(algorithm);
//            messageDigest.update(textToHash.getBytes(UTF_ENCODING));
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        //For the UI
//        if(messageDigest.getAlgorithm() != null){
//            setmHashAlgorithmr(messageDigest.getAlgorithm());
//            Log.d(TAG, "hashString(): messageDigest.getAlgorithm(): " + messageDigest.getAlgorithm());
//        } else {
//            setmHashAlgorithmr(NA_STRING);
//        }
//
//        //For the UI
//        if(messageDigest.getProvider().getName() != null){
//            setmHashProvider(messageDigest.getProvider().getName());
//            Log.d(TAG, "hashString(): messageDigest.getProvider(): " + messageDigest.getProvider().getName());
//        } else {
//            setmHashProvider(NA_STRING);
//        }
//
//        Log.d(TAG, "hashString(): Successfully hashed the input string.");
//        return encodeHexString(messageDigest.digest()); //The hash of the text in hex encoding
//    }
//
//    /**
//     * Gets the specified number of secure random bytes
//     * using the javax security API and returns this
//     * array for use as a salt in PBKD functions.
//     * @param numberOfBytes
//     * @return
//     */
//    private byte[] generateSaltBytes(int numberOfBytes){
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] salt = new byte[numberOfBytes];
//        secureRandom.nextBytes(salt);
//
//        Log.d(TAG, "generateSaltBytes(): Salt generation complete.");
//
//        setmSalt(salt);
//
//        return salt;
//    }
//
//    /**
//     * Generates a keyed, stretched, and multi-iterated
//     * hash of the supplied character array using the
//     * supplied algorithm and number of iterations.
//     * @param password
//     * @param saltBitLength
//     * @param pbkdfAlgorithm
//     * @param iterations
//     * @return
//     */
//    private String pbkdfHashString(char[] password, String pbkdfAlgorithm, int iterations, int saltBitLength){
//        //Generate secure random salt
//        byte[] salt = generateSaltBytes(saltBitLength);
//        setmSaltBitLength(saltBitLength);
//
//        //Setup the PBKD function
//        PBEKeySpec keySpec = new PBEKeySpec(password, salt, iterations, saltBitLength);
//        setmPBKDFiterations(iterations);
//
//        byte[] hashedBytes = null;
//        SecretKeyFactory keyFactory = null;
//
//        try{
//            keyFactory = SecretKeyFactory.getInstance(PBKDF_ALGORITHM);
//            hashedBytes = keyFactory.generateSecret(keySpec).getEncoded();
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        } catch (InvalidKeySpecException e) {
//            e.printStackTrace();
//        }
//
//        //For the UI
//        Log.d(TAG, "pbkdfHashString(): secretKeyFactory.getProvider(): " + keyFactory.getProvider() + ", kf.getAlgorithm(): " + keyFactory.getAlgorithm());
//        if(keyFactory.getProvider().getName() != null){
//            setmPBKDFprovider(keyFactory.getProvider().getName());
//        } else {
//            setmPBKDFprovider(NA_STRING);
//        }
//
//        //For the UI
//        if(keyFactory.getAlgorithm().toString() != null){
//            setmPBKDFalgorithm(keyFactory.getAlgorithm().toString());
//        } else {
//            setmPBKDFalgorithm(NA_STRING);
//        }
//
//        //For the UI
//        if(hashedBytes == null){
//            Log.d(TAG, "pbkdfHashString(): hashedBytes = NULL");
//            return null;
//        }
//
//        Log.d(TAG, "pbkdfHashString(): PBKDF hash generated successfully.");
//        return encodeHexString(hashedBytes); //Hash of the input in hex encoding
//    }
//
//    /**
//     * Takes a Message Digest or any other byte
//     * array and converts it into a hexidecimal
//     * string using a format string for hex.
//     * @param byteArray
//     * @return
//     */
//    private String encodeHexString(byte[] byteArray){
//        StringBuilder sb = new StringBuilder();
//
//        for(byte b : byteArray){
//            sb.append(String.format(HEX_FORMATTER, b));
//        }
//
//        Log.d(TAG, "encodeHexString(): Byte array encoded to HEX string.");
//
//        return sb.toString();
//    }

    // ============ Getters and Setters for Module Variables  ======== //
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

    // ============ Callbacks for Updating the User Interface After Hashing Complete ======== //
    @Override
    public void onHashComplete(String hashString, String provider, String algorithm) {
        Log.d(LogTag.TAG, "onHashComplete(): hashString= " + hashString + ", Provider= " + provider + ", Algorithm= " + algorithm);
        textViewHash.setText(hashString);
        textViewHashAlgorithm.setText(String.format(mResources.getString(R.string.hash_algorithm), algorithm));
        textViewHashProvider.setText(String.format(mResources.getString(R.string.hash_provider), provider));
    }

    //TODO change this into the overridden callback after implementing it in the Interface
    public void updatePBKDFhashUserInterface() {
        Log.d(LogTag.TAG, "onPBKDFhashComplete()");
        textViewPBKDFhash.setText(getmPBKDFhashString());
        textviewPBKDFsalt.setText(String.format(mResources.getString(R.string.pbkdf_salt), getmSalt()));
        textviewPBKDFsaltBitLength.setText(String.format(mResources.getString(R.string.pbkdf_salt_bitLength), getmSaltBitLength()));
        textviewPBKDFAlgorithm.setText(String.format(mResources.getString(R.string.pbkdf_algorithm), getmPBKDFalgorithm()));
        textviewPBKDFprovider.setText(String.format(mResources.getString(R.string.pbkdf_provider), getmPBKDFprovider()));
        textviewPBKDiterations.setText(String.format(mResources.getString(R.string.pbkdf_iterations), getmPBKDFiterations()));
    }

}//End Class
