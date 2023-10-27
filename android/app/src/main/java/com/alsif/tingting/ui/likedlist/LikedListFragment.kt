package com.alsif.tingting.ui.likedlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.alsif.tingting.R
import com.alsif.tingting.base.BaseFragment
import com.alsif.tingting.data.model.ConcertDto
import com.alsif.tingting.databinding.FragmentHomeBinding
import com.alsif.tingting.databinding.FragmentLikedListBinding
import com.alsif.tingting.ui.home.HomeFragmentDirections
import com.alsif.tingting.ui.home.HomeFragmentViewModel
import com.alsif.tingting.ui.home.tab.recyclerview.ConcertPagingAdapter
import com.alsif.tingting.ui.likedlist.recyclerview.LikedListPagingAdapter
import com.alsif.tingting.ui.login.LoginModalBottomSheet
import com.alsif.tingting.ui.main.MainActivity
import com.alsif.tingting.ui.main.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "LikedListFragment"
@AndroidEntryPoint
class LikedListFragment : BaseFragment<FragmentLikedListBinding>(FragmentLikedListBinding::bind, R.layout.fragment_liked_list) {
    private val viewModel: LikedListFragmentViewModel by viewModels()
    private val sharedViewModel: MainActivityViewModel by activityViewModels()
    private lateinit var likedListAdapter: LikedListPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: 뷰 붙음")
        
        if (mActivity.requireLogin()) {
            initRecyclerView()
            subscribe()
            setClickListeners()
            getLikedConcertList()
        }
    }

    override fun onResume() {
        getLikedConcertList()
        super.onResume()
    }

    private fun getLikedConcertList() {
        viewModel.getLikedConcertList()
    }

    private fun subscribe() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.likedListPagingDataFlow.collect {
                likedListAdapter.submitData(it)
            }
        }
    }

    private fun initRecyclerView() {
        likedListAdapter = LikedListPagingAdapter()
        binding.recyclerLikedList.apply {
            adapter = likedListAdapter
            layoutManager = LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setClickListeners() {
        likedListAdapter.itemClickListner = object: LikedListPagingAdapter.ItemClickListener {
            override fun onClick(view: View, concert: ConcertDto) {
                val action = LikedListFragmentDirections.actionLikedListFragmentToConcertDetailFragment(concert.concertSeq)
                findNavController().navigate(action)
            }
        }
        binding.layoutSwipeRefresh.setOnRefreshListener {
            getLikedConcertList()
            binding.layoutSwipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: 프레그먼트가 destroyView 되었습니다.")
        super.onDestroyView()
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy: 프레그먼트가 destroy 되었습니다.")
        super.onDestroy()
    }
}