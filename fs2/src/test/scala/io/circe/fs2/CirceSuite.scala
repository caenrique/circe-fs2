package io.circe.fs2

import cats.effect.testing.scalatest.AssertingSyntax
import cats.effect.testing.scalatest.AsyncIOSpec
import cats.effect.testing.scalatest.EffectTestSupport
import cats.instances.AllInstances
import cats.syntax.AllSyntax
import cats.syntax.EitherOps
import io.circe.testing.ArbitraryInstances
import io.circe.testing.EqInstances
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.scalacheck.Checkers
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import org.typelevel.discipline.Laws

import scala.language.implicitConversions

/**
 * An opinionated stack of traits to improve consistency and reduce boilerplate in circe tests.
 */
trait CirceSuite
    extends AsyncFlatSpec
    with AsyncIOSpec
    with AssertingSyntax
    with EffectTestSupport
    with ScalaCheckDrivenPropertyChecks
    with AllInstances
    with AllSyntax
    with ArbitraryInstances
    with EqInstances {
  override def convertToEqualizer[T](left: T): Equalizer[T] =
    sys.error("Intentionally ambiguous implicit for Equalizer")

  implicit def prioritizedCatsSyntaxEither[A, B](eab: Either[A, B]): EitherOps[A, B] = new EitherOps(eab)

  def checkLaws(name: String, ruleSet: Laws#RuleSet): Unit = ruleSet.all.properties.zipWithIndex.foreach {
    case ((id, prop), 0) => name should s"obey $id" in Checkers.check(prop)
    case ((id, prop), _) => it should s"obey $id" in Checkers.check(prop)
  }
}
