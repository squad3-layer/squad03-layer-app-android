package com.example.feature.news.presentation.view

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.feature.news.databinding.ActivityFiltersBinding
import com.example.mylibrary.ds.text.DsText

class FiltersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFiltersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFiltersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupWindowInsets()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setToolbarTitle("Filtros", DsText.TextStyle.DESCRIPTION)
            setBackButton(show = true) {
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

    private fun setupListeners() {
        binding.buttonApply.setDsClickListener {
            // TODO: Aplicar os filtros selecionados e retornar para a HomeActivity
            finish()
        }

        binding.buttonClear.setDsClickListener {
            // TODO: limpar todos os filtros para o estado padrão
            clearFilters()
        }
    }

    private fun clearFilters() {
        // TODO: viewmodel.clearFilters()
        binding.chipOrderingPublishedAt.isChecked = true
        binding.chipCategoryGeneral.isChecked = true
        binding.chipLangPt.isChecked = true
        binding.inputStartDate.setText("")
        binding.inputEndDate.setText("")
    }
}