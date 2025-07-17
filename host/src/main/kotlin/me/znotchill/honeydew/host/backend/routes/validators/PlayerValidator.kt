package me.znotchill.honeydew.host.backend.routes.validators

import io.konform.validation.Validation
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.pattern
import me.znotchill.honeydew.common.database.model.PlayerModel

val playerValidator = Validation {
    PlayerModel::uuid {
        pattern(Regex("[a-fA-F0-9\\-]{36}")) hint "Invalid UUID"
    }
    PlayerModel::username {
        pattern(Regex("^[a-zA-Z0-9_\\-]+$")) hint("Invalid name")
        minLength(3) hint "Username too short"
    }
    PlayerModel::location {
        run(locationValidator)
    }
}