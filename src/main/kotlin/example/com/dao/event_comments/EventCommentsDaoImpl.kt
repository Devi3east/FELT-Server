package example.com.dao.event_comments

import example.com.dao.DatabaseFactory.dbQuery
import example.com.dao.user.UserTable
import example.com.util.IdGenerator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.text.insert

class EventCommentsDaoImpl : EventCommentsDao {

    override suspend fun addComment(eventId: Long, userId: Long, content: String): EventCommentRow? {
        return dbQuery {
            val commentId = IdGenerator.generateId()

            EventCommentsTable.insert {
                it[EventCommentsTable.commentId] = commentId
                it[EventCommentsTable.eventId] = eventId
                it[EventCommentsTable.userId] = userId
                it[EventCommentsTable.content] = content
            }

            EventCommentsTable
                .join(
                    otherTable = UserTable,
                    onColumn = EventCommentsTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .select { (EventCommentsTable.eventId eq eventId) and (EventCommentsTable.commentId eq commentId) }
                .singleOrNull()
                ?.let { toEventCommentRow(it) }
        }
    }

    override suspend fun removeComment(commentId: Long, eventId: Long): Boolean {
        return dbQuery {
            EventCommentsTable.deleteWhere {
                (EventCommentsTable.commentId eq commentId) and (EventCommentsTable.eventId eq eventId)
            } > 0
        }
    }

    override suspend fun findComment(commentId: Long, eventId: Long): EventCommentRow? {
        return dbQuery {
            EventCommentsTable
                .join(
                    otherTable = UserTable,
                    onColumn = EventCommentsTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .select { (EventCommentsTable.eventId eq eventId) and (EventCommentsTable.commentId eq commentId) }
                .singleOrNull()
                ?.let { toEventCommentRow(it) }
        }
    }

    override suspend fun getComments(eventId: Long, pageNumber: Int, pageSize: Int): List<EventCommentRow> {
        return dbQuery {
            EventCommentsTable
                .join(
                    otherTable = UserTable,
                    onColumn = EventCommentsTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .select(where = { EventCommentsTable.eventId eq eventId })
                .orderBy(column = EventCommentsTable.createdAt, SortOrder.DESC)
                .limit(n = pageSize, offset = ((pageNumber -1) * pageSize).toLong())
                .map { toEventCommentRow(it) }
        }
    }

    private fun toEventCommentRow(resultRow: ResultRow): EventCommentRow {
        return EventCommentRow(
            commentId = resultRow[EventCommentsTable.commentId],
            content = resultRow[EventCommentsTable.content],
            eventId = resultRow[EventCommentsTable.eventId],
            userId = resultRow[EventCommentsTable.userId],
            name = resultRow[UserTable.name],
            organizationName = resultRow[UserTable.organizationName],
            profileImageUrl = resultRow[UserTable.profileImageUrl],
            createdAt = resultRow[EventCommentsTable.createdAt].toString()
        )
    }

}