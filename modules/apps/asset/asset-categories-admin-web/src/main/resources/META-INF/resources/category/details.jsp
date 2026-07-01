<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = PortalUtil.escapeRedirect(ParamUtil.getString(request, "redirect", assetCategoriesDisplayContext.getEditCategoryRedirect()));

long categoryId = ParamUtil.getLong(request, "categoryId");

AssetCategory category = AssetCategoryLocalServiceUtil.fetchCategory(categoryId);

long groupId = ParamUtil.getLong(request, "groupId", scopeGroupId);

long parentCategoryId = BeanParamUtil.getLong(category, request, "parentCategoryId");

long vocabularyId = ParamUtil.getLong(request, "vocabularyId");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

String title = LanguageUtil.get(request, "add-new-category");

if (category != null) {
	title = category.getTitle(locale);
}
else if (parentCategoryId > 0) {
	title = LanguageUtil.get(request, "add-new-subcategory");
}

renderResponse.setTitle(title);
%>

<portlet:actionURL name="/asset_categories_admin/edit_asset_category" var="editCategoryURL">
	<portlet:param name="mvcPath" value="/edit_asset_category.jsp" />
	<portlet:param name="groupId" value="<%= String.valueOf(groupId) %>" />
	<portlet:param name="vocabularyId" value="<%= String.valueOf(vocabularyId) %>" />
</portlet:actionURL>

