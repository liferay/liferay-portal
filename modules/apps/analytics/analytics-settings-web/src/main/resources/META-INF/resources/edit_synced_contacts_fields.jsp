<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String cmd = ParamUtil.getString(request, Constants.CMD);

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/configuration_admin/view_configuration_screen"
).setParameter(
	"configurationScreenKey", "2-synced-contact-data"
).buildPortletURL();

String redirect = ParamUtil.getString(request, "redirect", portletURL.toString());

boolean syncAllContacts = ParamUtil.getBoolean(request, "syncAllContacts");
String[] syncedOrganizationIds = ParamUtil.getStringValues(request, "syncedOrganizationIds");
String[] syncedUserGroupIds = ParamUtil.getStringValues(request, "syncedUserGroupIds");

AnalyticsConfiguration analyticsConfiguration = (AnalyticsConfiguration)request.getAttribute(AnalyticsSettingsWebKeys.ANALYTICS_CONFIGURATION);

String[] syncedContactFieldNames = GetterUtil.getStringValues(analyticsConfiguration.syncedContactFieldNames());
String[] syncedUserFieldNames = GetterUtil.getStringValues(analyticsConfiguration.syncedUserFieldNames());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", redirect));

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "select-contact-data"), portletURL.toString());

if (StringUtil.equals(cmd, "update_synced_groups")) {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "sync-by-user-groups"), redirect);
}
else if (StringUtil.equals(cmd, "update_synced_organizations")) {
	PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "sync-by-organizations"), redirect);
}

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "select-data-fields"), currentURL);
%>

<portlet:actionURL name="/analytics_settings/edit_synced_contacts" var="editSyncedContactsURL" />

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
		<liferay-ui:message key="sync-data-fields" />
	</h2>

	<p class="mt-3 text-secondary">
		<liferay-ui:message key="sync-data-fields-help" />
	</p>

	<aui:form action="<%= editSyncedContactsURL %>" method="post" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" value="update_synced_contacts_fields" />
		<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
		<aui:input name="exit" type="hidden" value="<%= false %>" />
		<aui:input name="referrer" type="hidden" value="<%= cmd %>" />
		<aui:input name="syncAllContacts" type="hidden" value="<%= String.valueOf(syncAllContacts) %>" />
		<aui:input name="syncedOrganizationIds" type="hidden" value="<%= StringUtil.merge(syncedOrganizationIds) %>" />
		<aui:input name="syncedUserGroupIds" type="hidden" value="<%= StringUtil.merge(syncedUserGroupIds) %>" />

		<liferay-ui:tabs
			names='<%= LanguageUtil.format(resourceBundle, "contact-x", syncedContactFieldNames.length, false) + "," + LanguageUtil.format(resourceBundle, "user-x", syncedUserFieldNames.length, false) %>'
			refresh="<%= false %>"
		>
			<liferay-ui:section>

				<%
				FieldDisplayContext fieldDisplayContext = new FieldDisplayContext("/analytics_settings/edit_synced_contacts_fields", renderRequest, renderResponse);
				%>

				<clay:management-toolbar
					managementToolbarDisplayContext="<%= new FieldManagementToolbarDisplayContext(fieldDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
				/>

				<liferay-ui:search-container
					id="selectContactsFields"
					searchContainer="<%= fieldDisplayContext.getFieldSearch() %>"
					var="contactsFieldsSearchContainer"
				>
					<liferay-ui:search-container-row
						className="com.liferay.analytics.settings.web.internal.model.Field"
						escapedModel="<%= true %>"
						keyPropertyName="name"
						modelVar="contactField"
					>
						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="attribute"
							value="<%= HtmlUtil.escape(contactField.getName()) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="category"
							value="<%= HtmlUtil.escape(contactField.getCategory()) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="data-type"
							value="<%= HtmlUtil.escape(contactField.getDataType()) %>"
						/>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						markupView="lexicon"
						paginate="<%= false %>"
						searchResultCssClass="show-quick-actions-on-hover table table-autofit"
					/>
				</liferay-ui:search-container>
			</liferay-ui:section>

			<liferay-ui:section>

				<%
				FieldDisplayContext fieldDisplayContext = new FieldDisplayContext("/analytics_settings/edit_synced_users_fields", renderRequest, renderResponse);
				%>

				<clay:management-toolbar
					managementToolbarDisplayContext="<%= new FieldManagementToolbarDisplayContext(fieldDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
				/>

				<liferay-ui:search-container
					id="selectUsersFields"
					searchContainer="<%= fieldDisplayContext.getFieldSearch() %>"
					var="usersFieldsSearchContainer"
				>
					<liferay-ui:search-container-row
						className="com.liferay.analytics.settings.web.internal.model.Field"
						escapedModel="<%= true %>"
						keyPropertyName="name"
						modelVar="userField"
					>
						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="attribute"
							value="<%= HtmlUtil.escape(userField.getName()) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="category"
							value="<%= HtmlUtil.escape(userField.getCategory()) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand"
							name="data-type"
							value="<%= HtmlUtil.escape(userField.getDataType()) %>"
						/>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						markupView="lexicon"
						paginate="<%= false %>"
						searchResultCssClass="show-quick-actions-on-hover table table-autofit"
					/>
				</liferay-ui:search-container>
			</liferay-ui:section>
		</liferay-ui:tabs>

		<div class="text-right">
			<aui:button-row>
				<aui:button href="" onClick='<%= liferayPortletResponse.getNamespace() + "showConfirmationModal(this);" %>' value="cancel" />

				<aui:button id="submitForm" type="submit" value="save" />
			</aui:button-row>
		</div>
	</aui:form>
