<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long previewClassNameId = ParamUtil.getLong(request, "previewClassNameId");
long previewClassPK = ParamUtil.getLong(request, "previewClassPK");
int previewType = ParamUtil.getInteger(request, "previewType");

AssetEntryResult assetEntryResult = (AssetEntryResult)request.getAttribute("view.jsp-assetEntryResult");

Group stageableGroup = themeDisplay.getScopeGroup();

if (stageableGroup.isLayout()) {
	stageableGroup = layout.getGroup();
}
%>

<c:if test="<%= Validator.isNotNull(assetEntryResult.getTitle()) %>">
	<p class="h4">
		<%= HtmlUtil.escape(assetEntryResult.getTitle()) %>
	</p>
</c:if>

<div class="table-responsive">
	<table class="table table-autofit table-head-bordered table-hover table-list table-striped">
		<thead>
			<tr>
				<th class="table-cell-expand table-title">
					<liferay-ui:message key="title" />
				</th>

				<%
				for (String metadataField : assetPublisherDisplayContext.getMetadataFields()) {
				%>

					<th class="table-cell-expand">
						<liferay-ui:message key="<%= metadataField %>" />
					</th>

				<%
				}
				%>

				<c:if test="<%= !stageableGroup.hasStagingGroup() %>">
					<th>
						<span class="sr-only"><liferay-ui:message key="item-actions" /></span>
					</th>
				</c:if>
			</tr>
		</thead>

		<tbody>

			<%
			for (AssetEntry assetEntry : assetEntryResult.getAssetEntries()) {
				AssetRendererFactory<?> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(assetEntry.getClassName());

				if (assetRendererFactory == null) {
					continue;
				}

				AssetRenderer<?> assetRenderer = null;

				try {
					if ((previewClassNameId == assetEntry.getClassNameId()) && (previewClassPK == assetEntry.getClassPK())) {
						assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK(), previewType);
					}
					else {
						assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK());
					}
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(exception);
					}
				}

				if ((assetRenderer == null) || (!assetRenderer.isDisplayable() && (previewClassPK <= 0))) {
					continue;
				}

				request.setAttribute("view.jsp-assetEntry", assetEntry);
				request.setAttribute("view.jsp-assetRenderer", assetRenderer);

				AssetAnalyticsAttributesProvider assetAnalyticsAttributesProvider = new AssetAnalyticsAttributesProvider(assetEntry, assetRenderer, locale);
				Map<String, Object> fragmentsEditorData = HashMapBuilder.<String, Object>put(
					"fragments-editor-item-id", PortalUtil.getClassNameId(assetRenderer.getClassName()) + "-" + assetRenderer.getClassPK()
				).put(
					"fragments-editor-item-type", "fragments-editor-mapped-item"
				).build();
				String title = assetRenderer.getTitle(LocaleUtil.fromLanguageId(LanguageUtil.getLanguageId(request)));
				boolean viewMode = Objects.equals(ParamUtil.getString(PortalUtil.getOriginalServletRequest(request), "p_l_mode", Constants.VIEW), Constants.VIEW);
				String viewURL = assetPublisherHelper.getAssetViewURL(liferayPortletRequest, liferayPortletResponse, assetRenderer, assetEntry, assetPublisherDisplayContext.isAssetLinkBehaviorViewInPortlet());
			%>

				<tr class="<%= ((previewClassNameId == assetEntry.getClassNameId()) && (previewClassPK == assetEntry.getClassPK())) ? "table-active" : StringPool.BLANK %>" <%= AUIUtil.buildData(fragmentsEditorData) %>>
					<td class="table-cell-expand table-title" <%= viewMode ? assetAnalyticsAttributesProvider.buildAttributes(AssetAnalyticsAttributesProvider.ACTION_IMPRESSION, AssetAnalyticsAttributesProvider.FIELD_TITLE) : StringPool.BLANK %>>
						<span class="asset-anchor lfr-asset-anchor" id="<%= assetEntry.getEntryId() %>"></span>

						<c:choose>
							<c:when test="<%= Validator.isNotNull(viewURL) %>">
								<a class="text-truncate-inline" href="<%= viewURL %>">
									<span class="text-truncate"><%= HtmlUtil.escape(title) %></span>
								</a>
							</c:when>
							<c:otherwise>
								<span class="text-truncate-inline">
									<span class="text-truncate"><%= HtmlUtil.escape(title) %></span>
								</span>
							</c:otherwise>
						</c:choose>
					</td>

					<%
					for (String metadataField : assetPublisherDisplayContext.getMetadataFields()) {
					%>

						<c:choose>
							<c:when test='<%= Objects.equals(metadataField, "author") %>'>
								<td class="table-cell-expand" <%= viewMode ? assetAnalyticsAttributesProvider.buildAttributes(AssetAnalyticsAttributesProvider.ACTION_IMPRESSION, AssetAnalyticsAttributesProvider.FIELD_AUTHOR) : StringPool.BLANK %>>
									<%= HtmlUtil.escape(PortalUtil.getUserName(assetRenderer.getUserId(), assetRenderer.getUserName())) %>
								</td>
							</c:when>
							<c:when test='<%= Objects.equals(metadataField, "categories") %>'>
								<td class="table-cell-expand">
									<liferay-asset:asset-categories-summary
										className="<%= assetEntry.getClassName() %>"
										classPK="<%= assetEntry.getClassPK() %>"
										displayStyle="simple-category"
										portletURL="<%= renderResponse.createRenderURL() %>"
									/>
								</td>
							</c:when>
							<c:when test='<%= Objects.equals(metadataField, "tags") %>'>
								<td class="table-cell-expand">
									<liferay-asset:asset-tags-summary
										className="<%= assetEntry.getClassName() %>"
										classPK="<%= assetEntry.getClassPK() %>"
										portletURL="<%= renderResponse.createRenderURL() %>"
									/>
								</td>
							</c:when>
							<c:otherwise>

								<%
								String value = null;

								if (Objects.equals(metadataField, "create-date")) {
									value = dateFormat.format(assetEntry.getCreateDate());
								}
								else if (Objects.equals(metadataField, "modified-date")) {
									value = dateFormat.format(assetEntry.getModifiedDate());
								}
								else if (Objects.equals(metadataField, "publish-date")) {
									if (assetEntry.getPublishDate() == null) {
										value = StringPool.BLANK;
									}
									else {
										value = dateFormat.format(assetEntry.getPublishDate());
									}
								}
								else if (Objects.equals(metadataField, "expiration-date")) {
									if (assetEntry.getExpirationDate() == null) {
										value = StringPool.BLANK;
									}
									else {
										value = dateFormat.format(assetEntry.getExpirationDate());
									}
								}
								else if (Objects.equals(metadataField, "priority")) {
									value = String.valueOf(assetEntry.getPriority());
								}
								else if (Objects.equals(metadataField, "view-count")) {
									value = String.valueOf(assetEntry.getViewCount());
								}

								boolean dateField = ArrayUtil.contains(new String[] {"create-date", "modified-date", "publish-date", "expiration-date"}, metadataField);
								%>

								<td class="table-cell-expand-smallest" <%= (viewMode && dateField) ? assetAnalyticsAttributesProvider.buildAttributes(AssetAnalyticsAttributesProvider.ACTION_IMPRESSION, metadataField) : StringPool.BLANK %>>
									<liferay-ui:message key="<%= value %>" />
								</td>
							</c:otherwise>
						</c:choose>

					<%
					}
					%>

					<c:if test="<%= !stageableGroup.hasStagingGroup() %>">
						<td>
							<span class="table-action-link">
								<liferay-util:include page="/asset_actions.jsp" servletContext="<%= application %>" />
							</span>
						</td>
					</c:if>
				</tr>

			<%
			}
			%>

		</tbody>
	</table>
</div>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_asset_publisher_web.view_asset_entries_table_jsp");
%>