<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewSpaceContentsSectionDisplayContext viewSpaceContentsSectionDisplayContext = (ViewSpaceContentsSectionDisplayContext)request.getAttribute(ViewSpaceContentsSectionDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<frontend-data-set:headless-display
		apiURL="<%= viewSpaceContentsSectionDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= viewSpaceContentsSectionDisplayContext.getBulkActionDropdownItems() %>"
		creationMenu="<%= viewSpaceContentsSectionDisplayContext.getCreationMenu() %>"
		emptyState="<%= viewSpaceContentsSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewSpaceContentsSectionDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.SPACE_CONTENTS_SECTION %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{ContentsFDSPropsTransformer} from site-cms-site-initializer"
		selectedItemsKey="id"
		selectionType="multiple"
		style="fluid"
	/>
</div>