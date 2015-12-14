package ru.redenergy.report.common

/**
 * Contains statistics from server
 *
 * @param tickets: Contains amount of tickets in each category (TicketReason: amount)
 * @param activeUsers: Contains five of most active users (UserName: amount)
 * @param averageTime: Average response time for a ticket (int minutes)
 */
data class Stats(val tickets: Map<TicketReason, Int>, val activeUsers: Map<String, Int>, val averageTime: Int)