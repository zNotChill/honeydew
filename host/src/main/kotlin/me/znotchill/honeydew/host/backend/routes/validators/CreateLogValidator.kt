package me.znotchill.honeydew.host.backend.routes.validators

import io.konform.validation.Validation
import io.konform.validation.constraints.pattern
import me.znotchill.honeydew.common.routes.model.CreateLogRequest

val createLogValidator = Validation {
    CreateLogRequest::channelId {
        pattern(Regex("[a-fA-F0-9\\-]{36}")) hint "Invalid UUID"
    }
}