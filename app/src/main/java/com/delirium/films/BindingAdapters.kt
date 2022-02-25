package com.delirium.films

import android.widget.ImageView
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.MaskTransformation

object BindingAdapters {
    @JvmStatic
    fun loadImageWithCorner(view: ImageView, url: String?) {
        val transform: Transformation = MaskTransformation(view.context, R.drawable.rounded_corner)
        url?.let {
            Picasso.with(view.context).load(it).transform(transform).error(R.drawable.not_found).into(view)
        } ?: view.setImageResource(R.drawable.not_found)
    }

    @JvmStatic
    fun loadImage(view: ImageView, url: String?) {
        url?.let {
            Picasso.with(view.context).load(it).into(view)
        } ?: view.setImageResource(R.drawable.not_found_foreground)
    }
}