<liferay-frontend:edit-form
	action="<%= editCategoryURL %>"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="categoryId" type="hidden" value="<%= categoryId %>" />

	<liferay-frontend:edit-form-body>
		<liferay-ui:error exception="<%= AssetCategoryLimitException.class %>" message="the-maximum-number-of-categories-for-the-vocabulary-has-been-exceeded" />
		<liferay-ui:error exception="<%= AssetCategoryNameException.class %>" message="please-enter-a-valid-name" />
		<liferay-ui:error exception="<%= DuplicateCategoryException.class %>" message="please-enter-a-unique-name" />
		<liferay-ui:error exception="<%= DuplicateCategoryExternalReferenceCodeException.class %>" message="please-enter-a-unique-external-reference-code" />

		<aui:model-context bean="<%= category %>" model="<%= AssetCategory.class %>" />

		<c:if test="<%= (assetCategoriesDisplayContext.getAssetEntryAssetCategoryRelsCountByClassNameId(categoryId) > 0) && (category != null) %>">
			<clay:alert
				displayType="info"
				message="changes-made-to-the-category-will-impact-the-associated-friendly-url"
			/>
		</c:if>

		<liferay-frontend:fieldset
			collapsed="<%= false %>"
			collapsible="<%= true %>"
			label="details"
		>
			<aui:input disabled="<%= assetCategoriesDisplayContext.isSystemCategory(category) %>" label="name" localized="<%= true %>" name="title" placeholder="name" required="<%= true %>" type="text" value="<%= (category == null) ? StringPool.BLANK : assetCategoriesDisplayContext.getCategoryLocalizationXML(category) %>">
				<aui:validator name="maxLength"><%= ModelHintsUtil.getMaxLength(AssetCategory.class.getName(), "name") %></aui:validator>
			</aui:input>

			<aui:input disabled="<%= assetCategoriesDisplayContext.isSystemCategory(category) %>" label="external-reference-code" name="externalReferenceCode" placeholder="external-reference-code" />

			<div>
				<label for="<portlet:namespace />description"><liferay-ui:message key="description" /></label>

				<liferay-ui:input-localized
					availableLocales="<%= assetCategoriesDisplayContext.getAvailableLocales() %>"
					cssClass="form-control"
					defaultLanguageId="<%= assetCategoriesDisplayContext.getDefaultLanguageId(category) %>"
					disabled="<%= assetCategoriesDisplayContext.isSystemCategory(category) %>"
					editorName="ckeditor"
					formName="fm"
					name="description"
					selectedLanguageId="<%= assetCategoriesDisplayContext.getSelectedLanguageId(category) %>"
					type="editor"
					xml="<%= (category == null) ? StringPool.BLANK : category.getDescription() %>"
				/>
			</div>

			<c:choose>
				<c:when test="<%= assetCategoriesDisplayContext.isFlattenedNavigationAllowed() %>">

					<%
					AssetCategory parentCategory = AssetCategoryLocalServiceUtil.fetchCategory(parentCategoryId);
					%>

					<aui:field-wrapper label="parent-category">
						<div>
							<div id="<portlet:namespace />parentCategoryContainer">
								<div class="field-content">
									<div class="form-group" id="namespace_assetCategoriesSelector_<%= vocabularyId %>">
										<div class="input-group">
											<div class="input-group-item">
												<div class="form-control form-control-tag-group input-group">
													<div class="input-group-item">
														<c:if test="<%= parentCategory != null %>">
															<clay:label
																dismissible="<%= true %>"
																label="<%= parentCategory.getTitle(locale) %>"
															/>

															<input name="parentCategoryId" type="hidden" value="<%= parentCategoryId %>" />
														</c:if>

														<input class="form-control-inset" type="text" value="" />
													</div>
												</div>
											</div>

											<div class="input-group-item input-group-item-shrink">
												<clay:button
													displayType="secondary"
													label="select"
												/>
											</div>
										</div>
									</div>
								</div>
							</div>

							<%
							List<Map<String, Object>> selectedCategories = new ArrayList<>();

							if (parentCategory != null) {
								selectedCategories.add(
									HashMapBuilder.<String, Object>put(
										"label", parentCategory.getTitle(locale)
									).put(
										"value", parentCategory.getCategoryId()
									).build());
							}
							%>

							<react:component
								module="{AssetCategoriesSelectorTag} from asset-categories-admin-web"
								props='<%=
									HashMapBuilder.<String, Object>put(
										"categoryIds", Collections.singletonList(parentCategoryId)
									).put(
										"groupIds", Collections.singletonList(scopeGroupId)
									).put(
										"namespace", liferayPortletResponse.getNamespace()
									).put(
										"portletURL", assetCategoriesDisplayContext.getCategorySelectorURL()
									).put(
										"selectedCategories", selectedCategories
									).put(
										"vocabularyIds", Collections.singletonList(vocabularyId)
									).build()
								%>'
							/>
						</div>
					</aui:field-wrapper>
				</c:when>
				<c:otherwise>
					<aui:input name="parentCategoryId" type="hidden" value="<%= parentCategoryId %>" />
				</c:otherwise>
			</c:choose>
		</liferay-frontend:fieldset>

		<c:if test="<%= assetCategoriesDisplayContext.isShowSelectAssetDisplayPage() %>">
			<liferay-frontend:fieldset
				collapsed="<%= true %>"
				collapsible="<%= true %>"
				label="display-page"
			>
				<liferay-asset:select-asset-display-page
					classNameId="<%= PortalUtil.getClassNameId(AssetCategory.class) %>"
					classPK="<%= (category != null) ? category.getCategoryId() : 0 %>"
					classTypeId="<%= 0 %>"
					groupId="<%= scopeGroupId %>"
					parentClassPK="<%= parentCategoryId %>"
					showViewInContextLink="<%= true %>"
				/>
			</liferay-frontend:fieldset>
		</c:if>

		<c:if test="<%= (category == null) && !assetCategoriesDisplayContext.isItemSelector() %>">
			<liferay-frontend:fieldset
				collapsed="<%= true %>"
				collapsible="<%= true %>"
				label="permissions"
			>
				<liferay-ui:input-permissions
					modelName="<%= AssetCategory.class.getName() %>"
				/>
			</liferay-frontend:fieldset>
		</c:if>
	</liferay-frontend:edit-form-body>

	<c:choose>
		<c:when test="<%= !assetCategoriesDisplayContext.isItemSelector() %>">
			<liferay-frontend:edit-form-footer>
				<clay:button
					disabled="<%= assetCategoriesDisplayContext.isSaveButtonDisabled() %>"
					label="save"
					type="submit"
				/>

				<clay:button
					additionalProps='<%=
						HashMapBuilder.<String, Object>put(
							"redirect", assetCategoriesDisplayContext.getAddCategoryRedirect()
						).build()
					%>'
					className="mr-3"
					disabled="<%= assetCategoriesDisplayContext.isSaveAndAddNewButtonDisabled() %>"
					displayType="secondary"
					label="save-and-add-a-new-one"
					propsTransformer="{SaveAndAddNewPropsTransformer} from asset-categories-admin-web"
				/>

				<clay:link
					borderless="<%= false %>"
					displayType="secondary"
					href="<%= redirect %>"
					label="cancel"
					type="button"
				/>
			</liferay-frontend:edit-form-footer>
		</c:when>
		<c:otherwise>
			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"currentURL", currentURL
					).put(
						"redirect", redirect
					).build()
				%>'
				module="{ItemSelectorAddCategory} from asset-categories-admin-web"
				servletContext="<%= application %>"
			/>
		</c:otherwise>
	</c:choose>
</liferay-frontend:edit-form>