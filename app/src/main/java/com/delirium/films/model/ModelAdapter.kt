package com.delirium.films.model

open class ModelAdapter

class Genres(val genre: String) : ModelAdapter()
class Films(val film: FilmInfo) : ModelAdapter()
class Titles(val titleBlock: Title) : ModelAdapter()