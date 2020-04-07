package com.github.amraneze.model

trait GenericClass {
	val packageName: String
	val classType: String
	val name: String
}

case class Enum(packageName: String, classType: String, name: String) extends GenericClass
