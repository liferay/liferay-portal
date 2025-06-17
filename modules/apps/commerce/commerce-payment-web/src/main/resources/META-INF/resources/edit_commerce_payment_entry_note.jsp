<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommercePaymentEntryDisplayContext commercePaymentEntryDisplayContext = (CommercePaymentEntryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommercePaymentEntry commercePaymentEntry = commercePaymentEntryDisplayContext.getCommercePaymentEntry();
%>

<portlet:actionURL name="/commerce_payment/edit_commerce_payment_entry" var="editCommercePaymentEntryNoteActionURL" />

<aui:form action="<%= editCommercePaymentEntryNoteActionURL %>" cssClass="container-fluid container-fluid-max-xl p-4" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="updateNote" />
	<aui:input name="commercePaymentEntryId" type="hidden" value="<%= commercePaymentEntry.getCommercePaymentEntryId() %>" />

	<aui:model-context bean="<%= commercePaymentEntry %>" model="<%= CommercePaymentEntry.class %>" />

	<aui:input name="note" wrapperCssClass="form-group-item" />
</aui:form>