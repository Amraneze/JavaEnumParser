package com.github.amraneze.parse

import com.github.amraneze.model.Enum

import scala.util.matching.Regex

object Parser {

	val enumPackagePattern: Regex = """package\s(.*);""".r
	val enumNamePattern: Regex = """(?:public\s)?(class|interface|enum)\s([^\s\n]*)(?:\s\{)?""".r
	val enumValuePattern: Regex = """([^\s\n\,\;\}]*)(,|;)(?:.*)?$""".r

	def matchEnum(line: String): Either[Option[String], Enum] = {
		if (isStringMatched(line, enumPackagePattern)) {
			Right(matchPackageName(line))
		} else if (isStringMatched(line, enumNamePattern)) {
			Right(matchClassName(line))
		} else {
			Left(matchEnumValue(line))
		}
	}

	private def matchPackageName(line: String): Enum = {
		val enumPackagePattern(packageName) = line
		Enum(packageName, "", "")
	}

	private def matchClassName(line: String): Enum = {
		val enumNamePattern(classType, name) = line
		Enum("", classType, name)
	}

	private def matchEnumValue(line: String): Option[String] = {
		if (!isStringMatched(line, enumValuePattern)) return Option.empty
		val enumValuePattern(value, _) = line
		Option(value)
	}

	private def isStringMatched(string: String, regex: Regex): Boolean = regex.pattern.matcher(string).matches
}
