<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/fragment/renderer/order_summary/init.jsp" %>

<c:choose>
	<c:when test="<%= commerceOrderId == 0 %>">
		<span class="order-summary-label"><%= HtmlUtil.escape(fieldLabel) %></span>
	</c:when>
	<c:otherwise>
		<c:if test="<%= !Validator.isBlank(label) %>">
			<span class="order-summary-label"><%= HtmlUtil.escape(label) %></span>
		</c:if>

		<span class="order-summary-value" data-summary-field-name="<%= HtmlUtil.escapeAttribute(field) %>"><%= HtmlUtil.escape(fieldValue) %></span>

		<c:if test="<%= open %>">
			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"orderId", commerceOrderId
					).build()
				%>'
				module="{OrderSummary} from commerce-order-content-web"
			/>
		</c:if>
	</c:otherwise>
</c:choose>