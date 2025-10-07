<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
HttpServletRequest originalHttpServletRequest = PortalUtil.getOriginalServletRequest(PortalUtil.getHttpServletRequest(renderRequest));

String referer = ParamUtil.getString(request, WebKeys.REFERER, currentURL);
Ticket ticket = (Ticket)request.getAttribute(WebKeys.TICKET);
String ticketId = ParamUtil.getString(originalHttpServletRequest, "ticketId");
String ticketKey = ParamUtil.getString(originalHttpServletRequest, "ticketKey");

if (referer.startsWith(themeDisplay.getPathMain() + "/portal/update_password") && Validator.isNotNull(ticketKey)) {
	referer = themeDisplay.getPathMain();
}

String titlePage = (String)request.getAttribute(WebKeys.TITLE_SET_PASSWORD);
boolean showCancelButton = false;

if (Validator.isNull(titlePage)) {
	titlePage = "change-password";
	showCancelButton = true;
}
%>

<div class="sheet-header">
	<div class="autofit-padded-no-gutters-x autofit-row">
		<div class="autofit-col autofit-col-expand">
			<h2 class="sheet-title">
				<liferay-ui:message key="<%= titlePage %>" />
			</h2>
		</div>
	</div>
</div>

<div class="sheet-text">
	<c:choose>
		<c:when test="<%= !themeDisplay.isSignedIn() && (ticket == null) %>">
			<div class="alert alert-warning">
				<c:choose>
					<c:when test="<%= (ticket == null) && (ticketKey != null) && Validator.isNull(ticketId) %>">
						<liferay-ui:message key="this-link-format-is-no-longer-recognized-please-request-a-new-link" />
					</c:when>
					<c:otherwise>
						<liferay-ui:message key="your-password-reset-link-is-no-longer-valid" />
					</c:otherwise>
				</c:choose>
			</div>
		</c:when>
		<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserLockoutException.LDAPLockout.class.getName()) %>">
			<div class="alert alert-danger">
				<liferay-ui:message key="this-account-is-locked" />
			</div>
		</c:when>
		<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserLockoutException.PasswordPolicyLockout.class.getName()) %>">
			<div class="alert alert-danger">

				<%
				UserLockoutException.PasswordPolicyLockout ule = (UserLockoutException.PasswordPolicyLockout)SessionErrors.get(request, UserLockoutException.PasswordPolicyLockout.class.getName());

				Format dateFormat = FastDateFormatFactoryUtil.getDateTime(FastDateFormatConstants.SHORT, FastDateFormatConstants.LONG, locale, TimeZone.getTimeZone(ule.user.getTimeZoneId()));
				%>

				<liferay-ui:message arguments="<%= dateFormat.format(ule.user.getUnlockDate()) %>" key="this-account-is-locked-until-x" translateArguments="<%= false %>" />
			</div>
		</c:when>
		<c:otherwise>
			<portlet:actionURL name="/login/update_password" var="updatePasswordURL">
				<portlet:param name="mvcRenderCommandName" value="/login/update_password" />
			</portlet:actionURL>

			<aui:form action="<%= updatePasswordURL %>" method="post" name="fm">
				<aui:input name="p_l_id" type="hidden" value="<%= layout.getPlid() %>" />
				<aui:input name="p_auth" type="hidden" value="<%= AuthTokenUtil.getToken(request) %>" />
				<aui:input name="doAsUserId" type="hidden" value="<%= themeDisplay.getDoAsUserId() %>" />
				<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
				<aui:input name="<%= WebKeys.REFERER %>" type="hidden" value="<%= referer %>" />
				<aui:input name="ticketId" type="hidden" value="<%= ticketId %>" />
				<aui:input name="ticketKey" type="hidden" value="<%= ticketKey %>" />

				<c:if test="<%= !MultiSessionErrors.isEmpty(liferayPortletRequest) %>">
					<div class="alert alert-danger">
						<c:choose>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustBeLonger.class.getName()) %>">

								<%
								UserPasswordException.MustBeLonger upe = (UserPasswordException.MustBeLonger)MultiSessionErrors.get(liferayPortletRequest, UserPasswordException.MustBeLonger.class.getName());
								%>

								<liferay-ui:message arguments="<%= String.valueOf(upe.minLength) %>" key="that-password-is-too-short" translateArguments="<%= false %>" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustComplyWithModelListeners.class.getName()) %>">
								<liferay-ui:message key="that-password-is-invalid-please-enter-a-different-password" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustComplyWithRegex.class.getName()) %>">

								<%
								UserPasswordException.MustComplyWithRegex upe = (UserPasswordException.MustComplyWithRegex)MultiSessionErrors.get(liferayPortletRequest, UserPasswordException.MustComplyWithRegex.class.getName());
								%>

								<liferay-ui:message arguments="<%= upe.regex %>" key="that-password-does-not-comply-with-the-regular-expression" translateArguments="<%= false %>" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustHaveMoreAlphanumeric.class.getName()) %>">

								<%
								UserPasswordException.MustHaveMoreAlphanumeric upe = (UserPasswordException.MustHaveMoreAlphanumeric)MultiSessionErrors.get(liferayPortletRequest, UserPasswordException.MustHaveMoreAlphanumeric.class.getName());
								%>

								<liferay-ui:message arguments="<%= String.valueOf(upe.minAlphanumeric) %>" key="that-password-must-contain-at-least-x-alphanumeric-characters" translateArguments="<%= false %>" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustHaveMoreLowercase.class.getName()) %>">

								<%
								UserPasswordException.MustHaveMoreLowercase upe = (UserPasswordException.MustHaveMoreLowercase)MultiSessionErrors.get(liferayPortletRequest, UserPasswordException.MustHaveMoreLowercase.class.getName());
								%>

								<liferay-ui:message arguments="<%= String.valueOf(upe.minLowercase) %>" key="that-password-must-contain-at-least-x-lowercase-characters" translateArguments="<%= false %>" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustHaveMoreNumbers.class.getName()) %>">

								<%
								UserPasswordException.MustHaveMoreNumbers upe = (UserPasswordException.MustHaveMoreNumbers)MultiSessionErrors.get(liferayPortletRequest, UserPasswordException.MustHaveMoreNumbers.class.getName());
								%>

								<liferay-ui:message arguments="<%= String.valueOf(upe.minNumbers) %>" key="that-password-must-contain-at-least-x-numbers" translateArguments="<%= false %>" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustHaveMoreSymbols.class.getName()) %>">

								<%
								UserPasswordException.MustHaveMoreSymbols upe = (UserPasswordException.MustHaveMoreSymbols)MultiSessionErrors.get(liferayPortletRequest, UserPasswordException.MustHaveMoreSymbols.class.getName());
								%>

								<liferay-ui:message arguments="<%= String.valueOf(upe.minSymbols) %>" key="that-password-must-contain-at-least-x-symbols" translateArguments="<%= false %>" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustHaveMoreUppercase.class.getName()) %>">

								<%
								UserPasswordException.MustHaveMoreUppercase upe = (UserPasswordException.MustHaveMoreUppercase)MultiSessionErrors.get(liferayPortletRequest, UserPasswordException.MustHaveMoreUppercase.class.getName());
								%>

								<liferay-ui:message arguments="<%= String.valueOf(upe.minUppercase) %>" key="that-password-must-contain-at-least-x-uppercase-characters" translateArguments="<%= false %>" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustMatch.class.getName()) %>">
								<liferay-ui:message key="the-passwords-you-entered-do-not-match" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustNotBeEqualToCurrent.class.getName()) %>">
								<liferay-ui:message key="your-new-password-cannot-be-the-same-as-your-old-password-please-enter-a-different-password" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustNotBeNull.class.getName()) %>">
								<liferay-ui:message key="the-password-cannot-be-blank" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustNotBeRecentlyUsed.class.getName()) %>">
								<liferay-ui:message key="that-password-has-already-been-used-please-enter-a-different-password" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustNotBeTrivial.class.getName()) %>">
								<liferay-ui:message key="that-password-uses-common-words-please-enter-a-password-that-is-harder-to-guess-i-e-contains-a-mix-of-numbers-and-letters" />
							</c:when>
							<c:when test="<%= MultiSessionErrors.contains(liferayPortletRequest, UserPasswordException.MustNotContainDictionaryWords.class.getName()) %>">
								<liferay-ui:message key="that-password-uses-common-dictionary-words" />
							</c:when>
							<c:otherwise>
								<liferay-ui:message key="your-request-failed-to-complete" />
							</c:otherwise>
						</c:choose>
					</div>
				</c:if>

				<aui:fieldset>
					<aui:input class="lfr-input-text-container" label="password" name="password1" required="<%= true %>" showRequiredLabel="<%= false %>" type="password" />

					<aui:input class="lfr-input-text-container" label="reenter-password" name="password2" required="<%= true %>" showRequiredLabel="<%= false %>" type="password">
						<aui:validator name="equalTo">
							'#<portlet:namespace />password1'
						</aui:validator>
					</aui:input>
				</aui:fieldset>

				<aui:button-row>
					<aui:button type="submit" />

					<c:if test="<%= showCancelButton %>">
						<aui:button href='<%= themeDisplay.getPathMain() + "/portal/logout" %>' type="cancel" />
					</c:if>
				</aui:button-row>
			</aui:form>
		</c:otherwise>
	</c:choose>
</div>