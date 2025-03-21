<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ page import="jakarta.portlet.WindowState" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %>

<%@ include file="/init.jsp" %>

<c:choose>
	<c:when test="<%= themeDisplay.isSignedIn() %>">

		<%
		String signedInAs = HtmlUtil.escape(user.getFullName());

		if (themeDisplay.isShowMyAccountIcon() && (themeDisplay.getURLMyAccount() != null)) {
			String myAccountURL = String.valueOf(themeDisplay.getURLMyAccount());

			signedInAs = "<a class=\"signed-in\" href=\"" + HtmlUtil.escape(myAccountURL) + "\">" + signedInAs + "</a>";
		}
		%>

		<liferay-ui:message arguments="<%= signedInAs %>" key="you-are-signed-in-as-x" translateArguments="<%= false %>" />
	</c:when>
	<c:otherwise>

		<%
		String formName = "loginForm";

		if (windowState.equals(LiferayWindowState.EXCLUSIVE)) {
			formName += "Modal";
		}

		String redirect = ParamUtil.getString(request, "redirect");
		String login = (String)SessionErrors.get(renderRequest, "login");

		if (Validator.isNull(login)) {
			login = LoginUtil.getLogin(request, "login", company);
		}

		String password = StringPool.BLANK;
		boolean rememberMe = ParamUtil.getBoolean(request, "rememberMe");

		if (Validator.isNull(authType)) {
			authType = company.getAuthType();
		}
		%>

		<div class="login-container">
			<portlet:actionURL name="/login/login" secure="<%= request.isSecure() %>" var="loginURL">
				<portlet:param name="mvcRenderCommandName" value="/login/login" />
			</portlet:actionURL>

			<svg height="48" width="164" xmlns="http://www.w3.org/2000/svg">
				<g fill="none" fill-rule="evenodd">
					<path d="M0 5.444A2.444 2.444 0 0 1 2.444 3h28.112A2.444 2.444 0 0 1 33 5.444v28.112A2.444 2.444 0 0 1 30.556 36H2.444A2.444 2.444 0 0 1 0 33.556V5.444zM4.889 8.5c0-.338.273-.611.611-.611h3.667c.337 0 .61.273.61.611v3.667a.611.611 0 0 1-.61.61H5.5a.611.611 0 0 1-.611-.61V8.5zm6.722-.611A.611.611 0 0 0 11 8.5v3.667c0 .337.274.61.611.61h3.667a.611.611 0 0 0 .61-.61V8.5a.611.611 0 0 0-.61-.611H11.61zm5.5.611c0-.338.274-.611.611-.611h3.667c.337 0 .611.273.611.611v3.667a.611.611 0 0 1-.611.61h-3.667a.611.611 0 0 1-.61-.61V8.5zM5.5 14a.611.611 0 0 0-.611.611v3.667c0 .337.273.61.611.61h3.667a.611.611 0 0 0 .61-.61V14.61a.611.611 0 0 0-.61-.611H5.5zm5.5.611c0-.337.274-.611.611-.611h3.667c.337 0 .61.274.61.611v3.667a.611.611 0 0 1-.61.61H11.61a.611.611 0 0 1-.611-.61V14.61zM23.833 14a.611.611 0 0 0-.61.611v3.667c0 .337.273.61.61.61H27.5a.611.611 0 0 0 .611-.61V14.61A.611.611 0 0 0 27.5 14h-3.667zM4.89 20.722c0-.337.273-.61.611-.61h3.667c.337 0 .61.273.61.61v3.667a.611.611 0 0 1-.61.611H5.5a.611.611 0 0 1-.611-.611v-3.667zm12.833-.61a.611.611 0 0 0-.61.61v3.667c0 .337.273.611.61.611h3.667a.611.611 0 0 0 .611-.611v-3.667a.611.611 0 0 0-.611-.61h-3.667zm5.5.61c0-.337.274-.61.611-.61H27.5c.338 0 .611.273.611.61v3.667A.611.611 0 0 1 27.5 25h-3.667a.611.611 0 0 1-.61-.611v-3.667zm-11.61 5.5a.611.611 0 0 0-.612.611V30.5c0 .338.274.611.611.611h3.667a.611.611 0 0 0 .61-.611v-3.667a.611.611 0 0 0-.61-.61H11.61zm5.5.611c0-.337.273-.61.61-.61h3.667c.337 0 .611.273.611.61V30.5a.611.611 0 0 1-.611.611h-3.667a.611.611 0 0 1-.61-.611v-3.667zm6.721-.61a.611.611 0 0 0-.61.61V30.5c0 .338.273.611.61.611H27.5a.611.611 0 0 0 .611-.611v-3.667a.611.611 0 0 0-.611-.61h-3.667z" fill="#0B63CE"></path>
					<path d="M60.347 34.422H45.934V4.324c0-.346-.345-.692-.69-.692H43.69c-.346 0-.691.346-.691.692v32.173c0 .346.345.692.69.692h16.658c.345 0 .69-.346.69-.692v-1.384c0-.345-.345-.691-.69-.691h-.001zM66.56 12.54h-1.467c-.346 0-.69.346-.69.692v23.352c0 .345.344.692.69.692h1.467c.345 0 .69-.347.69-.692V13.232c0-.432-.258-.692-.69-.692zm17.52-9.513l.518-1.297c.086-.346 0-.692-.345-.865C83.045.346 81.837 0 80.801 0c-1.811 0-3.365.605-4.4 1.903-1.037 1.21-1.641 3.113-1.641 5.535v5.102l-2.589.174c-.345 0-.604.346-.604.692v1.21c0 .346.345.692.69.692h2.503v21.276c0 .345.345.692.69.692h1.468c.345 0 .69-.347.69-.692V15.308h4.834c.345 0 .69-.346.69-.692v-1.384c0-.346-.345-.692-.69-.692h-4.834V7.697c0-1.643.259-2.854.777-3.719.518-.778 1.208-1.124 2.33-1.124.431 0 .863.087 1.208.173.432.087.864.26 1.295.433a.55.55 0 0 0 .518 0c.172-.174.345-.26.345-.433zm10.184 8.908c-1.38 0-2.761.346-4.056.951a9.803 9.803 0 0 0-3.366 2.595c-.95 1.124-1.812 2.508-2.33 4.065-.605 1.557-.863 3.372-.863 5.362 0 1.989.258 3.805.862 5.363.605 1.556 1.382 2.94 2.417 4.064a10.751 10.751 0 0 0 3.625 2.595c1.381.605 2.849.865 4.402.865 1.812 0 3.366-.26 4.487-.779 1.123-.519 2.159-1.038 3.021-1.557.346-.172.432-.605.26-.864l-.691-1.297c-.086-.174-.259-.26-.431-.346-.173 0-.432 0-.518.086a10.061 10.061 0 0 1-2.676 1.47c-.949.346-2.07.519-3.28.519-1.294 0-2.416-.26-3.452-.778-1.036-.52-1.899-1.211-2.675-2.076-.777-.865-1.295-1.99-1.726-3.2-.346-1.038-.518-2.162-.605-3.373h16.399c.345 0 .604-.26.69-.52.086-.345.086-.691.086-.95v-.778c0-3.633-.863-6.487-2.503-8.39-1.899-2.076-4.229-3.027-7.077-3.027zm6.56 10.984H86.498c.172-1.125.43-2.162.776-3.114.432-1.124 1.036-2.076 1.812-2.854.691-.778 1.468-1.383 2.417-1.73.863-.432 1.812-.605 2.762-.605 1.985 0 3.625.692 4.747 2.162 1.122 1.384 1.726 3.46 1.812 6.14v.001zm18.642-10.465c-.432-.26-.863-.346-1.295-.432-.431-.087-.777-.087-1.208-.087-1.554 0-3.02.519-4.229 1.557-.69.605-1.295 1.297-1.899 2.076l-.086-2.336c0-.346-.345-.692-.69-.692h-1.295c-.346 0-.69.346-.69.692v23.352c0 .345.344.692.69.692h1.467c.345 0 .69-.347.69-.692V20.497c.863-2.162 1.813-3.633 2.935-4.411 1.036-.865 1.985-1.211 2.935-1.211.517 0 .776 0 .949.087.173 0 .518.172.863.259a.55.55 0 0 0 .518 0c.172-.087.345-.26.345-.433l.432-1.384c.086-.432-.087-.778-.432-.95zm19.937.086h-1.382c-.345 0-.69.26-.69.692l-.086 1.038c-.863-.605-1.64-1.038-2.417-1.47a9.682 9.682 0 0 0-4.056-.865c-1.554 0-2.935.346-4.315.951a11.332 11.332 0 0 0-3.54 2.595c-1.035 1.124-1.811 2.508-2.416 4.065-.604 1.557-.863 3.372-.863 5.362 0 4.152.95 7.352 2.762 9.514 1.898 2.248 4.402 3.372 7.595 3.372 1.726 0 3.28-.432 4.747-1.21a13.894 13.894 0 0 0 2.589-1.816l.087 1.815c0 .346.345.606.69.606h1.295c.345 0 .69-.346.69-.692V13.146c0-.346-.345-.605-.69-.605zm-2.158 4.93v13.838c-1.209 1.211-2.417 2.076-3.539 2.768-1.122.605-2.33.95-3.625.95-1.208 0-2.33-.258-3.193-.691-.863-.432-1.726-1.124-2.33-1.989-.605-.865-1.122-1.99-1.468-3.2-.345-1.21-.517-2.68-.517-4.238 0-1.384.258-2.768.604-3.978.417-1.172.999-2.279 1.726-3.287.69-.95 1.554-1.643 2.59-2.162a6.376 6.376 0 0 1 3.106-.778c1.122 0 2.244.26 3.28.692 1.122.346 2.157 1.124 3.366 2.076zm26.669-4.67c-.173-.173-.345-.26-.518-.26h-1.554a.65.65 0 0 0-.604.433l-5.265 15.308c-.345.952-.69 2.076-1.122 3.287-.172.692-.43 1.296-.604 1.902-.259-.605-.517-1.297-.776-1.902a40.703 40.703 0 0 0-1.295-3.2l-5.955-15.308a.65.65 0 0 0-.604-.433h-1.64a.783.783 0 0 0-.604.26c-.087.086-.173.432-.087.605l9.58 23.697-.604 1.903c-.604 1.816-1.38 3.372-2.416 4.497-.863 1.125-1.9 1.643-3.194 1.643-.258 0-.604 0-.862-.086a11.05 11.05 0 0 1-.864-.26.55.55 0 0 0-.517 0c-.173.087-.26.26-.346.433l-.431 1.384c-.087.346.086.692.345.778a6.18 6.18 0 0 0 2.503.519c1.122 0 2.157-.26 3.02-.779.863-.432 1.64-1.124 2.33-1.902.691-.779 1.209-1.643 1.64-2.595.432-.951.863-1.816 1.123-2.767l9.32-26.638c.089-.228.138-.381 0-.52zM66.648 3.72H64.92a.866.866 0 0 0-.863.864v1.73c0 .432.345.864.863.864h1.727a.865.865 0 0 0 .862-.864v-1.73c0-.433-.43-.865-.862-.865z" fill="#09101D" fill-rule="nonzero"></path>
				</g>
			</svg>

			<div class="inline-alert-container lfr-alert-container"></div>

			<div class="sign-in-heading">Sign In</div>

			<div class="secondary-info">
				to continue to

				<span>analytics.liferay.com</span>
			</div>

			<aui:form action="<%= loginURL %>" autocomplete='<%= PropsValues.COMPANY_SECURITY_LOGIN_FORM_AUTOCOMPLETE ? "on" : "off" %>' cssClass="sign-in-form" method="post" name="<%= formName %>" onSubmit="event.preventDefault();" validateOnBlur="<%= false %>">
				<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />
				<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
				<aui:input name="doActionAfterLogin" type="hidden" value="<%= portletName.equals(PortletKeys.FAST_LOGIN) ? true : false %>" />

				<liferay-util:dynamic-include key="com.liferay.login.web#/login.jsp#alertPre" />

				<c:choose>
					<c:when test='<%= SessionMessages.contains(request, "passwordSent") %>'>
						<div class="alert alert-success"
							<liferay-ui:message key="your-password-was-sent-to-the-provided-email-address" />
						</div>
					</c:when>
					<c:when test='<%= SessionMessages.contains(request, "userAdded") %>'>

						<%
						String userEmailAddress = (String)SessionMessages.get(request, "userAdded");
						String userPassword = (String)SessionMessages.get(request, "userAddedPassword");
						%>

						<div class="alert alert-success">
							<c:choose>
								<c:when test="<%= company.isStrangersVerify() || Validator.isNull(userPassword) %>">
									<liferay-ui:message key="thank-you-for-creating-an-account" />

									<c:if test="<%= company.isStrangersVerify() %>">
										<liferay-ui:message arguments="<%= HtmlUtil.escape(userEmailAddress) %>" key="your-email-verification-code-was-sent-to-x" translateArguments="<%= false %>" />
									</c:if>
								</c:when>
								<c:otherwise>
									<liferay-ui:message arguments="<%= HtmlUtil.escape(userPassword) %>" key="thank-you-for-creating-an-account.-your-password-is-x" translateArguments="<%= false %>" />
								</c:otherwise>
							</c:choose>

							<c:if test="<%= PrefsPropsUtil.getBoolean(company.getCompanyId(), PropsKeys.ADMIN_EMAIL_USER_ADDED_ENABLED) %>">
								<liferay-ui:message arguments="<%= HtmlUtil.escape(userEmailAddress) %>" key="your-password-was-sent-to-x" translateArguments="<%= false %>" />
							</c:if>
						</div>
					</c:when>
					<c:when test='<%= SessionMessages.contains(request, "userPending") %>'>

						<%
						String userEmailAddress = (String)SessionMessages.get(request, "userPending");
						%>

						<div class="alert alert-success">
							<liferay-ui:message arguments="<%= HtmlUtil.escape(userEmailAddress) %>" key="thank-you-for-creating-an-account.-you-will-be-notified-via-email-at-x-when-your-account-has-been-approved" translateArguments="<%= false %>" />
						</div>
					</c:when>
				</c:choose>

				<liferay-ui:error exception="<%= AuthException.class %>" message="authentication-failed" />
				<liferay-ui:error exception="<%= CompanyMaxUsersException.class %>" message="unable-to-log-in-because-the-maximum-number-of-users-has-been-reached" />
				<liferay-ui:error exception="<%= CookieNotSupportedException.class %>" message="authentication-failed-please-enable-browser-cookies" />
				<liferay-ui:error exception="<%= NoSuchUserException.class %>" message="authentication-failed" />
				<liferay-ui:error exception="<%= PasswordExpiredException.class %>" message="your-password-has-expired" />
				<liferay-ui:error exception="<%= UserEmailAddressException.MustNotBeNull.class %>" message="please-enter-an-email-address" />
				<liferay-ui:error exception="<%= UserLockoutException.LDAPLockout.class %>" message="this-account-is-locked" />

				<liferay-ui:error exception="<%= UserLockoutException.PasswordPolicyLockout.class %>">

					<%
					UserLockoutException.PasswordPolicyLockout ule = (UserLockoutException.PasswordPolicyLockout)errorException;
					%>

					<c:choose>
						<c:when test="<%= ule.passwordPolicy.isRequireUnlock() %>">
							<liferay-ui:message key="this-account-is-locked" />
						</c:when>
						<c:otherwise>
							<liferay-ui:message arguments="<%= ule.user.getUnlockDate() %>" key="this-account-is-locked-until-x" translateArguments="<%= false %>" />
						</c:otherwise>
					</c:choose>
				</liferay-ui:error>

				<liferay-ui:error exception="<%= UserPasswordException.class %>" message="authentication-failed" />
				<liferay-ui:error exception="<%= UserScreenNameException.MustNotBeNull.class %>" message="the-screen-name-cannot-be-blank" />

				<liferay-util:dynamic-include key="com.liferay.login.web#/login.jsp#alertPost" />

				<aui:fieldset>

					<%
					String loginLabel = null;

					if (authType.equals(CompanyConstants.AUTH_TYPE_EA)) {
						loginLabel = "email-address";
					}
					else if (authType.equals(CompanyConstants.AUTH_TYPE_SN)) {
						loginLabel = "screen-name";
					}
					else if (authType.equals(CompanyConstants.AUTH_TYPE_ID)) {
						loginLabel = "id";
					}
					%>

					<aui:input autoFocus="<%= windowState.equals(LiferayWindowState.EXCLUSIVE) || windowState.equals(WindowState.MAXIMIZED) %>" cssClass="clearable" label="<%= loginLabel %>" name="login" showRequiredLabel="<%= false %>" type="text" value="<%= login %>">
						<aui:validator name="required" />
					</aui:input>

					<aui:input name="password" showRequiredLabel="<%= false %>" type="password" value="<%= password %>">
						<aui:validator name="required" />
					</aui:input>

					<span class="d-none" id="<portlet:namespace />passwordCapsLockSpan"><liferay-ui:message key="caps-lock-is-on" /></span>

					<c:if test="<%= company.isAutoLogin() %>">
						<aui:input checked="<%= rememberMe %>" name="rememberMe" type="checkbox" />
					</c:if>
				</aui:fieldset>

				<aui:button-row>
					<aui:button cssClass="btn-lg" type="submit" value="sign-in" />
				</aui:button-row>
			</aui:form>

			<%@ include file="/navigation.jspf" %>
		</div>

		<aui:script sandbox="<%= true %>">
			var form = document.querySelector(
				'[name=<portlet:namespace /><%= formName %>]'
			);

			form.addEventListener('submit', (event) => {
				<c:if test="<%= Validator.isNotNull(redirect) %>">
					var redirect = form.querySelector('[name=<portlet:namespace />redirect');

					if (redirect) {
						redirect.value = redirect.value + window.location.hash;
					}
				</c:if>

				submitForm(form);
			});

			var password = form.querySelector('[name=<portlet:namespace />password');

			password.addEventListener('keypress', (event) => {
				Liferay.Util.showCapsLock(
					event,
					'<portlet:namespace />passwordCapsLockSpan'
				);
			});
		</aui:script>
	</c:otherwise>
</c:choose>