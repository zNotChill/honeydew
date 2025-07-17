package me.znotchill.honeydew.host.backend.utils

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun getHumanTimestamp(): String {
    return ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
}