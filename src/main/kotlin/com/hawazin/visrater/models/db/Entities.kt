package com.hawazin.visrater.models.db

import org.hibernate.annotations.DynamicUpdate
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.ManyToOne


@Entity
class Artist(
    @Id var id: UUID,
    var vendorId:String?,
    var name:String,
    var thumbnail:String?
)

@Entity
class Album(
    @Id var id:UUID,
    var vendorId:String?,
    var name:String,
    var thumbnail:String?,
    var year:Int,
    @ManyToOne var artist:Artist
)

@Entity
@DynamicUpdate
class Song(
    @Id var id:UUID,
    var vendorId:String? = null,
    var name:String? = null,
    @ManyToOne var album:Album? =null,
    @ManyToOne var artist:Artist? = null,
    var score:Double
)