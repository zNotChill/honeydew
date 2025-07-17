package me.znotchill.honeydew.host.backend.interfaces

import io.ktor.server.application.Application

interface RouteModule {
    fun register(application: Application)
}