package com.delirium.films.description

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.delirium.films.BindingAdapters
import com.delirium.films.databinding.FragmentDescriptionFilmBinding
import com.delirium.films.model.FilmInfo

class DescriptionFilm : Fragment() {

    private lateinit var viewBinding: FragmentDescriptionFilmBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = FragmentDescriptionFilmBinding.inflate(inflater, container, false)
        val film = arguments?.get("film")

        if (film is FilmInfo) {
            BindingAdapters.loadImageWithCorner(viewBinding.imageFilm, film.image_url)
            viewBinding.descriptionFilm.text = film.description
            viewBinding.originName.text = film.name
            viewBinding.year.text = film.year
            viewBinding.rating.text = film.rating
        }

        return viewBinding.root
    }
}