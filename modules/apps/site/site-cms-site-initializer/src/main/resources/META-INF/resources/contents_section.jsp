<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ContentsSectionDisplayContext contentsSectionDisplayContext = (ContentsSectionDisplayContext)request.getAttribute(ContentsSectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<frontend-data-set:headless-display
		apiURL="<%= contentsSectionDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= contentsSectionDisplayContext.getBulkActionDropdownItems() %>"
		creationMenu="<%= contentsSectionDisplayContext.getCreationMenu() %>"
		emptyState="<%= contentsSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= contentsSectionDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.CONTENTS_SECTION %>"
		itemsPerPage="<%= 10 %>"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div>