<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String assetVocabularyExternalReferenceCode = null;

if (assetVocabulary != null) {
	assetVocabularyExternalReferenceCode = assetVocabulary.getExternalReferenceCode();
}
%>

<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL" />

<liferay-portlet:renderURL portletConfiguration="<%= true %>" var="configurationRenderURL" />

<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= configurationRenderURL %>" />

	<div class="portlet-configuration-body-content">
		<div class="container-fluid container-fluid-max-xl">
			<div class="sheet">
				<div class="panel-group panel-group-flush">
					<aui:fieldset>
						<div class="display-template">
							<liferay-template:template-selector
								className="<%= CPAssetCategoriesNavigationPortlet.class.getName() %>"
								displayStyle="<%= cpAssetCategoriesNavigationDisplayContext.getDisplayStyle() %>"
								displayStyleGroupKey="<%= cpAssetCategoriesNavigationDisplayContext.getDisplayStyleGroupKey() %>"
								refreshURL="<%= PortalUtil.getCurrentURL(request) %>"
								showEmptyOption="<%= true %>"
							/>
						</div>

						<%
						boolean useRootCategory = cpAssetCategoriesNavigationDisplayContext.useRootCategory();
						%>

						<aui:input id="preferencesUseRootCategory" label="use-root-category" name="preferences--useRootCategory--" type="toggle-switch" value="<%= useRootCategory %>" />

						<%
						String assetVocabularyContainerCssClass = StringPool.BLANK;
						String rootAssetCategoryContainerCssClass = "hide";

						if (useRootCategory) {
							assetVocabularyContainerCssClass += "hide";
							rootAssetCategoryContainerCssClass = StringPool.BLANK;
						}
						%>

						<div class="<%= assetVocabularyContainerCssClass %>" id="<portlet:namespace />assetVocabularyContainer">
							<aui:select label="vocabulary" name="preferences--assetVocabularyExternalReferenceCode--" showEmptyOption="<%= true %>">

								<%
								for (AssetVocabulary curAssetVocabulary : cpAssetCategoriesNavigationDisplayContext.getAssetVocabularies()) {
								%>

									<aui:option label="<%= HtmlUtil.escape(curAssetVocabulary.getTitle(locale)) %>" selected="<%= Objects.equals(curAssetVocabulary.getExternalReferenceCode(), assetVocabularyExternalReferenceCode) %>" value="<%= curAssetVocabulary.getExternalReferenceCode() %>" />

								<%
								}
								%>

							</aui:select>
						</div>

						<div class="<%= rootAssetCategoryContainerCssClass %>" id="<portlet:namespace />rootAssetCategoryContainer">

							<%
							boolean useCategoryFromRequest = cpAssetCategoriesNavigationDisplayContext.useCategoryFromRequest();
							%>

							<aui:input id="preferencesUseCategoryFromRequest" label="use-category-from-request" name="preferences--useCategoryFromRequest--" type="toggle-switch" value="<%= useCategoryFromRequest %>" />

							<%
							String rootAssetCategoryExternalReferenceCodeInputContainerCssClass = StringPool.BLANK;

							if (useCategoryFromRequest) {
								rootAssetCategoryExternalReferenceCodeInputContainerCssClass += "hide";
							}
							%>

							<div class="<%= rootAssetCategoryExternalReferenceCodeInputContainerCssClass %>" id="<portlet:namespace />rootAssetCategoryExternalReferenceCodeInputContainer">
								<aui:input id="preferencesRootAssetCategoryExternalReferenceCode" name="preferences--rootAssetCategoryExternalReferenceCode--" type="hidden" />

								<liferay-asset:asset-categories-selector
									categoryIds="<%= cpAssetCategoriesNavigationDisplayContext.getRootAssetCategoryId() %>"
									hiddenInput="assetCategoriesSelectorCategoryId"
									singleSelect="<%= true %>"
									useDataCategoriesAttribute="<%= true %>"
								/>
							</div>
						</div>
					</aui:fieldset>
				</div>
			</div>
		</div>
	</div>

	<aui:button-row>
		<aui:button cssClass="btn-lg" name="submitButton" onClick="event.preventDefault();" type="submit" />
	</aui:button-row>
</aui:form>

<liferay-frontend:component
	module="{configuration} from commerce-product-asset-categories-navigation-web"
/>