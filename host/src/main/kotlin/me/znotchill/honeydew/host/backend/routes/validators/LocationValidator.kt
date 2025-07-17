package me.znotchill.honeydew.host.backend.routes.validators

import io.konform.validation.Validation
import io.konform.validation.constraints.minLength
import io.konform.validation.constraints.pattern
import me.znotchill.honeydew.common.database.model.LocationModel

val locationValidator = Validation {
    LocationModel::worldName {
        minLength(1)
        pattern(Regex("^[a-zA-Z0-9_\\-]+$")) hint("Invalid world name")
    }
    LocationModel::worldUuid {
        pattern(Regex("[a-fA-F0-9\\-]{36}")) hint "Invalid UUID"
    }
    LocationModel::pitch {
        constrain("must be >= -90") { it >= -90 }
        constrain("must be <= 90") { it <= 90 }
    }
    LocationModel::yaw {
        constrain("must be >= -180") { it >= -180 }
        constrain("must be <= 180") { it <= 180 }
    }
}