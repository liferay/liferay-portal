<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PasswordPolicy passwordPolicy = userDisplayContext.getPasswordPolicy();
User selUser = userDisplayContext.getSelectedUser();

boolean passwordReset = false;
boolean passwordResetDisabled = false;

if (((selUser == null) || (selUser.getLastLoginDate() == null)) && passwordPolicy.isChangeable() && passwordPolicy.isChangeRequired()) {
	passwordReset = true;
	passwordResetDisabled = true;
}
else {
	passwordReset = BeanParamUtil.getBoolean(selUser, request, "passwordReset");

	if (!passwordPolicy.isChangeable()) {
		passwordResetDisabled = true;
	}
}
%>

<liferay-ui:error-marker
	key="<%= WebKeys.ERROR_SECTION %>"
	value="password"
/>

<aui:model-context bean="<%= selUser %>" model="<%= User.class %>" />

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

	<liferay-ui:message arguments="<%= upe.regex %>" key="that-password-does-not-comply-with-the-regular-expression" translateArguments="<%= false %>" />
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
<liferay-ui:error exception="<%= UserPasswordException.MustMatchCurrentPassword.class %>" message="the-password-you-entered-for-the-current-password-does-not-match-your-current-password.-please-try-again" />
<liferay-ui:error exception="<%= UserPasswordException.MustNotBeChanged.class %>" message="passwords-may-not-be-changed-under-the-current-password-policy" />

<liferay-ui:error exception="<%= UserPasswordException.MustNotBeChangedYet.class %>">

	<%
	UserPasswordException.MustNotBeChangedYet upe = (UserPasswordException.MustNotBeChangedYet)errorException;

	Format dateFormat = FastDateFormatFactoryUtil.getDateTime(FastDateFormatConstants.SHORT, FastDateFormatConstants.LONG, locale, TimeZone.getTimeZone(upe.timeZoneId));
	%>

	<liferay-ui:message arguments="<%= dateFormat.format(upe.changeableDate) %>" key="you-cannot-change-your-password-yet" translateArguments="<%= false %>" />
</liferay-ui:error>

<liferay-ui:error exception="<%= UserPasswordException.MustNotBeEqualToCurrent.class %>" message="your-new-password-cannot-be-the-same-as-your-old-password-please-enter-a-different-password" />
<liferay-ui:error exception="<%= UserPasswordException.MustNotBeNull.class %>" message="the-password-cannot-be-blank" />
<liferay-ui:error exception="<%= UserPasswordException.MustNotBeRecentlyUsed.class %>" message="that-password-has-already-been-used-please-enter-a-different-password" />
<liferay-ui:error exception="<%= UserPasswordException.MustNotBeTrivial.class %>" message="that-password-uses-common-words-please-enter-a-password-that-is-harder-to-guess-i-e-contains-a-mix-of-numbers-and-letters" />
<liferay-ui:error exception="<%= UserPasswordException.MustNotContainDictionaryWords.class %>" message="that-password-uses-common-dictionary-words" />

<clay:sheet-section>
	<h3 class="sheet-subtitle"><liferay-ui:message key="password" /></h3>

	<!-- Begin LPS-38289, LPS-55993, and LPS-61876 -->

	<input class="hide" type="password" />

	<input class="hide" type="password" />

	<!-- End LPS-38289, LPS-55993, and LPS-61876 -->

	<c:if test="<%= portletName.equals(myAccountPortletId) %>">
		<aui:input label="current-password" name="password0" required="<%= true %>" size="30" type="password" />
	</c:if>

	<aui:input autocomplete="new-password" label="new-password" name="password1" required="<%= true %>" size="30" type="password" />

	<aui:input autocomplete="new-password" label="reenter-password" name="password2" required="<%= true %>" size="30" type="password">
		<aui:validator name="equalTo">
			'#<portlet:namespace />password1'
		</aui:validator>
	</aui:input>

	<c:if test="<%= (selUser == null) || (user.getUserId() != selUser.getUserId()) %>">
		<aui:input disabled="<%= passwordResetDisabled %>" label="require-password-reset" name="passwordReset" type="checkbox" value="<%= passwordReset %>" />
	</c:if>
</clay:sheet-section>

