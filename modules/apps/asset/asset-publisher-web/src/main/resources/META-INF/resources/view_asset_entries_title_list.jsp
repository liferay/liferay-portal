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
%>

<ul class="list-group show-quick-actions-on-hover">
	<c:if test="<%= Validator.isNotNull(assetEntryResult.getTitle()) %>">
		<li class="list-group-header">
			<p class="h3 list-group-header-title"><%= HtmlUtil.escape(assetEntryResult.getTitle()) %></p>
		</li>
	</c:if>

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
		boolean viewMode = Objects.equals(ParamUtil.getString(PortalUtil.getOriginalServletRequest(request), "p_l_mode", Constants.VIEW), Constants.VIEW);
	%>

		<li class="list-group-item list-group-item-flex <%= ((previewClassNameId == assetEntry.getClassNameId()) && (previewClassPK == assetEntry.getClassPK())) ? "active" : StringPool.BLANK %>" <%= AUIUtil.buildData(fragmentsEditorData) %>>
			<c:if test="<%= assetPublisherDisplayContext.isShowAuthor() %>">
				<clay:content-col>
					<span class="inline-item">
						<liferay-user:user-portrait
							userId="<%= assetEntry.getUserId() %>"
						/>
					</span>
				</clay:content-col>
			</c:if>

			<clay:content-col
				expand="<%= true %>"
			>
				<p class="h4 list-group-title text-truncate" <%= viewMode ? assetAnalyticsAttributesProvider.buildAttributes(AssetAnalyticsAttributesProvider.ACTION_IMPRESSION, AssetAnalyticsAttributesProvider.FIELD_TITLE) : StringPool.BLANK %>>
					<span class="asset-anchor lfr-asset-anchor" id="<%= assetEntry.getEntryId() %>"></span>

					<aui:a href="<%= assetPublisherHelper.getAssetViewURL(liferayPortletRequest, liferayPortletResponse, assetRenderer, assetEntry, assetPublisherDisplayContext.isAssetLinkBehaviorViewInPortlet()) %>"> <%= HtmlUtil.escape(assetEntry.getTitle(locale)) %>
					</aui:a>
				</p>

				<%
				StringBundler sb = new StringBundler(13);

				if (assetPublisherDisplayContext.isShowCreateDate() && (assetEntry.getCreateDate() != null)) {
					sb.append(LanguageUtil.get(request, "created"));
					sb.append(StringPool.SPACE);
					sb.append(dateFormat.format(assetEntry.getCreateDate()));
					sb.append(" - ");
				}

				if (assetPublisherDisplayContext.isShowPublishDate() && (assetEntry.getPublishDate() != null)) {
					sb.append(LanguageUtil.get(request, "published"));
					sb.append(StringPool.SPACE);
					sb.append(dateFormat.format(assetEntry.getPublishDate()));
					sb.append(" - ");
				}

				if (assetPublisherDisplayContext.isShowExpirationDate() && (assetEntry.getExpirationDate() != null)) {
					sb.append(LanguageUtil.get(request, "expired"));
					sb.append(StringPool.SPACE);
					sb.append(dateFormat.format(assetEntry.getExpirationDate()));
					sb.append(" - ");
				}

				if (assetPublisherDisplayContext.isShowModifiedDate() && (assetEntry.getModifiedDate() != null)) {
					Date modifiedDate = assetEntry.getModifiedDate();

					String modifiedDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - modifiedDate.getTime(), true);

					sb.append(LanguageUtil.format(request, "modified-x-ago", modifiedDateDescription));
				}
				else if (sb.index() > 1) {
					sb.setIndex(sb.index() - 1);
				}
				%>

				<p class="list-group-subtitle text-truncate" <%= viewMode ? assetAnalyticsAttributesProvider.buildAttributes(AssetAnalyticsAttributesProvider.ACTION_IMPRESSION, "subtitle") : StringPool.BLANK %>>
					<%= sb.toString() %>
				</p>

				<c:if test="<%= assetPublisherDisplayContext.isShowCategories() || assetPublisherDisplayContext.isShowTags() %>">
					<div class="list-group-detail">
						<c:if test="<%= assetPublisherDisplayContext.isShowCategories() %>">
							<liferay-asset:asset-categories-summary
								className="<%= assetEntry.getClassName() %>"
								classPK="<%= assetEntry.getClassPK() %>"
								displayStyle="simple-category"
								portletURL="<%= renderResponse.createRenderURL() %>"
							/>
						</c:if>

						<c:if test="<%= assetPublisherDisplayContext.isShowTags() %>">
							<liferay-asset:asset-tags-summary
								className="<%= assetEntry.getClassName() %>"
								classPK="<%= assetEntry.getClassPK() %>"
								portletURL="<%= renderResponse.createRenderURL() %>"
							/>
						</c:if>
					</div>
				</c:if>
			</clay:content-col>

			<clay:content-col>
				<liferay-util:include page="/asset_actions.jsp" servletContext="<%= application %>" />
			</clay:content-col>
		</li>

	<%
	}
	%>

</ul>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_asset_publisher_web.view_asset_entries_title_list_jsp");
%>