<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LockedLayoutsDisplayContext lockedLayoutsDisplayContext = (LockedLayoutsDisplayContext)request.getAttribute(LockedLayoutsDisplayContext.class.getName());
%>

<liferay-ui:success key="unlockLayoutsRequestProcessed" message='<%= GetterUtil.getString(SessionMessages.get(liferayPortletRequest, "unlockLayoutsRequestProcessed")) %>' />

<clay:sheet-section
	cssClass="list-locked-pages"
>
	<clay:management-toolbar
		managementToolbarDisplayContext="<%= new LockedLayoutsSearchContainerManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, lockedLayoutsDisplayContext) %>"
		propsTransformer="{ManagementToolbarPropsTransformer} from layout-locked-layouts-web"
	/>

	<portlet:actionURL name="/locked_items/unlock_layouts" var="unlockLayoutsURL">
		<portlet:param name="redirect" value="<%= currentURL %>" />
	</portlet:actionURL>

	<aui:form action="<%= unlockLayoutsURL %>" name="fm">
		<liferay-ui:search-container
			id="lockedLayoutsSearchContainer"
			searchContainer="<%= lockedLayoutsDisplayContext.getSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.layout.model.LockedLayout"
				keyPropertyName="plid"
				modelVar="lockedLayout"
			>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", "unlockLockedLayouts"
					).build());

				String name = HtmlUtil.escape(lockedLayoutsDisplayContext.getName(lockedLayout));
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="name"
				>
					<clay:link
						aria-label="<%= name %>"
						href="<%= lockedLayoutsDisplayContext.getLayoutURL(lockedLayout) %>"
						label="<%= name %>"
						target="_blank"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="type"
					value="<%= lockedLayoutsDisplayContext.getLayoutType(lockedLayout) %>"
				/>

				<liferay-ui:search-container-column-text
					name="current-user"
					value="<%= HtmlUtil.escape(lockedLayout.getUserName()) %>"
				/>

				<liferay-ui:search-container-column-text
					name="last-autosave"
					value="<%= lockedLayoutsDisplayContext.getLastAutoSave(lockedLayout) %>"
				/>

				<%
				String label = LanguageUtil.format(request, "actions-for-x", name, false);
				%>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label="<%= label %>"
						dropdownItems="<%= lockedLayoutsDisplayContext.getLockedLayoutDropdownItems(lockedLayout) %>"
						propsTransformer="{LockedLayoutDropdownDefaultPropsTransformer} from layout-locked-layouts-web"
						title="<%= label %>"
					/>
				</liferay-ui:search-container-column-text>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
				paginate="<%= false %>"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:sheet-section>