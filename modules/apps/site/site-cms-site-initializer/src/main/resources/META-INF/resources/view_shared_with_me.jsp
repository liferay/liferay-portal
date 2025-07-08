<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewSharedWithMeSectionDisplayContext viewSharedWithMeSectionDisplayContext = (ViewSharedWithMeSectionDisplayContext)request.getAttribute(ViewSharedWithMeSectionDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<frontend-data-set:headless-display
		apiURL="<%= viewSharedWithMeSectionDisplayContext.getAPIURL() %>"
		emptyState="<%= viewSharedWithMeSectionDisplayContext.getEmptyState() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.SHARED_WITH_ME %>"
		itemsPerPage="<%= 20 %>"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div>