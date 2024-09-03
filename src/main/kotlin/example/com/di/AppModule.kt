package example.com.di

import example.com.dao.follows.FollowsDao
import example.com.dao.follows.FollowsDaoImpl
import example.com.dao.user.UserDao
import example.com.dao.user.UserDaoImpl
import example.com.repository.auth.AuthRepository
import example.com.repository.auth.AuthRepositoryImpl
import example.com.repository.follows.FollowsRepository
import example.com.repository.follows.FollowsRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl() }
    single<FollowsDao> { FollowsDaoImpl() }
    single<FollowsRepository> { FollowsRepositoryImpl(get(), get()) }
}