package me.znotchill.honeydew.client

import java.util.logging.Handler
import java.util.logging.LogRecord

class DashboardLogHandler(val onLog: (LogRecord) -> Unit) : Handler() {
    override fun publish(record: LogRecord) {
        if (!isLoggable(record)) return
        onLog(record)
    }

    override fun flush() {
    }

    override fun close() {
    }
}