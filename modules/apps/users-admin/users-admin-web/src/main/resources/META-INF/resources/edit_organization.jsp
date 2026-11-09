<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String backURL = ParamUtil.getString(request, "backURL", redirect);

long organizationId = ParamUtil.getLong(request, "organizationId");

Organization organization = OrganizationServiceUtil.fetchOrganization(organizationId);

String type = BeanParamUtil.getString(organization, request, "type");

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	liferayPortletResponse
).setMVCRenderCommandName(
	"/users_admin/edit_organization"
).buildPortletURL();

if (Validator.isNotNull(redirect)) {
	portletURL.setParameter("redirect", redirect);
}

if (Validator.isNull(backURL)) {
	backURL = PortletURLBuilder.createRenderURL(
		liferayPortletResponse
	).setParameter(
		"screenNavigationCategoryKey", UserScreenNavigationEntryConstants.CATEGORY_KEY_ORGANIZATIONS
	).setParameter(
		"usersListView", UserConstants.LIST_VIEW_FLAT_ORGANIZATIONS
	).buildString();
}

portletURL.setParameter("backURL", backURL);

if (organization != null) {
	portletURL.setParameter("organizationId", String.valueOf(organizationId));
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(backURL);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

String headerTitle = null;

if (organization != null) {
	headerTitle = LanguageUtil.format(request, "edit-x", organization.getName(), false);
}
else if (Validator.isNotNull(type)) {
	headerTitle = LanguageUtil.format(request, "add-x", type);
}
else {
	headerTitle = LanguageUtil.get(request, "add-organization");
}

renderResponse.setTitle(headerTitle);
%>

<liferay-frontend:screen-navigation
	containerCssClass="col-lg-8"
	containerWrapperCssClass="container-fluid container-fluid-max-xl container-form-lg"
	context="<%= organization %>"
	key="<%= UserScreenNavigationEntryConstants.SCREEN_NAVIGATION_KEY_ORGANIZATIONS %>"
	menubarCssClass="menubar menubar-transparent menubar-vertical-expand-lg"
	navCssClass="col-lg-3"
	portletURL="<%= portletURL %>"
/>