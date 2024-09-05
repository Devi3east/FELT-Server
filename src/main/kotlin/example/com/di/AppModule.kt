package example.com.di

import example.com.dao.event.EventDao
import example.com.dao.event.EventDaoImpl
import example.com.dao.event_comments.EventCommentsDao
import example.com.dao.event_comments.EventCommentsDaoImpl
import example.com.dao.event_likes.EventLikesDao
import example.com.dao.event_likes.EventLikesDaoImpl
import example.com.dao.follows.FollowsDao
import example.com.dao.follows.FollowsDaoImpl
import example.com.dao.user.UserDao
import example.com.dao.user.UserDaoImpl
import example.com.repository.auth.AuthRepository
import example.com.repository.auth.AuthRepositoryImpl
import example.com.repository.event.EventRepository
import example.com.repository.event.EventRepositoryImpl
import example.com.repository.event_comments.EventCommentsRepository
import example.com.repository.event_comments.EventCommentsRepositoryImpl
import example.com.repository.follows.FollowsRepository
import example.com.repository.follows.FollowsRepositoryImpl
import example.com.repository.profile.ProfileRepository
import example.com.repository.profile.ProfileRepositoryImpl
import org.koin.dsl.module

val appModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<UserDao> { UserDaoImpl() }
    single<FollowsDao> { FollowsDaoImpl() }
    single<FollowsRepository> { FollowsRepositoryImpl(get(), get()) }
    single<EventLikesDao> { EventLikesDaoImpl() }
    single<EventDao> { EventDaoImpl() }
    single<EventRepository> { EventRepositoryImpl(get(), get(), get()) }
    single<ProfileRepository> { ProfileRepositoryImpl(get(), get()) }
    single<EventCommentsDao> { EventCommentsDaoImpl() }
    single<EventCommentsRepository> { EventCommentsRepositoryImpl(get(), get()) }
}