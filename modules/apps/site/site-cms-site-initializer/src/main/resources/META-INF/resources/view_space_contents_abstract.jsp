<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewSpaceContentsAbstractSectionDisplayContext viewSpaceContentsAbstractSectionDisplayContext = (ViewSpaceContentsAbstractSectionDisplayContext)request.getAttribute(ViewSpaceContentsAbstractSectionDisplayContext.class.getName());
%>

<div class="cms-section">
	<div id="<%= CMSSiteInitializerFDSNames.SPACE_CONTENTS_ABSTRACT_SECTION %>">
		<react:component
			module="{SpaceAbstractHeader} from site-cms-site-initializer"
			props="<%= viewSpaceContentsAbstractSectionDisplayContext.getHeaderProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			apiURL="<%= viewSpaceContentsAbstractSectionDisplayContext.getAPIURL() %>"
			creationMenu="<%= viewSpaceContentsAbstractSectionDisplayContext.getCreationMenu() %>"
			emptyState="<%= viewSpaceContentsAbstractSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewSpaceContentsAbstractSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.SPACE_CONTENTS_ABSTRACT_SECTION %>"
			propsTransformer="{ContentsFDSPropsTransformer} from site-cms-site-initializer"
			showManagementBar="<%= false %>"
			showPagination="<%= false %>"
			showSearch="<%= false %>"
			showSelectAll="<%= false %>"
			style="fluid"
		/>
	</div>
</div>