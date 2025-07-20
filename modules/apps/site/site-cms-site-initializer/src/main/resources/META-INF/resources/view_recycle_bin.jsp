<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewRecycleBinSectionDisplayContext viewRecycleBinSectionDisplayContext = (ViewRecycleBinSectionDisplayContext)request.getAttribute(ViewRecycleBinSectionDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<frontend-data-set:headless-display
		apiURL="<%= viewRecycleBinSectionDisplayContext.getAPIURL() %>"
		emptyState="<%= viewRecycleBinSectionDisplayContext.getEmptyState() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.RECYCLE_BIN_SECTION %>"
		itemsPerPage="<%= 20 %>"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div> </div>