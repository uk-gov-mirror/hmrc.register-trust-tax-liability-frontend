/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package services

import java.time.LocalDate

import base.SpecBase
import connectors.SubmissionDraftConnector
import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear, StartDate, TaxLiabilityYear, YearReturnType}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.DidDeclareTaxToHMRCYesNoPage
import play.api.inject.bind
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.Future

class TaxLiabilityServiceSpec extends SpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  def setCurrentDate(date: LocalDate): LocalDateService = new LocalDateService {
    override def now: LocalDate = date
  }

  "getFirstYearOfTaxLiability" must {

    "return the cy minus four tax liability and and true to earlier years" when {

      "the current date is before the december deadline and date of death is more than 4 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2015, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2016), hasEarlierYearsToDeclare = true)
      }

      "the current date is on the december deadline and date of death is more than 4 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 12, 22)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2015, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2016), hasEarlierYearsToDeclare = true)
      }
    }

    "return the cy minus three tax liability and and true to earlier years" when {

      "the current date is after the december deadline and date of death is more than 3 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateAfterDec23rd = LocalDate.of(2020, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
          .build()

        val startDate = LocalDate.of(2015, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2017), hasEarlierYearsToDeclare = true)
      }
    }

    "return the cy minus four tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 4 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2016, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2016), hasEarlierYearsToDeclare = false)
      }
    }

    "return the cy minus three tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 3 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2017, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2017), hasEarlierYearsToDeclare = false)
      }

      "the current date is after the december deadline and date of death is 3 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2017, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2017), hasEarlierYearsToDeclare = false)
      }
    }

    "return the cy minus two tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 2 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2018, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2018), hasEarlierYearsToDeclare = false)
      }

      "the current date is after the december deadline and date of death is 2 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2018, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2018), hasEarlierYearsToDeclare = false)
      }
    }

    "return the cy minus one tax liability and and false to earlier years" when {
      "the current date is before the december deadline and date of death is 1 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2019, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2019), hasEarlierYearsToDeclare = false)
      }

      "the current date is after the december deadline and date of death is 3 years ago" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 12, 23)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2019, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getFirstYearOfTaxLiability(draftId)

        result.futureValue mustEqual TaxLiabilityYear(TaxYear(2019), hasEarlierYearsToDeclare = false)
      }
    }
  }

  "getTaxYearOfDeath" must {
    "return the correct tax year the date of death falls in" when {
      "date of death is between Jan 1st and April 5th (inclusive)" in {

      val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
        .build()

      val startDate = LocalDate.of(2018, 1, 1)

      when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
        .thenReturn(Future.successful(Some(StartDate(startDate))))

      val service = application.injector.instanceOf[TaxLiabilityService]

      val result = service.getTaxYearOfStartDate(draftId)

      result.futureValue mustEqual TaxYear(2017)
      }

      "date of death is between April 6th and Dec 31st (inclusive)" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        val startDate = LocalDate.of(2018, 6, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        val service = application.injector.instanceOf[TaxLiabilityService]

        val result = service.getTaxYearOfStartDate(draftId)

        result.futureValue mustEqual TaxYear(2018)
      }
    }
  }

  "evaluate answers to CY-1, CY-2, CY-3 and CY-4" when {

    "need to pay tax for CY-1, CY-2, CY-3, CY-4 years (Before 5 October)" must {

      "generate a list with 4 tax consequences" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(2020, 5, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = "17", taxConsequence = true),
          YearReturnType(taxReturnYear = "18", taxConsequence = true),
          YearReturnType(taxReturnYear = "19", taxConsequence = true),
          YearReturnType(taxReturnYear = "20", taxConsequence = false)
        )
      }

      "when CY-1 is late (after 5 October)" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(2020, 10, 6)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = "17", taxConsequence = true),
          YearReturnType(taxReturnYear = "18", taxConsequence = true),
          YearReturnType(taxReturnYear = "19", taxConsequence = true),
          YearReturnType(taxReturnYear = "20", taxConsequence = true)
        )
      }

    }

    "need to pay tax for CY-1, CY-2 (before 5 October)" when {

      "generate a list with 2 tax consequences" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(2020, 5, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = "19", taxConsequence = true),
          YearReturnType(taxReturnYear = "20", taxConsequence = false)
        )
      }

    }

    "need to pay tax for CY-2, CY-4" when {

      "generate a list with 2 tax consequences" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), false).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), false).success.value

        val today = LocalDate.of(2020, 5, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = "17", taxConsequence = true),
          YearReturnType(taxReturnYear = "19", taxConsequence = true)
        )
      }

    }

  }

  "must evaluate answers for CY-1" when {

    "tax owed is late (after 5 October)" must {

      "generate a list with 1 tax consequence (false)" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(2020, 10, 6)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = "20", taxConsequence = true)
        )
      }

    }

    "tax owed is not late (before 5 October)" must {

      "generate a list with 1 tax consequence (true)" in {
        val userAnswers = emptyUserAnswers
          .set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), false).success.value

        val today = LocalDate.of(2020, 10, 5)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(today)))
          .build()

        val service = application.injector.instanceOf[TaxLiabilityService]

        val expected = service.evaluateTaxYears(userAnswers)

        expected mustBe List(
          YearReturnType(taxReturnYear = "20", taxConsequence = false)
        )
      }

    }

  }

}
