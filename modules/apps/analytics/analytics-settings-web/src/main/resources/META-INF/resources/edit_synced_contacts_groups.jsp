<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/configuration_admin/view_configuration_screen"
).setParameter(
	"configurationScreenKey", "2-synced-contact-data"
).buildPortletURL();

String redirect = ParamUtil.getString(request, "redirect", portletURL.toString());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", redirect));

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "select-contact-data"), portletURL.toString());
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "select-contacts"), redirect);
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "sync-by-user-groups"), currentURL);
%>

<portlet:renderURL var="editSyncedContactsFieldsURL">
	<portlet:param name="mvcRenderCommandName" value="/analytics_settings/edit_synced_contacts_fields" />
</portlet:renderURL>

<clay:container-fluid>
	<clay:row>
		<clay:col
			size="12"
		>
			<div id="breadcrumb">
				<liferay-site-navigation:breadcrumb
					breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, true, true) %>"
				/>
			</div>
		</clay:col>
	</clay:row>
</clay:container-fluid>

<clay:sheet>
	<h2>
		<liferay-ui:message key="select-contacts-by-user-groups" />
	</h2>

	<hr />

	<div class="c-pb-3 form-text">
		<liferay-ui:message key="select-contacts-by-user-groups-help" />
	</div>

	<%
	UserGroupDisplayContext userGroupDisplayContext = new UserGroupDisplayContext(renderRequest, renderResponse);
	%>

	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new UserGroupManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, userGroupDisplayContext) %>"
	/>

	<aui:form action="<%= editSyncedContactsFieldsURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="update_synced_groups" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<liferay-ui:search-container
			id="selectUserGroups"
			searchContainer="<%= userGroupDisplayContext.getUserGroupSearch() %>"
			var="userGroupSearchContainer"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.UserGroup"
				escapedModel="<%= true %>"
				keyPropertyName="userGroupId"
				modelVar="userGroup"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="user-group-name"
					value="<%= HtmlUtil.escape(userGroup.getName()) %>"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
				searchResultCssClass="show-quick-actions-on-hover table table-autofit"
			/>
		</liferay-ui:search-container>

		<div class="text-right">
			<aui:button-row>
				<aui:button href="<%= redirect %>" type="cancel" value="cancel" />
				<aui:button type="submit" value="save-and-next" />
			</aui:button-row>
		</div>
	</aui:form>
</clay:sheet>