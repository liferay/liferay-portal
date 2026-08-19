<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/configuration_admin/view_configuration_screen"
).setParameter(
	"configurationScreenKey", "2-synced-contact-data"
).buildString();

AnalyticsConfiguration analyticsConfiguration = (AnalyticsConfiguration)request.getAttribute(AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION);
AnalyticsUsersManager analyticsUsersManager = (AnalyticsUsersManager)request.getAttribute(AnalyticsSettingsWebKeys.ANALYTICS_USERS_MANAGER);

boolean connected = false;

if (!Validator.isBlank(analyticsConfiguration.token())) {
	connected = true;
}

boolean syncAllContacts = analyticsConfiguration.syncAllContacts();
Set<String> syncedOrganizationIds = SetUtil.fromArray(analyticsConfiguration.syncedOrganizationIds());
Set<String> syncedUserGroupIds = SetUtil.fromArray(analyticsConfiguration.syncedUserGroupIds());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", redirect));

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "select-contact-data"), redirect);
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "select-contacts"), currentURL);
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

<clay:sheet
	cssClass="portlet-analytics-settings"
>
	<h2>
		<liferay-ui:message key="contact-data" />
	</h2>

	<div class="c-pb-3 form-text">
		<liferay-ui:message key="contact-data-help" />
	</div>

	<aui:form action="<%= editSyncedContactsFieldsURL %>" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<fieldset <%= connected ? "" : "disabled" %>>
			<label class="control-label">
				<liferay-ui:message key="sync-all-contacts" />
			</label>

			<br />

			<label class="mb-5 mt-3 toggle-switch">
				<input class="toggle-switch-check" name="<portlet:namespace />syncAllContacts" type="checkbox" <%= syncAllContacts ? "checked" : "" %> />

				<span aria-hidden="true" class="toggle-switch-bar">
					<span class="toggle-switch-handle"></span>
				</span>
				<span class="toggle-switch-text toggle-switch-text-right">
					<liferay-ui:message arguments="<%= analyticsUsersManager.getCompanyUsersCount(themeDisplay.getCompanyId()) %>" key="sync-all-x-contacts" />
				</span>
			</label>
		</fieldset>

		<fieldset <%= connected ? "" : "disabled" %>>
			<label class="control-label">
				<liferay-ui:message key="sync-by-user-groups-and-organizations" />
			</label>

			<c:choose>
				<c:when test="<%= connected %>">
					<portlet:renderURL var="createUserGroupURL">
						<portlet:param name="mvcRenderCommandName" value="/analytics_settings/edit_synced_contacts_groups" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:renderURL>

					<a class="d-flex m-4 p-2 text-decoration-none" href=<%= createUserGroupURL %>>
				</c:when>
				<c:otherwise>
					<span class="contacts-link-disabled d-flex m-4 p-2">
				</c:otherwise>
			</c:choose>

				<div class="pr-3">
					<clay:sticker
						cssClass="sticker-dark"
						displayType="dark"
						icon="users"
					/>
				</div>

				<div>
					<p class="list-group-title">
						<liferay-ui:message key="sync-by-user-groups" />
					</p>

					<small class="list-group-subtext">
						<liferay-ui:message arguments='<%= syncAllContacts ? "all" : syncedUserGroupIds.size() %>' key="x-user-groups-selected" />
					</small>
				</div>

			<c:choose>
				<c:when test="<%= connected %>">
					</a>
				</c:when>
				<c:otherwise>
					</span>
				</c:otherwise>
			</c:choose>

			<c:choose>
				<c:when test="<%= connected %>">
					<portlet:renderURL var="createOrganizationsURL">
						<portlet:param name="mvcRenderCommandName" value="/analytics_settings/edit_synced_contacts_organizations" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:renderURL>

					<a class="d-flex m-4 p-2 text-decoration-none" href=<%= createOrganizationsURL %>>
				</c:when>
				<c:otherwise>
					<span class="contacts-link-disabled d-flex m-4 p-2">
				</c:otherwise>
			</c:choose>

				<div class="pr-3">
					<clay:sticker
						cssClass="sticker-dark"
						displayType="dark"
						icon="organizations"
					/>
				</div>

				<div>
					<p class="list-group-title">
						<liferay-ui:message key="sync-by-organizations" />
					</p>

					<small class="list-group-subtext">
						<liferay-ui:message arguments='<%= syncAllContacts ? "all" : syncedOrganizationIds.size() %>' key="x-organizations-selected" />
					</small>
				</div>

			<c:choose>
				<c:when test="<%= connected %>">
					</a>
				</c:when>
				<c:otherwise>
					</span>
				</c:otherwise>
			</c:choose>
		<fieldset>

		<div class="text-right">
			<aui:button-row>
				<aui:button href="<%= redirect %>" type="cancel" value="cancel" />
				<aui:button disabled="<%= !connected %>" type="submit" value="save-and-next" />
			</aui:button-row>
		</div>
	</aui:form>
</clay:sheet>