package com.kasirku.pro.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.kasirku.pro.adapter.DeliveryAdapter
import com.kasirku.pro.data.entity.DeliveryStatus
import com.kasirku.pro.data.entity.Transaction
import com.kasirku.pro.databinding.FragmentDeliveryBinding
import com.kasirku.pro.viewmodel.DeliveryViewModel

class DeliveryFragment : Fragment() {

    private var _binding: FragmentDeliveryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DeliveryViewModel by viewModels()
    private lateinit var adapter: DeliveryAdapter
    private var allDeliveries: List<Transaction> = emptyList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDeliveryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DeliveryAdapter { transaction -> }
        binding.rvDeliveries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DeliveryFragment.adapter
        }

        viewModel.allDeliveries.observe(viewLifecycleOwner) { deliveries ->
            allDeliveries = deliveries
            adapter.submitList(deliveries)
            binding.tvEmpty.visibility = if (deliveries.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.chipGroupStatus.setOnCheckedStateChangeListener { _, checkedIds ->
            val filtered = when {
                checkedIds.contains(binding.chipWaiting.id) ->
                    allDeliveries.filter { it.deliveryStatus == DeliveryStatus.WAITING }
                checkedIds.contains(binding.chipInProgress.id) ->
                    allDeliveries.filter { it.deliveryStatus == DeliveryStatus.IN_PROGRESS }
                checkedIds.contains(binding.chipDelivered.id) ->
                    allDeliveries.filter { it.deliveryStatus == DeliveryStatus.DELIVERED }
                else -> allDeliveries
            }
            adapter.submitList(filtered)
            binding.tvEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
