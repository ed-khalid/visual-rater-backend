package com.hawazin.visrater.services

import com.hawazin.visrater.models.graphql.NewAlbumInput
import com.hawazin.visrater.models.graphql.SongInput
import com.hawazin.visrater.models.db.*
import com.hawazin.visrater.models.graphql.ArtistInput
import org.springframework.stereotype.Service
import java.util.*


@Service
class MusicService(private val songRepo:SongRepository , private val albumRepo: AlbumRepository, private val artistRepo:ArtistRepository) {

    fun readArtists() : Iterable<Artist> = artistRepo.findAll()
    fun readAlbumsForArtist(artist:Artist) : Iterable<Album> = albumRepo.findByArtistId(artist.id)
    fun deleteSongById(id:UUID) : Boolean
    {
        songRepo.deleteById(id)
        return true
    }

    fun deleteAlbumById(id:UUID): Boolean
    {
        albumRepo.deleteById(id)
        return true
    }


    fun updateSong(songInput: SongInput) : Song
    {
        val song = songRepo.findById(songInput.id).get()
        song.score = songInput.score ?: song.score
        // if input score is null, ignroe update, if it's a negative number, nullify score
        if (song.score!! < 0) {
            song.score = null
        }
        song.name = songInput.name ?: song.name
        song.number = songInput.number ?: song.number
        songRepo.save(song)
        return song
    }

    fun createArtist(artistInput: ArtistInput): Artist
    {
        var artist:Artist = artistInput.let { Artist(id = UUID.randomUUID(), name= it.name, vendorId = it.vendorId , thumbnail = it.thumbnail    )   }
        return artistRepo.save(artist)
    }

    fun createAlbum(albumInput: NewAlbumInput) : Album
    {
        val artist = artistRepo.findById(albumInput.artistId)
        if (artist.isPresent()) {
            var album = albumInput.let  { Album(id = UUID.randomUUID(), vendorId =  it.vendorId, isComplete=false, name = it.name, year= it.year, artist = artist.get(), thumbnail = it.thumbnail) }
            if (album.vendorId != null) {
                val existingAlbum = albumRepo.findByVendorId(album.vendorId!!)
                if (existingAlbum != null) {
                    album = existingAlbum
                } else {
                    albumRepo.save(album)
                }
            }
            var songs = albumInput.songs.map { Song( id =  UUID.randomUUID(),  vendorId = it.vendorId, name = it.name, album = album, artist = artist.get(), score = it.score, number= it.number, discNumber = it.discNumber   ) }
            songRepo.saveAll(songs)
            album.songs = songs.toMutableList()
            return album
        } else {
            throw Error("Artist ${albumInput.artistId} not found ")
        }
    }

//    fun createSong(spotifySong:NewSongInput) : Song
//    {
//        var album:Album? = null
//        var artist:Artist = spotifySong.artist.let { Artist(id = UUID.randomUUID(), name= it.name, vendorId = it.vendorId , thumbnail = it.thumbnail    )   }
//        if (artist.vendorId != null) {
//            val existingArtist  = artistRepo.findByVendorId(artist.vendorId!!)
//            if (existingArtist != null) {
//                artist = existingArtist;
//            } else {
//                artistRepo.save(artist)
//            }
//        }
//        if (spotifySong.album != null) {
//            album = spotifySong.album.let  { Album(id = UUID.randomUUID(), vendorId =  it.vendorId, isComplete=false, name = it.name, year= it.year, artist = artist, thumbnail = it.thumbnail) }
//            if (album.vendorId != null) {
//                val existingAlbum = albumRepo.findByVendorId(album.vendorId!!)
//                if (existingAlbum != null) {
//                    album = existingAlbum
//                } else {
//                    albumRepo.save(album)
//                }
//            }
//        }
//        var song:Song  = spotifySong.let { Song( id =  UUID.randomUUID(),  vendorId = it.vendorId, name = it.name, album = album, artist = artist, score = it.score, number= it.number, discNumber = it.discNumber   ) }
//        songRepo.save(song)
//        return song
//    }
}