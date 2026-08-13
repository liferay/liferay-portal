<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long passwordPolicyId = ParamUtil.getLong(request, "passwordPolicyId");

PasswordPolicy passwordPolicy = PasswordPolicyLocalServiceUtil.fetchPasswordPolicy(passwordPolicyId);

if (passwordPolicy == null) {
	passwordPolicy = new PasswordPolicyImpl();

	passwordPolicy.setNew(true);
}

boolean cryptoOfficerPasswordPolicy = FIPSUtil.isCryptoOfficerPasswordPolicy(passwordPolicy.getName());
boolean defaultPolicy = BeanParamUtil.getBoolean(passwordPolicy, request, "defaultPolicy");

PasswordPoliciesConfiguration passwordPoliciesConfiguration = (PasswordPoliciesConfiguration)request.getAttribute(PasswordPoliciesConfiguration.class.getName());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(String.valueOf(renderResponse.createRenderURL()));

renderResponse.setTitle(passwordPolicy.isNew() ? LanguageUtil.get(request, "new-password-policy") : passwordPolicy.getName());
%>

<liferay-util:include page="/edit_password_policy_tabs.jsp" servletContext="<%= application %>" />

<portlet:actionURL name="editPasswordPolicy" var="editPasswordPolicyURL" />

<aui:form action="<%= editPasswordPolicyURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= (passwordPolicyId > 0) ? currentURL : String.valueOf(renderResponse.createRenderURL()) %>" />
	<aui:input name="passwordPolicyId" type="hidden" value="<%= passwordPolicyId %>" />

	<liferay-ui:error exception="<%= DuplicatePasswordPolicyException.class %>" message="please-enter-a-unique-name" />
	<liferay-ui:error exception="<%= PasswordPolicyNameException.class %>" message="please-enter-a-valid-name" />

	<aui:model-context bean="<%= passwordPolicy %>" model="<%= PasswordPolicy.class %>" />

	<clay:sheet
		size="full"
	>
		<clay:sheet-section>
			<aui:input disabled="<%= cryptoOfficerPasswordPolicy || defaultPolicy %>" name="name" required="<%= true %>" />

			<aui:input name="description" />
		</clay:sheet-section>

		<clay:panel-group>
			<clay:panel
				displayTitle='<%= LanguageUtil.get(request, "password-changes") %>'
			>
				<div class="panel-body">
					<aui:input disabled="<%= cryptoOfficerPasswordPolicy %>" helpMessage="changeable-help" inlineLabel="right" labelCssClass="simple-toggle-switch" name="changeable" type="toggle-switch" value="<%= passwordPolicy.isChangeable() %>" />

					<div class="password-policy-options" id="<portlet:namespace />changeableSettings">
						<aui:input disabled="<%= cryptoOfficerPasswordPolicy %>" helpMessage="change-required-help" inlineLabel="right" labelCssClass="simple-toggle-switch" name="changeRequired" type="toggle-switch" value="<%= passwordPolicy.isChangeRequired() %>" />

						<aui:select helpMessage="minimum-age-help" label="minimum-age" name="minAge">
							<aui:option label="none" value="0" />

							<%
							for (long duration : _sort(passwordPoliciesConfiguration.minimumAgeDurations())) {
								if (duration == 0) {
									continue;
								}
							%>

								<aui:option label="<%= LanguageUtil.getTimeDescription(request, duration * 1000) %>" value="<%= duration %>" />

							<%
							}
							%>

						</aui:select>
					</div>

					<aui:select helpMessage="reset-ticket-max-age-help" name="resetTicketMaxAge">
						<aui:option label="eternal" value="0" />

						<%
						for (long duration : _sort(passwordPoliciesConfiguration.resetTicketMaxAgeDurations())) {
							if (duration == 0) {
								continue;
							}
						%>

							<aui:option label="<%= LanguageUtil.getTimeDescription(request, duration * 1000) %>" value="<%= duration %>" />

						<%
						}
						%>

					</aui:select>
				</div>
			</clay:panel>

			<clay:panel
				collapseClassNames="mt-3"
				displayTitle='<%= LanguageUtil.get(request, "password-syntax-checking") %>'
			>
				<div class="panel-body">
					<aui:input helpMessage="enable-syntax-checking-help" inlineLabel="right" label="enable-syntax-checking" labelCssClass="simple-toggle-switch" name="checkSyntax" type="toggle-switch" value="<%= passwordPolicy.isCheckSyntax() %>" />

					<div class="password-policy-options" id="<portlet:namespace />syntaxSettings">
						<aui:input helpMessage="allow-dictionary-words-help" inlineLabel="right" labelCssClass="simple-toggle-switch" name="allowDictionaryWords" type="toggle-switch" value="<%= passwordPolicy.isAllowDictionaryWords() %>" />

						<aui:input helpMessage="minimum-alpha-numeric-help" label="minimum-alpha-numeric" name="minAlphanumeric" />

						<aui:input helpMessage="minimum-length-help" label="minimum-length" name="minLength" />

						<aui:input helpMessage="minimum-lower-case-help" label="minimum-lower-case" name="minLowerCase" />

						<aui:input helpMessage="minimum-numbers-help" label="minimum-numbers" name="minNumbers" />

						<aui:input helpMessage="minimum-symbols-help" label="minimum-symbols" name="minSymbols" />

						<aui:input helpMessage="minimum-upper-case-help" label="minimum-upper-case" name="minUpperCase" />

						<%
						String taglibHelpMessage = LanguageUtil.format(request, "regular-expression-help", new Object[] {"<a href=\"http://docs.oracle.com/javase/tutorial/essential/regex\" target=\"_blank\">", "</a>"}, false);
						%>

						<aui:input helpMessage="<%= taglibHelpMessage %>" label="regular-expression" name="regex" />
					</div>
				</div>
			</clay:panel>

			<clay:panel
				collapseClassNames="mt-3"
				displayTitle='<%= LanguageUtil.get(request, "password-history") %>'
			>
				<div class="panel-body">
					<aui:input helpMessage="enable-history-help" inlineLabel="right" label="enable-history" labelCssClass="simple-toggle-switch" name="history" type="toggle-switch" value="<%= passwordPolicy.isHistory() %>" />

					<div class="password-policy-options" id="<portlet:namespace />historySettings">
						<aui:select helpMessage="history-count-help" name="historyCount">

							<%
							for (int i = 2; i < 25; i++) {
							%>

								<aui:option label="<%= i %>" />

							<%
							}
							%>

						</aui:select>
					</div>
				</div>
			</clay:panel>

			<clay:panel
				collapseClassNames="mt-3"
				displayTitle='<%= LanguageUtil.get(request, "password-expiration") %>'
			>
				<div class="panel-body">
					<aui:input helpMessage="enable-expiration-help" inlineLabel="right" label="enable-expiration" labelCssClass="simple-toggle-switch" name="expireable" type="toggle-switch" value="<%= passwordPolicy.isExpireable() %>" />

					<div class="password-policy-options" id="<portlet:namespace />expirationSettings">
						<aui:select helpMessage="maximum-age-help" label="maximum-age" name="maxAge">

							<%
							for (long duration : _sort(passwordPoliciesConfiguration.maximumAgeDurations())) {
							%>

								<aui:option label="<%= LanguageUtil.getTimeDescription(request, duration * 1000) %>" value="<%= duration %>" />

							<%
							}
							%>

						</aui:select>

						<aui:select helpMessage="warning-time-help" name="warningTime">

							<%
							for (long duration : _sort(passwordPoliciesConfiguration.expirationWarningTimeDurations())) {
							%>

								<aui:option label="<%= LanguageUtil.getTimeDescription(request, duration * 1000) %>" value="<%= duration %>" />

							<%
							}
							%>

							<aui:option label="do-not-warn" value="<%= 0 %>" />
						</aui:select>

						<aui:input helpMessage="grace-limit-help" name="graceLimit" />
					</div>
				</div>
			</clay:panel>

			<clay:panel
				collapseClassNames="mt-3"
				displayTitle='<%= LanguageUtil.get(request, "lockout") %>'
			>
				<div class="panel-body">
					<aui:input disabled="<%= cryptoOfficerPasswordPolicy %>" helpMessage="enable-lockout-help" inlineLabel="right" label="enable-lockout" labelCssClass="simple-toggle-switch" name="lockout" type="toggle-switch" value="<%= passwordPolicy.isLockout() %>" />

					<div class="password-policy-options" id="<portlet:namespace />lockoutSettings">
						<aui:input disabled="<%= cryptoOfficerPasswordPolicy %>" helpMessage="maximum-failure-help" label="maximum-failure" name="maxFailure" />

						<aui:select helpMessage="reset-failure-count-help" name="resetFailureCount">

							<%
							for (long duration : _sort(passwordPoliciesConfiguration.resetFailureDurations())) {
							%>

								<aui:option label="<%= LanguageUtil.getTimeDescription(request, duration * 1000) %>" value="<%= duration %>" />

							<%
							}
							%>

						</aui:select>

						<aui:select helpMessage="lockout-duration-help" name="lockoutDuration">
							<aui:option label="until-unlocked-by-an-administrator" value="0" />

							<%
							for (long duration : _sort(passwordPoliciesConfiguration.lockoutDurations())) {
								if (duration == 0) {
									continue;
								}
							%>

								<aui:option label="<%= LanguageUtil.getTimeDescription(request, duration * 1000) %>" value="<%= duration %>" />

							<%
							}
							%>

						</aui:select>
					</div>
				</div>
			</clay:panel>
		</clay:panel-group>

		<clay:sheet-footer>
			<aui:button type="submit" />

			<aui:button href="<%= String.valueOf(renderResponse.createRenderURL()) %>" type="cancel" />
		</clay:sheet-footer>
	</clay:sheet>
</aui:form>

<aui:script>
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />changeable',
		'<portlet:namespace />changeableSettings'
	);
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />checkSyntax',
		'<portlet:namespace />syntaxSettings'
	);
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />expireable',
		'<portlet:namespace />expirationSettings'
	);
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />history',
		'<portlet:namespace />historySettings'
	);
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />lockout',
		'<portlet:namespace />lockoutSettings'
	);
</aui:script>

<%
if (passwordPolicy != null) {
	PortalUtil.addPortletBreadcrumbEntry(request, passwordPolicy.getName(), null);
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "edit"), currentURL);
}
else {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "add-user"), currentURL);
}
%>

<%!
private static long[] _sort(long[] array) {
	Arrays.sort(array);

	return array;
}
%>

<%@ include file="/action/delete_password_policy.jspf" %>