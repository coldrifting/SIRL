package com.coldrifting.sirl.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.coldrifting.sirl.AppApplication
import com.coldrifting.sirl.repo.AppRepo

class AppViewModel(repository: AppRepo) : ViewModel() {
    val items: ItemViewModel = ItemViewModel(repository)
    val recipes: RecipeViewModel = RecipeViewModel(repository)
    val stores: StoreViewModel = StoreViewModel(repository)
    val cart: CartViewModel = CartViewModel(repository)

    companion object AppViewModelProvider {
        // Fetches the application singleton and extracts the repository in it
        val Factory = viewModelFactory {
            val appKey = ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY
            initializer {
                AppViewModel(
                    (this[appKey] as AppApplication).appRepository
                )
            }
        }
    }
}