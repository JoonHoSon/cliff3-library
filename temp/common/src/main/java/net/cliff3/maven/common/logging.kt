package net.cliff3.maven.common

import org.slf4j.LoggerFactory

inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!

val topLogger = LoggerFactory.getLogger(object {}::class.java.`package`.name)
