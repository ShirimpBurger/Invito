package com.hbs.invito.data.presentation

data class RecyclerViewItem<T>(val item:T) {
    var isVisible = false
    var labeling = ""
}