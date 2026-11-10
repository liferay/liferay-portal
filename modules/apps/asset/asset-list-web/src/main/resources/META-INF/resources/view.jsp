<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AssetListManagementToolbarDisplayContext assetListManagementToolbarDisplayContext = new AssetListManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, assetListDisplayContext);
%>

<liferay-ui:error exception="<%= RequiredAssetListEntryException.class %>" message="you-cannot-delete-collections-that-are-used-by-one-or-more-items.-please-view-the-usages-and-try-to-unassign-them" />

<clay:navigation-bar
	navigationItems='<%= assetListDisplayContext.getNavigationItems("collections") %>'
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= assetListManagementToolbarDisplayContext %>"
	propsTransformer="{ManagementToolbarPropsTransformer} from asset-list-web"
/>

<portlet:actionURL name="/asset_list/delete_asset_list_entries" var="deleteAssetListEntryURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteAssetListEntryURL %>" cssClass="container-fluid container-fluid-max-xl" name="fm">
	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
	/>

	<c:choose>
		<c:when test="<%= assetListDisplayContext.getAssetListEntriesCount() > 0 %>">
			<liferay-ui:search-container
				id="assetListEntries"
				searchContainer="<%= assetListDisplayContext.getAssetListEntriesSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.asset.list.model.AssetListEntry"
					keyPropertyName="assetListEntryId"
					modelVar="assetListEntry"
				>

					<%
					row.setData(
						HashMapBuilder.<String, Object>put(
							"actions", assetListManagementToolbarDisplayContext.getAvailableActions(assetListEntry)
						).build());

					int assetListEntryVariationsCount = assetListDisplayContext.getAssetListEntryVariationsCount(assetListEntry);
					Date createDate = assetListEntry.getCreateDate();
					%>

					<c:choose>
						<c:when test='<%= Objects.equals(assetListDisplayContext.getDisplayStyle(), "descriptive") %>'>
							<liferay-ui:search-container-column-text>
								<div class="lfr-portal-tooltip sticker sticker-secondary" title="<%= assetListDisplayContext.getAssetListEntryTypeLabel(assetListEntry) %>">
									<clay:icon
										cssClass="mr-2 text-secondary"
										symbol='<%= (assetListEntry.getType() == AssetListEntryTypeConstants.TYPE_DYNAMIC) ? "bolt" : "list" %>'
									/>
								</div>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								colspan="<%= 2 %>"
							>
								<div class="h5">
									<aui:a href="<%= assetListDisplayContext.getEditURL(assetListEntry) %>">
										<strong><%= HtmlUtil.escape(assetListEntry.getTitle()) %></strong>
									</aui:a>
								</div>

								<div class="h6 text-default">
									<%= assetListDisplayContext.getAssetEntrySubtypeLabel(assetListEntry) %>
								</div>

								<div class="h6 text-default">
									<liferay-ui:message arguments="<%= assetListDisplayContext.getAssetListEntryUsageCount(assetListEntry) %>" key="x-usages" translateArguments="<%= false %>" />
								</div>

								<c:choose>
									<c:when test="<%= assetListEntryVariationsCount > 0 %>">
										<clay:label
											cssClass="mr-auto"
											displayType="info"
											label='<%= LanguageUtil.format(locale, "x-variations", new String[] {String.valueOf(assetListEntryVariationsCount)}) %>'
										/>
									</c:when>
									<c:otherwise>
										<clay:label
											cssClass="mr-auto"
											label="no-variations"
										/>
									</c:otherwise>
								</c:choose>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text>
								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= assetListDisplayContext.getActionDropdownItems(assetListEntry) %>"
									propsTransformer="{AssetEntryListDropdownDefaultPropsTransformer} from asset-list-web"
								/>
							</liferay-ui:search-container-column-text>
						</c:when>
						<c:when test='<%= Objects.equals(assetListDisplayContext.getDisplayStyle(), "icon") %>'>
							<liferay-ui:search-container-column-text>
								<clay:vertical-card
									propsTransformer="{AssetEntryListDropdownDefaultPropsTransformer} from asset-list-web"
									verticalCard="<%= new AssetListEntryVerticalCard(assetListDisplayContext, assetListEntry, renderRequest, searchContainer.getRowChecker()) %>"
								/>
							</liferay-ui:search-container-column-text>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand text-truncate"
								name="name"
							>
								<span class="lfr-portal-tooltip" title="<%= assetListDisplayContext.getAssetListEntryTypeLabel(assetListEntry) %>">
									<clay:icon
										cssClass="mr-2 text-secondary"
										symbol='<%= (assetListEntry.getType() == AssetListEntryTypeConstants.TYPE_DYNAMIC) ? "bolt" : "list" %>'
									/>
								</span>

								<aui:a href="<%= assetListDisplayContext.getEditURL(assetListEntry) %>">
									<%= HtmlUtil.escape(assetListEntry.getTitle()) %>
								</aui:a>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								cssClass="text-truncate"
								name="type"
							>
								<liferay-ui:message key="<%= HtmlUtil.escape(assetListEntry.getTypeLabel()) %>" />
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand text-truncate"
								name="item-type"
								value="<%= assetListDisplayContext.getAssetEntryTypeLabel(assetListEntry) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand text-truncate"
								name="subtype"
								value="<%= assetListDisplayContext.getClassTypeLabel(assetListEntry) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="text-truncate"
								name="variations"
								value="<%= String.valueOf(assetListEntryVariationsCount) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="text-truncate"
								name="usages"
								value="<%= String.valueOf(assetListDisplayContext.getAssetListEntryUsageCount(assetListEntry)) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand text-truncate"
								name="modified"
							>
								<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - createDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text>
								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= assetListDisplayContext.getActionDropdownItems(assetListEntry) %>"
									propsTransformer="{AssetEntryListDropdownDefaultPropsTransformer} from asset-list-web"
								/>
							</liferay-ui:search-container-column-text>
						</c:otherwise>
					</c:choose>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="<%= assetListDisplayContext.getDisplayStyle() %>"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</c:when>
		<c:otherwise>
			<liferay-frontend:empty-result-message
				actionDropdownItems="<%= assetListDisplayContext.isShowAddAssetListEntryAction() ? assetListDisplayContext.getAddAssetListEntryDropdownItems() : null %>"
				buttonCssClass="secondary"
				description="<%= assetListDisplayContext.getEmptyResultMessageDescription() %>"
				elementType='<%= LanguageUtil.get(request, "collections") %>'
				propsTransformer="{EmptyResultMessagePropsTransformer} from asset-list-web"
				propsTransformerServletContext="<%= application %>"
			/>
		</c:otherwise>
	</c:choose>
</aui:form>