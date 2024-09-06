package example.com.dao.event

import example.com.dao.DatabaseFactory.dbQuery
import example.com.dao.user.UserTable
import example.com.util.IdGenerator
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EventDaoImpl: EventDao {
    override suspend fun createEvent(eventTitle: String, description: String, isScheduled: Boolean, dateTime: String?, eventImagePrimary: String, userId: Long, organizationName: String): Boolean {
        return dbQuery{
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss") // Adjust pattern as needed
            val parsedDateTime = dateTime?.let { LocalDateTime.parse(it, formatter) }

            val insertStatement = EventTable.insert {
                it[eventId] = IdGenerator.generateId()
                it[EventTable.eventTitle] = eventTitle
                it[EventTable.description] = description
                it[EventTable.isScheduled] = isScheduled
                it[EventTable.dateTime] = parsedDateTime
                it[EventTable.eventImagePrimary] = eventImagePrimary
                it[likesCount] = 0
                it[commentsCount] = 0
                it[viewsCount] = 0
                it[EventTable.userId] = userId
                it[EventTable.organizationName] = organizationName
            }
            insertStatement.resultedValues?.singleOrNull() != null
        }
    }

    override suspend fun getFeedsEvent(
        userId: Long,
        follows: List<Long>,
        pageNumber: Int,
        pageSize: Int
    ): List<EventRow> {
        return dbQuery {
            if (follows.size > 1) {
                getEvents(users = follows, pageSize = pageSize, pageNumber = pageNumber)
            } else {
                EventTable
                    .join(
                        otherTable = UserTable,
                        onColumn = EventTable.userId,
                        otherColumn = UserTable.userId,
                        joinType = JoinType.INNER
                    )
                    .selectAll()
                    .orderBy(column = EventTable.likesCount, order = SortOrder.DESC)
                    .limit(n = pageSize, offset = ((pageNumber -1) * pageSize).toLong())
                    .map { toEventRow(it) }
            }
        }
    }

    override suspend fun getEventByUser(userId: Long, pageNumber: Int, pageSize: Int): List<EventRow> {
        return dbQuery {
            getEvents(users = listOf(userId), pageSize = pageSize, pageNumber = pageNumber)
        }
    }

    override suspend fun updateLikesCount(eventId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            EventTable.update(where = { EventTable.eventId eq eventId }) {
                it.update(column = likesCount, value = likesCount.plus(value))
            } > 0
        }
    }

    override suspend fun updateCommentsCount(eventId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            EventTable.update(where = { EventTable.eventId eq eventId }) {
                it.update(column = commentsCount, value = commentsCount.plus(value))
            } > 0
        }
    }

    override suspend fun getEvent(eventId: Long): EventRow? {
        return dbQuery {
            EventTable
                .join(
                    otherTable = UserTable,
                    onColumn = EventTable.userId,
                    otherColumn = UserTable.userId,
                    joinType = JoinType.INNER
                )
                .select { EventTable.eventId eq eventId }
                .singleOrNull()
                ?.let { toEventRow(it) }
        }
    }

    override suspend fun deleteEvent(eventId: Long): Boolean {
        return dbQuery {
            EventTable.deleteWhere { EventTable.eventId eq eventId } > 0
        }
    }

    override suspend fun getPopularEvents(limit: Int): List<EventRow> {
        return dbQuery {
            EventTable.selectAll()
                .orderBy(column = EventTable.likesCount, order = SortOrder.DESC)
                .limit(n = limit)
                .map { toEventRow(it) }
        }
    }

    private fun getEvents(users: List<Long>, pageSize: Int, pageNumber: Int): List<EventRow> {
        return EventTable
            .join(
                otherTable = UserTable,
                onColumn = EventTable.userId,
                otherColumn = UserTable.userId,
                joinType = JoinType.INNER
            )
            .select(where = EventTable.userId inList users)
            .orderBy(column = EventTable.createdAt, order = SortOrder.DESC)
            .limit(n = pageSize, offset = ((pageNumber -1) * pageSize).toLong())
            .map { toEventRow(it) }
    }

    private fun toEventRow(row: ResultRow): EventRow {
        return EventRow(
            eventId = row[EventTable.eventId],
            eventTitle = row[EventTable.eventTitle],
            description = row[EventTable.description],
            isScheduled = row[EventTable.isScheduled],
            dateTime = row[EventTable.dateTime].toString(),
            eventImagePrimary = row[EventTable.eventImagePrimary],
            createdAt = row[EventTable.createdAt].toString(),
            likesCount = row[EventTable.likesCount],
            commentsCount = row[EventTable.commentsCount],
            viewsCount = row[EventTable.viewsCount],
            userId = row[EventTable.userId],
            organizationName = row[EventTable.organizationName],
            organizationProfileImageUrl = row[UserTable.profileImageUrl]
        )
    }
}