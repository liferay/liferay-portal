<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
User selUser = (User)request.getAttribute(UsersAdminWebKeys.SELECTED_USER);
%>

<aui:model-context bean="<%= selUser %>" model="<%= User.class %>" />

<liferay-ui:success key="verificationEmailSent" message="your-email-verification-code-has-been-sent-and-the-new-email-address-will-be-applied-to-your-account-once-it-has-been-verified" />

<liferay-ui:error exception="<%= CompanyMaxUsersException.class %>" message="unable-to-create-user-account-because-the-maximum-number-of-users-has-been-reached" />

<liferay-ui:error exception="<%= GroupFriendlyURLException.class %>" focusField="screenName">

	<%
	GroupFriendlyURLException gfurle = (GroupFriendlyURLException)errorException;
	%>

	<c:if test="<%= gfurle.getType() == GroupFriendlyURLException.DUPLICATE %>">
		<liferay-ui:message key="the-screen-name-you-requested-is-associated-with-an-existing-friendly-url" />
	</c:if>
</liferay-ui:error>

<liferay-ui:error exception="<%= UserFieldException.class %>">

	<%
	UserFieldException ufe = (UserFieldException)errorException;

	List<String> fields = ufe.getFields();

	StringBundler sb = new StringBundler((2 * fields.size()) - 1);

	for (int i = 0; i < fields.size(); i++) {
		String field = fields.get(i);

		sb.append(LanguageUtil.get(request, TextFormatter.format(field, TextFormatter.K)));

		if ((i + 1) < fields.size()) {
			sb.append(StringPool.COMMA_AND_SPACE);
		}
	}
	%>

	<liferay-ui:message arguments="<%= sb.toString() %>" key="your-portal-administrator-has-disabled-the-ability-to-modify-the-following-fields" translateArguments="<%= false %>" />
</liferay-ui:error>

<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeDuplicate.class %>" focusField="screenName" message="the-screen-name-you-requested-is-already-taken" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNull.class %>" focusField="screenName" message="the-screen-name-cannot-be-blank" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNumeric.class %>" focusField="screenName" message="the-screen-name-cannot-contain-only-numeric-values" />
<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeReserved.class %>" focusField="screenName" message="the-screen-name-you-requested-is-reserved" />
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

<c:choose>
	<c:when test="<%= selUser != null %>">
		<c:choose>
			<c:when test='<%= UsersAdminUtil.hasUpdateFieldPermission(permissionChecker, user, selUser, "portrait") %>'>
				<liferay-frontend:logo-selector
					aspectRatio="<%= 1 %>"
					currentLogoURL="<%= selUser.getPortraitURL(themeDisplay) %>"
					defaultLogoURL="<%= UserConstants.getPortraitURL(themeDisplay.getPathImage(), selUser.isMale(), 0, null) %>"
					label='<%= LanguageUtil.get(request, "image") %>'
					preserveRatio="<%= true %>"
					type="user_portrait"
				/>
			</c:when>
			<c:otherwise>
				<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="portrait" />" src="<%= selUser.getPortraitURL(themeDisplay) %>" />
			</c:otherwise>
		</c:choose>
	</c:when>
	<c:otherwise>
		<liferay-frontend:logo-selector
			aspectRatio="<%= 1 %>"
			currentLogoURL='<%= themeDisplay.getPathImage() + "/user_portrait?img_id=0" %>'
			defaultLogoURL='<%= themeDisplay.getPathImage() + "/user_portrait?img_id=0" %>'
			label='<%= LanguageUtil.get(request, "image") %>'
			preserveRatio="<%= true %>"
			type="user_portrait"
		/>
	</c:otherwise>
</c:choose>

<aui:input name="password" type="hidden" value="" />

