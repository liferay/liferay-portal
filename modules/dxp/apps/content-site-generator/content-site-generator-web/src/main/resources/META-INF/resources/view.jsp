<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewGenerationsDisplayContext viewGenerationsDisplayContext = (ViewGenerationsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<frontend-data-set:headless-display
	apiURL="<%= viewGenerationsDisplayContext.getAPIURL() %>"
	bulkActionDropdownItems="<%= viewGenerationsDisplayContext.getBulkActionDropdownItems() %>"
	creationMenu="<%= viewGenerationsDisplayContext.getCreationMenu() %>"
	emptyState="<%= viewGenerationsDisplayContext.getEmptyState() %>"
	fdsActionDropdownItems="<%= viewGenerationsDisplayContext.getFDSActionDropdownItems() %>"
	id="<%= ContentSiteGeneratorFDSNames.GENERATIONS %>"
	itemsPerPage="<%= 20 %>"
	selectedItemsKey="id"
	selectionType="multiple"
	style="fluid"
/>