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
String[] syncedGroupIds = new String[0];
String token = "";
long totalContactsSelected = 0;

if (analyticsConfiguration != null) {
	syncedGroupIds = analyticsConfiguration.syncedGroupIds();

	token = analyticsConfiguration.token();

	if (!Validator.isBlank(token)) {
		connected = true;
	}

	if (analyticsConfiguration.syncAllContacts()) {
		totalContactsSelected = AnalyticsUsersUtil.getCompanyUsersCount(themeDisplay.getCompanyId());
	}
	else {
		String[] syncedOrganizationIds = GetterUtil.getStringValues(analyticsConfiguration.syncedOrganizationIds());

		long[] syncedOrganizationIdsLong = new long[syncedOrganizationIds.length];

		for (int i = 0; i < syncedOrganizationIds.length; i++) {
			syncedOrganizationIdsLong[i] = GetterUtil.getLong(syncedOrganizationIds[i]);
		}

		String[] syncedUserGroupIds = GetterUtil.getStringValues(analyticsConfiguration.syncedUserGroupIds());

		long[] syncedUserGroupIdsLong = new long[syncedUserGroupIds.length];

		for (int i = 0; i < syncedUserGroupIds.length; i++) {
			syncedUserGroupIdsLong[i] = GetterUtil.getLong(syncedUserGroupIds[i]);
		}

		totalContactsSelected = AnalyticsUsersUtil.getOrganizationsAndUserGroupsUsersCount(syncedOrganizationIdsLong, syncedUserGroupIdsLong);
	}
}
%>

<c:if test='<%= SessionErrors.contains(renderRequest, "unableToNotifyAnalyticsCloud") %>'>
	<aui:script>
		Liferay.Util.openToast({
			message: '<liferay-ui:message key="unable-to-notify-analytics-cloud" />',
			title: Liferay.Language.get('warning'),
			toastProps: {
				autoClose: 5000,
			},
			type: 'warning',
		});
	</aui:script>
</c:if>

<portlet:actionURL name="/analytics_settings/edit_workspace_connection" var="editWorkspaceConnectionURL" />

<clay:sheet>
	<h2>
		<liferay-ui:message key="connect-to-analytics-cloud" />
	</h2>

	<aui:form action="<%= editWorkspaceConnectionURL %>" data-senna-off="true" method="post" name="fm" onSubmit='<%= liferayPortletResponse.getNamespace() + "confirmation(event);" %>'>
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<c:if test="<%= connected %>">
			<aui:input name="<%= Constants.CMD %>" type="hidden" value="disconnect" />
		</c:if>

		<aui:fieldset>
			<c:if test="<%= !connected %>">
				<aui:input autocomplete="off" label="analytics-cloud-token" name="token" oninput='<%= liferayPortletResponse.getNamespace() + "validateTokenButton();" %>' placeholder="paste-token-here" value="<%= token %>" wrapperCssClass="mb-1" />

				<div class="form-text">
					<liferay-ui:message key="analytics-cloud-token-help" />
				</div>
			</c:if>

			<c:if test="<%= connected %>">
				<label class="control-label d-block mb-2">
					<liferay-ui:message key="analytics-cloud-token" />
				</label>

				<label class="control-label d-block">
					<liferay-ui:message key="your-dxp-instance-is-connected-to-analytics-cloud" />
				</label>
			</c:if>

			<aui:button-row cssClass="mt-2">
				<c:if test="<%= connected %>">
					<a class="btn btn-primary mr-2" href="<%= analyticsConfiguration.liferayAnalyticsURL() %>" rel="noopener noreferrer" target="_blank">
						<liferay-ui:message key="go-to-workspace" />

						<span class="inline-item inline-item-after">
							<clay:icon
								symbol="shortcut"
							/>
						</span>
					</a>
				</c:if>

				<aui:button id="tokenButton" primary="<%= connected ? false : true %>" type="submit" value='<%= connected ? "disconnect" : "connect" %>' />
			</aui:button-row>
		</aui:fieldset>
	</aui:form>

	<aui:fieldset>
		<label class="control-label">
			<liferay-ui:message key="synced-sites" />
		</label>

		<div class="form-text">
			<liferay-ui:message key="synced-sites-help" />
		</div>

		<small>
			<strong>
				<liferay-ui:message arguments="<%= ArrayUtil.getLength(syncedGroupIds) %>" key="total-sites-selected-x" />
			</strong>
		</small>

		<aui:button-row>
			<liferay-portlet:renderURL varImpl="selectSitesURL">
				<portlet:param name="mvcRenderCommandName" value="/configuration_admin/view_configuration_screen" />
				<portlet:param name="configurationScreenKey" value="1-synced-sites" />
			</liferay-portlet:renderURL>

			<aui:button disabled="<%= !connected %>" href="<%= selectSitesURL.toString() %>" primary="<%= true %>" value="select-sites" />
		</aui:button-row>
	</aui:fieldset>

	<aui:fieldset>
		<label class="control-label">
			<liferay-ui:message key="synced-contacts" />
		</label>

		<div class="form-text">
			<liferay-ui:message key="synced-contacts-help" />
		</div>

		<small>
			<strong>
				<liferay-ui:message arguments="<%= totalContactsSelected %>" key="total-contacts-selected-x" />
			</strong>
		</small>

		<aui:button-row>
			<liferay-portlet:renderURL varImpl="selectContactDataURL">
				<portlet:param name="mvcRenderCommandName" value="/configuration_admin/view_configuration_screen" />
				<portlet:param name="configurationScreenKey" value="2-synced-contact-data" />
			</liferay-portlet:renderURL>

			<aui:button disabled="<%= !connected %>" href="<%= selectContactDataURL.toString() %>" primary="<%= true %>" value="select-contacts" />
		</aui:button-row>
	</aui:fieldset>
</clay:sheet>

<aui:script>
	function <portlet:namespace />confirmation(event) {
		<c:if test="<%= connected %>">
			Liferay.Util.openConfirmModal({
				message:
					'<liferay-ui:message key="are-you-sure-you-want-to-disconnect-your-analytics-cloud-workspace-from-this-dxp-instance" />',
				onConfirm: (isConfirmed) => {
					if (!isConfirmed) {
						event.preventDefault();
					}
				},
			});
		</c:if>
	}

	function <portlet:namespace />validateTokenButton() {
		var token = document.getElementById('<portlet:namespace />token');
		var tokenButton = document.getElementById(
			'<portlet:namespace />tokenButton'
		);

		var value = token.value;

		tokenButton.disabled = value.length === 0;
	}

	<c:if test="<%= !connected %>">
		<portlet:namespace />validateTokenButton();
	</c:if>
</aui:script>