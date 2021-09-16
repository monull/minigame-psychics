package io.github.monull.psychics.format

import java.text.DecimalFormat


private val DECIMAL_FORMAT = DecimalFormat("#.##")
private val PERCENT_FORMAT = DecimalFormat("#.##%")

internal fun Number.decimalFormat(): String {
    return DECIMAL_FORMAT.format(this)
}

internal fun Number.percentFormat(): String {
    return PERCENT_FORMAT.format(this)
}