<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
MoneyOrderGroupServiceConfiguration moneyOrderGroupServiceConfiguration = (MoneyOrderGroupServiceConfiguration)request.getAttribute(MoneyOrderGroupServiceConfiguration.class.getName());

String messageAsLocalizedXML = moneyOrderGroupServiceConfiguration.messageAsLocalizedXML();
%>

<portlet:actionURL name="/commerce_payment_methods/edit_money_order_commerce_payment_method_configuration" var="editCommercePaymentMethodActionURL" />

<aui:form action="<%= editCommercePaymentMethodActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="commerceChannelId" type="hidden" value='<%= ParamUtil.getLong(request, "commerceChannelId") %>' />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<commerce-ui:panel>
		<aui:input helpMessage="this-toggles-whether-the-money-order-message-page-is-shown-as-a-checkout-step-or-not" label="show-message-page" labelOff="no" labelOn="yes" name="settings--showMessagePage--" type="toggle-switch" value="<%= moneyOrderGroupServiceConfiguration.showMessagePage() %>" />

		<div id="<portlet:namespace />message">
			<aui:field-wrapper label="message">
				<c:choose>
					<c:when test='<%= !FeatureFlagManagerUtil.isEnabled("LPD-11235") %>'>
						<liferay-ui:input-localized
							fieldPrefix="settings"
							fieldPrefixSeparator="--"
							name="messageAsLocalizedXML"
							type="editor"
							xml="<%= messageAsLocalizedXML %>"
						/>
					</c:when>
					<c:otherwise>
						<liferay-editor:input-localized
							defaultLanguageId="<%= themeDisplay.getLanguageId() %>"
							name="messageAsLocalizedXML"
							xml="<%= messageAsLocalizedXML %>"
						/>
					</c:otherwise>
				</c:choose>
			</aui:field-wrapper>
		</div>
	</commerce-ui:panel>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>

<aui:script>
	Liferay.Util.toggleBoxes(
		'<portlet:namespace />showMessagePage',
		'<portlet:namespace />message'
	);
</aui:script>