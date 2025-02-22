/*
 * Copyright 2021 HM Revenue & Customs
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

package views

import forms.YesNoFormProviderWithArguments
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.CYMinusTwoYesNoView

class CYMinusTwoLiabilityYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "cyMinusTwo.liability"
  val taxYear = "tax range"

  val form: Form[Boolean] = new YesNoFormProviderWithArguments().withPrefix(messageKeyPrefix, Seq("6 April 2019", "5 April 2020"))

  "CYMinusTwoLiabilityYesNo view" must {

    val view = viewFor[CYMinusTwoYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, draftId, taxYear, NormalMode)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, taxYear)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, Some(taxYear))

    behave like pageWithASubmitButton(applyView(form))
  }
}
