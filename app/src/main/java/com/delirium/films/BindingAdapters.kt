package com.delirium.films

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import jp.wasabeef.picasso.transformations.MaskTransformation

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("app:url_corner")
    fun loadImageWithCorner(view: ImageView, url: String?) {
        val transform: Transformation = MaskTransformation(view.context, R.drawable.rounded_corner)
        url?.let {
            Picasso.with(view.context).load(it).transform(transform).into(view)
        } ?: view.setImageResource(R.drawable.not_found_foreground)
    }

    @JvmStatic
    @BindingAdapter("app:url")
    fun loadImage(view: ImageView, url: String?) {
        url?.let {
            Picasso.with(view.context).load(it).into(view)
        } ?: view.setImageResource(R.drawable.not_found_foreground)
    }
}