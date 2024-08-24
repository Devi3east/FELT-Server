package example.com.di

import example.com.dao.user.UserDao
import example.com.dao.user.UserDaoImpl
import example.com.repository.user.UserRepository
import example.com.repository.user.UserRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl() }
}