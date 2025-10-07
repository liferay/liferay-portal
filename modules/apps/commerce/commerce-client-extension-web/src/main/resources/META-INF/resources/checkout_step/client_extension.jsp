<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String clientId = (String)request.getAttribute("clientId");

Long orderId = (Long)request.getAttribute("orderId");

if (orderId == null) {
	orderId = 0L;
}

String renderURL = (String)request.getAttribute(CommerceClientExtensionWebKeys.RENDER_URL);
%>

<div class="form-group form-group-item" id="<portlet:namespace />commerceCheckoutStepContainer"></div>

<c:if test="<%= Validator.isNotNull(clientId) %>">
	<input id="payment-client-id" name="clientId" type="hidden" value="<%= clientId %>" />
</c:if>

<c:if test="<%= orderId > 0 %>">
	<input id="payment-order-id" name="orderId" type="hidden" value="<%= orderId %>" />
</c:if>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"renderURL", renderURL
		).build()
	%>'
	module="{main} from commerce-client-extension-web"
/>