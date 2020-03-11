package me.demo

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction


object TestTable : IntIdTable() {
    val flag = integer("flag").index()
}

fun createNRows(n: Int) = repeat(n) {
    TestTable.insert { it[flag] = 0 }
}

fun main(args: Array<String>) {
    val ds = HikariDataSource(HikariConfig().apply {
        jdbcUrl = "jdbc:postgresql://localhost:5432/testdb"
        username = "rbrodetski"
        password = ""
        setDriverClassName("org.postgresql.Driver")
    })

    Database.connect(ds)
//    Database.connect("jdbc:postgresql://localhost:5432/testdb", driver = "org.postgresql.Driver", user = "rbrodetski", password = "")
    transaction {
//          SchemaUtils.create(TestTable)
//          createNRows(1000000)
        println("total ${TestTable.selectAll().count()} elements")
    }

    repeat(10000) {
        transaction {
            val started3 = System.currentTimeMillis()
            TestTable.select { TestTable.flag.eq(1) }.limit(1).toList()
            println("Query took ${System.currentTimeMillis() - started3}")
        }
        Thread.sleep(10)
    }
}
