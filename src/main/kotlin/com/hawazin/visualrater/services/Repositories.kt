package com.hawazin.visualrater.services

import com.hawazin.visualrater.models.db.Album
import com.hawazin.visualrater.models.db.Artist
import com.hawazin.visualrater.models.db.Song
import com.hawazin.visualrater.models.db.ArtistMetadataProjection
import com.hawazin.visualrater.models.db.ComparisonSong
import jakarta.persistence.NamedNativeQuery
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.query.Procedure
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*



interface ArtistRepository: JpaRepository<Artist, UUID>  {
    fun findByName(name:String) : Optional<Artist>
    fun findMetadataById(id:UUID) : Optional<ArtistMetadataProjection>
}


interface AlbumRepository: CrudRepository<Album, UUID> {
    fun findByArtistId(artistId: UUID?) : List<Album>
}

interface SongRepository:CrudRepository<Song,UUID> {
    fun findByAlbumId(albumId:UUID): Iterable<Song>?
    @Procedure(value = "get_other_artists_comparison_songs")
    fun findComparisonSongsForOtherArtists(songId:UUID, excludedArtistId:UUID) : Iterable<ComparisonSong>

    @Query(nativeQuery = true, value = "SELECT score as songScore, album_name as albumName, artist_name as artistName, album_thumbnail as thumbnail, song_name as songName FROM get_artist_comparison_songs(:songId, :artistId, :excludedAlbumId)  ")
    fun findComparisonSongsForSameArtist(@Param("songId")songId:UUID, @Param("artistId") artistId:UUID, @Param("excludedAlbumId") excludedAlbumId: UUID) : Iterable<ComparisonSong>

}
