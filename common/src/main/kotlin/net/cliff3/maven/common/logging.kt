package net.cliff3.maven.common

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * logging
 *
 * @author JoonHo Son
 * @since 0.3.0
 */
inline fun <reified T> T.logger() = LoggerFactory.getLogger(T::class.java)!!
val topLogger: Logger = LoggerFactory.getLogger(object {}::class.java.`package`.name)