package example.com.dao.event

import example.com.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object EventTable: Table(name = "events") {
    val eventId = long(name = "event_id").uniqueIndex()
    val eventTitle = varchar(name = "event_title", length = 150)
    val description = varchar(name = "description", length = 300)
    val isScheduled = bool(name = "is_scheduled")
    val dateTime = datetime(name = "date_time").defaultExpression(defaultValue = CurrentDateTime).nullable()
    val eventImagePrimary = varchar(name = "event_image_primary", length = 300)
    val likesCount = integer(name = "likes_count")
    val commentsCount = integer(name = "comments_count")
    val viewsCount = integer(name = "views_count")
    val userId = long(name = "user_id").references(ref = UserTable.userId, onDelete = ReferenceOption.CASCADE)
    val organizationName = varchar(name = "organization_name", length = 150)
    val createdAt = datetime(name = "created_at").defaultExpression(defaultValue = CurrentDateTime)
}

data class EventRow(
    val eventId: Long,
    val eventTitle: String,
    val description: String,
    val eventImagePrimary: String,
    val isScheduled: Boolean,
    val dateTime: String?,
    val createdAt: String,
    val likesCount: Int,
    val commentsCount: Int,
    val viewsCount: Int,
    val userId: Long,
    val organizationName: String,
    val organizationProfileImageUrl: String?
)