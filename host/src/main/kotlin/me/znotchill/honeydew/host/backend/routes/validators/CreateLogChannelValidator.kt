package me.znotchill.honeydew.host.backend.routes.validators

import io.konform.validation.Validation
import io.konform.validation.constraints.pattern
import me.znotchill.honeydew.common.routes.model.CreateLogChannelRequest

val createLogChannelValidator = Validation {
    CreateLogChannelRequest::name {
        pattern(Regex("^[a-zA-Z0-9_\\-]+$")) hint("Invalid name")
    }
}