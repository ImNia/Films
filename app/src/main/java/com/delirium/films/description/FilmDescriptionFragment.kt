package com.delirium.films.description

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.delirium.films.R
import com.delirium.films.databinding.FragmentFilmDescriptionBinding
import com.squareup.picasso.Picasso

class FilmDescriptionFragment : Fragment() {

    private var _viewBinding: FragmentFilmDescriptionBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val args by navArgs<FilmDescriptionFragmentArgs>()
    private val film by lazy { args.selectedFilm }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentFilmDescriptionBinding.inflate(inflater, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.with(viewBinding.imageFilmDesc.context)
                .load(film.image_url)
                .placeholder(R.drawable.not_found)
                .into(viewBinding.imageFilmDesc)

        viewBinding.originName.text = film.localized_name

        val genresWithYear = joinGenreWithYear()
        viewBinding.genresWithYear.text = getString(
            R.string.genres_with_year, genresWithYear
        )

        val rating: String = getString(
                R.string.rating,
                film.rating?.let {
                    String.format("%.1f", it.toDouble()).replace(",", ".")
                } ?: getString(R.string.rating_without_value)
            )
        viewBinding.rating.text = rating

        viewBinding.descriptionFilm.text = film.description
            ?: getString(R.string.without_description)
    }

    private fun joinGenreWithYear() : String {
        var genreWithYear : MutableList<String> = mutableListOf()
        if (film.genres.isNotEmpty()) {
            genreWithYear = film.genres as MutableList<String>
        }
        film.year?.let {
            genreWithYear.add(it + " " + getString(R.string.year))
        }
        return genreWithYear.joinToString()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}