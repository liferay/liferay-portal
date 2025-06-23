<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewSpaceFilesAbstractSectionDisplayContext viewSpaceFilesAbstractSectionDisplayContext = (ViewSpaceFilesAbstractSectionDisplayContext)request.getAttribute(ViewSpaceFilesAbstractSectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<div id="<%= CMSSiteInitializerFDSNames.SPACE_FILES_ABSTRACT_SECTION %>">
		<react:component
			module="{SpaceAbstractHeader} from site-cms-site-initializer"
			props="<%= viewSpaceFilesAbstractSectionDisplayContext.getHeaderProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			apiURL="<%= viewSpaceFilesAbstractSectionDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewSpaceFilesAbstractSectionDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewSpaceFilesAbstractSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewSpaceFilesAbstractSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.SPACE_FILES_ABSTRACT_SECTION %>"
			propsTransformer="{FilesFDSPropsTransformer} from site-cms-site-initializer"
			showManagementBar="<%= false %>"
			showPagination="<%= false %>"
			showSearch="<%= false %>"
			showSelectAll="<%= false %>"
			style="fluid"
		/>
	</div>
</div>