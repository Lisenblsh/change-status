package com.example.changesatus.data.model

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenModel(
    @SerialName("access_token")
    val accessToken: String
)

data class TokenErrorModel(
    val error: String,
    @SerializedName("validation_sid")
    val validationSid: String = "",
    @SerializedName("error_description")
    val errorDescription: String = "",
    @SerializedName("captcha_sid")
    val captchaSid: String = "",
    @SerializedName("captcha_img")
    val captchaImg: String = ""
)

enum class TokenErrorType {
    NEED_VALIDATION {
        override fun getTitle() = "need_validation"
    },
    INVALID_CLIENT {
        override fun getTitle() = "invalid_client"
    },
    NEED_CAPTCHA {
        override fun getTitle() = "need_captcha"
    };

    abstract fun getTitle(): String
}
