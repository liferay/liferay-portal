<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

if (Validator.isNull(redirect)) {
	redirect = ParamUtil.getString(PortalUtil.getOriginalServletRequest(request), "redirect");
}

redirect = PortalUtil.escapeRedirect(redirect);

boolean showBackURL = GetterUtil.getBoolean(request.getAttribute("view.jsp-showBackURL"));

if (Validator.isNull(redirect)) {
	redirect = PortletURLBuilder.createRenderURL(
		renderResponse
	).setMVCPath(
		"/view.jsp"
	).buildString();
}

AssetEntry assetEntry = (AssetEntry)request.getAttribute("view.jsp-assetEntry");
AssetRendererFactory<?> assetRendererFactory = (AssetRendererFactory<?>)request.getAttribute("view.jsp-assetRendererFactory");
AssetRenderer<?> assetRenderer = (AssetRenderer<?>)request.getAttribute("view.jsp-assetRenderer");

long previewClassNameId = ParamUtil.getLong(request, "previewClassNameId");
long previewClassPK = ParamUtil.getLong(request, "previewClassPK");

boolean print = GetterUtil.getBoolean(request.getAttribute("view.jsp-print"));
boolean viewSingleAsset = ParamUtil.getBoolean(request, "viewSingleAsset", true);

assetPublisherDisplayContext.setLayoutAssetEntry(assetEntry);

assetEntry = assetPublisherDisplayContext.incrementViewCounter(assetEntry);

String title = assetRenderer.getTitle(LocaleUtil.fromLanguageId(LanguageUtil.getLanguageId(request)));

PortletURL viewFullContentURL = assetPublisherHelper.getBaseAssetViewURL(liferayPortletRequest, liferayPortletResponse, assetRenderer, assetEntry);

if (print) {
	viewFullContentURL.setParameter("viewMode", Constants.PRINT);
}

String viewInContextURL = assetRenderer.getURLViewInContext(liferayPortletRequest, liferayPortletResponse, HttpComponentsUtil.setParameter(viewFullContentURL.toString(), "redirect", currentURL));

Map<String, Object> fragmentsEditorData = HashMapBuilder.<String, Object>put(
	"fragments-editor-item-id", PortalUtil.getClassNameId(assetRenderer.getClassName()) + "-" + assetRenderer.getClassPK()
).put(
	"fragments-editor-item-type", "fragments-editor-mapped-item"
).build();
%>

