package example.com.dao.event_comments

import example.com.dao.event.EventTable
import example.com.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object EventCommentsTable: Table(name = "event_comments") {
    val commentId = long(name = "comment_id").uniqueIndex()
    val eventId = long(name = "event_id").references(ref = EventTable.eventId, onDelete = ReferenceOption.CASCADE)
    val userId = long(name = "user_id").references(ref = UserTable.userId, onDelete = ReferenceOption.CASCADE)
    val content = varchar(name = "content", length = 300)
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
}

data class EventCommentRow(
    val commentId: Long,
    val content: String,
    val eventId: Long,
    val userId: Long,
    val name: String,
    val organizationName: String?,
    val profileImageUrl: String?,
    val createdAt: String
)