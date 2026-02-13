package com.example.feature.news.presentation.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feature.news.databinding.ActivityFiltersBinding
import com.example.feature.news.presentation.viewModel.FiltersViewModel
import com.example.mylibrary.ds.chip.DsChip
import com.example.mylibrary.ds.chip.DsChipGroup
import com.example.mylibrary.ds.text.DsText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FiltersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFiltersBinding
    private val viewModel: FiltersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFiltersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.analyticsHelper.logScreenView("screen_view_filters")

        setupWindowInsets()
        setupToolbar()
        setupChips()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setToolbarTitle("Filtros", DsText.TextStyle.HEADER)
            setBackButton(show = true) {
                viewModel.analyticsHelper.logEvent("button_click", mapOf("button_name" to "filters_back_button"))
                finish()
            }
        }
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.screenViewFilters) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupChips() {
        val orderingOptions = viewModel.getOrderingOptions()

        if (orderingOptions.isNotEmpty()) {
            binding.chipsOrdering.addChips(orderingOptions)

            val selectedOrderingPosition = viewModel.selectedOrdering.value ?: 0
            binding.chipsOrdering.selectChip(selectedOrderingPosition)
        }

        val categories = viewModel.getCategories()

        if(categories.isNotEmpty()) {
            binding.chipsCategory.addChips(categories)
            val selectedCategoryPosition = viewModel.selectedCategory.value ?: 0
            binding.chipsCategory.selectChip(selectedCategoryPosition)
        }
    }

    private fun setupListeners() {
        binding.buttonApply.setDsClickListener {
            viewModel.analyticsHelper.logEvent("button_click", mapOf("button_name" to "filters_apply_button"))
            viewModel.applyFilters()

            val filters = viewModel.getCurrentFilters()

            val resultIntent = Intent().apply {
                putExtra(EXTRA_CATEGORY, filters.category)
                putExtra(EXTRA_SHOULD_REVERSE, filters.shouldReverseOrder)
            }

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.buttonClear.setDsClickListener {
            viewModel.analyticsHelper.logEvent("button_click", mapOf("button_name" to "filters_clear_button"))
            viewModel.clearFilters()
            binding.chipsOrdering.selectChip(0)
            binding.chipsCategory.selectChip(0)
        }


        binding.chipsOrdering.setOnChipSelectionListener(
            object : DsChipGroup.OnChipSelectionListener {
                override fun onChipSelected(
                    chip: DsChip,
                    position: Int,
                    isSelected: Boolean
                ) {
                    if (isSelected) {
                        val orderingOptions = viewModel.getOrderingOptions()
                        val selectedOption = orderingOptions.getOrNull(position) ?: "unknown"
                        chip.contentDescription = "$selectedOption, selecionado"

                        viewModel.analyticsHelper.logEvent("chip_click", mapOf(
                            "chip_type" to "ordering",
                            "chip_value" to selectedOption
                        ))
                        viewModel.onOrderingSelected(position)
                    }
                }
            }
        )

        binding.chipsCategory.setOnChipSelectionListener(
            object : DsChipGroup.OnChipSelectionListener {
                override fun onChipSelected(
                    chip: DsChip,
                    position: Int,
                    isSelected: Boolean
                ) {
                    if (isSelected) {
                        val categories = viewModel.getCategories()
                        val selectedCategory = categories.getOrNull(position) ?: "unknown"
                        chip.contentDescription = "$selectedCategory, selecionado"

                        viewModel.analyticsHelper.logEvent("chip_click", mapOf(
                            "chip_type" to "category",
                            "chip_value" to selectedCategory
                        ))
                        viewModel.onCategorySelected(position)
                    }
                }
            }
        )
    }

    companion object {
        const val EXTRA_CATEGORY = "extra_category"
        const val EXTRA_SHOULD_REVERSE = "extra_should_reverse"
    }
}