package com.delirium.films.favorite

import com.delirium.films.RealmConfiguration
import com.delirium.films.model.FavoriteFilm
import com.delirium.films.model.FilmInfo
import io.realm.Realm

class FavoriteModel(val presenter: FavoritePresenter) {
    private val configDB: RealmConfiguration = RealmConfiguration()
    private val realmDB: Realm = Realm.getInstance(configDB.getConfigDB())

    fun deleteFilmInFavorite(film: FilmInfo): List<FilmInfo> {
        realmDB.beginTransaction()
        val removeObject = realmDB.where(FavoriteFilm::class.java)
            .equalTo("id", film.id)
            .findFirst()
        removeObject?.deleteFromRealm()
        realmDB.commitTransaction()
        return getAllFavorite()
    }

    fun getAllFavorite(): List<FilmInfo> {
        val data = realmDB.where(FavoriteFilm::class.java).findAll()

        return converterFavoriteFilmToFilmInfo(data)
    }

    private fun converterFavoriteFilmToFilmInfo(favoriteFilm: List<FavoriteFilm>)
            : List<FilmInfo> {
        return favoriteFilm.map {
            val genres = it.genres?.split(",") ?: listOf()
            genres.forEach { it.trim() }

            FilmInfo(
                id = it.id,
                localized_name = it.localized_name,
                name = it.name,
                year = it.year,
                rating = it.rating,
                image_url = it.image_url,
                description = it.description,
                genres = genres,
                isFavorite = it.isFavorite
            )
        }
    }
}