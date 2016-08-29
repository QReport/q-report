package ru.redenergy.report.common

/**
 * Mostly used to describe blocked player
 */
class BlockedPlayer(var name: String, var blocked: Boolean, var blockedBy: String, var blockTime: Long) {

    constructor(): this("", false, "", -1)
}

