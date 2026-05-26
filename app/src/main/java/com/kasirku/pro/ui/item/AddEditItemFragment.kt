package com.kasirku.pro.ui.item

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.zxing.integration.android.IntentIntegrator
import com.kasirku.pro.data.entity.Category
import com.kasirku.pro.data.entity.Item
import com.kasirku.pro.databinding.FragmentAddEditItemBinding
import com.kasirku.pro.util.ImageUtils
import com.kasirku.pro.viewmodel.ItemViewModel
import kotlinx.coroutines.launch
import java.io.File

class AddEditItemFragment : Fragment() {

    private var _binding: FragmentAddEditItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ItemViewModel by viewModels()
    private var editingItem: Item? = null
    private var selectedImagePath: String = ""
    private var photoUri: Uri? = null
    private var categories: List<Category> = emptyList()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val path = ImageUtils.compressImage(requireContext(), it)
            if (path != null) {
                selectedImagePath = path
                Glide.with(this).load(File(path)).centerCrop().into(binding.ivItemImage)
            }
        }
    }

    private val takePhotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) {
            val path = ImageUtils.compressImage(requireContext(), photoUri!!)
            if (path != null) {
                selectedImagePath = path
                Glide.with(this).load(File(path)).centerCrop().into(binding.ivItemImage)
            }
        }
    }

    private val cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) takePhoto()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemId = arguments?.getString("itemId") ?: ""

        viewModel.allCategories.observe(viewLifecycleOwner) { cats ->
            categories = cats
            val names = cats.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, names)
            binding.spinnerCategory.adapter = adapter
        }

        if (itemId.isNotEmpty()) {
            binding.tvTitle.text = "Edit Item"
            lifecycleScope.launch {
                val item = viewModel.getItemById(itemId)
                item?.let { populateForm(it) }
            }
        }

        binding.btnGallery.setOnClickListener { pickImageLauncher.launch("image/*") }
        binding.btnCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                takePhoto()
            } else {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }

        binding.btnScanBarcode.setOnClickListener {
            val integrator = IntentIntegrator.forSupportFragment(this)
            integrator.setPrompt("Scan barcode")
            integrator.initiateScan()
        }

        binding.btnSave.setOnClickListener { saveItem() }

        viewModel.saveResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "Item tersimpan", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
            result.onFailure { e ->
                Toast.makeText(requireContext(), "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun populateForm(item: Item) {
        editingItem = item
        binding.etItemName.setText(item.name)
        binding.etSellPrice.setText(item.price.toString())
        binding.etCostPrice.setText(item.costPrice.toString())
        binding.etStock.setText(item.stock.toString())
        binding.etSupplier.setText(item.supplierName)
        binding.etBarcode.setText(item.barcode)
        binding.etDescription.setText(item.description)
        selectedImagePath = item.imagePath

        if (item.imagePath.isNotEmpty()) {
            Glide.with(this).load(File(item.imagePath)).centerCrop().into(binding.ivItemImage)
        }

        val catIndex = categories.indexOfFirst { it.id == item.categoryId }
        if (catIndex >= 0) binding.spinnerCategory.setSelection(catIndex)
    }

    private fun takePhoto() {
        val file = ImageUtils.createImageFile(requireContext())
        photoUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)
        takePhotoLauncher.launch(photoUri)
    }

    private fun saveItem() {
        val name = binding.etItemName.text.toString().trim()
        val sellPrice = binding.etSellPrice.text.toString().toDoubleOrNull() ?: 0.0
        val costPrice = binding.etCostPrice.text.toString().toDoubleOrNull() ?: 0.0
        val stock = binding.etStock.text.toString().toIntOrNull() ?: 0
        val supplier = binding.etSupplier.text.toString().trim()
        val barcode = binding.etBarcode.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Nama item wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedCategoryIndex = binding.spinnerCategory.selectedItemPosition
        val categoryId = if (selectedCategoryIndex >= 0 && selectedCategoryIndex < categories.size) {
            categories[selectedCategoryIndex].id
        } else ""

        val item = editingItem?.copy(
            name = name,
            price = sellPrice,
            costPrice = costPrice,
            stock = stock,
            categoryId = categoryId,
            imagePath = selectedImagePath,
            supplierName = supplier,
            barcode = barcode,
            description = description,
            updatedAt = System.currentTimeMillis(),
            isSynced = false
        ) ?: Item(
            name = name,
            price = sellPrice,
            costPrice = costPrice,
            stock = stock,
            categoryId = categoryId,
            imagePath = selectedImagePath,
            supplierName = supplier,
            barcode = barcode,
            description = description
        )

        viewModel.saveItem(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
