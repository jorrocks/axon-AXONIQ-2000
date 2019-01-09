package com.example.axonsimple

import org.axonframework.commandhandling.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle
import org.axonframework.modelling.command.TargetAggregateIdentifier
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Aggregate
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Aggregate
@Profile("A")
class AggA() {
    @AggregateIdentifier
    lateinit var id: UUID

    @CommandHandler
    constructor(command: CreateACommand) : this() {
        AggregateLifecycle.apply(CreatedAEvent(
                id = command.id
        ))
    }

    @EventSourcingHandler
    fun on(event: CreatedAEvent) {
        id = event.id
    }
}

@Saga
@Profile("A")
class SagaA() {
    @Autowired @Transient
    lateinit var gateway: CommandGateway

    @StartSaga
    @SagaEventHandler(associationProperty = "id")
    fun on(event: CreatedAEvent) {
        gateway.send<Unit>(CreateBCommand(event.id))
    }
}

data class CreateACommand(@TargetAggregateIdentifier var id: UUID)
data class CreatedAEvent(var id: UUID)

@RestController
@Profile("A")
class Controller(@Autowired val gateway: CommandGateway ) {
    @PostMapping("/create")
    fun create(@RequestBody command: CreateACommand): ResponseEntity<UUID> {
        gateway.send<Unit>(command)
        return ResponseEntity(command.id, HttpStatus.CREATED)
    }
}