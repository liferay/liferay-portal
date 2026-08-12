<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/revamp/init.jsp" %>

<liferay-staging:defineObjects />

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNotNull(redirect)) {
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(redirect);
}

if (liveGroup == null) {
	liveGroup = group;
	liveGroupId = groupId;
}

ExportImportProcessesDisplayContext exportImportProcessesDisplayContext = new ExportImportProcessesDisplayContext(liveGroup, liveGroupId, request, liferayPortletResponse, privateLayout);

String title = exportImportProcessesDisplayContext.getImportTitle();

if (title != null) {
	renderResponse.setTitle(title);
}
%>

<frontend-data-set:headless-display
	apiURL="<%= exportImportProcessesDisplayContext.getImportProcessesAPIURL() %>"
	creationMenu="<%= exportImportProcessesDisplayContext.getImportCreationMenu() %>"
	fdsActionDropdownItems="<%= exportImportProcessesDisplayContext.getImportFDSActionDropdownItems() %>"
	id="<%= exportImportProcessesDisplayContext.getImportFDSName() %>"
	propsTransformer="{ImportProcessesFDSPropsTransformer} from exportimport-web"
	style="fluid"
	uniformActionsDisplay="<%= true %>"
/>