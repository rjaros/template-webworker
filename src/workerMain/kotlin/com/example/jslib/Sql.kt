package com.example.jslib

import io.kvision.require
import io.kvision.utils.obj

class Sql {

    init {
        require("process")
        initSqlJs { it }.then<dynamic> { SQL ->
            val IndexedDBBackend = require("absurd-sql/dist/indexeddb-backend").default
            val backend = js("new IndexedDBBackend()")
            val SQLLiteFS = require("absurd-sql").SQLiteFS
            val sqlFS = js("new SQLLiteFS(SQL.FS, backend)")
            SQL.register_for_idb(sqlFS)
            SQL.FS.mkdir("/sql")
            SQL.FS.mount(sqlFS, obj {}, "/sql")
            val path = "/sql/db.sqlite"
            val params = obj { filename = true }
            val db = js("new SQL.Database(path, params)")
            db.exec("PRAGMA journal_mode=MEMORY;")
            db.exec("CREATE TABLE kv (key TEXT PRIMARY KEY, value TEXT)")
            val insert = db.prepare("INSERT INTO kv (key, value) VALUES (?, ?)")
            insert.run(arrayOf("item-id-00001", 35725.29))
            insert.free()
            val select = db.prepare("SELECT SUM(value) FROM kv")
            select.step()
            console.log(select.getAsObject())
            select.free()
        }
    }
}