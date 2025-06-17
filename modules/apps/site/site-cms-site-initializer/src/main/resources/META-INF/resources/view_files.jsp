<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewFilesSectionDisplayContext viewFilesSectionDisplayContext = (ViewFilesSectionDisplayContext)request.getAttribute(ViewFilesSectionDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<frontend-data-set:headless-display
		apiURL="<%= viewFilesSectionDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= viewFilesSectionDisplayContext.getBulkActionDropdownItems() %>"
		creationMenu="<%= viewFilesSectionDisplayContext.getCreationMenu() %>"
		emptyState="<%= viewFilesSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewFilesSectionDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.FILES_SECTION %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{FilesFDSPropsTransformer} from site-cms-site-initializer"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div>