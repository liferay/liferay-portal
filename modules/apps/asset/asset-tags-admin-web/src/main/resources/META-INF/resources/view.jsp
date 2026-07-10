<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new AssetTagsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, assetTagsDisplayContext) %>"
	propsTransformer="{ManagementToolbarPropsTransformer} from asset-tags-admin-web"
/>

<portlet:actionURL name="deleteTag" var="deleteTagURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteTagURL %>" cssClass="container-fluid container-fluid-max-xxxl" name="fm">
	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
	/>

	<liferay-ui:search-container
		id="assetTags"
		searchContainer="<%= assetTagsDisplayContext.getTagsSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.asset.kernel.model.AssetTag"
			keyProperty="tagId"
			modelVar="tag"
		>

			<%
			long fullTagsCount = assetTagsDisplayContext.getFullTagsCount(tag);
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(assetTagsDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-icon
						icon="tag"
						toggleRowChecker="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<h2 class="h5">
							<%= HtmlUtil.escape(tag.getName()) %>
						</h2>

						<span class="text-default">
							<strong><liferay-ui:message key="usages" /></strong>: <span><%= String.valueOf(fullTagsCount) %></span>
						</span>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= assetTagsDisplayContext.getAssetTagActionDropdownItems(tag) %>"
							propsTransformer="{AssetTagActionDropdownPropsTransformer} from asset-tags-admin-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(assetTagsDisplayContext.getDisplayStyle(), "list") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200 table-title"
						name="name"
						value="<%= HtmlUtil.escape(tag.getName()) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-column-text-center"
						name="usages"
						value="<%= String.valueOf(fullTagsCount) %>"
					/>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= assetTagsDisplayContext.getAssetTagActionDropdownItems(tag) %>"
							propsTransformer="{AssetTagActionDropdownPropsTransformer} from asset-tags-admin-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= assetTagsDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>