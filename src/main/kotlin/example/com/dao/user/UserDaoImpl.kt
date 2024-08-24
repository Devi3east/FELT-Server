package example.com.dao.user

import example.com.dao.DatabaseFactory.dbQuery
import example.com.model.SignUpParams
import example.com.model.User
import example.com.model.UserRow
import example.com.secutiry.hashPassword
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select


class UserDaoImpl : UserDao {

    override suspend fun insert(params: SignUpParams): User? {
        return dbQuery {
            val insertStatement = UserRow.insert {
                it[name] = params.name
                it[email] = params.email
                it[password] = hashPassword(params.password)
                it[isEventOrganizer] = params.isEventOrganizer
                it[organizationName] = params.organizationName
                it[isAgreementChecked] = params.isAgreementChecked
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                rowToUser(it)
            }
        }
    }

    override suspend fun findByEmail(email: String): User? {
        return dbQuery {
            UserRow.select { UserRow.email eq email }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[UserRow.id],
            name = row[UserRow.name],
            email = row[UserRow.email],
            password = row[UserRow.password],
            userImage = row[UserRow.userImage],
            isEventOrganizer = row[UserRow.isEventOrganizer],
            organizationName = row[UserRow.organizationName],
            isAgreementChecked = row[UserRow.isAgreementChecked]
        )
    }
}