package com.leader.fingerprintlogin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
//    private FingerprintManager fingerprintManager;
//    private KeyguardManager keyguardManager;
//
//    private KeyStore keyStore;
//    private Cipher cipher;
//    private String KEY_NAME = "AndroidKey";

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView tv_finger_print = findViewById(R.id.tv_finger_print);
        final EditText et_username = findViewById(R.id.et_username);
        final EditText et_password = findViewById(R.id.et_password);

        final Button btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = et_username.getText().toString();
                String password = et_password.getText().toString();

                if (username.equals("nabil") && password.equals("123"))
                    onSuccess();
                else
                    onFailure();
            }
        });

        // Check 1: Android version should be greater or equal to Marshmallow
        // Check 2: Device has Fingerprint Scanner
        // Check 3: Have permission to use fingerprint scanner in the app
        // Check 4: Lock screen is secured with atleast 1 type of lock
        // Check 5: Atleast 1 Fingerprint is registered

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
//            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
//
//            if (!fingerprintManager.isHardwareDetected()) {
//                tv_finger_print.setText("Fingerprint Scanner not detected in Device");
//            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
//                tv_finger_print.setText("Permission not granted to use Fingerprint Scanner");
//            } else if (!keyguardManager.isKeyguardSecure()) {
//                tv_finger_print.setText("Add Lock to your Phone in Settings");
//            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
//                tv_finger_print.setText("You should add atleast 1 Fingerprint to use this Feature");
//            } else {
//                tv_finger_print.setText("Place your Finger on Scanner to Access the App.");
//
//                generateKey();
//                if (cipherInit()) {
//                    FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
//                    FingerprintHandler fingerprintHandler = new FingerprintHandler(this);
//                    fingerprintHandler.startAuth(fingerprintManager, cryptoObject);
//                }
//            }
//        }


        //for new biometric api
        final Button btn_bio = findViewById(R.id.btn_bio);

        if (!BiometricUtils.isSdkVersionSupported()) {
            tv_finger_print.setText("Below marshmallow. Not Possible");
            btn_bio.setVisibility(View.GONE);
        } else if (!BiometricUtils.isHardwareSupported(this)) {
            tv_finger_print.setText("Fingerprint Scanner not detected in Device");
            btn_bio.setVisibility(View.GONE);
        } else if (!BiometricUtils.isPermissionGranted(this)) {
            tv_finger_print.setText("Permission not granted to use Fingerprint Scanner");
            btn_bio.setVisibility(View.GONE);
        } else if (!BiometricUtils.isFingerprintAvailable(this)) {
            tv_finger_print.setText("You should add at least 1 Fingerprint to use this Feature");
            btn_bio.setVisibility(View.GONE);
        } else {
            tv_finger_print.setText("Click \"New Bio\" button and Place your Finger on Scanner to Access the App.");
            btn_bio.setVisibility(View.VISIBLE);

            Executor executor = Executors.newSingleThreadExecutor();
            biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                        handlerThread("negative button");
                    } else {
                        handlerThread("unrecoverable error " + errString);
                    }
                }

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    handlerThread("onAuthenticationSucceeded " + result);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    handlerThread("onAuthenticationFailed");
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle(getString(R.string.app_name))
                    .setSubtitle("Use your finger print for faster login")
                    .setDescription("Place your finger print in the scanner to login without username/password")
                    .setNegativeButtonText("Cancel")
                    .build();
        }

        btn_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (biometricPrompt != null && promptInfo != null)
                    biometricPrompt.authenticate(promptInfo);
            }
        });
    }

    private void handlerThread(final String message) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onSuccess() {
        Toast.makeText(this, "Yeaaaah", Toast.LENGTH_SHORT).show();
    }

    private void onFailure() {
        Toast.makeText(this, "Noooooooo", Toast.LENGTH_SHORT).show();
    }

//    @TargetApi(Build.VERSION_CODES.M)
//    private void generateKey() {
//
//        try {
//
//            keyStore = KeyStore.getInstance("AndroidKeyStore");
//            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
//
//            keyStore.load(null);
//            keyGenerator.init(new
//                    KeyGenParameterSpec.Builder(KEY_NAME,
//                    KeyProperties.PURPOSE_ENCRYPT |
//                            KeyProperties.PURPOSE_DECRYPT)
//                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//                    .setUserAuthenticationRequired(true)
//                    .setEncryptionPaddings(
//                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
//                    .build());
//            keyGenerator.generateKey();
//
//        } catch (KeyStoreException | IOException | CertificateException
//                | NoSuchAlgorithmException | InvalidAlgorithmParameterException
//                | NoSuchProviderException e) {
//
//            e.printStackTrace();
//
//        }
//
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    public boolean cipherInit() {
//        try {
//            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
//        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
//            throw new RuntimeException("Failed to get Cipher", e);
//        }
//
//        try {
//            keyStore.load(null);
//
//            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
//            cipher.init(Cipher.ENCRYPT_MODE, key);
//            return true;
//        } catch (KeyPermanentlyInvalidatedException e) {
//            return false;
//        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | InvalidKeyException e) {
//            throw new RuntimeException("Failed to init Cipher", e);
//        }
//    }
}
