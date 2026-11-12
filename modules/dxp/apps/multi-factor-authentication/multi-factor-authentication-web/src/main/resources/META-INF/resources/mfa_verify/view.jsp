<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String state = ParamUtil.getString(request, "state");
int mfaCheckerIndex = ParamUtil.getInteger(request, "mfaCheckerIndex");

BrowserMFAChecker browserMFAChecker = (BrowserMFAChecker)request.getAttribute(MFAWebKeys.BROWSER_MFA_CHECKER);
String browserMFACheckerName = (String)request.getAttribute(MFAWebKeys.BROWSER_MFA_CHECKER_NAME);
List<BrowserMFAChecker> browserMFACheckers = (List<BrowserMFAChecker>)request.getAttribute(MFAWebKeys.BROWSER_MFA_CHECKERS);
long mfaUserId = (Long)request.getAttribute(MFAWebKeys.MFA_USER_ID);
%>

<liferay-portlet:renderURL portletName="<%= LoginPortletKeys.LOGIN %>" var="loginURL">
	<portlet:param name="redirect" value="<%= redirect %>" />
</liferay-portlet:renderURL>

<%
portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", loginURL));
%>

<portlet:actionURL name="/mfa_verify/verify" var="verifyURL">
	<portlet:param name="mvcRenderCommandName" value="/mfa_verify/view" />
</portlet:actionURL>

<c:choose>
	<c:when test="<%= browserMFACheckers.isEmpty() %>">
		<div class="container-fluid container-fluid-max-xl sign-in-form">
			<liferay-ui:error key="noAvailableBrowserMFAChecker" message="you-have-no-available-multi-factor-authentication-methods-please-contact-your-administrator" />
		</div>
	</c:when>
	<c:otherwise>
		<aui:form action="<%= verifyURL %>" cssClass="container-fluid container-fluid-max-xl sign-in-form" data-senna-off="true" method="post" name="fm">
			<aui:input name="saveLastPath" type="hidden" value="<%= false %>" />
			<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
			<aui:input name="state" type="hidden" value="<%= state %>" />
			<aui:input name="mfaCheckerIndex" type="hidden" value="<%= mfaCheckerIndex %>" />

			<liferay-ui:error key="mfaVerificationFailed" message="multi-factor-authentication-has-failed" />

			<%
			browserMFAChecker.includeBrowserVerification(request, PipingServletResponseFactory.createPipingServletResponse(pageContext), mfaUserId);
			%>

			<c:if test="<%= browserMFACheckers.size() > 1 %>">
				<portlet:renderURL copyCurrentRenderParameters="<%= false %>" var="useAnotherBrowserMFAChecker">
					<portlet:param name="saveLastPath" value="<%= Boolean.FALSE.toString() %>" />
					<portlet:param name="mvcRenderCommandName" value="/mfa_verify/view" />
					<portlet:param name="redirect" value="<%= redirect %>" />
					<portlet:param name="state" value="<%= state %>" />
					<portlet:param name="mfaCheckerIndex" value='<%= ((mfaCheckerIndex + 1) < browserMFACheckers.size()) ? String.valueOf(mfaCheckerIndex + 1) : "0" %>' />
				</portlet:renderURL>

				<b><a href="<%= HtmlUtil.escapeAttribute(useAnotherBrowserMFAChecker) %>"><%= LanguageUtil.format(request, "use-another-mfa-checker", browserMFACheckerName, false) %></a></b>
			</c:if>
		</aui:form>
	</c:otherwise>
</c:choose>