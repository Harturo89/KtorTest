package com.itcs.back

import com.itcs.back.controller.UsersController
import com.itcs.back.model.User
import com.itcs.back.utils.Ok
import com.itcs.back.utils.Error
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import org.jetbrains.exposed.sql.Database
import java.lang.IllegalStateException
import java.text.DateFormat
import java.util.UUID


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }
    }

    install(CORS) {
        anyHost()
    }
    install(Compression)
    install(DefaultHeaders)
    install(CallLogging)

    initDB()

    user()

}

/*
    Init Postgresql database connection
 */
fun initDB() {
    val config = HikariConfig("/hikari.properties")
    val ds = HikariDataSource(config)
    Database.connect(ds)
}

/*
User routing
 */

fun Application.user() {

    routing {
        route("api/user") {

            val usersController = UsersController()

            get("/") {
                call.respond(usersController.getUsers())
            }

            get("/{id}") {

                try {
                    val id = UUID.fromString(call.parameters["id"])

                    when (val user = usersController.getUser(id)) {
                        null -> call.respond(HttpStatusCode.NotFound, Error("user not found"))
                        else -> call.respond(user)
                    }

                } catch (exception: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, Error("id must be a valid UUID"))
                }

            }

            post("/") {
                //todo controlar todo tmb
                val candidate = call.receive<User>()
                usersController.addUser(candidate)
                call.respond(HttpStatusCode.Created, Ok("Usuario creado con éxito"))
            }


            put("/{id}") {
                try {
                    val id: UUID = UUID.fromString(call.parameters["id"])
                    val candidate = call.receive<User>()
                    usersController.updateUser(candidate, id)
                    call.respond(HttpStatusCode.OK, Ok("Usuario actualizado con éxito"))
                    //todo controlar que el usuario existe,
                } catch (exception: IllegalArgumentException) {
                    call.respond(HttpStatusCode.BadRequest, Error(exception.message))
                } catch (exception: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest, Error(exception.message))
                }

            }

        }
    }
}
