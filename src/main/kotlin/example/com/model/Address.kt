package example.com.model

import kotlinx.serialization.Serializable

@Serializable
data class Address(
    val lineOne: String,
    val lineTwo: String? = null,
    val city: String,
    val state: String,
    val zipCode: String
)