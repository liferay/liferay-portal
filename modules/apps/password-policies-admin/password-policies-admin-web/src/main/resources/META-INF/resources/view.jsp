<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String displayStyle = ParamUtil.getString(request, "displayStyle");

if (Validator.isNull(displayStyle)) {
	displayStyle = portalPreferences.getValue(PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN, "display-style", "list");
}
else {
	portalPreferences.setValue(PasswordPoliciesAdminPortletKeys.PASSWORD_POLICIES_ADMIN, "display-style", displayStyle);

	request.setAttribute(WebKeys.SINGLE_PAGE_APPLICATION_CLEAR_CACHE, Boolean.TRUE);
}

String description = LanguageUtil.get(request, "jakarta.portlet.description.com_liferay_password_policies_admin_web_portlet_PasswordPoliciesAdminPortlet") + " " + LanguageUtil.get(request, "when-no-password-policy-is-assigned-to-a-user,-either-explicitly-or-through-an-organization,-the-default-password-policy-is-used");

portletDisplay.setDescription(description);

ViewPasswordPoliciesManagementToolbarDisplayContext viewPasswordPoliciesManagementToolbarDisplayContext = new ViewPasswordPoliciesManagementToolbarDisplayContext(request, renderRequest, renderResponse, displayStyle);

SearchContainer<PasswordPolicy> searchContainer = viewPasswordPoliciesManagementToolbarDisplayContext.getSearchContainer();

PortletURL portletURL = viewPasswordPoliciesManagementToolbarDisplayContext.getPortletURL();
%>

<clay:management-toolbar
	actionDropdownItems="<%= viewPasswordPoliciesManagementToolbarDisplayContext.getActionDropdownItems() %>"
	additionalProps='<%=
		HashMapBuilder.<String, Object>put(
			"basePortletURL", portletURL.toString()
		).build()
	%>'
	clearResultsURL="<%= viewPasswordPoliciesManagementToolbarDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= viewPasswordPoliciesManagementToolbarDisplayContext.getCreationMenu() %>"
	itemsTotal="<%= searchContainer.getTotal() %>"
	propsTransformer="{ManagementToolbarDefaultPropsTransformer} from password-policies-admin-web"
	searchActionURL="<%= viewPasswordPoliciesManagementToolbarDisplayContext.getSearchActionURL() %>"
	searchContainerId="passwordPolicies"
	searchFormName="searchFm"
	selectable="<%= true %>"
	showCreationMenu="<%= viewPasswordPoliciesManagementToolbarDisplayContext.showCreationMenu() %>"
	showSearch="<%= true %>"
	sortingOrder="<%= searchContainer.getOrderByType() %>"
	sortingURL="<%= viewPasswordPoliciesManagementToolbarDisplayContext.getSortingURL() %>"
	viewTypeItems="<%= viewPasswordPoliciesManagementToolbarDisplayContext.getViewTypeItems() %>"
/>

<aui:form action="<%= portletURL %>" cssClass="container-fluid container-fluid-max-xl" method="get" name="fm">
	<aui:input name="passwordPolicyIds" type="hidden" />
	<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />

	<div id="breadcrumb">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, true, true) %>"
		/>
	</div>

	<c:if test="<%= windowState.equals(WindowState.MAXIMIZED) %>">
		<liferay-ui:search-container
			id="passwordPolicies"
			searchContainer="<%= searchContainer %>"
			var="passwordPolicySearchContainer"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.PasswordPolicy"
				escapedModel="<%= true %>"
				keyProperty="passwordPolicyId"
				modelVar="passwordPolicy"
			>

				<%
				String rowHREF = null;

				if (passwordPolicyDisplayContext.hasPermission(ActionKeys.UPDATE, passwordPolicy.getPasswordPolicyId())) {
					PortletURL redirectURL = passwordPolicySearchContainer.getIteratorURL();

					rowHREF = PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCPath(
						"/edit_password_policy.jsp"
					).setRedirect(
						redirectURL
					).setParameter(
						"passwordPolicyId", passwordPolicy.getPasswordPolicyId()
					).buildString();
				}
				%>

				<%@ include file="/search_columns.jspf" %>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= displayStyle %>"
				markupView="lexicon"
				searchContainer="<%= passwordPolicySearchContainer %>"
			/>
		</liferay-ui:search-container>
	</c:if>
</aui:form>