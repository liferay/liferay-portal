<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/announcements_admin/init.jsp" %>

<%
String navigation = ParamUtil.getString(request, "navigation", "announcements");

AnnouncementsAdminViewDisplayContext announcementsAdminViewDisplayContext = new AnnouncementsAdminViewDisplayContext(request, liferayPortletRequest, liferayPortletResponse, renderRequest);
%>

<clay:navigation-bar
	navigationItems='<%=
		new JSPNavigationItemList(pageContext) {
			{
				add(
					navigationItem -> {
						navigationItem.setActive(navigation.equals("announcements"));
						navigationItem.setHref(renderResponse.createRenderURL());
						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "announcements"));
					});
				add(
					navigationItem -> {
						navigationItem.setActive(navigation.equals("alerts"));
						navigationItem.setHref(renderResponse.createRenderURL(), "navigation", "alerts");
						navigationItem.setLabel(LanguageUtil.get(httpServletRequest, "alerts"));
					});
			}
		}
	%>'
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new AnnouncementsAdminViewManagementToolbarDisplayContext(announcementsAdminViewDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
	propsTransformer="{AnnouncementsManagementToolbarPropsTransformer} from announcements-web"
/>

<clay:container-fluid>
	<aui:form action="<%= currentURL %>" method="get" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

		<liferay-ui:search-container
			searchContainer="<%= announcementsAdminViewDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.announcements.kernel.model.AnnouncementsEntry"
				keyPropertyName="entryId"
				modelVar="entry"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(announcementsAdminViewDisplayContext.getAvailableActions(entry))
					).build());
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					href='<%=
						PortletURLBuilder.createRenderURL(
							renderResponse
						).setMVCRenderCommandName(
							"/announcements/view_entry"
						).setRedirect(
							currentURL
						).setParameter(
							"entryId", entry.getEntryId()
						).buildPortletURL()
					%>'
					name="title"
					value="<%= HtmlUtil.escape(entry.getTitle()) %>"
				/>

				<liferay-ui:search-container-column-text
					name="type"
					value="<%= HtmlUtil.escape(LanguageUtil.get(resourceBundle, entry.getType())) %>"
				/>

				<liferay-ui:search-container-column-date
					name="modified-date"
					value="<%= entry.getModifiedDate() %>"
				/>

				<liferay-ui:search-container-column-date
					name="display-date"
					value="<%= entry.getDisplayDate() %>"
				/>

				<liferay-ui:search-container-column-date
					name="expiration-date"
					value="<%= entry.getExpirationDate() %>"
				/>

				<liferay-ui:search-container-column-jsp
					path="/announcements_admin/entry_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>