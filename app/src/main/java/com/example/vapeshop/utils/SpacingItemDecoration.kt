package com.example.vapeshop.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpacingItemDecoration(private val spacing: Int, private val spanCount: Int) :
    RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        outRect.left = spacing * (spanCount - column) / spanCount
        outRect.right = spacing * (column + 1) / spanCount

        if (position >= spanCount) {
            outRect.top = spacing
        } else {
            outRect.top = spacing / 2
        }
    }
}