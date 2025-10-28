<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:error exception="<%= DuplicateCategoryException.class %>" message="there-is-another-category-with-the-same-name-and-the-same-parent" />

<liferay-ui:success key="categoryAdded" message='<%= GetterUtil.getString(MultiSessionMessages.get(renderRequest, "categoryAdded")) %>' />
<liferay-ui:success key="categoryUpdated" message='<%= GetterUtil.getString(MultiSessionMessages.get(renderRequest, "categoryUpdated")) %>' />

<clay:container-fluid
	cssClass="container-view"
	size="xl"
>
	<liferay-site-navigation:breadcrumb
		breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
	/>

	<clay:row>
		<clay:col
			lg="3"
		>
			<c:choose>
				<c:when test="<%= MapUtil.isNotEmpty(assetCategoriesDisplayContext.getInheritedVocabularies()) || ListUtil.isNotEmpty(assetCategoriesDisplayContext.getVocabularies()) %>">
					<clay:content-row
						cssClass="mb-4"
						verticalAlign="center"
					>
						<clay:content-col
							expand="<%= true %>"
						>
							<strong class="text-uppercase">
								<liferay-ui:message key="vocabularies" />
							</strong>
						</clay:content-col>

						<clay:content-col>
							<ul class="navbar-nav">
								<li>
									<c:if test="<%= assetCategoriesDisplayContext.hasAddVocabularyPermission() %>">

										<%
										PortletURL editVocabularyURL = assetCategoriesDisplayContext.getEditVocabularyURL();
										%>

										<clay:link
											aria-label='<%= LanguageUtil.get(request, "add-new-vocabulary") %>'
											borderless="<%= true %>"
											cssClass="component-action"
											href="<%= editVocabularyURL.toString() %>"
											icon="plus"
											type="button"
										/>
									</c:if>
								</li>
								<li>
									<clay:dropdown-actions
										aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
										dropdownItems="<%= assetCategoriesDisplayContext.getVocabulariesDropdownItems() %>"
										propsTransformer="{ActionsComponentPropsTransformer} from asset-categories-admin-web"
									/>
								</li>
							</ul>
						</clay:content-col>
					</clay:content-row>

					<c:if test="<%= MapUtil.isNotEmpty(assetCategoriesDisplayContext.getInheritedVocabularies()) %>">

						<%
						Map<Long, List<AssetVocabulary>> inheritedVocabularies = assetCategoriesDisplayContext.getInheritedVocabularies();

						for (Map.Entry<Long, List<AssetVocabulary>> entry : inheritedVocabularies.entrySet()) {
							Group group = GroupLocalServiceUtil.getGroup(entry.getKey());
						%>

							<span class="text-truncate"><%= group.getDescriptiveName(locale) %></span>

							<clay:vertical-nav
								verticalNavItems="<%= assetCategoriesDisplayContext.getVerticalNavItemList(entry.getValue()) %>"
							/>

						<%
						}
						%>

					</c:if>

					<c:if test="<%= ListUtil.isNotEmpty(assetCategoriesDisplayContext.getVocabularies()) %>">
						<span class="text-truncate"><%= HtmlUtil.escape(assetCategoriesDisplayContext.getGroupName()) %></span>

						<clay:vertical-nav
							verticalNavItems="<%= assetCategoriesDisplayContext.getVerticalNavItemList(assetCategoriesDisplayContext.getVocabularies()) %>"
						/>
					</c:if>
				</c:when>
				<c:otherwise>
					<p class="text-uppercase">
						<strong><liferay-ui:message key="vocabularies" /></strong>
					</p>

					<liferay-frontend:empty-result-message
						actionDropdownItems="<%= assetCategoriesDisplayContext.getVocabularyActionDropdownItems() %>"
						animationType="<%= EmptyResultMessageKeys.AnimationType.NONE %>"
						componentId='<%= liferayPortletResponse.getNamespace() + "emptyResultMessageComponent" %>'
						description='<%= LanguageUtil.get(request, "vocabularies-are-needed-to-create-categories") %>'
						elementType='<%= LanguageUtil.get(request, "vocabularies") %>'
					/>
				</c:otherwise>
			</c:choose>
		</clay:col>

		<clay:col
			lg="9"
		>

			<%
			AssetVocabulary vocabulary = assetCategoriesDisplayContext.getVocabulary();
			%>

			<c:if test="<%= vocabulary != null %>">
				<clay:sheet
					size="full"
				>
					<h2 class="sheet-title">
						<clay:content-row
							verticalAlign="center"
						>
							<clay:content-col>
								<div class="inline-item">
									<%= HtmlUtil.escape(vocabulary.getTitle(locale)) %>

									<c:if test="<%= vocabulary.getVisibilityType() == AssetVocabularyConstants.VISIBILITY_TYPE_EMPTY %>">
										<clay:label
											cssClass="c-ml-3"
											displayType="warning"
											label='<%= LanguageUtil.get(request, "empty") %>'
										/>
									</c:if>
								</div>
							</clay:content-col>

							<clay:content-col
								cssClass="inline-item-after justify-content-end"
							>

								<%
								AssetVocabularyActionDropdownItemsProvider assetVocabularyActionDropdownItemsProvider = new AssetVocabularyActionDropdownItemsProvider(request, renderResponse);
								%>

								<clay:dropdown-actions
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									cssClass="component-action"
									dropdownItems="<%= assetVocabularyActionDropdownItemsProvider.getActionDropdownItems(vocabulary) %>"
									propsTransformer="{VocabularyActionDropdownPropsTransformer} from asset-categories-admin-web"
								/>
							</clay:content-col>
						</clay:content-row>
					</h2>

					<div class="mb-5">
						<div class="mb-2">
							<span class="mr-1"><liferay-ui:message key="asset-types" />:</span>
							<span class="text-secondary"><%= assetCategoriesDisplayContext.getAssetType(vocabulary) %></span>
						</div>

						<%
						String description = vocabulary.getDescription(locale);
						%>

						<c:if test="<%= Validator.isNotNull(description) %>">
							<div class="mb-2">
								<span class="mr-1"><liferay-ui:message key="description" />:</span>
								<span class="text-break text-secondary"><%= HtmlUtil.escape(description) %></span>
							</div>
						</c:if>
					</div>

					<p class="mb-5 text-secondary">
						<span class="mr-2">
							<liferay-ui:message arguments="<%= assetCategoriesDisplayContext.getMaximumNumberOfCategoriesPerVocabulary() %>" key="the-maximum-number-of-categories-per-vocabulary-is-x" />
						</span>

						<liferay-learn:message
							key="general"
							resource="asset-taglib"
						/>
					</p>

					<c:if test="<%= assetCategoriesDisplayContext.isAssetCategoriesLimitExceeded() %>">
						<clay:alert
							displayType="warning"
							message='<%= LanguageUtil.format(request, "you-have-reached-the-limit-of-x-categories-for-this-vocabulary", assetCategoriesDisplayContext.getMaximumNumberOfCategoriesPerVocabulary()) %>'
						/>
					</c:if>

					<clay:sheet-section>
						<liferay-util:include page="/view_asset_categories.jsp" servletContext="<%= application %>" />
					</clay:sheet-section>
				</clay:sheet>
			</c:if>
		</clay:col>
	</clay:row>
</clay:container-fluid>