<div class="asset-full-content clearfix mb-5 <%= assetPublisherDisplayContext.isDefaultAssetPublisher() ? "default-asset-publisher" : StringPool.BLANK %> <%= assetPublisherDisplayContext.isShowAssetTitle() ? "show-asset-title" : "no-title" %> <%= ((previewClassNameId == assetEntry.getClassNameId()) && (previewClassPK == assetEntry.getClassPK())) ? "p-1 preview-asset-entry" : StringPool.BLANK %>" <%= AUIUtil.buildData(fragmentsEditorData) %>>

	<%
	String fullContentRedirect = themeDisplay.getURLCurrent();

	int assetDisplayPageType = AssetDisplayPageConstants.TYPE_NONE;

	AssetDisplayPageEntry assetDisplayPageEntry = AssetDisplayPageEntryLocalServiceUtil.fetchAssetDisplayPageEntry(assetEntry.getGroupId(), assetEntry.getClassNameId(), assetEntry.getClassPK());

	if (assetDisplayPageEntry != null) {
		assetDisplayPageType = assetDisplayPageEntry.getType();
	}

	if (WorkflowDefinitionLinkLocalServiceUtil.hasWorkflowDefinitionLink(assetEntry.getCompanyId(), assetEntry.getGroupId(), assetEntry.getClassName()) && (assetDisplayPageType != AssetDisplayPageConstants.TYPE_SPECIFIC)) {
		fullContentRedirect = redirect;
	}

	request.setAttribute("view.jsp-fullContentRedirect", fullContentRedirect);
	%>

	<liferay-util:buffer
		var="assetActions"
	>
		<liferay-util:include page="/asset_actions.jsp" servletContext="<%= application %>" />
	</liferay-util:buffer>

	<c:if test="<%= (showBackURL && Validator.isNotNull(redirect)) || assetPublisherDisplayContext.isShowAssetTitle() || (!print && Validator.isNotNull(assetActions)) %>">
		<div class="align-items-center d-flex mb-2">
			<p class="component-title h4">
				<c:if test="<%= showBackURL && Validator.isNotNull(redirect) %>">
					<clay:link
						aria-label='<%= LanguageUtil.get(request, "back") %>'
						cssClass="header-back-to lfr-portal-tooltip"
						href="<%= redirect %>"
						icon="angle-left"
						title='<%= LanguageUtil.get(request, "back") %>'
					/>
				</c:if>

				<c:if test="<%= assetPublisherDisplayContext.isShowAssetTitle() %>">
					<span class="asset-title d-inline">
						<%= HtmlUtil.escape(title) %>
					</span>
				</c:if>
			</p>

			<c:if test="<%= !print %>">
				<c:if test="<%= Validator.isNotNull(assetActions) %>">
					<div class="d-inline-flex">
						<%= assetActions %>
					</div>
				</c:if>
			</c:if>
		</div>
	</c:if>

	<span class="asset-anchor lfr-asset-anchor" id="<%= assetEntry.getEntryId() %>"></span>

	<c:if test="<%= assetPublisherDisplayContext.isShowAuthor() || (assetPublisherDisplayContext.isShowCreateDate() && (assetEntry.getCreateDate() != null)) || (assetPublisherDisplayContext.isShowPublishDate() && (assetEntry.getPublishDate() != null)) || (assetPublisherDisplayContext.isShowExpirationDate() && (assetEntry.getExpirationDate() != null)) || (assetPublisherDisplayContext.isShowModifiedDate() && (assetEntry.getModifiedDate() != null)) || assetPublisherDisplayContext.isShowViewCount() %>">
		<clay:content-row
			cssClass="mb-4 metadata-author"
		>
			<c:if test="<%= assetPublisherDisplayContext.isShowAuthor() %>">
				<clay:content-col
					cssClass="asset-avatar inline-item-before mr-3 pt-1"
				>
					<liferay-user:user-portrait
						size="lg"
						userId="<%= assetRenderer.getUserId() %>"
					/>
				</clay:content-col>
			</c:if>

			<clay:content-col
				expand="<%= true %>"
			>
				<c:if test="<%= assetPublisherDisplayContext.isShowAuthor() %>">
					<div class="text-truncate-inline">
						<span class="text-truncate user-info"><strong><%= HtmlUtil.escape(AssetRendererUtil.getAssetRendererUserFullName(assetRenderer, request)) %></strong></span>
					</div>
				</c:if>

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

				<div class="asset-user-info text-secondary">
					<span class="date-info"><%= sb.toString() %></span>
				</div>

				<c:if test="<%= assetPublisherDisplayContext.isShowViewCount() %>">
					<div class="asset-view-count-info text-secondary">
						<span class="view-count-info"><%= assetEntry.getViewCount() %> <liferay-ui:message key='<%= (assetEntry.getViewCount() == 1) ? "view" : "views" %>' /></span>
					</div>
				</c:if>
			</clay:content-col>
		</clay:content-row>
	</c:if>

	<div class="asset-content mb-3">
		<liferay-asset:asset-display
			assetEntry="<%= assetEntry %>"
			assetRenderer="<%= assetRenderer %>"
			assetRendererFactory="<%= assetRendererFactory %>"
			showExtraInfo="<%= assetPublisherDisplayContext.isShowExtraInfo() %>"
		/>
	</div>

	<c:if test="<%= assetPublisherDisplayContext.isShowCategories() %>">
		<div class="asset-categories mb-3">
			<liferay-asset:asset-categories-summary
				className="<%= assetEntry.getClassName() %>"
				classPK="<%= assetEntry.getClassPK() %>"
				displayStyle="simple-category"
				portletURL="<%= renderResponse.createRenderURL() %>"
			/>
		</div>
	</c:if>

	<c:if test="<%= assetPublisherDisplayContext.isShowTags() %>">
		<div class="asset-tags mb-3">
			<liferay-asset:asset-tags-summary
				className="<%= assetEntry.getClassName() %>"
				classPK="<%= assetEntry.getClassPK() %>"
				portletURL="<%= renderResponse.createRenderURL() %>"
			/>
		</div>
	</c:if>

	<c:if test="<%= assetPublisherDisplayContext.isShowPriority() %>">
		<div class="asset-priority mb-4 text-secondary">
			<liferay-ui:message key="priority" />: <%= assetEntry.getPriority() %>
		</div>
	</c:if>

	<c:if test="<%= assetPublisherDisplayContext.isEnableRelatedAssets() %>">

		<%
		PortletURL assetLingsURL = PortletURLBuilder.createRenderURL(
			renderResponse
		).setMVCPath(
			"/view_content.jsp"
		).buildPortletURL();

		if (print) {
			assetLingsURL.setParameter("viewMode", Constants.PRINT);
		}
		%>

		<div class="asset-links mb-4">
			<liferay-asset:asset-links
				assetEntryId="<%= assetEntry.getEntryId() %>"
				portletURL="<%= assetLingsURL %>"
				viewInContext="<%= assetPublisherDisplayContext.isAssetLinkBehaviorViewInPortlet() %>"
			/>
		</div>
	</c:if>

	<%
	boolean showContextLink = false;

	if (viewSingleAsset) {
		showContextLink = assetPublisherDisplayContext.isShowContextLink(assetRenderer.getGroupId(), assetRendererFactory.getPortletId()) && !print && assetEntry.isVisible();
	}
	else {
		showContextLink = assetPublisherDisplayContext.isShowContextLink() && !print && assetEntry.isVisible();
	}

	boolean showRatings = assetPublisherDisplayContext.isEnableRatings() && assetRenderer.isRatable();
	%>

	<c:if test="<%= showContextLink || showRatings || assetPublisherDisplayContext.isEnableFlags() || assetPublisherDisplayContext.isEnablePrint() || Validator.isNotNull(assetPublisherDisplayContext.getSocialBookmarksTypes()) %>">
		<hr class="separator" />

		<clay:content-row
			cssClass="asset-details"
			floatElements=""
			verticalAlign="center"
		>
			<c:if test="<%= showContextLink %>">
				<clay:content-col
					cssClass="asset-more mr-3"
				>
					<a href="<%= viewInContextURL %>"><liferay-ui:message key="<%= assetRenderer.getViewInContextMessage() %>" /> &raquo;</a>
				</clay:content-col>
			</c:if>

			<c:if test="<%= showRatings %>">
				<clay:content-col
					cssClass="asset-ratings mr-3"
				>
					<liferay-ratings:ratings
						className="<%= assetEntry.getClassName() %>"
						classPK="<%= assetEntry.getClassPK() %>"
					/>
				</clay:content-col>
			</c:if>

			<c:if test="<%= assetPublisherDisplayContext.isEnableFlags() %>">

				<%
				TrashHandler trashHandler = TrashHandlerRegistryUtil.getTrashHandler(assetRenderer.getClassName());

				boolean inTrash = trashHandler.isInTrash(assetEntry.getClassPK());
				%>

				<clay:content-col
					cssClass="asset-flag mr-3"
				>
					<liferay-flags:flags
						className="<%= assetEntry.getClassName() %>"
						classPK="<%= assetEntry.getClassPK() %>"
						contentTitle="<%= title %>"
						enabled="<%= !inTrash %>"
						label="<%= false %>"
						message='<%= inTrash ? "flags-are-disabled-because-this-entry-is-in-the-recycle-bin" : null %>'
						reportedUserId="<%= assetRenderer.getUserId() %>"
					/>
				</clay:content-col>
			</c:if>

			<c:if test="<%= assetPublisherDisplayContext.isEnablePrint() %>">
				<clay:content-col
					cssClass="component-subtitle mr-3 print-action"
				>

					<%
					String label = LanguageUtil.format(request, "print-x", HtmlUtil.escape(title));
					%>

					<c:choose>
						<c:when test="<%= print %>">
							<clay:button
								aria-label="<%= label %>"
								borderless="<%= true %>"
								displayType="secondary"
								icon="print"
								onClick="javascript:print();"
								small="<%= true %>"
								type="button"
							/>

							<aui:script>
								print();
							</aui:script>
						</c:when>
						<c:otherwise>

							<%
							String printPageURL = PortletURLBuilder.createRenderURL(
								renderResponse
							).setMVCPath(
								"/view_content.jsp"
							).setParameter(
								"assetEntryId", assetEntry.getEntryId()
							).setParameter(
								"languageId", LanguageUtil.getLanguageId(request)
							).setParameter(
								"type", assetRendererFactory.getType()
							).setParameter(
								"viewMode", Constants.PRINT
							).setWindowState(
								LiferayWindowState.POP_UP
							).buildString();
							%>

							<clay:button
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"printPageURL", printPageURL
									).build()
								%>'
								aria-label="<%= label %>"
								borderless="<%= true %>"
								displayType="secondary"
								icon="print"
								propsTransformer="{printPageButtonPropsTransformer} from asset-publisher-web"
								small="<%= true %>"
								type="button"
							/>
						</c:otherwise>
					</c:choose>
				</clay:content-col>
			</c:if>

			<c:if test="<%= Validator.isNotNull(assetPublisherDisplayContext.getSocialBookmarksTypes()) %>">
				<clay:content-col>
					<liferay-social-bookmarks:bookmarks
						className="<%= assetEntry.getClassName() %>"
						classPK="<%= assetEntry.getClassPK() %>"
						displayStyle="<%= assetPublisherDisplayContext.getSocialBookmarksDisplayStyle() %>"
						target="_blank"
						title="<%= title %>"
						types="<%= assetPublisherDisplayContext.getSocialBookmarksTypes() %>"
						url="<%= assetPublisherHelper.getAssetSocialURL(liferayPortletRequest, liferayPortletResponse, assetEntry) %>"
					/>
				</clay:content-col>
			</c:if>
		</clay:content-row>
	</c:if>

	<%
	boolean showConversions = assetPublisherDisplayContext.isEnableConversions() && assetRenderer.isConvertible() && !print;
	boolean showLocalization = assetPublisherDisplayContext.isShowAvailableLocales() && assetRenderer.isLocalizable() && !print;
	%>

	<c:if test="<%= showConversions || showLocalization %>">
		<hr class="separator" />

		<clay:content-row
			cssClass="asset-details"
			floatElements=""
			verticalAlign="center"
		>
			<c:if test="<%= showLocalization %>">

				<%
				String languageId = LanguageUtil.getLanguageId(request);

				String[] availableLanguageIds = assetRenderer.getAvailableLanguageIds();

				if (ArrayUtil.isNotEmpty(availableLanguageIds) && !ArrayUtil.contains(availableLanguageIds, languageId)) {
					languageId = assetRenderer.getDefaultLanguageId();
				}
				%>

				<c:if test="<%= availableLanguageIds.length > 1 %>">
					<div class="autofit-col locale-actions mr-3">
						<liferay-site-navigation:language
							formAction="<%= viewFullContentURL.toString() %>"
							languageId="<%= languageId %>"
							languageIds="<%= availableLanguageIds %>"
						/>
					</div>
				</c:if>
			</c:if>

			<c:if test="<%= showConversions %>">

				<%
				PortletURL exportAssetURL = PortletURLBuilder.create(
					assetRenderer.getURLExport(liferayPortletRequest, liferayPortletResponse)
				).setPortletResource(
					portletDisplay.getId()
				).setParameter(
					"plid", themeDisplay.getPlid()
				).setWindowState(
					LiferayWindowState.EXCLUSIVE
				).buildPortletURL();

				for (String extension : assetPublisherDisplayContext.getExtensions(assetRenderer)) {
					exportAssetURL.setParameter("targetExtension", extension);

					Map<String, Object> data = HashMapBuilder.<String, Object>put(
						"resource-href", exportAssetURL.toString()
					).build();
				%>

					<clay:content-col
						cssClass="component-subtitle export-action"
					>
						<aui:a cssClass="btn btn-outline-borderless btn-outline-secondary btn-sm" data="<%= data %>" href="<%= exportAssetURL.toString() %>" label='<%= LanguageUtil.format(request, "x-convert-x-to-x", new Object[] {"hide-accessible", title, StringUtil.toUpperCase(HtmlUtil.escape(extension))}, false) %>' />
					</clay:content-col>

				<%
				}
				%>

			</c:if>
		</clay:content-row>
	</c:if>

	<c:if test="<%= assetPublisherDisplayContext.isEnableComments() && assetRenderer.isCommentable() %>">
		<clay:col
			cssClass="mt-4"
			md="12"
		>
			<liferay-comment:discussion
				className="<%= assetEntry.getClassName() %>"
				classPK="<%= assetEntry.getClassPK() %>"
				formName='<%= "fm" + assetEntry.getClassPK() %>'
				ratingsEnabled="<%= assetPublisherDisplayContext.isEnableCommentRatings() %>"
				redirect="<%= currentURL %>"
				userId="<%= assetRenderer.getUserId() %>"
			/>
		</clay:col>
	</c:if>
</div>