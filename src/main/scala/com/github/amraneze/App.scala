package com.github.amraneze

import com.github.amraneze.io.EnumIO.{readEnum, readEnums, writeEnum, writeEnums}

object App extends App {
	import com.github.amraneze.util.CommonUtil._

	if (args.length == 0) {
		println(usage)
		System.exit(1)
	}
	val parsedArgs: ArgMap = parseArgs(Map.empty, args.toSeq)
	val file: String = parsedArgs.get(Symbol("file")).asInstanceOf[Option[String]]
		.filter(!_.isEmpty).getOrElse(throw new RuntimeException("File can't be empty"))
	val isDirectory: Boolean = !file.endsWith(".java")
	if (isDirectory) writeEnums(readEnums(file)) else writeEnum(file, readEnum(file))
}
