package com.hawazin.visualrater.models.db

import jakarta.persistence.*
import java.util.*

@Entity()
@Table(name = "genre")
class GenreTable(@Id @GeneratedValue override var id: Int, @Enumerated(
    EnumType.STRING) override var value:Genre) : DatabaseEnum<Genre>(id,value)

enum class Genre {
    POP, METAL, ROCK, RAP, ARABIC_POP, ARABIC_CLASSICAL, LATOM
}
