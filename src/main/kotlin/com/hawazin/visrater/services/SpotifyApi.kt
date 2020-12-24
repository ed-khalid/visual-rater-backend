package com.hawazin.visrater.services

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.hawazin.visrater.graphql.VisRaterGraphQLError
import com.hawazin.visrater.models.graphql.Album
import com.hawazin.visrater.models.graphql.Artist
import com.hawazin.visrater.models.graphql.PaginatedSearchResult
import com.hawazin.visrater.models.graphql.Track
import com.hawazin.visrater.configurations.SpotifyConfiguration
import com.hawazin.visrater.graphql.CustomRestTemplateCustomizer
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@JsonIgnoreProperties(ignoreUnknown = true)
data class SpotifyAuthToken(@JsonProperty("access_token") val accessToken:String?, @JsonProperty("expires_in") val expiresIn:Int?)


@Service
class SpotifyApi(private val configuration: SpotifyConfiguration) {

    private var token:SpotifyAuthToken? = null
    private val accountsTemplate: RestTemplate = RestTemplateBuilder()
            .rootUri(configuration.accounts)
            .basicAuthentication(configuration.client.id, configuration.client.secret)
            .build()

    private fun api() : RestTemplate {
        if (token == null) {
            token = requestToken()
        }
        return RestTemplateBuilder(CustomRestTemplateCustomizer())
                .rootUri(configuration.api)
                .defaultHeader("Authorization", "Bearer ${token?.accessToken}")
                .build()
    }

    private fun <R> makeCall(call: () -> R ): R {
        return try {
            call();
        }
        catch(hex:HttpClientErrorException.Unauthorized)
        {
            token = requestToken();
            call();
        }
    }

    private fun requestToken() : SpotifyAuthToken?  {
        val postParams = LinkedMultiValueMap<String,String>()
        postParams.add("grant_type", "client_credentials")
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val request = HttpEntity(postParams,headers)
        return accountsTemplate.postForObject<SpotifyAuthToken>("/api/token", request, SpotifyAuthToken::class.java)
    }

    fun searchArtist(name:String) :List<Artist> {
        val response = makeCall { api().getForObject<ArtistListResponse>("/search?q={name}&type=artist", name) }
        return response.artists.items
    }

    fun getAlbumsForArtist(artistId:String, _offset:Int) : PaginatedSearchResult {
        if (_offset  < 0) {
            throw VisRaterGraphQLError("Album Page Number must be positive")
        }
        val offset = _offset*12
        val response = makeCall { api().getForObject<AlbumList>("/artists/{artistId}/albums?limit=12&county=US&include_groups=album&offset={offset}", artistId, offset) }
        return PaginatedSearchResult(
         results = response.items
            , pageNumber =  _offset)
    }

    fun getTracksForAlbum(albumId:String) : List<Track> {
        val response = makeCall { api().getForObject<TrackList>("/albums/{albumId}/tracks", albumId) }
        return response.items
    }
}

data class AlbumList(val items:List<Album>)
data class ArtistListResponse(val artists:ArtistList)
data class ArtistList(val items:List<Artist>)
data class TrackList(val items:List<Track>)
