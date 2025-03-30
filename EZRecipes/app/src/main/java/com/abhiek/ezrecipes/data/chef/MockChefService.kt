package com.abhiek.ezrecipes.data.chef

import com.abhiek.ezrecipes.data.models.*
import com.abhiek.ezrecipes.utils.Constants
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

object MockChefService: ChefService {
    var isSuccess = true
    var isEmailVerified = true

    val chef = Constants.Mocks.CHEF
    val loginResponse = LoginResponse(
        uid = chef.uid,
        token = chef.token,
        emailVerified = isEmailVerified
    )
    val chefEmailResponse = ChefEmailResponse(
        kind = "identitytoolkit#GetOobConfirmationCodeResponse",
        email = chef.email,
        token = chef.token
    )

    private const val TOKEN_ERROR_STRING =
        "{\"error\":\"Invalid Firebase token provided: Error: Decoding Firebase ID token failed. Make sure you passed the entire string JWT which represents an ID token. See https://firebase.google.com/docs/auth/admin/verify-id-tokens for details on how to retrieve an ID token.\"}"
    val tokenError =
        RecipeError(error = "Invalid Firebase token provided: Error: Decoding Firebase ID token failed. Make sure you passed the entire string JWT which represents an ID token. See https://firebase.google.com/docs/auth/admin/verify-id-tokens for details on how to retrieve an ID token.")

    override suspend fun getChef(token: String): Response<Chef> {
        return if (isSuccess) {
            Response.success(chef.copy(
                emailVerified = isEmailVerified
            ))
        } else {
            Response.error(401, TOKEN_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun createChef(credentials: LoginCredentials): Response<LoginResponse> {
        return if (isSuccess) {
            Response.success(loginResponse.copy(
                emailVerified = isEmailVerified
            ))
        } else {
            Response.error(401, TOKEN_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun updateChef(
        fields: ChefUpdate,
        token: String?
    ): Response<ChefEmailResponse> {
        return if (isSuccess) {
            Response.success(chefEmailResponse)
        } else {
            Response.error(401, TOKEN_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun deleteChef(token: String): Response<Void> {
        return if (isSuccess) {
            Response.success(null)
        } else {
            Response.error(401, TOKEN_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun verifyEmail(token: String): Response<ChefEmailResponse> {
        return if (isSuccess) {
            Response.success(chefEmailResponse)
        } else {
            Response.error(401, TOKEN_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun login(credentials: LoginCredentials): Response<LoginResponse> {
        return if (isSuccess) {
            Response.success(loginResponse.copy(
                emailVerified = isEmailVerified
            ))
        } else {
            Response.error(401, TOKEN_ERROR_STRING.toResponseBody())
        }
    }

    override suspend fun logout(token: String): Response<Void> {
        return if (isSuccess) {
            Response.success(null)
        } else {
            Response.error(401, TOKEN_ERROR_STRING.toResponseBody())
        }
    }
}
