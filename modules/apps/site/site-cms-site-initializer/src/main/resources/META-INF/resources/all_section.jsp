<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AllSectionDisplayContext allSectionDisplayContext = (AllSectionDisplayContext)request.getAttribute(AllSectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<frontend-data-set:headless-display
		apiURL="<%= allSectionDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= allSectionDisplayContext.getBulkActionDropdownItems() %>"
		creationMenu="<%= allSectionDisplayContext.getCreationMenu() %>"
		emptyState="<%= allSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= allSectionDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.ALL_SECTION %>"
		itemsPerPage="<%= 10 %>"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div>