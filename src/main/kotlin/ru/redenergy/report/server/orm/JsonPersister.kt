package ru.redenergy.report.server.orm

import com.google.gson.Gson
import com.j256.ormlite.field.FieldType
import com.j256.ormlite.field.SqlType
import com.j256.ormlite.field.types.StringType
import java.lang.reflect.ParameterizedType

/**
 * Contains classes which would be persisted as a json
 */
val classes = arrayOf(java.util.List::class.java)

/**
 * Custom ORMLite persister to convert java objects into json sql object <br>
 */
open class JsonPersister(): StringType(SqlType.LONG_STRING, classes) {

    override fun javaToSqlArg(fieldType: FieldType, obj: Any): Any? {
        return toJson(fieldType, obj)
    }

    override fun sqlArgToJava(fieldType: FieldType, sqlArg: Any?, columnPos: Int): Any? {
        return fromJson(fieldType, sqlArg as String)
    }

    /**
     * Translates object into sql argument (in out case it's string)
     */
    open fun toJson(fieldType: FieldType, obj: Any): String{
        return Gson().toJson(obj, fieldType.field.genericType as ParameterizedType)
    }

    /**
     * Translates sql argument (string) into java object (which type is taken from field)
     */
    open fun fromJson(type: FieldType, json: String): Any{
        return Gson().fromJson(json, type.field.genericType as ParameterizedType)
    }


    companion  object{
        /**
         * Requested by ORMLite
         */
        @JvmStatic fun getSingleton(): JsonPersister = JsonPersister()
    }
}

