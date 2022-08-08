package com.example.changesatus.tools

import com.example.changesatus.data.model.TokenErrorModel
import com.example.changesatus.data.model.TokenErrorType
import com.example.changesatus.data.model.TokenModel
import com.example.changesatus.data.network.RetrofitService
import com.google.gson.Gson
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val jsonSerialization = Json { ignoreUnknownKeys = true }

class TokenReceiver {

    @ExperimentalSerializationApi
    suspend fun getToken(
        username: String,
        password: String,
        code: String? = null,
        captchaSid: String? = null,
        captchaKey: String? = null
    ): String? {
        val baseUrl = "https://oauth.vk.com/"
        val retrofitService = RetrofitService.create(baseUrl)
        val response = retrofitService.getToken(username, password, code, captchaSid, captchaKey)

        if (response.isSuccessful) {
            val json: TokenModel? = response.body()
                ?.let { jsonSerialization.decodeFromString(it) }
            return json?.accessToken
        } else if (response.code() == 401) {
            val json: TokenErrorModel? =
                Gson().fromJson(response.errorBody()?.charStream(), TokenErrorModel::class.java)
            when (json?.error) {
                TokenErrorType.NEED_VALIDATION.getTitle() -> {
                    validatePhone(json.validationSid)
                    throw TokenException(
                        code = TokenExceptionType.TWO_FA_REQ,
                        validationSid = json.validationSid,
                        message = json.errorDescription
                    )
                }
                TokenErrorType.INVALID_CLIENT.getTitle() -> {
                    throw TokenException(
                        code = TokenExceptionType.REGISTRATION_ERROR,
                        message = json.errorDescription
                    )
                }
                TokenErrorType.NEED_CAPTCHA.getTitle() -> {
                    throw TokenException(
                        code = TokenExceptionType.NEED_CAPTCHA,
                        captchaSid = json.captchaSid,
                        captchaImg = json.captchaImg,
                        message = json.errorDescription
                    )
                }
                else -> throw TokenException(
                    TokenExceptionType.REQUEST_ERR,
                    "",
                    "Я хуй знанет че произошло, но буду разбираться\n${json}"
                )
            }
        } else {
            throw TokenException(
                TokenExceptionType.REQUEST_ERR, "", "Я хуй знанет че произошло, но буду разбираться"
            )
        }
    }

    private suspend fun validatePhone(sid: String) {
        val baseUrl = "https://api.vk.com/"
        val retrofitService = RetrofitService.create(baseUrl)
        retrofitService.validatePhone(sid)
    }
}