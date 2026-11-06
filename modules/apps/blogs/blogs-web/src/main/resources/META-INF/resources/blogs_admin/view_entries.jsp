<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/blogs_admin/init.jsp" %>

<%
long assetCategoryId = ParamUtil.getLong(request, "categoryId");
String assetTagName = ParamUtil.getString(request, "tag");

BlogsViewEntriesDisplayContext blogsViewEntriesDisplayContext = (BlogsViewEntriesDisplayContext)request.getAttribute(BlogsViewEntriesDisplayContext.class.getName());

String displayStyle = blogsViewEntriesDisplayContext.getDisplayStyle();

SearchContainer<BlogsEntry> entriesSearchContainer = blogsViewEntriesDisplayContext.getSearchContainer();

PortletURL portletURL = entriesSearchContainer.getIteratorURL();
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new BlogsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, entriesSearchContainer, trashHelper) %>"
	propsTransformer="{BlogEntriesManagementToolbarPropsTransformer} from blogs-web"
/>

<portlet:actionURL name="/blogs/edit_entry" var="restoreTrashEntriesURL">
	<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RESTORE %>" />
</portlet:actionURL>

<liferay-trash:undo
	portletURL="<%= restoreTrashEntriesURL %>"
/>

<clay:container-fluid
	size="xxxl"
>
	<aui:form action="<%= portletURL %>" method="get" name="fm">
		<aui:input name="<%= Constants.CMD %>" type="hidden" />
		<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
		<aui:input name="deleteEntryIds" type="hidden" />
		<aui:input name="selectAll" type="hidden" value="<%= false %>" />

		<c:if test="<%= (assetCategoryId != 0) || Validator.isNotNull(assetTagName) %>">
			<liferay-asset:categorization-filter
				assetType="entries"
				portletURL="<%= portletURL %>"
			/>
		</c:if>

		<liferay-ui:search-container
			id="blogEntries"
			searchContainer="<%= entriesSearchContainer %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.blogs.model.BlogsEntry"
				escapedModel="<%= true %>"
				keyPropertyName="entryId"
				modelVar="entry"
			>
				<liferay-portlet:renderURL varImpl="rowURL">
					<portlet:param name="mvcRenderCommandName" value="/blogs/edit_entry" />
					<portlet:param name="redirect" value="<%= portletURL.toString() %>" />
					<portlet:param name="entryId" value="<%= String.valueOf(entry.getEntryId()) %>" />
				</liferay-portlet:renderURL>

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", StringUtil.merge(blogsViewEntriesDisplayContext.getAvailableActions(entry))
					).build());
				%>

				<%@ include file="/blogs_admin/entry_search_columns.jspf" %>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				displayStyle="<%= displayStyle %>"
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</aui:form>
</clay:container-fluid>

<liferay-frontend:component
	componentId="<%= BlogsWebConstants.BLOGS_ELEMENTS_DEFAULT_EVENT_HANDLER %>"
	context="<%= blogsViewEntriesDisplayContext.getComponentContext() %>"
	module="{ElementsDefaultEventHandler} from blogs-web"
/>