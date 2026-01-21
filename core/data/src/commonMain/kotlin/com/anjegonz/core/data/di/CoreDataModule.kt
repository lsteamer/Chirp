package com.anjegonz.core.data.di

import com.anjegonz.core.data.auth.KtorAuthService
import com.anjegonz.core.data.logging.KermitLogger
import com.anjegonz.core.data.networking.HttpClientFactory
import com.anjegonz.core.domain.auth.AuthService
import com.anjegonz.core.domain.logging.ChirpLogger
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformCodeDataModule: Module

val coreDataModule = module {
    includes(platformCodeDataModule)
    single<ChirpLogger> { KermitLogger }
    single {
        HttpClientFactory(get()).create(get())
    }
    singleOf(::KtorAuthService) bind AuthService::class
}