<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewStructuresDisplayContext viewStructuresDisplayContext = (ViewStructuresDisplayContext)request.getAttribute(ViewStructuresDisplayContext.class.getName());
%>

<div class="cms-section">
	<frontend-data-set:headless-display
		apiURL="<%= viewStructuresDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= viewStructuresDisplayContext.getBulkActionDropdownItems() %>"
		creationMenu="<%= viewStructuresDisplayContext.getCreationMenu() %>"
		fdsActionDropdownItems="<%= viewStructuresDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.STRUCTURES_SECTION %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{StructuresFDSPropsTransformer} from site-cms-site-initializer"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div>