package me.znotchill.honeydew.host.backend.routes.validators

import io.konform.validation.Validation
import io.konform.validation.constraints.minLength
import io.konform.validation.onEach
import me.znotchill.honeydew.common.routes.model.UpdatePlayersRequest

val updatePlayersValidator = Validation {
    UpdatePlayersRequest::key {
        minLength(10) hint "Key must be at least 10 characters"
    }
    UpdatePlayersRequest::players {
//        minItems(1) hint "At least one player is required"

        onEach { run(playerValidator) }
    }
}
