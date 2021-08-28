package com.classroom.classdeck.ui.profile

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.classroom.classdeck.R
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.ProfileFragmentBinding
import com.classroom.classdeck.util.*
import com.marcoscg.dialogsheet.DialogSheet
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog

@AndroidEntryPoint
class ProfileFragment : Fragment() {


    private val viewModel: ProfileViewModel by lazy {
        ViewModelProvider(this).get(ProfileViewModel::class.java)
    }
    private lateinit var binding: ProfileFragmentBinding

    private var user: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?
        }
    }

    private lateinit var dialog: AlertDialog

    companion object {
        const val IMAGE_REQ_CODE = 123
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        setData()
        observeData()
        setOnClicklistenr()
    }

    private fun setOnClicklistenr() {
//        binding.topLay.root.setOnClickListener {
//            val bundle = Bundle().apply {
//                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
//            }
//            findNavController().navigate(R.id.action_profileFragment2_to_profileFragment, bundle)
//        }
//        binding.updateProf.setOnClickListener {
//            val bundle = Bundle().apply {
//                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
//            }
//            findNavController().navigate(R.id.action_profileFragment2_to_profileFragment, bundle)
//        }

        binding.gameRecordCard.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
            }
          //  findNavController().navigate(R.id.action_profileFragment2_to_gameRecordEntryFragment, bundle)
        }

        binding.customerCareCard.setOnClickListener {



         //   findNavController().navigate(R.id.action_profileFragment2_to_customerCareFragment)

        }

        binding.coinsStoreCard.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
            }
            //findNavController().navigate(R.id.action_profileFragment2_to_coinsStoreFragment, bundle)
        }
        binding.transactionRecordCard.setOnClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.USERS_BUNDLE_OBJ, user)
            }
         //   findNavController().navigate(R.id.action_profileFragment2_to_transactionRecordFragment, bundle)
        }


        binding.topLay.profilePic.setOnClickListener {
            selectImageFromGallery()
        }




    }

    private fun getData() {


        binding.progressBar.visible()

        user?.uid?.let { viewModel.getUserFromDataBase(it) }
        viewModel.userLiveData.observe(viewLifecycleOwner, { userStatus ->
            when (userStatus) {
                is ResponseState.Success -> {
                    user = userStatus.data
                    setData()
                    //   binding.progressBar.gone()
                }
                is ResponseState.Loading -> {

                }
                is ResponseState.Error -> {

                    userStatus.message?.let { binding.root.snackbar(it) }
                    binding.progressBar.gone()
                }
            }


        })

    }


    private fun setData() {
        setImageFromUrl(user?.imageUrl)
        binding.topLay.userName.text = user?.name
        binding.topLay.userId.text = user?.uid
        binding.topLay.userEmail.text = user?.email
        binding.topLay.userPhone.text = user?.phoneNumber

    }

    private fun observeData() {


    }

    private fun setImageFromUrl(imageUrl: String?) {

        binding.progressBar.visible()


        Glide.with(this).load(imageUrl)
            .placeholder(R.drawable.ic_baseline_person_24)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?, model: Any?,
                    target: Target<Drawable>?, isFirstResource: Boolean
                ): Boolean {

                    binding.progressBar.gone()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?, model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?, isFirstResource: Boolean
                ): Boolean {

                    binding.progressBar.gone()
                    return false
                }

            })
            .into(binding.topLay.profilePic)

    }





    private fun selectImageFromGallery() {


        val dialogSheet = context?.let {
            DialogSheet(it)
                .setTitle(R.string.app_name)
                .setMessage("Do you want to change your profile picture")
                .setColoredNavigationBar(true)
                .setTitleTextSize(20) // In SP
                .setCancelable(true)
                .setPositiveButton(android.R.string.ok) {
                    chooseImage()
                }
                .setNegativeButton(android.R.string.cancel) {
                }
                .setRoundedCorners(true) // Default value is true
                .setBackgroundColor(Color.WHITE) // Your custom background color
                .setButtonsColorRes(R.color.primary_color_amoled)
                .setNegativeButtonColorRes(R.color.red)
                .show()

        }
    }

    private fun chooseImage() {
        Matisse.from(this)
            .choose(MimeType.ofImage())
            .countable(true)
            .maxSelectable(1)
            .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .imageEngine(GlideEngine())
            .thumbnailScale(0.85f)
            .showPreview(false) // Default is `true`
            .forResult(IMAGE_REQ_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQ_CODE && resultCode == RESULT_OK) {
            val mSelected = Matisse.obtainResult(data)

            if (mSelected.isNotEmpty()) {
                binding.topLay.profilePic.setImageURI(mSelected[0])
                uploadImageToFirestore(mSelected[0])
            }


        }
    }

    private fun uploadImageToFirestore(uri: Uri) {


        showDialog("Uploading image")



        user?.uid?.let { viewModel.uploadImageInFirebaseStorage(it, uri) }
        viewModel.uploadLiveData.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is ResponseState.Success -> {
                    dataState.data?.let { binding.root.snackbar(it) }
                    dismissDialog()
                }
                is ResponseState.Error -> {
                    dataState.message?.let { binding.root.snackbar(it) }
                    dismissDialog()
                }
                is ResponseState.Loading -> {
                }
            }
        })
    }

    private fun dismissDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    private fun showDialog(mesg: String) {
        dialog = SpotsDialog.Builder()
            .setContext(context)
            .setTheme(R.style.CustomProgressDialog)
            .setMessage(mesg)
            .setCancelable(false)
            .build()
        dialog.show()

    }




}