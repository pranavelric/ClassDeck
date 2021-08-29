package com.classroom.classdeck.ui.dashBorad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.classroom.classdeck.adapters.AnnouncementAdapter
import com.classroom.classdeck.adapters.AssignmentAdapter
import com.classroom.classdeck.adapters.CourseAdapter
import com.classroom.classdeck.adapters.EventAdapter
import com.classroom.classdeck.data.model.User
import com.classroom.classdeck.databinding.DashBoardFragmentBinding
import com.classroom.classdeck.util.*
import com.thecode.aestheticdialogs.DialogAnimation
import com.thecode.aestheticdialogs.DialogStyle
import com.thecode.aestheticdialogs.DialogType
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class DashBoardFragment : Fragment() {


    private lateinit var binding: DashBoardFragmentBinding
    private val viewModel: DashBoardViewModel by lazy {
        ViewModelProvider(this).get(DashBoardViewModel::class.java)
    }

    @Inject
    lateinit var eventAdapter: EventAdapter

    @Inject
    lateinit var courseAdapter: CourseAdapter

    @Inject
    lateinit var announceAdapter: AnnouncementAdapter

    @Inject
    lateinit var assignAdapter: AssignmentAdapter


    var user: User? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            user = (it.getSerializable(Constants.USERS_BUNDLE_OBJ) as User?)
        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DashBoardFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getData()
        setData()
        observeData()
        setMyClickListeners()
    }

    private fun setMyClickListeners() {


        binding.categoriesCard.setOnClickListener {

            activity?.showCustomDialog(
                "Coomig soon",
                "These functionalities will be added in future updates",
                true,
                DialogStyle.EMOTION,
                DialogType.WARNING,
                DialogAnimation.SHRINK
            )


        }

        binding.announcementViewAll.setOnClickListener {

            activity?.showCustomDialog(
                "Coomig soon",
                "These functionalities will be added in future updates",
                true,
                DialogStyle.EMOTION,
                DialogType.WARNING,
                DialogAnimation.SHRINK
            )


        }
        binding.courseViewAll.setOnClickListener {

            activity?.showCustomDialog(
                "Coomig soon",
                "These functionalities will be added in future updates",
                true,
                DialogStyle.EMOTION,
                DialogType.WARNING,
                DialogAnimation.SHRINK
            )


        }

        binding.assignViewAll.setOnClickListener {
            activity?.showCustomDialog(
                "Coomig soon",
                "These functionalities will be added in future updates",
                true,
                DialogStyle.EMOTION,
                DialogType.WARNING,
                DialogAnimation.SHRINK
            )

        }


    }

    private fun observeData() {


        viewModel.getAnnouncement.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { data ->

                        if (data.isEmpty()) {
                            binding.announcementRc.gone()
                            binding.announceCards.visible()
                        } else {
                            binding.announcementRc.visible()
                            binding.announceCards.gone()
                        }

                        announceAdapter.submitList(data)
                    }

                }
                is ResponseState.Error -> {
                    binding.announcementRc.gone()
                    binding.announceCards.visible()
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }
                }
                is ResponseState.Loading -> {
                }

            }
        })


        viewModel.getEvents.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { data ->

                        if (data.isEmpty()) {
                            binding.eventRc.gone()
                            binding.eventsCards.visible()
                        } else {
                            binding.eventRc.visible()
                            binding.eventsCards.gone()
                        }

                        eventAdapter.submitList(data)
                    }

                }
                is ResponseState.Error -> {
                    binding.eventRc.gone()
                    binding.eventsCards.visible()
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }
                }
                is ResponseState.Loading -> {
                }

            }
        })


        viewModel.getAssignment.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { data ->

                        if (data.isEmpty()) {
                            binding.assignRc.gone()
                            binding.assignCards.visible()
                        } else {
                            binding.assignRc.visible()
                            binding.assignCards.gone()
                        }

                        assignAdapter.submitList(data)
                    }

                }
                is ResponseState.Error -> {
                    binding.assignRc.gone()
                    binding.assignCards.visible()
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }
                }
                is ResponseState.Loading -> {
                }

            }
        })

        viewModel.getCourse.observe(viewLifecycleOwner, {
            when (it) {
                is ResponseState.Success -> {
                    binding.progressBar.gone()
                    it.data?.let { data ->

                        if (data.isEmpty()) {
                            binding.courcesRc.gone()
                            binding.courseCards.visible()
                        } else {
                            binding.courcesRc.visible()
                            binding.courseCards.gone()
                        }

                        courseAdapter.submitList(data)
                    }

                }
                is ResponseState.Error -> {
                    binding.courcesRc.gone()
                    binding.courseCards.visible()
                    binding.progressBar.gone()
                    it.message?.let { it1 -> context?.toast(it1) }
                }
                is ResponseState.Loading -> {
                }

            }
        })


    }

    private fun setData() {

        binding.announcementRc.apply {
            adapter = announceAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.eventRc.apply {
            adapter = eventAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.courcesRc.apply {
            adapter = courseAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
        binding.assignRc.apply {
            adapter = assignAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

    }

    private fun getData() {

        binding.progressBar.visible()

        user?.uid?.let { viewModel.getCourse(it) }
        user?.uid?.let { viewModel.getAssignments(it) }
        user?.uid?.let { viewModel.getAnnouncements(it) }
        user?.uid?.let { viewModel.getEvents(it) }


    }


}