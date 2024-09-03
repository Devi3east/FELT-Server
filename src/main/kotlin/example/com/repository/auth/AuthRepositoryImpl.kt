package example.com.repository.auth

import example.com.dao.user.UserDao
import example.com.model.AuthResponse
import example.com.model.AuthResponseData
import example.com.model.SignInParams
import example.com.model.SignUpParams
import example.com.plugins.generateToken
import example.com.secutiry.hashPassword
import example.com.util.Response
import io.ktor.http.*

class AuthRepositoryImpl(
    private val userDao: UserDao
): AuthRepository {

    override suspend fun signUp(params: SignUpParams): Response<AuthResponse> {
        return if (userAlreadyExist(params.email)) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = AuthResponse(
                    error = "A user with this email already exists."
                )
            )
        } else {
            val insertedUser = userDao.insert(params)

            if (insertedUser == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError,
                    data = AuthResponse(
                        error = "UserRepositoryImpl Sign Up error."
                    )
                )
            } else {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            userId = insertedUser.userId,
                            name = insertedUser.name,
                            email = insertedUser.email,
                            isOrganization = insertedUser.isOrganization,
                            organizationName = insertedUser.organizationName,
                            isAgreementChecked = insertedUser.isAgreementChecked,
                            token = generateToken(params.email)
                        )
                    )
                )
            }
        }
    }

    override suspend fun signIn(params: SignInParams): Response<AuthResponse> {
        val user = userDao.findByEmail(params.email)

        return if (user == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = AuthResponse(
                    error = "Invalid credentials, no user with this email!"
                )
            )
        } else {
            val hashedPassword = hashPassword(params.password)

            if (user.password == hashedPassword) {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            userId = user.userId,
                            name = user.name,
                            email = user.email,
                            isOrganization = user.isOrganization,
                            organizationName = user.organizationName,
                            isAgreementChecked = user.isAgreementChecked,
                            token = generateToken(params.email)
                        )
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Forbidden,
                    data = AuthResponse(
                        error = "Invalid credentials, wrong password!"
                    )
                )
            }
        }
    }

    private suspend fun userAlreadyExist(email: String): Boolean {
        return userDao.findByEmail(email) != null
    }

}