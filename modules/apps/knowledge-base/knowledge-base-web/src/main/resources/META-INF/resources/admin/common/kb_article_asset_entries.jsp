<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/common/init.jsp" %>

<%
KBArticle kbArticle = (KBArticle)request.getAttribute(KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE);
%>

<c:if test="<%= showKBArticleAssetEntries %>">

	<%
	long[] groupIds = KBArticleAssetEntriesUtil.getGroupIds(company.getGroup(), kbArticle);

	long[] assetTagIds = KBArticleAssetEntriesUtil.getAssetTagIds(groupIds, kbArticle);
	%>

	<c:if test="<%= assetTagIds.length > 0 %>">

		<%
		long[] classNameIds = {ClassNameLocalServiceUtil.getClassNameId(BlogsEntry.class), ClassNameLocalServiceUtil.getClassNameId(JournalArticle.class), ClassNameLocalServiceUtil.getClassNameId(KBArticle.class), ClassNameLocalServiceUtil.getClassNameId(MBMessage.class), ClassNameLocalServiceUtil.getClassNameId(WikiPage.class)};

		List<AssetEntry> mostPopularAssetEntries = KBArticleAssetEntriesUtil.getAssetEntries(groupIds, classNameIds, assetTagIds, kbArticle.getResourcePrimKey(), 0, 10, "viewCount");
		List<AssetEntry> mostRecentAssetEntries = KBArticleAssetEntriesUtil.getAssetEntries(groupIds, classNameIds, assetTagIds, kbArticle.getResourcePrimKey(), 0, 10, "modifiedDate");
		%>

		<c:if test="<%= !mostPopularAssetEntries.isEmpty() || !mostRecentAssetEntries.isEmpty() %>">
			<div class="kb-article-asset-entries">
				<table class="lfr-table" width="100%">
					<tr>
						<td class="kb-most-recent-column">
							<div class="kb-header">
								<liferay-ui:message key="most-recent" />
							</div>

							<c:if test="<%= mostRecentAssetEntries.isEmpty() %>">
								<liferay-ui:message key="there-are-no-entries" />
							</c:if>

							<%
							for (AssetEntry assetEntry : mostRecentAssetEntries) {
								AssetRendererFactory<?> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(assetEntry.getClassName());

								AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());
							%>

								<div class="kb-title">
									<liferay-ui:icon
										icon="<%= assetRenderer.getIconCssClass() %>"
										label="<%= true %>"
										markupView="lexicon"
										message="<%= HtmlUtil.escape(assetRenderer.getTitle(locale)) %>"
										url="<%= KBArticleAssetEntriesUtil.getURL(request, themeDisplay, assetRendererFactory, assetRenderer) %>"
									/>

									<span class="kb-info"><%= dateFormat.format(assetEntry.getModifiedDate()) %></span>
								</div>

							<%
							}
							%>

						</td>
						<td class="kb-most-popular-column">
							<div class="kb-header">
								<liferay-ui:message key="most-popular" />
							</div>

							<c:if test="<%= mostPopularAssetEntries.isEmpty() %>">
								<liferay-ui:message key="there-are-no-entries" />
							</c:if>

							<%
							for (AssetEntry assetEntry : mostPopularAssetEntries) {
								AssetRendererFactory<?> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(assetEntry.getClassName());

								AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());
							%>

								<div class="kb-title">
									<liferay-ui:icon
										icon="<%= assetRenderer.getIconCssClass() %>"
										label="<%= true %>"
										markupView="lexicon"
										message="<%= HtmlUtil.escape(assetRenderer.getTitle(locale)) %>"
										url="<%= KBArticleAssetEntriesUtil.getURL(request, themeDisplay, assetRendererFactory, assetRenderer) %>"
									/>

									<span class="kb-info">
										<c:choose>
											<c:when test="<%= assetEntry.getViewCount() == 1 %>">
												<%= assetEntry.getViewCount() %> <liferay-ui:message key="view[count]" />
											</c:when>
											<c:otherwise>
												<%= assetEntry.getViewCount() %> <liferay-ui:message key="views" />
											</c:otherwise>
										</c:choose>
									</span>
								</div>

							<%
							}
							%>

						</td>
					</tr>
				</table>
			</div>
		</c:if>
	</c:if>
</c:if>