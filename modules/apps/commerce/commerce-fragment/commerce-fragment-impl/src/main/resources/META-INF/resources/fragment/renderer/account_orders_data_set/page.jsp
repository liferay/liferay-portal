<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/fragment/renderer/account_orders_data_set/init.jsp" %>

<%
PendingAccountOrdersDataSetDisplayContext pendingAccountOrdersDataSetDisplayContext = (PendingAccountOrdersDataSetDisplayContext)request.getAttribute(PendingAccountOrdersDataSetDisplayContext.class.getName());
%>

<frontend-data-set:headless-display
	additionalProps="<%= pendingAccountOrdersDataSetDisplayContext.getAdditionalProps() %>"
	apiURL="<%= pendingAccountOrdersDataSetDisplayContext.getAPIURL() %>"
	fdsActionDropdownItems="<%= pendingAccountOrdersDataSetDisplayContext.getFDSActionDropdownItems() %>"
	id="<%= CommerceFragmentFDSNames.PENDING_ACCOUNT_ORDERS %>"
	itemsPerPage='<%= (int)pendingAccountOrdersDataSetDisplayContext.getConfigurationValue("pageSize") %>'
	propsTransformer="{PendingAccountOrdersFDSPropsTransformer} from commerce-fragment-impl"
	showPagination='<%= (boolean)pendingAccountOrdersDataSetDisplayContext.getConfigurationValue("showPagination") %>'
	showSearch='<%= (boolean)pendingAccountOrdersDataSetDisplayContext.getConfigurationValue("showSearch") %>'
	style='<%= (String)pendingAccountOrdersDataSetDisplayContext.getConfigurationValue("displayStyle") %>'
/>

<c:if test='<%= (boolean)pendingAccountOrdersDataSetDisplayContext.getConfigurationValue("hideActionsColumn") %>'>
	<aui:style type="text/css">
		.lfr-layout-structure-item-com-liferay-commerce-fragment-internal-renderer-accountsdatasetfragmentrenderer table th:last-of-type,
		.lfr-layout-structure-item-com-liferay-commerce-fragment-internal-renderer-accountsdatasetfragmentrenderer table td:last-of-type {
			display: none;
		}
	</aui:style>
</c:if>