</clay:sheet>

<aui:script>
	var submitButton = document.querySelector('#<portlet:namespace />submitForm');
	var form = document.querySelector(
		"form[action='<%= editSyncedContactsURL %>']"
	);

	submitButton.addEventListener('click', () => {
		var selectUsersFields = document.querySelector(
			'#<portlet:namespace />selectUsersFields'
		);

		if (selectUsersFields) {
			form.appendChild(selectUsersFields);
		}

		form.submit();
	});

	Liferay.provide(
		window,
		'<portlet:namespace />showConfirmationModal',
		(event) => {
			var dialog = Liferay.Util.Window.getWindow({
				dialog: {
					bodyContent:
						'<div><h2><liferay-ui:message key="exit-without-saving" /></h2><p class="mt-3 text-secondary"><liferay-ui:message key="exit-without-saving-help" /></p></div>',
					destroyOnHide: true,
					height: 300,
					resizable: false,
					toolbars: {
						footer: [
							{
								cssClass: 'btn-cancel',
								label: '<liferay-ui:message key="cancel" />',
								on: {
									click: function () {
										dialog.hide();
									},
								},
							},
							{
								cssClass: 'btn-primary',
								label: '<liferay-ui:message key="exit" />',
								on: {
									click: function () {
										var form = document.getElementById(
											'<portlet:namespace />fm'
										);

										var exit = form.querySelector(
											'#<portlet:namespace />exit'
										);

										if (exit) {
											exit.setAttribute('value', 'true');
										}

										submitForm(
											form,
											'<portlet:actionURL name="/analytics_settings/edit_synced_contacts" var="editSyncedContactsURL" />'
										);
									},
								},
							},
						],
						header: [
							{
								cssClass: 'close',
								discardDefaultButtonCssClasses: true,
								labelHTML:
									'<span aria-hidden="true">&times;</span>',
								on: {
									click: function (event) {
										dialog.hide();
									},
								},
							},
						],
					},
					width: 500,
				},
				title: '<%= LanguageUtil.get(resourceBundle, "unsaved-changes") %>',
			});
		},
		['aui-base', 'liferay-util-window']
	);
</aui:script>