package com.github.amraneze.io

import java.io.{File, FileNotFoundException, PrintWriter}

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

	@throws[Exception]
	def readEnum(file: Any): EnumClass = {
		var enum: EnumClass = (Enum("", "", ""), Seq.empty)
		val enumSource: BufferedSource = file match {
			case fileString: String => Source.fromFile(fileString)
			case fileSource: File => Source.fromFile(fileSource)
			case unknown => throw new RuntimeException(s"The file $unknown is not supported")
		}
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

	@throws[FileNotFoundException]
	def readEnums(directory: String): Seq[(File, EnumClass)] = {
		val dir: File = new File(directory)
		if (dir.exists && dir.isDirectory) {
			dir.listFiles.filter(file => file.isFile && file.getName.endsWith(".java")).map(file => (file, readEnum(file))).toSeq
		} else {
			throw new RuntimeException(s"The directory path ${if (dir.exists) "is not a directory" else "does not exist" }")
		}
	}

	def writeEnum(file: String, enum: EnumClass, useCaseClass: Boolean = true): Unit = {
		val scalaFile = file.replace(".java", ".scala")
		val writer: PrintWriter = new PrintWriter(new File(scalaFile))
		writer.write(s"package ${enum._1.packageName} ${System.getProperty("line.separator")}")
		if (useCaseClass) writeEnumCaseClass(writer, enum) else writeEnumEnumeration(writer, enum)
		writer.close()
	}

	def writeEnums(files: Seq[(File,  EnumClass)], useCaseClass: Boolean = true): Unit = files.foreach(file => writeEnum(file._1.getPath, file._2, useCaseClass))

	private def writeEnumCaseClass(writer: PrintWriter, enum: EnumClass): Unit = {
		writer.write(s"sealed trait ${enum._1.name} ${System.getProperty("line.separator")}")
		enum._2.foreach(line => writer.write(s"case object ${line} extends ${enum._1.name} ${System.getProperty("line.separator")}"))
	}

	private def writeEnumEnumeration(writer: PrintWriter, enum: EnumClass): Unit = {
		writer.write(s"object ${enum._1.name} extends Enumeration {${System.getProperty("line.separator")}")
		writer.write("val ")
		enum._2.zipWithIndex.foreach {
			case (line, index) => writer.write(s"${line}${ if (index != enum._2.length - 1) ", " else ""}")
		}
		writer.write(System.getProperty("line.separator"))
		writer.write("}")
	}
}
