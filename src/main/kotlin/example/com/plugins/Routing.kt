package example.com.plugins

import example.com.route.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRouting()
        followsRouting()
        eventRouting()
        profileRouting()
        eventCommentsRouting()
        eventLikesRouting()
        static {
            resources("static")
        }
    }
}
