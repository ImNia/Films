package com.delirium.films.description

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delirium.films.BindingAdapters
import com.delirium.films.R
import com.delirium.films.databinding.FragmentDescriptionFilmBinding
import com.delirium.films.model.FilmInfo

class DescriptionFilmFragment : Fragment() {

    private lateinit var viewBinding: FragmentDescriptionFilmBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = FragmentDescriptionFilmBinding.inflate(inflater, container, false)
        val film = arguments?.get("film")

        if (film is FilmInfo) {
            BindingAdapters.loadImageWithCorner(viewBinding.imageFilm, film.image_url)
            viewBinding.originName.text = film.localized_name

            if(film.genres.isNotEmpty()) {
                viewBinding.year.text = getString(R.string.year, film.genres.first(), film.year)
            } else{
                viewBinding.year.text = getString(R.string.year_without_genre, film.year)
            }
            viewBinding.rating.text = film.rating?.let {
                getString(R.string.rating,
                    String.format("%.1f", film.rating!!.toDouble()).replace(",", ".")
                )
            } ?: getString(R.string.rating_without_value)

            viewBinding.descriptionFilm.text = film.description
                ?: getString(R.string.without_description)
        }
        return viewBinding.root
    }
}