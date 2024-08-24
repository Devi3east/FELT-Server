package example.com.model

import org.jetbrains.exposed.sql.Table

object UserRow: Table(name = "users") {
    val id = long(name = "user_id").autoIncrement()
    val name = varchar(name = "user_name", length = 250)
    val email = varchar(name = "user_email", length = 250)
    val password = varchar(name = "user_password", length = 100)
    val userImage = text(name = "user_image").nullable()
    var isEventOrganizer = bool(name = "event_organizer").default(false)
    val organizationName = varchar(name = "organization_name", length = 250).nullable()
    val isAgreementChecked = bool(name = "agreement_checked").default(false)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val password: String,
    val userImage: String?,
    val isEventOrganizer: Boolean,
    val organizationName: String?,
    val isAgreementChecked: Boolean?
)