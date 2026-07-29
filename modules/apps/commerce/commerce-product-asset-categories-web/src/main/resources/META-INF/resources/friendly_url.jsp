<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AssetCategory category = (AssetCategory)request.getAttribute(CPAssetCategoriesWebKeys.ASSET_CATEGORY);
String commerceFriendlyURLBase = (String)request.getAttribute(CPAssetCategoriesWebKeys.COMMERCE_FRIENDLY_URL_BASE);
String commerceFriendlyURLSeparator = (String)request.getAttribute(CPAssetCategoriesWebKeys.COMMERCE_FRIENDLY_URL_SEPARATOR);
String siteFriendlyURLBase = (String)request.getAttribute(CPAssetCategoriesWebKeys.SITE_FRIENDLY_URL_BASE);
String siteFriendlyURLSeparator = (String)request.getAttribute(CPAssetCategoriesWebKeys.SITE_FRIENDLY_URL_SEPARATOR);
String titleMapAsXML = (String)request.getAttribute(CPAssetCategoriesWebKeys.TITLE_MAP_AS_XML);
String urlTitle = (String)request.getAttribute(CPAssetCategoriesWebKeys.URL_TITLE);
long vocabularyId = ParamUtil.getLong(request, "vocabularyId");

long parentCategoryId = BeanParamUtil.getLong(category, request, "parentCategoryId");

PortletURL categoryRedirectURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCPath(
	"/view_asset_categories.jsp"
).buildPortletURL();

if (parentCategoryId > 0) {
	categoryRedirectURL.setParameter("categoryId", String.valueOf(parentCategoryId));
}

if (vocabularyId > 0) {
	categoryRedirectURL.setParameter("vocabularyId", String.valueOf(vocabularyId));
}

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(categoryRedirectURL.toString());

renderResponse.setTitle(category.getTitle(locale));
%>

<portlet:actionURL name="/commerce_product_asset_categories/edit_asset_category_friendly_url" var="editCategoryURL">
</portlet:actionURL>

<liferay-frontend:edit-form
	action="<%= editCategoryURL %>"
	name="fm"
>
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="categoryId" type="hidden" value="<%= category.getCategoryId() %>" />

	<liferay-frontend:edit-form-body>
		<liferay-frontend:fieldset>

			<%
			String friendlyURLHelpMessage = LanguageUtil.format(request, "this-text-is-shared-between-sites-and-commerce.-only-the-prefix-changes-x-for-sites-and-x-for-commerce", new String[] {HtmlUtil.escape(siteFriendlyURLSeparator), HtmlUtil.escape(commerceFriendlyURLSeparator)}, false);
			%>

			<label for="<portlet:namespace />urlTitleMapAsXML"><liferay-ui:message key="friendly-url" /><span aria-label="<%= friendlyURLHelpMessage %>" class="c-ml-1 lfr-portal-tooltip" tabindex="0" title="<%= friendlyURLHelpMessage %>"><clay:icon symbol="question-circle-full" /></span></label>

			<div class="c-mb-2 text-3 text-secondary">
				<div><%= HtmlUtil.escape(siteFriendlyURLBase) %><strong class="text-dark" id="<portlet:namespace />siteURLTitle"><%= HtmlUtil.escape(urlTitle) %></strong></div>
				<div><%= HtmlUtil.escape(commerceFriendlyURLBase) %><strong class="text-dark" id="<portlet:namespace />commerceURLTitle"><%= HtmlUtil.escape(urlTitle) %></strong></div>
			</div>

			<liferay-ui:input-localized
				defaultLanguageId="<%= LocaleUtil.toLanguageId(themeDisplay.getSiteDefaultLocale()) %>"
				name="urlTitleMapAsXML"
				xml="<%= HttpComponentsUtil.decodeURL(titleMapAsXML) %>"
			/>
		</liferay-frontend:fieldset>
	</liferay-frontend:edit-form-body>

	<liferay-frontend:edit-form-footer>
		<liferay-frontend:edit-form-buttons
			redirect="<%= categoryRedirectURL.toString() %>"
		/>
	</liferay-frontend:edit-form-footer>
</liferay-frontend:edit-form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"portletNamespace", liferayPortletResponse.getNamespace()
		).build()
	%>'
	module="{CategoryCPFriendlyURL} from commerce-product-asset-categories-web"
/>