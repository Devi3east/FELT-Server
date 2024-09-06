package example.com.dao.event_likes

import example.com.dao.DatabaseFactory.dbQuery
import example.com.util.IdGenerator
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class EventLikesDaoImpl : EventLikesDao {
    override suspend fun addLike(eventId: Long, userId: Long): Boolean {
        return dbQuery {

            val insertStatement = EventLikesTable.insert {
                it[likeId] = IdGenerator.generateId()
                it[EventLikesTable.eventId] = eventId
                it[EventLikesTable.userId] = userId
            }

            insertStatement.resultedValues?.isNotEmpty() ?: false
        }
    }

    override suspend fun removeLike(eventId: Long, userId: Long): Boolean {
        return dbQuery {
            EventLikesTable
                .deleteWhere { (EventLikesTable.eventId eq eventId) and (EventLikesTable.userId eq userId) } > 0
        }
    }

    override suspend fun isEventLikedByUser(eventId: Long, userId: Long): Boolean {
        return dbQuery {
            EventLikesTable
                .select { (EventLikesTable.eventId eq eventId) and (EventLikesTable.userId eq userId) }
                .toList()
                .isNotEmpty()
        }
    }
}