package example.com.dao.user

import org.jetbrains.exposed.sql.Table

object UserTable: Table(name = "users") {
    val userId = long(name = "user_id").autoIncrement()
    val name = varchar(name = "name", length = 250)
    val email = varchar(name = "user_email", length = 250)
    val password = varchar(name = "user_password", length = 100)
    val profileImageUrl = text(name = "profile_image_url").nullable()
    val isOrganization = bool(name = "is_organization").default(false)
    val organizationName = varchar(name = "organization_name", length = 250).nullable()
    val isAgreementChecked = bool(name = "agreement_checked").default(false)
    val followersCount = integer(name = "followers_count").default(defaultValue = 0)
    val followingCount = integer(name = "following_count").default(defaultValue = 0)
    val isPremium = bool(name = "is_premium").default(false)
    val isPopular = bool(name = "is_popular").default(false)

    override val primaryKey: PrimaryKey
        get() = PrimaryKey(userId)
}

data class UserRow(
    val userId: Long,
    val name: String,
    val email: String,
    val password: String,
    val profileImageUrl: String?,
    val isOrganization: Boolean,
    val organizationName: String?,
    val isAgreementChecked: Boolean?,
    val followersCount: Int,
    val followingCount: Int,
    val isPremium: Boolean,
    val isPopular: Boolean
)