package com.example.travelplanner.data.api

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.http.*

class PostServiceImpl(
    private val client: HttpClient
): PostService {

    override suspend fun getPosts(): List<PostResponse> {
        return try {
            client.get { url(HttpRoutes.RECOMMEND) }
        } catch (e: RedirectResponseException) {
            //3xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ClientRequestException){
            //4xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: ServerResponseException){
            //5xx
            println("Error: ${e.response.status.description}")
            emptyList()
        } catch (e: Exception){
            println("Error: ${e.message}")
            emptyList()
        }
    }

    override suspend fun createPost(postRequest: PostRequest): List<PostResponse>? {
        return try {
            client.post<List<PostResponse>> {
                url(HttpRoutes.RECOMMEND)
                contentType(ContentType.Application.Json)
                body = postRequest
            }
        } catch (e: RedirectResponseException) {
            //3xx
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ClientRequestException){
            //4xx
            println("Error: ${e.response.status.description}")
            null
        } catch (e: ServerResponseException){
            //5xx
            println("Error: ${e.response.status.description}")
            null
        } catch (e: Exception){
            println("Error: ${e.message}")
            null
        }
    }
}