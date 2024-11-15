package com.example.minilivescore.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class TeamWithDetails(
    @Embedded
    val team: TeamEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "teamId"
    )
    val players: List<PlayerEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "teamId"
    )
    val coach: CoachEntity?
)