package com.hawazin.visualrater.models.db
import java.util.UUID

abstract class DatabaseEnum<T>(
    open var id:Int,
    open var value:T
) {
    constructor()  : this(0, "" as T) {

    }
    override fun equals(other: Any?): Boolean {
        return this.toString() == other.toString()
    }


    override fun toString(): String {
        return value.toString()
    }
}