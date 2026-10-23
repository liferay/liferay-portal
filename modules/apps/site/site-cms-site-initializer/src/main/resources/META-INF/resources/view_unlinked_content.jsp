<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewUnlinkedContentSectionDisplayContext viewUnlinkedContentSectionDisplayContext = (ViewUnlinkedContentSectionDisplayContext)request.getAttribute(ViewUnlinkedContentSectionDisplayContext.class.getName());
%>

<div class="cms-unlinked-content-view position-relative">
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewUnlinkedContentSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewUnlinkedContentSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewUnlinkedContentSectionDisplayContext.getAPIURL() %>"
			emptyState="<%= viewUnlinkedContentSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewUnlinkedContentSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.UNLINKED_CONTENT_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{UnlinkedContentFDSPropsTransformer} from site-cms-site-initializer"
			uniformActionsDisplay="<%= true %>"
		/>
	</div>
</div>