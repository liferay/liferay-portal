<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewMissingRequiredFieldsSectionDisplayContext viewMissingRequiredFieldsSectionDisplayContext = (ViewMissingRequiredFieldsSectionDisplayContext)request.getAttribute(ViewMissingRequiredFieldsSectionDisplayContext.class.getName());
%>

<div class="cms-missing-required-fields-view position-relative">
	<div>
		<react:component
			module="{Breadcrumb} from site-cms-site-initializer"
			props="<%= viewMissingRequiredFieldsSectionDisplayContext.getBreadcrumbProps() %>"
		/>
	</div>

	<div class="cms-section custom-empty-state">
		<frontend-data-set:headless-display
			additionalProps="<%= viewMissingRequiredFieldsSectionDisplayContext.getAdditionalProps() %>"
			apiURL="<%= viewMissingRequiredFieldsSectionDisplayContext.getAPIURL() %>"
			emptyState="<%= viewMissingRequiredFieldsSectionDisplayContext.getEmptyState() %>"
			fdsActionDropdownItems="<%= viewMissingRequiredFieldsSectionDisplayContext.getFDSActionDropdownItems() %>"
			formName="fm"
			id="<%= CMSSiteInitializerFDSNames.MISSING_REQUIRED_FIELDS_SECTION %>"
			itemsPerPage="<%= 20 %>"
			propsTransformer="{MissingRequiredFieldsFDSPropsTransformer} from site-cms-site-initializer"
			uniformActionsDisplay="<%= true %>"
		/>
	</div>
</div>