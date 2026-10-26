<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AnalyticsConfiguration analyticsConfiguration = (AnalyticsConfiguration)request.getAttribute(AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION);

boolean connected = false;

if (!Validator.isBlank(analyticsConfiguration.token())) {
	connected = true;
}

boolean syncAllContacts = analyticsConfiguration.syncAllContacts();
Set<String> syncedContactFieldNames = SetUtil.fromArray(analyticsConfiguration.syncedContactFieldNames());
Set<String> syncedOrganizationIds = SetUtil.fromArray(analyticsConfiguration.syncedOrganizationIds());
Set<String> syncedUserFieldNames = SetUtil.fromArray(analyticsConfiguration.syncedUserFieldNames());
Set<String> syncedUserGroupIds = SetUtil.fromArray(analyticsConfiguration.syncedUserGroupIds());

long totalContactsSelected = 0;

if (analyticsConfiguration.syncAllContacts()) {
	totalContactsSelected = AnalyticsUsersUtil.getCompanyUsersCount(themeDisplay.getCompanyId());
}
else {
	String[] syncedOrganizationIdsArray = GetterUtil.getStringValues(analyticsConfiguration.syncedOrganizationIds());

	long[] syncedOrganizationIdsLong = new long[syncedOrganizationIdsArray.length];

	for (int i = 0; i < syncedOrganizationIdsArray.length; i++) {
		syncedOrganizationIdsLong[i] = GetterUtil.getLong(syncedOrganizationIdsArray[i]);
	}

	String[] syncedUserGroupIdsArray = GetterUtil.getStringValues(analyticsConfiguration.syncedUserGroupIds());

	long[] syncedUserGroupIdsLong = new long[syncedUserGroupIdsArray.length];

	for (int i = 0; i < syncedUserGroupIdsArray.length; i++) {
		syncedUserGroupIdsLong[i] = GetterUtil.getLong(syncedUserGroupIdsArray[i]);
	}

	totalContactsSelected = AnalyticsUsersUtil.getOrganizationsAndUserGroupsUsersCount(syncedOrganizationIdsLong, syncedUserGroupIdsLong);
}
%>

<c:if test='<%= SessionErrors.contains(renderRequest, "unsavedContactsFields") %>'>
	<aui:script>
		Liferay.Util.openToast({
			message: '<liferay-ui:message key="synced-fields-have-not-been-saved" />',
			title: Liferay.Language.get('warning'),
			toastProps: {
				autoClose: 5000,
			},
			type: 'warning',
		});
	</aui:script>
</c:if>

<clay:sheet
	cssClass="portlet-analytics-settings"
>
	<h2>
		<liferay-ui:message key="contact-data" />
	</h2>

	<hr />

	<div class="c-pb-3 form-text">
		<liferay-ui:message key="select-contact-data-help" />
	</div>

	<fieldset <%= connected ? "" : "disabled" %>>
		<label class="control-label">
			<liferay-ui:message key="contact-sync-options" />
		</label>

		<c:choose>
			<c:when test="<%= connected %>">
				<portlet:renderURL var="editSyncedContactsURL">
					<portlet:param name="mvcRenderCommandName" value="/analytics_settings/edit_synced_contacts" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<a class="d-flex m-4 p-2 text-decoration-none" href=<%= editSyncedContactsURL %>>
			</c:when>
			<c:otherwise>
				<span class="contacts-link-disabled d-flex m-4 p-2">
			</c:otherwise>
		</c:choose>

		<div class="pr-3">
			<clay:sticker
				cssClass="sticker-dark"
				displayType="dark"
				icon="user"
			/>
		</div>

		<div>
			<p class="list-group-title">
				<liferay-ui:message key="sync-contacts" />
			</p>

			<small class="list-group-subtext">
				<liferay-ui:message arguments='<%= syncAllContacts ? "all" : totalContactsSelected %>' key="x-contacts-selected" />
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
			<c:when test="<%= connected && (syncAllContacts || (totalContactsSelected > 0)) %>">
				<portlet:renderURL var="editSyncedContactsFieldsURL">
					<portlet:param name="mvcRenderCommandName" value="/analytics_settings/edit_synced_contacts_fields" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
					<portlet:param name="syncAllContacts" value="<%= String.valueOf(syncAllContacts) %>" />
					<portlet:param name="syncedOrganizationIds" value="<%= StringUtil.merge(syncedOrganizationIds) %>" />
					<portlet:param name="syncedUserGroupIds" value="<%= StringUtil.merge(syncedUserGroupIds) %>" />
				</portlet:renderURL>

				<a class="d-flex m-4 p-2 text-decoration-none" href=<%= editSyncedContactsFieldsURL %>>
			</c:when>
			<c:otherwise>
				<span class="contacts-link-disabled d-flex m-4 p-2">
			</c:otherwise>
		</c:choose>

		<div class="pr-3">
			<clay:sticker
				cssClass="sticker-dark"
				displayType="dark"
				icon="check-square"
			/>
		</div>

		<div>
			<p class="list-group-title">
				<liferay-ui:message key="sync-data-fields" />
			</p>

			<small class="list-group-subtext">
				<liferay-ui:message arguments="<%= syncedContactFieldNames.size() + syncedUserFieldNames.size() %>" key="x-fields-selected" />
			</small>
		</div>

		<c:choose>
			<c:when test="<%= connected && (syncAllContacts || (totalContactsSelected > 0)) %>">
				</a>
			</c:when>
			<c:otherwise>
				</span>
			</c:otherwise>
		</c:choose>
	<fieldset>
</clay:sheet>