<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_REMINDER_QUERIES_ENABLED, PropsValues.USERS_REMINDER_QUERIES_ENABLED) && portletName.equals(myAccountPortletId) %>">
	<clay:sheet-section>
		<h3 class="sheet-subtitle"><liferay-ui:message key="reminder" /></h3>

		<%
		boolean hasCustomQuestion = true;
		%>

		<%@ include file="/user/password_reminder_query_questions.jspf" %>

		<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_REMINDER_QUERIES_CUSTOM_QUESTION_ENABLED, PropsValues.USERS_REMINDER_QUERIES_CUSTOM_QUESTION_ENABLED) %>">
			<div class="<%= hasCustomQuestion ? "" : "hide" %>" id="<portlet:namespace />customQuestionDiv">
				<aui:input autocomplete='<%= PropsValues.COMPANY_SECURITY_PASSWORD_REMINDER_QUERY_FORM_AUTOCOMPLETE ? "on" : "off" %>' fieldParam="reminderQueryCustomQuestion" label="custom-question" name="reminderQueryQuestion" />
			</div>
		</c:if>

		<%
		String answer = selUser.getReminderQueryAnswer();

		if (!PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_REMINDER_QUERIES_DISPLAY_IN_PLAIN_TEXT, PropsValues.USERS_REMINDER_QUERIES_DISPLAY_IN_PLAIN_TEXT) && Validator.isNotNull(answer)) {
			answer = Portal.TEMP_OBFUSCATION_VALUE;
		}
		%>

		<aui:input autocomplete='<%= PropsValues.COMPANY_SECURITY_PASSWORD_REMINDER_QUERY_FORM_AUTOCOMPLETE ? "on" : "off" %>' label="answer[noun]" maxlength="<%= ModelHintsConstants.TEXT_MAX_LENGTH %>" name="reminderQueryAnswer" size="50" type='<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_REMINDER_QUERIES_DISPLAY_IN_PLAIN_TEXT, PropsValues.USERS_REMINDER_QUERIES_DISPLAY_IN_PLAIN_TEXT) ? "text" : "password" %>' value="<%= answer %>" />
	</clay:sheet-section>

	<aui:script sandbox="<%= true %>">
		var reminderQueryQuestionSelect = document.getElementById(
			'<portlet:namespace />reminderQueryQuestion'
		);

		if (reminderQueryQuestionSelect) {
			reminderQueryQuestionSelect.addEventListener('change', (event) => {
				var customQuestion =
					event.currentTarget.value ===
					'<%= UsersAdminUtil.CUSTOM_QUESTION %>';

				var focusInput;

				if (customQuestion) {
					var reminderQueryCustomQuestionInput = document.getElementById(
						'<portlet:namespace />reminderQueryCustomQuestion'
					);

					if (reminderQueryCustomQuestionInput) {

						<%
						for (String question : PrefsPropsUtil.getStringArray(company.getCompanyId(), PropsKeys.USERS_REMINDER_QUERIES_QUESTIONS, StringPool.COMMA)) {
						%>

							if (
								reminderQueryCustomQuestionInput.value ===
								'<%= UnicodeFormatter.toString(question) %>'
							) {
								reminderQueryCustomQuestionInput.value = '';
							}

						<%
						}
						%>

						focusInput = reminderQueryCustomQuestionInput;
					}
				}
				else {
					focusInput = '#<portlet:namespace />reminderQueryAnswer';
				}

				var customQuestionDiv = document.getElementById(
					'<portlet:namespace />customQuestionDiv'
				);

				if (customQuestionDiv) {
					if (!customQuestion) {
						customQuestionDiv.classList.add('hide');
					}
					else {
						customQuestionDiv.classList.remove('hide');
					}
				}

				Liferay.Util.focusFormField(focusInput);
			});
		}
	</aui:script>
</c:if>

<clay:sheet-section>
	<h3 class="sheet-subtitle"><liferay-ui:message key="web-dav-password" /></h3>

	<c:if test="<%= Validator.isNotNull(selUser.getDigest()) %>">
		<div class="alert alert-info">
			<liferay-ui:message key="a-webdav-password-has-already-been-generated-and-will-be-expired-if-a-new-one-is-generated" />
		</div>
	</c:if>

	<aui:button data-qa-id="generateWebDAVPasswordButton" onClick='<%= liferayPortletResponse.getNamespace() + "generateWebDavPassword()" %>' value="generate-webdav-password" />
</clay:sheet-section>

<aui:script>
	window['<portlet:namespace />generateWebDavPassword'] = function () {
		var baseUrl;

		var data = {
			p_auth: '<%= AuthTokenUtil.getToken(request) %>',
		};

		baseUrl =
			'<portlet:actionURL copyCurrentRenderParameters="<%= false %>" name="/users_admin/generate_webdav_password" windowState="<%= LiferayWindowState.EXCLUSIVE.toString() %>"><portlet:param name="mvcRenderCommandName" value="/users_admin/generate_webdav_password" /><portlet:param name="p_u_i_d" value="<%= String.valueOf(selUser.getUserId()) %>" /></portlet:actionURL>';

		Liferay.Util.fetch(new URL(baseUrl), {
			body: Liferay.Util.objectToURLSearchParams(data),
			method: 'POST',
		})
			.then((response) => {
				return response.text();
			})
			.then((text) => {
				Liferay.Util.openModal({
					bodyHTML: text,
					onOpen: function (event) {
						var webdavPasswordInput = document.getElementById(
							'<portlet:namespace />webDAVPassword'
						);

						if (webdavPasswordInput) {
							webdavPasswordInput.focus();
						}
					},
					title: '<%= UnicodeLanguageUtil.get(request, "webdav-password-generated") %>',
				});
			})
			.catch((error) => {
				Liferay.Util.openToast({
					message: Liferay.Language.get(
						'an-unexpected-system-error-occurred'
					),
					type: 'danger',
				});
			});
	};
</aui:script>