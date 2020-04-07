package com.github.amraneze

import com.github.amraneze.io.EnumIO.{readEnum, writeEnum}

object App extends App {
	import com.github.amraneze.util.CommonUtil._

	if (args.length == 0) {
		println(usage)
		System.exit(1)
	}
	val parsedArgs: ArgMap = parseArgs(Map.empty, args.toSeq)
	val file: Option[String] = parsedArgs.get(Symbol("file")).asInstanceOf[Option[String]]
	writeEnum(file, readEnum(file))
}
