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

        film.image_url?.let {
            Picasso.with(viewBinding.imageFilmDesc.context)
                .load(it)
                .error(R.drawable.not_found)
                .into(viewBinding.imageFilmDesc)
        } ?: viewBinding.imageFilmDesc.setImageResource(R.drawable.not_found)

        viewBinding.originName.text = film.localized_name

        val genresWithYear = listOfNotNull(film.genres.joinToString(), film.year)
            .filter { element: String -> element != "" }
        viewBinding.genresWithYear?.text = getString(
            R.string.genres_with_year, genresWithYear.joinToString()
        )

        viewBinding.rating.text = film.rating?.let {
            getString(
                R.string.rating,
                String.format("%.1f", film.rating!!.toDouble()).replace(",", ".")
            )
        } ?: getString(R.string.rating_without_value)

        viewBinding.descriptionFilm.text = film.description
            ?: getString(R.string.without_description)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}