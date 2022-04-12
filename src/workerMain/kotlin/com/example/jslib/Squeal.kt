package com.example.jslib

import kotlin.js.Promise

@JsModule("@jlongster/sql.js")
@JsNonModule
external fun initSqlJs(locateFile: (String) -> String) : Promise<dynamic>
