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
	"configurationScreenKey", "1-synced-sites"
).buildString();

String channelId = ParamUtil.getString(request, "channelId");
String channelName = ParamUtil.getString(request, "channelName");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", redirect));

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "synced-sites"), redirect);
PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(resourceBundle, "edit-property"), currentURL);
%>

<portlet:actionURL name="/analytics_settings/edit_channel" var="editChannelURL" />

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

<aui:form action="<%= editChannelURL %>" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="channelId" type="hidden" value="<%= channelId %>" />

	<clay:sheet
		cssClass="portlet-analytics-settings"
	>
		<h2>
			<liferay-ui:message arguments="<%= channelName %>" key="sites-to-sync-x" />
		</h2>

		<p class="mt-3 text-secondary">
			<liferay-ui:message key="sites-can-only-be-assigned-to-a-single-property-at-a-time" />
		</p>

		<%
		GroupDisplayContext groupDisplayContext = new GroupDisplayContext("/analytics_settings/edit_channel", renderRequest, renderResponse);
		%>

		<clay:management-toolbar
			managementToolbarDisplayContext="<%= new GroupManagementToolbarDisplayContext(groupDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
		/>

		<liferay-ui:search-container
			id="selectGroups"
			searchContainer="<%= groupDisplayContext.getGroupSearch() %>"
			var="groupSearchContainer"
		>
			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.Group"
				escapedModel="<%= true %>"
				keyPropertyName="groupId"
				modelVar="group"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="site-name"
					value="<%= HtmlUtil.escape(group.getDescriptiveName(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="site-friendly-url"
					value="<%= HtmlUtil.escape(group.getFriendlyURL()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="assigned-property"
					value="<%= HtmlUtil.escape(groupDisplayContext.getChannelName(group.getGroupId())) %>"
				/>

				<%
				List<Group> childrenGroups = group.getChildren(true);
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smaller table-cell-ws-nowrap"
					name="child-sites"
					value="<%= String.valueOf(childrenGroups.size()) %>"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
				searchResultCssClass="show-quick-actions-on-hover table table-autofit"
			/>
		</liferay-ui:search-container>

		<div class="text-right">
			<aui:button-row>
				<aui:button href="<%= redirect %>" value="cancel" />

				<aui:button type="submit" value="done" />
			</aui:button-row>
		</div>
	</clay:sheet>
</aui:form>