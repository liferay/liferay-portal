<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
EditClientExtensionEntryDisplayContext<CommerceCheckoutStepCET> editClientExtensionEntryDisplayContext = (EditClientExtensionEntryDisplayContext)renderRequest.getAttribute(ClientExtensionAdminWebKeys.EDIT_CLIENT_EXTENSION_ENTRY_DISPLAY_CONTEXT);

CommerceCheckoutStepCET commerceCheckoutStepCET = editClientExtensionEntryDisplayContext.getCET();
%>

<aui:input label="active" name="active" type="checkbox" value="<%= commerceCheckoutStepCET.getActive() %>" />

<aui:input label="checkout-step-label" name="checkoutStepLabel" required="<%= true %>" type="text" value="<%= commerceCheckoutStepCET.getCheckoutStepLabel() %>" />

<aui:input label="checkout-step-name" name="checkoutStepName" required="<%= true %>" type="text" value="<%= commerceCheckoutStepCET.getCheckoutStepName() %>" />

<aui:input label="checkout-step-order" name="checkoutStepOrder" required="<%= true %>" type="text" value="<%= commerceCheckoutStepCET.getCheckoutStepOrder() %>" />

<c:if test='<%= FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-69290") %>'>
	<aui:input label="continue-after-payment" name="continueAfterPayment" type="checkbox" value="<%= commerceCheckoutStepCET.getContinueAfterPayment() %>" />
</c:if>

<aui:input label="oauth2-application-external-reference-code" name="oAuth2ApplicationExternalReferenceCode" required="<%= true %>" type="text" value="<%= commerceCheckoutStepCET.getOAuth2ApplicationExternalReferenceCode() %>" />

<aui:input label="order" name="order" type="checkbox" value="<%= commerceCheckoutStepCET.getOrder() %>" />

<aui:input label="senna-diabled" name="sennaDisabled" type="checkbox" value="<%= commerceCheckoutStepCET.getSennaDisabled() %>" />

<aui:input label="show-controls" name="showControls" type="checkbox" value="<%= commerceCheckoutStepCET.getShowControls() %>" />

<aui:input label="visible" name="visible" type="checkbox" value="<%= commerceCheckoutStepCET.getVisible() %>" />