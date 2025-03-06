package com.coldrifting.sirl

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel: ViewModel() {

    private var _title = MutableStateFlow("App Top Bar")
    var title = _title.asStateFlow()

    private var _editAction = MutableStateFlow<(() -> Unit)?>(null)
    var editAction = _editAction.asStateFlow()

    fun setEditAction(editAction: () -> Unit) {
        _editAction.value = editAction
    }

    fun setTitle(title: String) {
        _title.value = title
    }
}