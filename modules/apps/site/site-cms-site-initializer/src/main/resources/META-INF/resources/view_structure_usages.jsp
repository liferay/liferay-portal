<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewStructureUsagesDisplayContext viewStructureUsagesDisplayContext = (ViewStructureUsagesDisplayContext)request.getAttribute(ViewStructureUsagesDisplayContext.class.getName());
%>

<div class="cms-section">
	<frontend-data-set:headless-display
		apiURL="<%= viewStructureUsagesDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= viewStructureUsagesDisplayContext.getBulkActionDropdownItems() %>"
		fdsActionDropdownItems="<%= viewStructureUsagesDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.STRUCTURE_USAGES %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{StructureUsagesFDSPropsTransformer} from site-cms-site-initializer"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div>