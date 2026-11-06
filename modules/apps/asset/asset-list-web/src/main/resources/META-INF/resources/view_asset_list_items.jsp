<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AssetListItemsDisplayContext assetListItemsDisplayContext = (AssetListItemsDisplayContext)request.getAttribute(AssetListWebKeys.ASSET_LIST_ITEMS_DISPLAY_CONTEXT);
ListItemsActionDropdownItems listItemsActionDropdownItems = (ListItemsActionDropdownItems)request.getAttribute(AssetListWebKeys.LIST_ITEMS_ACTION_DROPDOWN_ITEMS);
%>

<clay:container-fluid
	cssClass="container-view"
>
	<liferay-ui:search-container
		id="assetEntries"
		searchContainer="<%= assetListItemsDisplayContext.getAssetListContentSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.asset.kernel.model.AssetEntry"
			keyPropertyName="entryId"
			modelVar="assetEntry"
		>

			<%
			AssetRenderer<?> assetRenderer = assetEntry.getAssetRenderer();

			AssetRendererFactory<?> assetRendererFactory = assetRenderer.getAssetRendererFactory();
			%>

			<liferay-ui:search-container-column-text
				name="title"
			>

				<%
				String displayPageURL = StringPool.BLANK;

				if (assetListItemsDisplayContext.isShowActions()) {
					displayPageURL = listItemsActionDropdownItems.getViewDisplayPageURL(AssetEntry.class.getName(), assetEntry);
				}
				%>

				<c:choose>
					<c:when test="<%= Validator.isNull(displayPageURL) %>">
						<%= HtmlUtil.escape(assetRenderer.getTitle(locale)) %>
					</c:when>
					<c:otherwise>
						<aui:a href="<%= displayPageURL %>" target="_top">
							<%= HtmlUtil.escape(assetRenderer.getTitle(locale)) %>
						</aui:a>
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				name="type"
				value="<%= assetRendererFactory.getTypeName(locale) %>"
			/>

			<liferay-ui:search-container-column-text
				name="author"
				value="<%= assetEntry.getUserName() %>"
			/>

			<liferay-ui:search-container-column-date
				name="modified-date"
				value="<%= assetEntry.getModifiedDate() %>"
			/>

			<liferay-ui:search-container-column-date
				name="create-date"
				value="<%= assetEntry.getCreateDate() %>"
			/>

			<c:if test="<%= assetListItemsDisplayContext.isShowActions() %>">
				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= listItemsActionDropdownItems.getActionDropdownItems(AssetEntry.class.getName(), assetEntry) %>"
						propsTransformer="{ListItemsDropdownPropsTransformer} from asset-list-web"
					/>
				</liferay-ui:search-container-column-text>
			</c:if>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</clay:container-fluid>

<liferay-frontend:component
	module="{TopLinkEventHandler} from asset-list-web"
/>