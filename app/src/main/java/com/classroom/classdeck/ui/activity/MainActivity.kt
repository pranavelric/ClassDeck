package com.classroom.classdeck.ui.activity

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.classroom.classdeck.R
import com.classroom.classdeck.databinding.ActivityMainBinding
import com.classroom.classdeck.util.NetworkConnection
import com.classroom.classdeck.util.NetworkHelper
import com.classroom.classdeck.util.setFullScreenForNotch
import com.classroom.classdeck.util.setFullScreenWithBtmNav
import com.classroom.classdeck.util.MySharedPrefrences
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.thecode.aestheticdialogs.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.android.gms.auth.api.signin.GoogleSignIn

import com.google.android.gms.auth.api.signin.GoogleSignInOptions





@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    private val navController: NavController by lazy {
        Navigation.findNavController(this, R.id.nav_host_fragment)
    }

    @Inject
    lateinit var sharedPrefrences: MySharedPrefrences

    @Inject
    lateinit var networkConnection: NetworkConnection

    @Inject
    lateinit var networkHelper: NetworkHelper

    lateinit var dialog: AestheticDialog.Builder

    lateinit var googleSignInClient:GoogleSignInClient

    private val permList: Array<String> = arrayOf(READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)
    lateinit var firebaseAuth: FirebaseAuth
    companion object {
        private const val PERMISSION_CODE = 121
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


        networkConnection.observe(this) {
            if (it) {
                hideNetworkDialog()


            } else {
                showNetworkDialog()
            }

        }






        if (!checkForPermission(READ_EXTERNAL_STORAGE)) {
            askPermission(permList)
        }
        if (!checkForPermission(WRITE_EXTERNAL_STORAGE)) {
            askPermission(permList)
        }

    }






    override fun onStart() {
        super.onStart()
        setFullScreenForNotch()
        setFullScreenWithBtmNav()
    }

    private fun hideNetworkDialog() {
        if (this::dialog.isInitialized)
            dialog.dismiss()
    }

    private fun showNetworkDialog() {
        dialog = AestheticDialog.Builder(this, DialogStyle.EMOTION, DialogType.ERROR)
            .setTitle("No internet")
            .setMessage("Please check your internet connection")
            .setCancelable(false)
            .setDarkMode(false)
            .setGravity(Gravity.CENTER)
            .setAnimation(DialogAnimation.SHRINK)
            .setOnClickListener(object : OnDialogClickListener {
                override fun onClick(dialog: AestheticDialog.Builder) {
                    dialog.dismiss()
                }
            })
        dialog.show()

    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }


    private fun checkForPermission(permission: String): Boolean {

        return ActivityCompat.checkSelfPermission(
            this@MainActivity,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty()) {
            val bool = grantResults[0] == PackageManager.PERMISSION_GRANTED
            onPermissionResult(bool)
            val bool1 = grantResults[1] == PackageManager.PERMISSION_GRANTED
            onPermissionResult(bool1)
        }

    }

    private fun onPermissionResult(granted: Boolean) {
        if (!granted) {
            askPermission(permList)
        }
    }

    private fun askPermission(permList: Array<String>) {
        ActivityCompat.requestPermissions(this@MainActivity, permList, PERMISSION_CODE)
    }







}