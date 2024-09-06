package example.com.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import example.com.dao.user.UserDao
import example.com.model.AuthResponse
import example.com.util.Constants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject
import java.util.*
import java.util.concurrent.TimeUnit

private val jwtAudience = System.getenv("jwt.audience")
private val jwtIssuer = System.getenv("jwt.domain")
private val jwtSecret = System.getenv("jwt.secret")

//private const val CLAIM = "email"

fun Application.configureSecurity() {
    val userDao by inject<UserDao>()

    authentication {
        jwt {
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                val email = credential.payload.getClaim(Constants.JWT_CLAIM_EMAIL).asString()
                if (email != null) {
                    val userExists = userDao.findByEmail(email) != null
                    val isValidAudience = credential.payload.audience.contains(jwtAudience)

                    if (userExists && isValidAudience) {
                        JWTPrincipal(payload = credential.payload)
                    } else {
                        null
                    }
                } else {
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(
                        error = "Token is not valid or has expired."
                    )
                )
            }
        }
    }

}

fun generateToken(email: String): String {
    // Set the expiration time to be 1 hour from now
    val expirationTime = Date(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1))

    // Set the expiration time to be 20 seconds from now for testing
    //val expirationTime = Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(20))


    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtIssuer)
        .withClaim(Constants.JWT_CLAIM_EMAIL, email)
        .withExpiresAt(expirationTime)
        .sign(Algorithm.HMAC256(jwtSecret))
}