package org.ranapat.sensors.gps.example.ui.tools

fun formatRating(rating: Double): String {
    var value = 0
    var measure = ""

    when {
        rating > 1000000 -> {
            value = rating.toInt() / 1000000
            measure = "M"
        }
        rating > 1000 -> {
            value = rating.toInt() / 1000
            measure = "K"
        }
        else -> {
            value = rating.toInt()
            measure = ""
        }
    }

    return String.format("%d%s", value, measure)
}

fun formatSeconds(input: Long): String {
    val numberOfDays = input / 86400;
    val numberOfHours = (input % 86400 ) / 3600
    val numberOfMinutes = ((input % 86400 ) % 3600 ) / 60
    val numberOfSeconds = ((input % 86400 ) % 3600 ) % 60

    val parts: ArrayList<String> = arrayListOf()
    if (numberOfDays > 0) {
        parts.add("${numberOfDays}d")
    }
    if (numberOfHours > 0) {
        parts.add("${numberOfHours}h")
    }
    if (numberOfMinutes > 0) {
        parts.add("${numberOfMinutes}m")
    }
    if (numberOfSeconds > 0) {
        parts.add("${numberOfSeconds}s")
    }
    return parts.joinToString(" ")
}