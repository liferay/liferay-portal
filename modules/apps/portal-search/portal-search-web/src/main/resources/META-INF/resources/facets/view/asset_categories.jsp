<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/facets/init.jsp" %>

<%
AssetCategoriesSearchFacetDisplayContextBuilder assetCategoriesSearchFacetDisplayContextBuilder = new AssetCategoriesSearchFacetDisplayContextBuilder(GroupLocalServiceUtil.getService(), renderRequest);

assetCategoriesSearchFacetDisplayContextBuilder.setAssetCategoryLocalService(AssetCategoryLocalServiceUtil.getService());
assetCategoriesSearchFacetDisplayContextBuilder.setAssetCategoryPermissionChecker(new AssetCategoryPermissionCheckerImpl(themeDisplay.getPermissionChecker()));
assetCategoriesSearchFacetDisplayContextBuilder.setAssetVocabularyLocalService(AssetVocabularyLocalServiceUtil.getService());
assetCategoriesSearchFacetDisplayContextBuilder.setDisplayStyle(dataJSONObject.getString("displayStyle", "cloud"));
assetCategoriesSearchFacetDisplayContextBuilder.setFacet(facet);
assetCategoriesSearchFacetDisplayContextBuilder.setFrequenciesVisible(dataJSONObject.getBoolean("showAssetCount", true));
assetCategoriesSearchFacetDisplayContextBuilder.setFrequencyThreshold(dataJSONObject.getInt("frequencyThreshold"));
assetCategoriesSearchFacetDisplayContextBuilder.setLocale(locale);
assetCategoriesSearchFacetDisplayContextBuilder.setMaxTerms(dataJSONObject.getInt("maxTerms", 10));
assetCategoriesSearchFacetDisplayContextBuilder.setParameterName(facet.getFieldId());
assetCategoriesSearchFacetDisplayContextBuilder.setParameterValue(fieldParam);
assetCategoriesSearchFacetDisplayContextBuilder.setPortal(PortalUtil.getPortal());

AssetCategoriesSearchFacetDisplayContext assetCategoriesSearchFacetDisplayContext = assetCategoriesSearchFacetDisplayContextBuilder.build();
%>

<c:choose>
	<c:when test="<%= assetCategoriesSearchFacetDisplayContext.isRenderNothing() %>">
		<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetCategoriesSearchFacetDisplayContext.getParameterName()) %>" type="hidden" value="<%= assetCategoriesSearchFacetDisplayContext.getParameterValue() %>" />
	</c:when>
	<c:otherwise>
		<div class="panel panel-secondary">
			<div class="panel-heading">
				<div class="panel-title">
					<liferay-ui:message key="categories" />
				</div>
			</div>

			<div class="panel-body">
				<div class="asset-tags <%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(assetCategoriesSearchFacetDisplayContext.getParameterName()) %>" id="<%= randomNamespace %>facet">
					<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(assetCategoriesSearchFacetDisplayContext.getParameterName()) %>" type="hidden" value="<%= assetCategoriesSearchFacetDisplayContext.getParameterValue() %>" />

					<ul class="<%= assetCategoriesSearchFacetDisplayContext.isCloud() ? "tag-cloud" : "tag-list" %> list-unstyled">
						<li class="default facet-value">
							<a class="<%= assetCategoriesSearchFacetDisplayContext.isNothingSelected() ? "facet-term-selected" : "facet-term-unselected" %>" data-value="" href="javascript:void(0);"><liferay-ui:message key="<%= HtmlUtil.escape(facetConfiguration.getLabel()) %>" /></a>
						</li>

						<%
						for (BucketDisplayContext bucketDisplayContext : assetCategoriesSearchFacetDisplayContext.getBucketDisplayContexts()) {
						%>

							<li class="facet-value tag-popularity-<%= bucketDisplayContext.getPopularity() %>">
								<a class="<%= bucketDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>" data-value="<%= HtmlUtil.escapeAttribute(bucketDisplayContext.getFilterValue()) %>" href="javascript:void(0);">
									<%= HtmlUtil.escape(bucketDisplayContext.getBucketText()) %>

									<c:if test="<%= bucketDisplayContext.isFrequencyVisible() %>">
										<span class="frequency">(<%= bucketDisplayContext.getFrequency() %>)</span>
									</c:if>
								</a>
							</li>

						<%
						}
						%>

					</ul>
				</div>
			</div>
		</div>
	</c:otherwise>
</c:choose>