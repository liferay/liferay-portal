<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ taglib uri="http://liferay.com/tld/aui" prefix="aui" %><%@
taglib uri="http://liferay.com/tld/frontend" prefix="liferay-frontend" %><%@
taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %><%@
taglib uri="http://liferay.com/tld/react" prefix="react" %><%@
taglib uri="http://liferay.com/tld/template" prefix="liferay-template" %>

<%@ page import="com.liferay.asset.kernel.service.AssetVocabularyLocalService" %><%@
page import="com.liferay.learn.LearnMessageUtil" %><%@
page import="com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil" %><%@
page import="com.liferay.portal.kernel.service.GroupLocalService" %><%@
page import="com.liferay.portal.kernel.util.Constants" %><%@
page import="com.liferay.portal.kernel.util.HashMapBuilder" %><%@
page import="com.liferay.portal.kernel.util.StringUtil" %><%@
page import="com.liferay.portal.kernel.util.WebKeys" %><%@
page import="com.liferay.portal.search.web.internal.category.facet.configuration.CategoryFacetPortletInstanceConfiguration" %><%@
page import="com.liferay.portal.search.web.internal.category.facet.display.context.CategoryFacetConfigurationDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.category.facet.portlet.CategoryFacetPortlet" %><%@
page import="com.liferay.portal.search.web.internal.category.facet.portlet.CategoryFacetPortletPreferences" %><%@
page import="com.liferay.portal.search.web.internal.category.facet.portlet.CategoryFacetPortletPreferencesImpl" %><%@
page import="com.liferay.portal.search.web.internal.facet.display.context.AssetCategoriesSearchFacetDisplayContext" %><%@
page import="com.liferay.portal.search.web.internal.util.PortletPreferencesJspUtil" %>

<portlet:defineObjects />

<%
AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = new AssetCategoriesSearchFacetDisplayContext(request);

CategoryFacetPortletInstanceConfiguration categoryFacetPortletInstanceConfiguration = assetCategoriesSearchFacetDisplayContext.getCategoryFacetPortletInstanceConfiguration();

AssetVocabularyLocalService assetVocabularyLocalService = (AssetVocabularyLocalService)request.getAttribute(AssetVocabularyLocalService.class.getName());
GroupLocalService groupLocalService = (GroupLocalService)request.getAttribute(GroupLocalService.class.getName());

CategoryFacetPortletPreferences categoryFacetPortletPreferences = new CategoryFacetPortletPreferencesImpl(assetVocabularyLocalService, groupLocalService, portletPreferences);

CategoryFacetConfigurationDisplayContext categoryFacetConfigurationDisplayContext = (CategoryFacetConfigurationDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<liferay-frontend:edit-form
	action="<%= configurationActionURL %>"
	method="post"
	name="fm"
>
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="display-settings"
		>
			<div class="display-template">
				<liferay-template:template-selector
					className="<%= CategoryFacetPortlet.class.getName() %>"
					displayStyle="<%= categoryFacetPortletInstanceConfiguration.displayStyle() %>"
					displayStyleGroupId="<%= assetCategoriesSearchFacetDisplayContext.getDisplayStyleGroupId() %>"
					refreshURL="<%= configurationRenderURL %>"
					showEmptyOption="<%= true %>"
				/>
			</div>
		</liferay-frontend:fieldset>

		<liferay-frontend:fieldset
			collapsible="<%= true %>"
			label="advanced-configuration"
		>
			<aui:input label="category-parameter-name" name="<%= PortletPreferencesJspUtil.getInputName(CategoryFacetPortletPreferences.PREFERENCE_KEY_PARAMETER_NAME) %>" value="<%= categoryFacetPortletPreferences.getParameterName() %>" />

			<aui:input label="max-terms" name="<%= PortletPreferencesJspUtil.getInputName(CategoryFacetPortletPreferences.PREFERENCE_KEY_MAX_TERMS) %>" value="<%= categoryFacetPortletPreferences.getMaxTerms() %>" />

			<aui:input label="frequency-threshold" name="<%= PortletPreferencesJspUtil.getInputName(CategoryFacetPortletPreferences.PREFERENCE_KEY_FREQUENCY_THRESHOLD) %>" value="<%= categoryFacetPortletPreferences.getFrequencyThreshold() %>" />

			<aui:select label="order-terms-by" name="<%= PortletPreferencesJspUtil.getInputName(CategoryFacetPortletPreferences.PREFERENCE_KEY_ORDER) %>" value="<%= categoryFacetPortletPreferences.getOrder() %>">
				<aui:option label="term-frequency-descending" value="count:desc" />
				<aui:option label="term-frequency-ascending" value="count:asc" />
				<aui:option label="term-value-ascending" value="key:asc" />
				<aui:option label="term-value-descending" value="key:desc" />
			</aui:select>

			<aui:input label="display-frequencies" name="<%= PortletPreferencesJspUtil.getInputName(CategoryFacetPortletPreferences.PREFERENCE_KEY_FREQUENCIES_VISIBLE) %>" type="checkbox" value="<%= categoryFacetPortletPreferences.isFrequenciesVisible() %>" />

			<div id="<portlet:namespace />selectVocabularies">
				<react:component
					module="{SelectVocabularies} from portal-search-web"
					props='<%=
						HashMapBuilder.<String, Object>put(
							"groups", categoryFacetConfigurationDisplayContext.getGroupsJSONArray()
						).put(
							"initialSelectedVocabularyExternalReferenceCodes", StringUtil.merge(categoryFacetPortletPreferences.getGroupVocabularyExternalReferenceCodes())
						).put(
							"learnResources", LearnMessageUtil.getReactDataJSONObject("portal-search-web")
						).put(
							"namespace", liferayPortletResponse.getNamespace()
						).put(
							"vocabularyExternalReferenceCodesInputName", PortletPreferencesJspUtil.getInputName(CategoryFacetPortletPreferences.PREFERENCE_GROUP_VOCABULARY_EXTERNAL_REFERENCE_CODES)
						).build()
					%>'
				/>
			</div>
		</liferay-frontend:fieldset>

		<c:if test='<%= FeatureFlagManagerUtil.isEnabled("LPD-71164") %>'>
			<liferay-frontend:fieldset
				collapsible="<%= true %>"
				label="seo-configuration"
			>
				<aui:input helpMessage="enable-web-crawler-indexing-help" label="enable-web-crawler-indexing" name="<%= PortletPreferencesJspUtil.getInputName(CategoryFacetPortletPreferences.PREFERENCE_KEY_WEB_CRAWLER_INDEXING_ENABLED) %>" type="checkbox" value="<%= categoryFacetPortletPreferences.isWebCrawlerIndexingEnabled() %>" />
			</liferay-frontend:fieldset>
		</c:if>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons />
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>