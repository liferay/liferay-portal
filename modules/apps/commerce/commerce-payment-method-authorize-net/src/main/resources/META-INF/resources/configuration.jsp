<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AuthorizeNetGroupServiceConfiguration authorizeNetGroupServiceConfiguration = (AuthorizeNetGroupServiceConfiguration)request.getAttribute(AuthorizeNetGroupServiceConfiguration.class.getName());
%>

<portlet:actionURL name="/commerce_payment_methods/edit_authorize_net_commerce_payment_method_configuration" var="editCommercePaymentMethodActionURL" />

<aui:form action="<%= editCommercePaymentMethodActionURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="commerceChannelId" type="hidden" value='<%= ParamUtil.getLong(request, "commerceChannelId") %>' />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<div class="alert alert-info">
		<%= LanguageUtil.format(request, "register-x-as-the-webhook-endpoint-url-in-your-authorize-net-account-and-subscribe-to-the-x-event", new Object[] {HtmlUtil.escape(themeDisplay.getPortalURL() + PortalUtil.getPathModule() + StringPool.SLASH + AuthorizeNetCommercePaymentMethodConstants.COMPLETE_PAYMENT_SERVLET_PATH), AuthorizeNetCommercePaymentMethodConstants.AUTH_CAPTURE_CREATED_EVENT_TYPE}, false) %>
	</div>

	<commerce-ui:panel>
		<commerce-ui:info-box
			title='<%= LanguageUtil.get(request, "authentication") %>'
		>
			<aui:input label="api-login-id" name="settings--apiLoginId--" value="<%= authorizeNetGroupServiceConfiguration.apiLoginId() %>" />

			<aui:input label="transaction-key" name="settings--transactionKey--" value="<%= authorizeNetGroupServiceConfiguration.transactionKey() %>" />

			<aui:input label="signature-key" name="settings--signatureKey--" value="<%= authorizeNetGroupServiceConfiguration.signatureKey() %>" />

			<aui:select name="settings--environment--">

				<%
				for (String environment : AuthorizeNetCommercePaymentMethodConstants.ENVIRONMENTS) {
				%>

					<aui:option label="<%= environment %>" selected="<%= environment.equals(authorizeNetGroupServiceConfiguration.environment()) %>" value="<%= environment %>" />

				<%
				}
				%>

			</aui:select>
		</commerce-ui:info-box>

		<commerce-ui:info-box
			title='<%= LanguageUtil.get(request, "display") %>'
		>
			<aui:input checked="<%= authorizeNetGroupServiceConfiguration.showBankAccount() %>" label="show-bank-account" name="settings--showBankAccount--" type="checkbox" />

			<aui:input checked="<%= authorizeNetGroupServiceConfiguration.showCreditCard() %>" label="show-credit-card" name="settings--showCreditCard--" type="checkbox" />

			<aui:input checked="<%= authorizeNetGroupServiceConfiguration.showStoreName() %>" label="show-store-name" name="settings--showStoreName--" type="checkbox" />
		</commerce-ui:info-box>

		<commerce-ui:info-box
			title='<%= LanguageUtil.get(request, "security") %>'
		>
			<aui:input checked="<%= authorizeNetGroupServiceConfiguration.requireCaptcha() %>" label="require-captcha" name="settings--requireCaptcha--" type="checkbox" />

			<aui:input checked="<%= authorizeNetGroupServiceConfiguration.requireCardCodeVerification() %>" label="require-card-code-verification" name="settings--requireCardCodeVerification--" type="checkbox" />
		</commerce-ui:info-box>
	</commerce-ui:panel>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />

		<aui:button cssClass="btn-lg" href="<%= redirect %>" type="cancel" />
	</aui:button-row>
</aui:form>