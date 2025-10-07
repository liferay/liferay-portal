<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
ViewVersionHistoryDisplayContext viewVersionHistoryDisplayContext = (ViewVersionHistoryDisplayContext)request.getAttribute(ViewVersionHistoryDisplayContext.class.getName());
%>

<div>
	<div>
		<react:component
			module="{Toolbar} from site-cms-site-initializer"
			props="<%= viewVersionHistoryDisplayContext.getProps() %>"
		/>
	</div>

	<frontend-data-set:headless-display
		additionalProps="<%= viewVersionHistoryDisplayContext.getProps() %>"
		apiURL="<%= viewVersionHistoryDisplayContext.getAPIURL() %>"
		fdsActionDropdownItems="<%= viewVersionHistoryDisplayContext.getFDSActionDropdownItems() %>"
		fdsSortItemList="<%= viewVersionHistoryDisplayContext.getFDSSortItemList() %>"
		formName="fm"
		id="<%= CMSSiteInitializerFDSNames.VIEW_HISTORY %>"
		itemsPerPage="<%= 20 %>"
		propsTransformer="{ViewVersionHistoryFDSPropsTransformer} from site-cms-site-initializer"
		style="fluid"
	/>
</div>