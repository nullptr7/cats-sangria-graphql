package com.github.nullptr7.models

import caliban.client.FieldBuilder._
import caliban.client.SelectionBuilder._
import caliban.client._
import caliban.client.Operations._
import caliban.client.__Value._

object Client {

  case class ruleRequestType(
      key: String,
      processId: String,
      application: String,
      domain: String,
      dqCd: String,
      name: String,
      typeCd: String,
      ruleClass: String,
      reqDescription: String,
      isActive: Option[String] = None
  )
  object ruleRequestType {
    implicit val encoder: ArgEncoder[ruleRequestType] =
      new ArgEncoder[ruleRequestType] {
        override def encode(value: ruleRequestType): __Value =
          __ObjectValue(
            List(
              "key" -> implicitly[ArgEncoder[String]].encode(value.key),
              "processId" -> implicitly[ArgEncoder[String]].encode(value.processId),
              "application" -> implicitly[ArgEncoder[String]]
                .encode(value.application),
              "domain" -> implicitly[ArgEncoder[String]].encode(value.domain),
              "dqCd" -> implicitly[ArgEncoder[String]].encode(value.dqCd),
              "name" -> implicitly[ArgEncoder[String]].encode(value.name),
              "typeCd" -> implicitly[ArgEncoder[String]].encode(value.typeCd),
              "ruleClass" -> implicitly[ArgEncoder[String]]
                .encode(value.ruleClass),
              "reqDescription" -> implicitly[ArgEncoder[String]]
                .encode(value.reqDescription),
              "isActive" -> value.isActive.fold(__NullValue: __Value)(value =>
                implicitly[ArgEncoder[String]].encode(value)
              )
            )
          )
        override def typeName: String = "ruleRequestType"
      }
  }

  type Mutation = RootMutation
  object Mutation {
    def mutateRuleRequest(
        ruleRequestType: ruleRequestType
    ): SelectionBuilder[RootMutation, String] = Field(
      "mutateRuleRequest",
      Scalar(),
      arguments = List(Argument("ruleRequestType", ruleRequestType))
    )
  }

}

