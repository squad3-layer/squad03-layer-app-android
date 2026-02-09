package com.example.feature.authentication.di

import com.example.feature.authentication.data.login.repository.LoginRepositoryImpl
import com.example.feature.authentication.data.register.repository.RegisterRepositoryImpl
import com.example.feature.authentication.domain.login.repository.LoginRepository
import com.example.feature.authentication.domain.register.repository.RegisterRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        impl: LoginRepositoryImpl
    ): LoginRepository

    @Binds
    @Singleton
    abstract fun bindRegisterRepository(
        impl: RegisterRepositoryImpl
    ): RegisterRepository
}