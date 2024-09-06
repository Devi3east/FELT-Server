package example.com.dao.event_likes

import example.com.dao.event.EventTable
import example.com.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object EventLikesTable: Table(name = "event_likes") {
    val likeId = long(name = "like_id").uniqueIndex()
    val eventId = long(name = "event_id").references(ref = EventTable.eventId, onDelete = ReferenceOption.CASCADE)
    val userId = long(name = "user_id").references(ref = UserTable.userId, onDelete = ReferenceOption.CASCADE)
    val likeDate = datetime(name = "like_date").defaultExpression(defaultValue = CurrentDateTime)
}