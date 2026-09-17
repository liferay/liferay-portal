<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/facets/init.jsp" %>

<%
FolderSearchFacetDisplayContextBuilder folderSearchFacetDisplayContextBuilder = new FolderSearchFacetDisplayContextBuilder(renderRequest);

folderSearchFacetDisplayContextBuilder.setFacet(facet);
folderSearchFacetDisplayContextBuilder.setFolderTitleLookup(new FolderTitleLookupImpl(folderId -> new FolderSearcher(folderId), request));
folderSearchFacetDisplayContextBuilder.setFrequenciesVisible(dataJSONObject.getBoolean("showAssetCount", true));
folderSearchFacetDisplayContextBuilder.setFrequencyThreshold(dataJSONObject.getInt("frequencyThreshold"));
folderSearchFacetDisplayContextBuilder.setMaxTerms(dataJSONObject.getInt("maxTerms", 10));
folderSearchFacetDisplayContextBuilder.setParameterName(facet.getFieldId());
folderSearchFacetDisplayContextBuilder.setParameterValue(fieldParam);

FolderSearchFacetDisplayContext folderSearchFacetDisplayContext = folderSearchFacetDisplayContextBuilder.build();
%>

<c:choose>
	<c:when test="<%= folderSearchFacetDisplayContext.isRenderNothing() %>">
		<c:if test="<%= folderSearchFacetDisplayContext.getParameterValue() != null %>">
			<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(folderSearchFacetDisplayContext.getParameterName()) %>" type="hidden" value="<%= folderSearchFacetDisplayContext.getParameterValue() %>" />
		</c:if>
	</c:when>
	<c:otherwise>
		<div class="panel panel-secondary">
			<div class="panel-heading">
				<div class="panel-title">
					<liferay-ui:message key="folders" />
				</div>
			</div>

			<div class="panel-body">
				<div class="<%= cssClass %>" data-facetFieldName="<%= HtmlUtil.escapeAttribute(facet.getFieldId()) %>" id="<%= randomNamespace %>facet">
					<aui:input autocomplete="off" name="<%= HtmlUtil.escapeAttribute(folderSearchFacetDisplayContext.getParameterName()) %>" type="hidden" value="<%= folderSearchFacetDisplayContext.getParameterValue() %>" />

					<ul class="folders list-unstyled">
						<li class="default facet-value">
							<a class="<%= folderSearchFacetDisplayContext.isNothingSelected() ? "facet-term-selected" : "facet-term-unselected" %>" data-value="" href="javascript:void(0);"><liferay-ui:message key="<%= HtmlUtil.escape(facetConfiguration.getLabel()) %>" /></a>
						</li>

						<%
						java.util.List<BucketDisplayContext> bucketDisplayContexts = folderSearchFacetDisplayContext.getBucketDisplayContexts();

						for (BucketDisplayContext bucketDisplayContext : bucketDisplayContexts) {
						%>

							<li class="facet-value">
								<a class="<%= bucketDisplayContext.isSelected() ? "facet-term-selected" : "facet-term-unselected" %>" data-value="<%= bucketDisplayContext.getFilterValue() %>" href="javascript:void(0);">
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