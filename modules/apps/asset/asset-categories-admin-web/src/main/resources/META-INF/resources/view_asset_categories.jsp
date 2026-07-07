<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AssetCategoriesManagementToolbarDisplayContext assetCategoriesManagementToolbarDisplayContext = new AssetCategoriesManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, assetCategoriesDisplayContext);
AssetCategoryActionDropdownItemsProvider assetCategoryActionDropdownItemsProvider = new AssetCategoryActionDropdownItemsProvider(assetCategoriesDisplayContext, request, renderRequest, renderResponse);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= assetCategoriesManagementToolbarDisplayContext %>"
	propsTransformer="{AssetCategoriesManagementToolbarPropsTransformer} from asset-categories-admin-web"
/>

<portlet:actionURL name="/asset_categories_admin/delete_asset_category" var="deleteCategoryURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:actionURL>

<aui:form action="<%= deleteCategoryURL %>" cssClass="container-fluid container-fluid-max-xl" name="fm">

	<%
	List<BreadcrumbEntry> breadcrumbEntries = AssetCategoryUtil.getAssetCategoriesBreadcrumbEntries(assetCategoriesDisplayContext.getVocabulary(), assetCategoriesDisplayContext.getCategory(), request, renderResponse);
	%>

	<c:if test="<%= ListUtil.isNotEmpty(breadcrumbEntries) %>">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= breadcrumbEntries %>"
		/>
	</c:if>

	<liferay-ui:search-container
		id="assetCategories"
		searchContainer="<%= assetCategoriesDisplayContext.getCategoriesSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.asset.kernel.model.AssetCategory"
			keyProperty="categoryId"
			modelVar="curCategory"
		>
			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcPath" value="/view.jsp" />
				<portlet:param name="categoryId" value="<%= String.valueOf(curCategory.getCategoryId()) %>" />
				<portlet:param name="vocabularyId" value="<%= String.valueOf(curCategory.getVocabularyId()) %>" />
			</portlet:renderURL>

			<%
			int subcategoriesCount = AssetCategoryLocalServiceUtil.getChildCategoriesCount(curCategory.getCategoryId());

			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", assetCategoriesManagementToolbarDisplayContext.getAvailableActions(curCategory)
				).build());

			int fullCategoriesCount = AssetEntryAssetCategoryRelLocalServiceUtil.getAssetEntryAssetCategoryRelsCountByAssetCategoryId(curCategory.getCategoryId());
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(assetCategoriesDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-icon
						icon="categories"
						toggleRowChecker="<%= true %>"
					/>

					<liferay-ui:search-container-column-text
						colspan="<%= 2 %>"
					>
						<span class="text-default">
							<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - curCategory.getCreateDate().getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
						</span>

						<h2 class="h5">
							<c:choose>
								<c:when test="<%= assetCategoriesDisplayContext.isSystemCategory(curCategory) %>">
									<clay:link
										aria-describedby='<%= liferayPortletResponse.getNamespace() + "systemCategory" + curCategory.getCategoryId() %>'
										href="<%= rowURL.toString() %>"
										label="<%= HtmlUtil.escape(curCategory.getTitle(locale)) %>"
									/>
								</c:when>
								<c:otherwise>
									<clay:link
										href="<%= rowURL.toString() %>"
										label="<%= HtmlUtil.escape(curCategory.getTitle(locale)) %>"
									/>
								</c:otherwise>
							</c:choose>

							<c:if test="<%= assetCategoriesDisplayContext.isSystemCategory(curCategory) %>">
								<span class="lfr-portal-tooltip" id="<portlet:namespace />systemCategory<%= curCategory.getCategoryId() %>" title="<%= LanguageUtil.get(request, "system-category") %>">
									<clay:icon
										symbol="lock"
									/>

									<span class="sr-only"><liferay-ui:message key="system-category" /></span>
								</span>
							</c:if>
						</h2>

						<span class="text-default">
							<%= HtmlUtil.stripHtml(curCategory.getDescription(locale)) %>
						</span>
						<span class="text-default">
							<liferay-ui:message arguments="<%= subcategoriesCount %>" key="x-subcategories" />
						</span>
						<span class="text-default">
							<strong><liferay-ui:message key="usages" /></strong>: <span><%= String.valueOf(fullCategoriesCount) %></span>
						</span>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= assetCategoryActionDropdownItemsProvider.getActionDropdownItems(curCategory) %>"
							propsTransformer="{CategoryActionDropdownPropsTransformer} from asset-categories-admin-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(assetCategoriesDisplayContext.getDisplayStyle(), "list") %>'>
					<c:choose>
						<c:when test="<%= assetCategoriesDisplayContext.isFlattenedNavigationAllowed() %>">
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200 table-title"
								name="category"
							>
								<%= HtmlUtil.escape(curCategory.getTitle(locale)) %>

								<c:if test="<%= assetCategoriesDisplayContext.isSystemCategory(curCategory) %>">
									<span class="lfr-portal-tooltip" title="<%= LanguageUtil.get(request, "system-category") %>">
										<clay:icon
											symbol="lock"
										/>

										<span class="sr-only"><liferay-ui:message key="system-category" /></span>
									</span>
								</c:if>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200"
								name="path"
							>
								<%= HtmlUtil.escape(curCategory.getPath(locale, true)) %> > <strong><%= HtmlUtil.escape(curCategory.getTitle(locale)) %></strong>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smaller table-column-text-center"
								name="usages"
								value="<%= String.valueOf(fullCategoriesCount) %>"
							/>
						</c:when>
						<c:otherwise>
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200 table-title"
								name="category"
							>
								<c:choose>
									<c:when test="<%= assetCategoriesDisplayContext.isSystemCategory(curCategory) %>">
										<clay:link
											aria-describedby='<%= liferayPortletResponse.getNamespace() + "systemCategory" + curCategory.getCategoryId() %>'
											href="<%= rowURL.toString() %>"
											label="<%= HtmlUtil.escape(curCategory.getTitle(locale)) %>"
										/>
									</c:when>
									<c:otherwise>
										<clay:link
											href="<%= rowURL.toString() %>"
											label="<%= HtmlUtil.escape(curCategory.getTitle(locale)) %>"
										/>
									</c:otherwise>
								</c:choose>

								<c:if test="<%= assetCategoriesDisplayContext.isSystemCategory(curCategory) %>">
									<span class="lfr-portal-tooltip" id="<portlet:namespace />systemCategory<%= curCategory.getCategoryId() %>" title="<%= LanguageUtil.get(request, "system-category") %>">
										<clay:icon
											symbol="lock"
										/>

										<span class="sr-only"><liferay-ui:message key="system-category" /></span>
									</span>
								</c:if>
							</liferay-ui:search-container-column-text>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200"
								name="description"
								value="<%= HtmlUtil.stripHtml(curCategory.getDescription(locale)) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smaller table-column-text-center"
								name="usages"
								value="<%= String.valueOf(fullCategoriesCount) %>"
							/>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smaller table-column-text-center"
								name="subcategories"
								value="<%= String.valueOf(subcategoriesCount) %>"
							/>
						</c:otherwise>
					</c:choose>

					<liferay-ui:search-container-column-date
						cssClass="table-cell-ws-nowrap"
						name="create-date"
						property="createDate"
					/>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= assetCategoryActionDropdownItemsProvider.getActionDropdownItems(curCategory) %>"
							propsTransformer="{CategoryActionDropdownPropsTransformer} from asset-categories-admin-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= assetCategoriesDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<portlet:actionURL name="/asset_categories_admin/move_asset_category" var="moveCategoryURL">
	<portlet:param name="redirect" value="<%= currentURL %>" />
	<portlet:param name="mvcPath" value="/view.jsp" />
</portlet:actionURL>

<aui:form action="<%= moveCategoryURL %>" name="moveCategoryFm">
	<aui:input name="categoryId" type="hidden" />
	<aui:input name="parentCategoryId" type="hidden" />
	<aui:input name="vocabularyId" type="hidden" />
</aui:form>