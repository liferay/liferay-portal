<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long categoryId = ParamUtil.getLong(request, "categoryId");

AssetCategory category = AssetCategoryLocalServiceUtil.fetchCategory(categoryId);

int[] categoryPropertiesIndexes = null;

List<AssetCategoryProperty> categoryProperties = Collections.emptyList();

String categoryPropertiesIndexesParam = ParamUtil.getString(request, "categoryPropertiesIndexes");

if (Validator.isNotNull(categoryPropertiesIndexesParam)) {
	categoryProperties = new ArrayList<AssetCategoryProperty>();

	categoryPropertiesIndexes = StringUtil.split(categoryPropertiesIndexesParam, 0);

	for (int categoryPropertiesIndex : categoryPropertiesIndexes) {
		AssetCategoryProperty assetCategoryProperty = AssetCategoryPropertyLocalServiceUtil.createAssetCategoryProperty(0);

		categoryProperties.add(assetCategoryProperty);
	}
}
else {
	if (category != null) {
		categoryProperties = AssetCategoryPropertyServiceUtil.getCategoryProperties(category.getCategoryId());

		categoryPropertiesIndexes = new int[categoryProperties.size()];

		for (int i = 0; i < categoryProperties.size(); i++) {
			categoryPropertiesIndexes[i] = i;
		}
	}

	if (categoryProperties.isEmpty()) {
		categoryProperties = new ArrayList<AssetCategoryProperty>();

		AssetCategoryProperty assetCategoryProperty = AssetCategoryPropertyLocalServiceUtil.createAssetCategoryProperty(0);

		categoryProperties.add(assetCategoryProperty);

		categoryPropertiesIndexes = new int[] {0};
	}

	if (categoryPropertiesIndexes == null) {
		categoryPropertiesIndexes = new int[0];
	}
}

String redirect = ParamUtil.getString(request, "redirect", assetCategoriesDisplayContext.getEditCategoryRedirect());

long vocabularyId = ParamUtil.getLong(request, "vocabularyId");

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(redirect);

renderResponse.setTitle(category.getTitle(locale));
%>

<portlet:actionURL name="/asset_categories_admin/edit_asset_category_properties" var="editPropertiesURL">
	<portlet:param name="mvcPath" value="/edit_asset_category.jsp" />
	<portlet:param name="screenNavigationCategoryKey" value='<%= ParamUtil.getString(request, "screenNavigationCategoryKey") %>' />
	<portlet:param name="vocabularyId" value="<%= String.valueOf(vocabularyId) %>" />
</portlet:actionURL>

<liferay-frontend:edit-form
	action="<%= editPropertiesURL %>"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="categoryId" type="hidden" value="<%= categoryId %>" />

	<liferay-frontend:edit-form-body>
		<liferay-ui:error exception="<%= CategoryPropertyKeyException.class %>" message="please-enter-a-valid-property-key" />
		<liferay-ui:error exception="<%= CategoryPropertyValueException.class %>" message="please-enter-a-valid-property-value" />
		<liferay-ui:error exception="<%= DuplicateCategoryPropertyException.class %>" message="please-enter-a-unique-property-key" />

		<liferay-frontend:fieldset>
			<div id="<portlet:namespace />categoryPropertiesId">
				<p class="text-muted">
					<liferay-ui:message key="properties-are-a-way-to-add-more-detailed-information-to-a-specific-category" />
				</p>

				<%
				for (int i = 0; i < categoryPropertiesIndexes.length; i++) {
					int categoryPropertiesIndex = categoryPropertiesIndexes[i];
				%>

					<aui:model-context bean="<%= categoryProperties.get(i) %>" model="<%= AssetCategoryProperty.class %>" />

					<div class="lfr-form-row lfr-form-row-inline">
						<div class="row-fields">
							<aui:input fieldParam='<%= "key" + categoryPropertiesIndex %>' id='<%= "key" + categoryPropertiesIndex %>' name="key" />

							<aui:input fieldParam='<%= "value" + categoryPropertiesIndex %>' id='<%= "value" + categoryPropertiesIndex %>' name="value" />
						</div>
					</div>

				<%
				}
				%>

			</div>

			<aui:input name="categoryPropertiesIndexes" type="hidden" value="<%= StringUtil.merge(categoryPropertiesIndexes) %>" />
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<aui:script type="module">
	import {autoFields} from '<%= FrontendESMUtil.buildURL(themeDisplay, "frontend-js-web", "auto_fields") %>';

	autoFields({
		contentBox: '#<portlet:namespace />categoryPropertiesId',
		fieldIndexes: '<portlet:namespace />categoryPropertiesIndexes',
		namespace: '<portlet:namespace />',
	});
</aui:script>