<c:if test="<%= !PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE) || (selUser != null) %>">
	<c:choose>
		<c:when test='<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_SCREEN_NAME_ALWAYS_AUTOGENERATE) || !UsersAdminUtil.hasUpdateFieldPermission(permissionChecker, user, selUser, "screenName") || ((selUser != null) && (selUser.getType() == UserConstants.TYPE_DEFAULT_SERVICE_ACCOUNT)) %>'>
			<aui:input disabled="<%= true %>" name="screenName" />
		</c:when>
		<c:otherwise>
			<aui:input name="screenName">

				<%
				ScreenNameValidator screenNameValidator = ScreenNameValidatorFactory.getInstance();
				%>

				<c:if test="<%= Validator.isNotNull(screenNameValidator.getAUIValidatorJS()) %>">
					<aui:validator errorMessage="<%= screenNameValidator.getDescription(locale) %>" name="custom">
						<%= screenNameValidator.getAUIValidatorJS() %>
					</aui:validator>
				</c:if>
			</aui:input>
		</c:otherwise>
	</c:choose>
</c:if>

<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeDuplicate.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-already-taken" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeNull.class %>" focusField="emailAddress" message="please-enter-an-email-address" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBePOP3User.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-reserved" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeReserved.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-reserved" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustNotUseCompanyMx.class %>" focusField="emailAddress" message="the-email-address-you-requested-is-not-valid-because-its-domain-is-reserved" />
<liferay-ui:error exception="<%= UserEmailAddressException.MustValidate.class %>" focusField="emailAddress" message="please-enter-a-valid-email-address" />

<c:choose>
	<c:when test='<%= !UsersAdminUtil.hasUpdateFieldPermission(permissionChecker, user, selUser, "emailAddress") %>'>
		<aui:input disabled="<%= true %>" name="emailAddress" />
	</c:when>
	<c:otherwise>

		<%
		User displayEmailAddressUser = null;

		if (selUser != null) {
			displayEmailAddressUser = (User)selUser.clone();

			displayEmailAddressUser.setEmailAddress(displayEmailAddressUser.getDisplayEmailAddress());
		}
		%>

		<aui:input bean="<%= displayEmailAddressUser %>" model="<%= User.class %>" name="emailAddress">
			<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.USERS_EMAIL_ADDRESS_REQUIRED) %>">
				<aui:validator name="required" />
			</c:if>
		</aui:input>
	</c:otherwise>
</c:choose>

<c:if test="<%= selUser != null %>">
	<liferay-ui:error exception="<%= UserIdException.MustNotBeNull.class %>" message="please-enter-a-user-id" />
	<liferay-ui:error exception="<%= UserIdException.MustNotBeReserved.class %>" message="the-user-id-you-requested-is-reserved" />

	<aui:input name="userId" readonly="true" type="text" value="<%= String.valueOf(selUser.getUserId()) %>" />
</c:if>

<portlet:renderURL var="verifyPasswordURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
	<portlet:param name="mvcPath" value="/user/password_verification.jsp" />
</portlet:renderURL>

<c:if test="<%= (selUser != null) && company.isUpdatePasswordRequired() %>">
	<aui:script use="liferay-form">
		Liferay.once('<portlet:namespace />formReady', () => {
			var form = Liferay.Form.get('<portlet:namespace />fm');

			form.set('onSubmit', (event) => {
				event.preventDefault();

				var emailAddressInput = document.getElementById(
					'<portlet:namespace />emailAddress'
				);
				var screenNameInput = document.getElementById(
					'<portlet:namespace />screenName'
				);

				if (
					(emailAddressInput.value &&
						emailAddressInput.value !==
							'<%= HtmlUtil.escapeJS(selUser.getEmailAddress()) %>') ||
					screenNameInput.value !==
						'<%= HtmlUtil.escapeJS(selUser.getScreenName()) %>'
				) {
					Liferay.Util.openModal({
						customEvents: [
							{
								name: '<%= liferayPortletResponse.getNamespace() + "verifyPassword" %>',
								onEvent: function (event) {
									var passwordInput = document.getElementById(
										'<portlet:namespace />password'
									);

									passwordInput.value = event.data;

									submitForm(form.form);
								},
							},
						],
						height: '320px',
						id: 'password-verification-dialog',
						size: 'md',
						title: '<%= LanguageUtil.get(request, "confirm-password") %>',
						url: '<%= verifyPasswordURL %>',
					});
				}
				else {
					submitForm(form.form);
				}
			});
		});
	</aui:script>
</c:if>