<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/fragment/renderer/accounts_data_set/init.jsp" %>

<%
AccountsDataSetDisplayContext accountsDataSetDisplayContext = (AccountsDataSetDisplayContext)request.getAttribute(AccountsDataSetDisplayContext.class.getName());
%>

<frontend-data-set:headless-display
	additionalProps="<%= accountsDataSetDisplayContext.getAdditionalProps() %>"
	apiURL="<%= accountsDataSetDisplayContext.getAPIURL() %>"
	fdsActionDropdownItems="<%= accountsDataSetDisplayContext.getFDSActionDropdownItems() %>"
	id="<%= CommerceFragmentFDSNames.ACCOUNT_ENTRIES %>"
	itemsPerPage='<%= (int)accountsDataSetDisplayContext.getConfigurationValue("pageSize") %>'
	propsTransformer="{AccountsFDSPropsTransformer} from commerce-fragment-impl"
	showPagination='<%= (boolean)accountsDataSetDisplayContext.getConfigurationValue("showPagination") %>'
	showSearch='<%= (boolean)accountsDataSetDisplayContext.getConfigurationValue("showSearch") %>'
	style='<%= (String)accountsDataSetDisplayContext.getConfigurationValue("displayStyle") %>'
/>

<c:if test='<%= (boolean)accountsDataSetDisplayContext.getConfigurationValue("hideActionsColumn") %>'>
	<aui:style type="text/css">
		.lfr-layout-structure-item-com-liferay-commerce-fragment-internal-renderer-accountsdatasetfragmentrenderer table th:last-of-type,
		.lfr-layout-structure-item-com-liferay-commerce-fragment-internal-renderer-accountsdatasetfragmentrenderer table td:last-of-type {
			display: none;
		}
	</aui:style>
</c:if>