package com.delirium.films.description

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.delirium.films.R
import com.delirium.films.databinding.FragmentDescriptionFilmBinding
import com.delirium.films.model.FilmInfo

class DescriptionFilm : Fragment() {

    private lateinit var viewBinding: FragmentDescriptionFilmBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val bindingDesc = DataBindingUtil.inflate<FragmentDescriptionFilmBinding>(inflater,
            R.layout.fragment_description_film, container, false)

        viewBinding = FragmentDescriptionFilmBinding.inflate(inflater, container, false)
        val film = arguments?.get("film")

        if (film is FilmInfo) {
            Log.i("DESCRIPTION_FILM", "$film")
            viewBinding.filmInfo = film
        }

        return viewBinding.root
    }
}