<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

boolean male = ParamUtil.getBoolean(request, "male", true);

Calendar birthdayCalendar = CalendarFactoryUtil.getCalendar();

birthdayCalendar.set(Calendar.MONTH, Calendar.JANUARY);
birthdayCalendar.set(Calendar.DATE, 1);
birthdayCalendar.set(Calendar.YEAR, 1970);

renderResponse.setTitle(LanguageUtil.get(request, "create-account"));
%>

<portlet:actionURL name="/login/create_account" secure="<%= request.isSecure() %>" var="createAccountURL" windowState="<%= LiferayWindowState.MAXIMIZED.toString() %>">
	<portlet:param name="mvcRenderCommandName" value="/login/create_account" />
</portlet:actionURL>

<aui:form action="<%= createAccountURL %>" method="post" name="fm" validateOnBlur="<%= false %>">
	<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.ADD %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />

	<liferay-ui:error exception="<%= AddressCityException.class %>" message="please-enter-a-valid-city" />
	<liferay-ui:error exception="<%= AddressStreetException.class %>" message="please-enter-a-valid-street" />
	<liferay-ui:error exception="<%= AddressZipException.class %>" message="please-enter-a-valid-postal-code" />
	<liferay-ui:error exception="<%= CaptchaConfigurationException.class %>" message="a-captcha-error-occurred-please-contact-an-administrator" />
	<liferay-ui:error exception="<%= CaptchaException.class %>" message="captcha-verification-failed" />
	<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />
	<liferay-ui:error exception="<%= CompanyMaxUsersException.class %>" message="unable-to-create-user-account-because-the-maximum-number-of-users-has-been-reached" />
	<liferay-ui:error exception="<%= ContactBirthdayException.class %>" message="please-enter-a-valid-birthday" />
	<liferay-ui:error exception="<%= ContactNameException.MustHaveFirstName.class %>" message="please-enter-a-valid-first-name" />
	<liferay-ui:error exception="<%= ContactNameException.MustHaveLastName.class %>" message="please-enter-a-valid-last-name" />
	<liferay-ui:error exception="<%= ContactNameException.MustHaveValidFullName.class %>" message="please-enter-a-valid-first-middle-and-last-name" />
	<liferay-ui:error exception="<%= EmailAddressException.class %>" message="please-enter-a-valid-email-address" />

	<liferay-ui:error exception="<%= GroupFriendlyURLException.class %>">

		<%
		GroupFriendlyURLException gfurle = (GroupFriendlyURLException)errorException;
		%>

		<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.DUPLICATE %>">
			<liferay-ui:message key="the-screen-name-you-requested-is-associated-with-an-existing-friendly-url" />
		</c:if>
	</liferay-ui:error>

	<liferay-ui:error exception="<%= NoSuchCountryException.class %>" message="please-select-a-country" />
	<liferay-ui:error exception="<%= NoSuchListTypeException.class %>" message="please-select-a-type" />
	<liferay-ui:error exception="<%= NoSuchRegionException.class %>" message="please-select-a-region" />
	<liferay-ui:error exception="<%= PhoneNumberException.class %>" message="please-enter-a-valid-phone-number" />
	<liferay-ui:error exception="<%= PhoneNumberExtensionException.class %>" message="please-enter-a-valid-phone-number-extension" />
	<liferay-ui:error exception="<%= RequiredFieldException.class %>" message="please-fill-out-all-required-fields" />
	<liferay-ui:error exception="<%= TermsOfUseException.class %>" message="you-must-agree-to-the-terms-of-use" />
	<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeNull.class %>" message="please-enter-an-email-address" />
	<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBePOP3User.class %>" message="the-email-address-you-requested-is-reserved" />
	<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeReserved.class %>" message="the-email-address-you-requested-is-reserved" />
	<liferay-ui:error exception="<%= UserEmailAddressException.MustNotUseCompanyMx.class %>" message="the-email-address-you-requested-is-not-valid-because-its-domain-is-reserved" />
	<liferay-ui:error exception="<%= UserEmailAddressException.MustValidate.class %>" message="please-enter-a-valid-email-address" />
	<liferay-ui:error exception="<%= UserIdException.MustNotBeNull.class %>" message="please-enter-a-user-id" />
	<liferay-ui:error exception="<%= UserIdException.MustNotBeReserved.class %>" message="the-user-id-you-requested-is-reserved" />

	<liferay-ui:error exception="<%= UserPasswordException.MustBeLonger.class %>">

		<%
		UserPasswordException.MustBeLonger upe = (UserPasswordException.MustBeLonger)errorException;
		%>

		<liferay-ui:message arguments="<%= String.valueOf(upe.minLength) %>" key="that-password-is-too-short" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= UserPasswordException.MustComplyWithModelListeners.class %>" message="that-password-is-invalid-please-enter-a-different-password" />

	<liferay-ui:error exception="<%= UserPasswordException.MustComplyWithRegex.class %>">

		<%
		UserPasswordException.MustComplyWithRegex upe = (UserPasswordException.MustComplyWithRegex)errorException;
		%>

		<liferay-ui:message arguments="<%= HtmlUtil.escape(upe.regex) %>" key="that-password-does-not-comply-with-the-regular-expression" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= UserPasswordException.MustHaveMoreAlphanumeric.class %>">

		<%
		UserPasswordException.MustHaveMoreAlphanumeric upe = (UserPasswordException.MustHaveMoreAlphanumeric)errorException;
		%>

		<liferay-ui:message arguments="<%= String.valueOf(upe.minAlphanumeric) %>" key="that-password-must-contain-at-least-x-alphanumeric-characters" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= UserPasswordException.MustHaveMoreLowercase.class %>">

		<%
		UserPasswordException.MustHaveMoreLowercase upe = (UserPasswordException.MustHaveMoreLowercase)errorException;
		%>

		<liferay-ui:message arguments="<%= String.valueOf(upe.minLowercase) %>" key="that-password-must-contain-at-least-x-lowercase-characters" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= UserPasswordException.MustHaveMoreNumbers.class %>">

		<%
		UserPasswordException.MustHaveMoreNumbers upe = (UserPasswordException.MustHaveMoreNumbers)errorException;
		%>

		<liferay-ui:message arguments="<%= String.valueOf(upe.minNumbers) %>" key="that-password-must-contain-at-least-x-numbers" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= UserPasswordException.MustHaveMoreSymbols.class %>">

		<%
		UserPasswordException.MustHaveMoreSymbols upe = (UserPasswordException.MustHaveMoreSymbols)errorException;
		%>

		<liferay-ui:message arguments="<%= String.valueOf(upe.minSymbols) %>" key="that-password-must-contain-at-least-x-symbols" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= UserPasswordException.MustHaveMoreUppercase.class %>">

		<%
		UserPasswordException.MustHaveMoreUppercase upe = (UserPasswordException.MustHaveMoreUppercase)errorException;
		%>

		<liferay-ui:message arguments="<%= String.valueOf(upe.minUppercase) %>" key="that-password-must-contain-at-least-x-uppercase-characters" translateArguments="<%= false %>" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= UserPasswordException.MustMatch.class %>" message="the-passwords-you-entered-do-not-match" />
	<liferay-ui:error exception="<%= UserPasswordException.MustNotBeNull.class %>" message="the-password-cannot-be-blank" />
	<liferay-ui:error exception="<%= UserPasswordException.MustNotBeTrivial.class %>" message="that-password-uses-common-words-please-enter-a-password-that-is-harder-to-guess-i-e-contains-a-mix-of-numbers-and-letters" />
	<liferay-ui:error exception="<%= UserPasswordException.MustNotContainDictionaryWords.class %>" message="that-password-uses-common-dictionary-words" />
	<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeDuplicate.class %>" focusField="screenName" message="the-screen-name-you-requested-is-already-taken" />
	<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNull.class %>" focusField="screenName" message="the-screen-name-cannot-be-blank" />
	<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNumeric.class %>" focusField="screenName" message="the-screen-name-cannot-contain-only-numeric-values" />
	<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReserved.class %>" message="the-screen-name-you-requested-is-reserved" />
	<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReservedForAnonymous.class %>" focusField="screenName" message="the-screen-name-you-requested-is-reserved-for-the-anonymous-user" />
	<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeUsedByGroup.class %>" focusField="screenName" message="the-screen-name-you-requested-is-already-taken-by-a-site" />

	<liferay-ui:error exception="<%= UserScreenNameException.MustNotExceedMaximumLength.class %>" focusField="screenName">

		<%
		int screenNameMaxLength = ModelHintsUtil.getMaxLength(User.class.getName(), "screenName");
		%>

		<liferay-ui:message arguments="<%= String.valueOf(screenNameMaxLength) %>" key="please-enter-a-screen-name-with-fewer-than-x-characters" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= UserScreenNameException.MustProduceValidFriendlyURL.class %>" focusField="screenName" message="the-screen-name-you-requested-must-produce-a-valid-friendly-url" />

	<liferay-ui:error exception="<%= UserScreenNameException.MustValidate.class %>" focusField="screenName">

		<%
		UserScreenNameException.MustValidate usne = (UserScreenNameException.MustValidate)errorException;
		%>

		<liferay-ui:message key="<%= usne.screenNameValidator.getDescription(locale) %>" />
	</liferay-ui:error>

	<liferay-ui:error exception="<%= WebsiteURLException.class %>" message="please-enter-a-valid-url" />

	<aui:model-context model="<%= Contact.class %>" />

	<clay:sheet>
		<clay:sheet-section>
			<div class="form-group">
				<h3 class="sheet-subtitle"><liferay-ui:message key="user-display-data" /></h3>

				<clay:row>
					<clay:col
						md="6"
					>

						<%
						Boolean autoGenerateScreenName = PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE);
						%>

						<c:if test="<%= !autoGenerateScreenName %>">
							<aui:input model="<%= User.class %>" name="screenName">

								<%
								ScreenNameValidator screenNameValidator = ScreenNameValidatorFactory.getInstance();
								%>

								<c:if test="<%= Validator.isNotNull(screenNameValidator.getAUIValidatorJS()) %>">
									<aui:validator errorMessage="<%= screenNameValidator.getDescription(locale) %>" name="custom">
										<%= screenNameValidator.getAUIValidatorJS() %>
									</aui:validator>
								</c:if>
							</aui:input>
						</c:if>

						<aui:input model="<%= User.class %>" name="emailAddress" required="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED) %>" />
					</clay:col>
				</clay:row>
			</div>

			<div class="form-group">
				<h3 class="sheet-subtitle"><liferay-ui:message key="personal-information" /></h3>

				<clay:row>
					<clay:col
						md="6"
					>
						<liferay-user:user-name-fields />
					</clay:col>

					<clay:col
						md="5"
					>
						<c:choose>
							<c:when test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_BIRTHDAY) %>">
								<aui:input name="birthday" value="<%= birthdayCalendar %>" />
							</c:when>
							<c:otherwise>
								<aui:input name="birthdayMonth" type="hidden" value="<%= Calendar.JANUARY %>" />
								<aui:input name="birthdayDay" type="hidden" value="1" />
								<aui:input name="birthdayYear" type="hidden" value="1970" />
							</c:otherwise>
						</c:choose>

						<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.FIELD_ENABLE_COM_LIFERAY_PORTAL_KERNEL_MODEL_CONTACT_MALE) %>">
							<aui:select label="gender" name="male">
								<aui:option label="male" value="1" />
								<aui:option label="female" selected="<%= !male %>" value="0" />
							</aui:select>
						</c:if>
					</clay:col>
				</clay:row>
			</div>

			<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.LOGIN_CREATE_ACCOUNT_ALLOW_CUSTOM_PASSWORD, PropsValues.LOGIN_CREATE_ACCOUNT_ALLOW_CUSTOM_PASSWORD) %>">
				<div class="form-group">
					<h3 class="sheet-subtitle"><liferay-ui:message key="password" /></h3>

					<clay:row>
						<clay:col
							md="6"
						>
							<aui:input label="password" name="password1" required="<%= true %>" size="30" type="password" value="" />

							<aui:input label="reenter-password" name="password2" required="<%= true %>" size="30" type="password" value="">
								<aui:validator name="equalTo">
									'#<portlet:namespace />password1'
								</aui:validator>
							</aui:input>
						</clay:col>
					</clay:row>
				</div>
			</c:if>

			<c:if test="<%= captchaConfiguration.createAccountCaptchaEnabled() %>">
				<div class="form-group">
					<h3 class="mb-2 sheet-subtitle"><liferay-ui:message key="verification" /></h3>

					<clay:row>
						<clay:col
							md="6"
						>
							<liferay-captcha:captcha />
						</clay:col>
					</clay:row>
				</div>
			</c:if>

			<div class="form-group">
				<aui:button-row>
					<aui:button type="submit" />
				</aui:button-row>

				<%@ include file="/navigation.jspf" %>
			</div>
		</clay:sheet-section>
	</clay:sheet>
</aui:form>