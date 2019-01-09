package com.example.axonsimple

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.spring.stereotype.Aggregate
import org.springframework.context.annotation.Profile
import java.util.UUID

@Aggregate
@Profile("B")
class AggregateB() {
    @AggregateIdentifier
    lateinit var id: UUID

    @CommandHandler
    constructor(command: CreateBCommand): this() {
        AggregateLifecycle.apply(CreatedBEvent(command.id))
    }

    @EventSourcingHandler
    fun on(event: CreatedBEvent) {
        id = event.id
    }
}

data class CreateBCommand(@TargetAggregateIdentifier var id: UUID)
data class CreatedBEvent(var id: UUID)