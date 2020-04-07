package com.github.amraneze.io

import java.io.{File, PrintWriter}

import com.github.amraneze.model.Enum
import com.github.amraneze.parse.Parser._

import scala.io.{BufferedSource, Source}

object EnumIO {

	type EnumClass = (Enum, Seq[String])

	def fillEnumClass(value: Enum, enum: Enum): Enum = {
		Enum(
			if (value.packageName.isEmpty) enum.packageName else value.packageName,
			if (value.classType.isEmpty) enum.classType else value.classType,
			if (value.name.isEmpty) enum.name else value.name,
		)
	}

	def readEnum(file: Option[String]): EnumClass = {
		var enum: EnumClass = (Enum("", "", ""), Seq.empty)
		val enumSource: BufferedSource = Source.fromFile(file.filter(!_.isEmpty).getOrElse(throw new RuntimeException("File can't be empty")))
		enumSource.getLines.filter(!_.isBlank).map(_.trim).foreach(line => {
			matchEnum(line) match {
				case Right(value: Enum) => enum = (fillEnumClass(value, enum._1), enum._2)
				// TODO handle comments too
				case Left(enumValue: Option[String]) =>
					if (enumValue.isDefined) enum = (enum._1, enum._2 :+ enumValue.get)
			}
		})
		enumSource.close
		enum
	}

	def readEnums(directory: String): Unit = {

	}

	def writeEnum(file: Option[String], enum: EnumClass, useCaseClass: Boolean = false): Unit = {
		val scalaFile = file.get.replace(".java", ".scala")
		val writer: PrintWriter = new PrintWriter(new File(scalaFile))
		writer.write(s"package ${enum._1.packageName} ${System.getProperty("line.separator")}")
		writer.write(s"sealed trait ${enum._1.name} ${System.getProperty("line.separator")}")
		enum._2.foreach(line => writer.write(s"case object ${line} extends ${enum._1.name} ${System.getProperty("line.separator")}"))
		writer.close()
	}

}
