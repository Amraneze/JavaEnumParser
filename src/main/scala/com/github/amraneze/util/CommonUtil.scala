package com.github.amraneze.util

import scala.annotation.tailrec

object CommonUtil {
	val usage = "Usage: app [--use-case-class useCaseClass] file"

	type ArgMap = Map[Symbol, Any]

	@tailrec
	def parseArgs(argsMap: ArgMap, argsList: Seq[String]): ArgMap = {
		argsList match {
			case Seq() => argsMap
			case Seq("--use-case-class", value, tail @ _*) => parseArgs(argsMap ++ Map(Symbol("useClassCase") -> value.toBoolean), tail)
			case file +: Nil => parseArgs(argsMap ++ Map(Symbol("file") -> file), Nil)
			// In case that we have another option added by the user
			case file +: tail => parseArgs(argsMap ++ Map(Symbol("file") -> file), tail)
			case option +: _ =>
				println(usage)
				throw new RuntimeException(s"The argument $option is not supported yet.")
		}
	}

}
