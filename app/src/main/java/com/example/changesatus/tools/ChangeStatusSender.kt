package com.example.changesatus.tools

import com.example.changesatus.data.network.RetrofitService

class ChangeStatusSender(private val token: String) {
    suspend fun setStatus(value: VkStatus) {
        retrofitService.changeStatus(value.getValue(), token)
    }

    suspend fun getStatus(): VkStatus {
        return VkStatus.ALL
    }

    companion object {
        private const val baseUrl = "https://api.vk.me/method/"
        private const val userAgent =
            "VKAndroidApp/1.777-777 (Android 777; SDK 777; bagosi; 1; ru; 777x777)"
        private var retrofitService: RetrofitService = RetrofitService.create(baseUrl = baseUrl, userAgent = userAgent)
    }
}

enum class VkStatus {
    ALL {
        override fun getValue(): String = "all"
    },
    FRIENDS {
        override fun getValue(): String = "friends"
    },
    FRIENDS_OF_FRIENDS {
        override fun getValue(): String = "friends_of_friends"
    },
    ONLY_ME {
        override fun getValue(): String = "only_me"
    };

    abstract fun getValue(): String
}