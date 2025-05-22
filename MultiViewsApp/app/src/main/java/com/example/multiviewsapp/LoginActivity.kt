package com.example.multiviewsapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import com.example.multiviewsapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val TAG = "LoginActivity"
    private lateinit var biometricPrompt: BiometricPrompt
    private val cryptographyManager = CryptographyManager()
    private val ciphertextWrapper
        get() = cryptographyManager.getCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val canAuthenticate = BiometricManager.from(applicationContext).canAuthenticate()
        if (canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS) {
            binding.useBiometrics.visibility = View.VISIBLE
            binding.useBiometrics.setOnClickListener {
                if (ciphertextWrapper != null) {
                    showBiometricPromptForDecryption()
                } else {
                    startActivity(Intent(this, EnableBiometricLoginActivity::class.java))
                }
            }
        } else {
            binding.useBiometrics.visibility = View.INVISIBLE
        }

        // Always setup password login (simplified)
        setupForLoginWithPassword()
    }

    override fun onResume() {
        super.onResume()
        if (ciphertextWrapper != null && SampleAppUser.fakeToken == null) {
            showBiometricPromptForDecryption()
        }
    }

    private fun showBiometricPromptForDecryption() {
        try {
            ciphertextWrapper?.let { textWrapper ->
                val secretKeyName = getString(R.string.secret_key_name)
                val cipher = cryptographyManager.getInitializedCipherForDecryption(
                    secretKeyName,
                    textWrapper.initializationVector
                )
                biometricPrompt = BiometricPromptUtils.createBiometricPrompt(this, ::decryptServerTokenFromStorage)
                val promptInfo = BiometricPromptUtils.createPromptInfo(this)
                biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
            }
        } catch (e: android.security.keystore.KeyPermanentlyInvalidatedException) {
            Log.e(TAG, "Biometric key permanently invalidated", e)
            showBiometricKeyInvalidatedDialog()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing cipher", e)
            Toast.makeText(this, "Biometric login error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showBiometricKeyInvalidatedDialog() {
        cryptographyManager.clearCiphertextWrapperFromSharedPrefs(
            applicationContext,
            SHARED_PREFS_FILENAME,
            Context.MODE_PRIVATE,
            CIPHERTEXT_WRAPPER
        )
        Toast.makeText(this, "Biometric data changed. Please log in again.", Toast.LENGTH_LONG).show()
        startActivity(Intent(this, EnableBiometricLoginActivity::class.java))
        finish()
    }

    private fun decryptServerTokenFromStorage(authResult: BiometricPrompt.AuthenticationResult) {
        ciphertextWrapper?.let { textWrapper ->
            authResult.cryptoObject?.cipher?.let { cipher ->
                val plaintext = cryptographyManager.decryptData(textWrapper.ciphertext, cipher)
                SampleAppUser.fakeToken = plaintext
                startActivity(Intent(this, HomePage::class.java))
                finish()
            }
        }
    }

    private fun setupForLoginWithPassword() {
        // Just navigate to HomePage when login button is clicked
        binding.login.setOnClickListener {
            startActivity(Intent(this, HomePage::class.java))
            finish()
        }
    }
}
