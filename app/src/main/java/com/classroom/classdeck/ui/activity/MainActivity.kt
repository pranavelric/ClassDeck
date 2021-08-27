package com.classroom.classdeck.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.classroom.classdeck.R
import com.classroom.classdeck.databinding.ActivityMainBinding
import com.classroom.classdeck.util.NetworkConnection
import com.classroom.classdeck.util.NetworkHelper
import com.gaming.earningvalley.utils.MySharedPrefrences
import com.thecode.aestheticdialogs.AestheticDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}