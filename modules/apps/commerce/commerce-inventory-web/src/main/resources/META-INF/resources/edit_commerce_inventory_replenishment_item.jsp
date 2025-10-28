<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceInventoryDisplayContext commerceInventoryDisplayContext = (CommerceInventoryDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceInventoryReplenishmentItem commerceInventoryReplenishmentItem = commerceInventoryDisplayContext.getCommerceInventoryReplenishmentItem();
%>

<liferay-ui:error exception="<%= CommerceInventoryReplenishmentQuantityException.class %>" message="please-enter-a-valid-quantity" />
<liferay-ui:error exception="<%= MVCCException.class %>" message="this-item-is-no-longer-valid-please-try-again" />

<portlet:actionURL name="/commerce_inventory/edit_commerce_inventory_replenishment_item" var="editCommerceInventoryReplenishmentItemActionURL" />

<c:choose>
	<c:when test="<%= commerceInventoryReplenishmentItem == null %>">
		<commerce-ui:modal-content
			title='<%= LanguageUtil.get(request, "add-incoming") %>'
		>
			<aui:form action="<%= editCommerceInventoryReplenishmentItemActionURL %>" method="post" name="fm">
				<%@ include file="/edit_commerce_inventory_replenishment_item.jspf" %>
			</aui:form>
		</commerce-ui:modal-content>
	</c:when>
	<c:otherwise>
		<liferay-frontend:side-panel-content
			title='<%= LanguageUtil.get(request, "edit-incoming-quantity") %>'
		>
			<commerce-ui:panel
				title='<%= LanguageUtil.get(request, "details") %>'
			>
				<aui:form action="<%= editCommerceInventoryReplenishmentItemActionURL %>" method="post" name="fm">
					<%@ include file="/edit_commerce_inventory_replenishment_item.jspf" %>

					<aui:button-row>
						<aui:button cssClass="btn-lg" type="submit" value="save" />

						<aui:button cssClass="btn-lg" type="cancel" />
					</aui:button-row>
				</aui:form>
			</commerce-ui:panel>
		</liferay-frontend:side-panel-content>
	</c:otherwise>
</c:choose>