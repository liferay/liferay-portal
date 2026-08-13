<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewDesignLibraryAdminDisplayContext viewDesignLibraryAdminDisplayContext = new ViewDesignLibraryAdminDisplayContext(request, liferayPortletResponse);
%>

<div class="design-library-fds-wrapper design-library-fds-wrapper--landing">
	<frontend-data-set:headless-display
		additionalProps="<%= viewDesignLibraryAdminDisplayContext.getFDSAdditionalProps() %>"
		apiURL="<%= viewDesignLibraryAdminDisplayContext.getAPIURL() %>"
		bulkActionDropdownItems="<%= viewDesignLibraryAdminDisplayContext.getBulkActionDropdownItems() %>"
		emptyState="<%= viewDesignLibraryAdminDisplayContext.getEmptyState() %>"
		fdsActionDropdownItems="<%= viewDesignLibraryAdminDisplayContext.getFDSActionDropdownItems() %>"
		formName="fm"
		id="<%= DesignLibraryAdminFDSNames.DESIGN_LIBRARIES %>"
		propsTransformer="{DesignLibraryAdminFDSPropsTransformer} from design-library-web"
		selectedItemsKey="id"
		selectionType="multiple"
	/>
</div>