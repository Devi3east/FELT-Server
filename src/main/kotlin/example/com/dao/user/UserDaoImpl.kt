package example.com.dao.user

import example.com.dao.DatabaseFactory.dbQuery
import example.com.model.SignUpParams
import example.com.secutiry.hashPassword
import example.com.util.IdGenerator
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update


class UserDaoImpl : UserDao {

    override suspend fun insert(params: SignUpParams): UserRow? {
        return dbQuery {
            val insertStatement = UserTable.insert {
                it[userId] = IdGenerator.generateId()
                it[name] = params.name
                it[email] = params.email
                it[password] = hashPassword(params.password)
                it[isOrganization] = params.isOrganization
                it[organizationName] = params.organizationName
                it[isAgreementChecked] = params.isAgreementChecked
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                rowToUser(it)
            }
        }
    }

    override suspend fun findByEmail(email: String): UserRow? {
        return dbQuery {
            UserTable.select { UserTable.email eq email }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override suspend fun findById(userId: Long): UserRow? {
        return dbQuery {
            UserTable.select { UserTable.userId eq userId }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override suspend fun updateUser(userId: Long, name: String, description: String, address: String, profileImageUrl: String, organizationName: String): Boolean {
        return dbQuery {
            UserTable.update(where = { UserTable.userId eq userId }) {
                it[UserTable.name] = name
                it[UserTable.description] = description
                it[UserTable.address] = address
                it[UserTable.profileImageUrl] = profileImageUrl
                it[UserTable.organizationName] = organizationName
            } > 0
        }
    }

    override suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean {
        return dbQuery {
            val count = if (isFollowing) + 1 else - 1

            val success1 = UserTable.update({ UserTable.userId eq follower }) {
                it.update(column = followingCount, value = followingCount.plus(count))
            } > 0

            val success2 = UserTable.update({ UserTable.userId eq following }) {
                it.update(column = followersCount, value = followersCount.plus(count))
            } > 0

            success1 && success2
        }
    }

    private fun rowToUser(row: ResultRow): UserRow {
        return UserRow(
            userId = row[UserTable.userId],
            name = row[UserTable.name],
            description = row[UserTable.description],
            email = row[UserTable.email],
            address = row[UserTable.address],
            password = row[UserTable.password],
            profileImageUrl = row[UserTable.profileImageUrl],
            isOrganization = row[UserTable.isOrganization],
            organizationName = row[UserTable.organizationName],
            isAgreementChecked = row[UserTable.isAgreementChecked],
            followersCount = row[UserTable.followersCount],
            followingCount = row[UserTable.followingCount],
            isPremium = row[UserTable.isPremium]
        )
    }

}