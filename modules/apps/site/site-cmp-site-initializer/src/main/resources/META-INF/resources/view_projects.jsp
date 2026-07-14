<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewProjectsSectionDisplayContext viewProjectsSectionDisplayContext = (ViewProjectsSectionDisplayContext)request.getAttribute(ViewProjectsSectionDisplayContext.class.getName());
%>

<div class="cms-section custom-empty-state">
	<frontend-data-set:headless-display
		additionalProps="<%= viewProjectsSectionDisplayContext.getAdditionalProps() %>"
		apiURL="<%= viewProjectsSectionDisplayContext.getAPIURL() %>"
		creationMenu="<%= viewProjectsSectionDisplayContext.getCreationMenu() %>"
		emptyState="<%= viewProjectsSectionDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewProjectsSectionDisplayContext.getFDSActionDropdownItems() %>"
		fdsFilters="<%= viewProjectsSectionDisplayContext.getFDSFilters() %>"
		formName="fm"
		id="<%= CMPSiteInitializerFDSNames.CMP_PROJECT %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{ProjectsFDSPropsTransformer} from site-cmp-site-initializer"
		selectedItemsKey="embedded.id"
	/>
</div>