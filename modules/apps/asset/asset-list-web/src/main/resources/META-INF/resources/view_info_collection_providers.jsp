<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
InfoCollectionProviderDisplayContext infoCollectionProviderDisplayContext = (InfoCollectionProviderDisplayContext)request.getAttribute(AssetListWebKeys.INFO_COLLECTION_PROVIDER_DISPLAY_CONTEXT);
%>

<clay:navigation-bar
	navigationItems='<%= assetListDisplayContext.getNavigationItems("collection-providers") %>'
/>

<div class="container-fluid container-fluid-max-xl lfr-search-container-wrapper" id="<portlet:namespace />collectionProviders">
	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
	/>

	<liferay-ui:search-container
		id="entries"
		searchContainer="<%= infoCollectionProviderDisplayContext.getSearchContainer() %>"
		var="collectionsSearch"
	>
		<liferay-ui:search-container-row
			className="com.liferay.info.collection.provider.InfoCollectionProvider"
			cssClass="entry"
			modelVar="infoCollectionProvider"
		>
			<liferay-ui:search-container-column-icon
				icon="bolt"
			/>

			<liferay-ui:search-container-column-text
				colspan="<%= 2 %>"
			>
				<div class="list-group-title">
					<%= HtmlUtil.escape(infoCollectionProviderDisplayContext.getTitle(infoCollectionProvider)) %>
				</div>

				<div class="list-group-subtext">
					<liferay-ui:message key="<%= HtmlUtil.escape(infoCollectionProviderDisplayContext.getSubtitle(infoCollectionProvider)) %>" />
				</div>

				<c:choose>
					<c:when test="<%= infoCollectionProvider instanceof BetaInfoCollectionProvider %>">
						<liferay-frontend:feature-indicator
							type="beta"
						/>
					</c:when>
					<c:when test="<%= infoCollectionProvider instanceof DeprecatedInfoCollectionProvider %>">
						<liferay-frontend:feature-indicator
							type="deprecated"
						/>
					</c:when>
				</c:choose>
			</liferay-ui:search-container-column-text>

			<%
			InfoCollectionProviderActionDropdownItems infoCollectionProviderActionDropdownItems = new InfoCollectionProviderActionDropdownItems(infoCollectionProvider, infoCollectionProviderDisplayContext.getTitle(infoCollectionProvider), liferayPortletRequest, liferayPortletResponse);
			%>

			<liferay-ui:search-container-column-text>
				<clay:dropdown-actions
					aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
					dropdownItems="<%= infoCollectionProviderActionDropdownItems.getActionDropdownItems() %>"
					propsTransformer="{InfoCollectionProviderDropdownDefaultPropsTransformer} from asset-list-web"
				/>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="descriptive"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</div>