<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SelectAssetVocabularyDisplayContext selectAssetVocabularyDisplayContext = (SelectAssetVocabularyDisplayContext)request.getAttribute(AssetCategoryItemSelectorWebKeys.SELECT_ASSET_VOCABULARY_DISPLAY_CONTEXT);
%>

<c:choose>
	<c:when test="<%= selectAssetVocabularyDisplayContext.getAssetCategoryTreeNodeId() >= 0 %>">
		<liferay-util:include page="/select_asset_category_tree_node.jsp" servletContext="<%= application %>" />
	</c:when>
	<c:otherwise>
		<div class="container-fluid container-fluid-max-xl p-4">
			<clay:alert
				displayType="info"
				message="select-the-vocabulary-or-category-to-be-displayed"
			/>

			<div class="align-items-center d-flex justify-content-between">
				<liferay-site-navigation:breadcrumb
					breadcrumbEntries="<%= selectAssetVocabularyDisplayContext.getBreadcrumbEntries() %>"
				/>

				<clay:button
					cssClass="asset-category-tree-node-selector"
					disabled="<%= true %>"
					displayType="primary"
					label="select-this-level"
					small="<%= true %>"
				/>
			</div>

			<liferay-ui:search-container
				cssClass="table-hover"
				searchContainer="<%= selectAssetVocabularyDisplayContext.getAssetVocabularySearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.asset.kernel.model.AssetVocabulary"
					keyProperty="vocabularyId"
					modelVar="assetVocabulary"
				>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="name"
					>
						<clay:sticker
							cssClass="bg-light mr-3"
							displayType="dark"
							icon="vocabulary"
						/>

						<a href="<%= selectAssetVocabularyDisplayContext.getAssetVocabularyURL(assetVocabulary.getVocabularyId()) %>">
							<b><%= HtmlUtil.escape(selectAssetVocabularyDisplayContext.getAssetVocabularyTitle(assetVocabulary)) %></b>
						</a>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-minw-300"
						name="number-of-items"
					>
						<%= assetVocabulary.getCategoriesCount() %>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					searchResultCssClass="table table-autofit"
				/>
			</liferay-ui:search-container>
		</div>
	</c:otherwise>
</c:choose>