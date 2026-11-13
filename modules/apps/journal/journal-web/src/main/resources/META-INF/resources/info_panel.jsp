<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<JournalFolder> folders = (List<JournalFolder>)request.getAttribute(JournalWebKeys.JOURNAL_FOLDERS);
List<JournalArticle> articles = (List<JournalArticle>)request.getAttribute(JournalWebKeys.JOURNAL_ARTICLES);

if (ListUtil.isEmpty(folders) && ListUtil.isEmpty(articles)) {
	long folderId = GetterUtil.getLong((String)request.getAttribute("view.jsp-folderId"), ParamUtil.getLong(request, "folderId"));

	folders = new ArrayList<JournalFolder>();

	if (folderId != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID) {
		folders.add(JournalFolderLocalServiceUtil.fetchFolder(folderId));
	}
	else {
		folders.add(null);
	}
}

Map<String, Object> componentContext = journalDisplayContext.getComponentContext();
%>

<c:choose>
	<c:when test="<%= ListUtil.isEmpty(articles) && ListUtil.isNotEmpty(folders) && (folders.size() == 1) %>">

		<%
		JournalFolder folder = folders.get(0);

		request.setAttribute("info_panel.jsp-folder", folder);
		%>

		<div class="sidebar-header">
			<clay:content-row
				cssClass="sidebar-section"
			>
				<clay:content-col
					expand="<%= true %>"
				>
					<h1 class="component-title"><%= (folder != null) ? HtmlUtil.escape(folder.getName()) : LanguageUtil.get(request, "home") %></h1>

					<h2 class="c-mt-3 text-3 text-secondary text-weight-normal">
						<liferay-ui:message key="folder" />
					</h2>
				</clay:content-col>

				<clay:content-col>
					<ul class="autofit-padded-no-gutters autofit-row">
						<li class="autofit-col">
							<liferay-util:include page="/subscribe.jsp" servletContext="<%= application %>" />
						</li>
						<li class="autofit-col">
							<clay:dropdown-actions
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"trashEnabled", componentContext.get("trashEnabled")
									).build()
								%>'
								aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
								dropdownItems="<%= journalDisplayContext.getFolderInfoPanelDropdownItems(folder) %>"
								propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
							/>
						</li>
					</ul>
				</clay:content-col>
			</clay:content-row>
		</div>

		<clay:tabs
			tabsItems="<%= journalDisplayContext.getInfoPanelTabsItems(false) %>"
		>
			<clay:tabs-panel>
				<div class="sidebar-body">
					<p class="sidebar-dt"><liferay-ui:message key="num-of-items" /></p>

					<%
					long folderId = JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;

					if (folder != null) {
						folderId = folder.getFolderId();
					}
					%>

					<p class="sidebar-dd">
						<%= JournalFolderServiceUtil.getFoldersAndArticlesCount(scopeGroupId, folderId, journalDisplayContext.getStatus()) %>
					</p>

					<c:if test="<%= folder != null %>">
						<p class="sidebar-dt"><liferay-ui:message key="created" /></p>

						<p class="sidebar-dd">
							<%= HtmlUtil.escape(folder.getUserName()) %>
						</p>
					</c:if>
				</div>
			</clay:tabs-panel>
		</clay:tabs>
	</c:when>
	<c:when test="<%= ListUtil.isEmpty(folders) && ListUtil.isNotEmpty(articles) && (articles.size() == 1) %>">

		<%
		JournalArticle article = articles.get(0);

		request.setAttribute("info_panel.jsp-entry", article);
		%>

		<div class="sidebar-header">
			<clay:content-row
				cssClass="sidebar-section"
			>
				<clay:content-col
					expand="<%= true %>"
				>
					<h1 class="component-title"><%= HtmlUtil.escape(article.getTitle(locale)) %></h1>

					<%
					DDMStructure ddmStructure = article.getDDMStructure();
					%>

					<h2 class="c-mt-3 text-3 text-secondary text-weight-normal">
						<%= HtmlUtil.escape(ddmStructure.getName(locale)) %>
					</h2>
				</clay:content-col>

				<clay:content-col>
					<ul class="autofit-padded-no-gutters autofit-row">
						<li class="autofit-col">
							<liferay-util:include page="/subscribe.jsp" servletContext="<%= application %>" />
						</li>
						<li class="autofit-col">
							<clay:dropdown-actions
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"trashEnabled", componentContext.get("trashEnabled")
									).build()
								%>'
								aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
								dropdownItems="<%= journalDisplayContext.getArticleInfoPanelDropdownItems(article) %>"
								propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
							/>
						</li>
					</ul>
				</clay:content-col>
			</clay:content-row>

			<div class="d-flex">
				<div>
					<clay:label
						displayType="info"
						label='<%= LanguageUtil.format(request, "version-x", article.getVersion()) %>'
					/>
				</div>

				<liferay-portal-workflow:status
					showStatusLabel="<%= false %>"
					status="<%= article.getStatus() %>"
				/>
			</div>
		</div>

		<%
		JournalVersionTabDisplayContext journalVersionTabDisplayContext = new JournalVersionTabDisplayContext(assetDisplayPageFriendlyURLProvider, article, liferayPortletRequest, liferayPortletResponse, trashHelper);
		%>

		<clay:tabs
			tabsItems="<%= journalDisplayContext.getInfoPanelTabsItems(true) %>"
		>
			<clay:tabs-panel>
				<p class="sidebar-dt"><liferay-ui:message key="id" /></p>

				<p class="sidebar-dd">
					<%= HtmlUtil.escape(article.getArticleId()) %>
				</p>

				<p class="sidebar-dt"><liferay-ui:message key="title" /></p>

				<p class="sidebar-dd">
					<%= HtmlUtil.escape(article.getTitle(locale)) %>
				</p>

				<%
				DDMTemplate ddmTemplate = DDMTemplateLocalServiceUtil.fetchTemplate(scopeGroupId, PortalUtil.getClassNameId(DDMStructure.class), article.getDDMTemplateKey(), true);
				%>

				<p class="sidebar-dt"><liferay-ui:message key="template" /></p>

				<p class="sidebar-dd">
					<c:choose>
						<c:when test="<%= ddmTemplate != null %>">
							<%= HtmlUtil.escape(ddmTemplate.getName(locale)) %>
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="no-template" />
						</c:otherwise>
					</c:choose>
				</p>

				<div class="lfr-asset-tags sidebar-dd">
					<liferay-asset:asset-tags-summary
						className="<%= JournalArticle.class.getName() %>"
						classPK="<%= JournalArticleAssetRenderer.getClassPK(article) %>"
						message="tags"
					/>
				</div>

				<p class="sidebar-dt"><liferay-ui:message key="original-author" /></p>

				<p class="sidebar-dd">
					<%= HtmlUtil.escape(journalDisplayContext.getOriginalAuthorUserName(article)) %>
				</p>

				<p class="sidebar-dt"><liferay-ui:message key="priority" /></p>

				<%
				AssetEntry assetEntry = AssetEntryLocalServiceUtil.fetchEntry(JournalArticle.class.getName(), JournalArticleAssetRenderer.getClassPK(article));
				%>

				<p class="sidebar-dd">
					<%= assetEntry.getPriority() %>
				</p>

				<c:if test="<%= article.getDisplayDate() != null %>">
					<p class="sidebar-dt"><liferay-ui:message key="display-date" /></p>

					<p class="sidebar-dd">
						<%= dateTimeFormat.format(article.getDisplayDate()) %>
					</p>
				</c:if>

				<p class="sidebar-dt"><liferay-ui:message key="expiration-date" /></p>

				<%
				Date expirationDate = article.getExpirationDate();
				%>

				<p class="sidebar-dd">
					<c:choose>
						<c:when test="<%= expirationDate != null %>">
							<%= dateTimeFormat.format(expirationDate) %>
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="never-expire" />
						</c:otherwise>
					</c:choose>
				</p>

				<p class="sidebar-dt"><liferay-ui:message key="review-date" /></p>

				<%
				Date reviewDate = article.getReviewDate();
				%>

				<p class="sidebar-dd">
					<c:choose>
						<c:when test="<%= reviewDate != null %>">
							<%= dateTimeFormat.format(reviewDate) %>
						</c:when>
						<c:otherwise>
							<liferay-ui:message key="never-review" />
						</c:otherwise>
					</c:choose>
				</p>
			</clay:tabs-panel>

			<clay:tabs-panel>
				<ul class="list-group sidebar-list-group">

					<%
					for (JournalArticle articleVersion : JournalArticleServiceUtil.getArticlesByArticleId(article.getGroupId(), article.getArticleId(), 0, 10, ArticleVersionComparator.getInstance(false))) {
					%>

						<li class="list-group-item list-group-item-flex p-0">
							<clay:content-col
								cssClass="autofit-col-expand"
							>
								<div class="list-group-title">
									<span class="text-3"><liferay-ui:message key="version" /> <%= articleVersion.getVersion() %></span>
								</div>

								<div class="list-group-subtitle">
									<liferay-ui:message arguments="<%= new Object[] {HtmlUtil.escape(articleVersion.getStatusByUserName()), dateTimeFormat.format(articleVersion.getStatusDate())} %>" key="by-x-on-x" translateArguments="<%= false %>" />
								</div>

								<div>
									<liferay-portal-workflow:status
										showStatusLabel="<%= false %>"
										status="<%= articleVersion.getStatus() %>"
									/>
								</div>
							</clay:content-col>

							<clay:content-col>
								<clay:dropdown-actions
									additionalProps='<%=
										HashMapBuilder.<String, Object>put(
											"trashEnabled", componentContext.get("trashEnabled")
										).build()
									%>'
									aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
									dropdownItems="<%= journalVersionTabDisplayContext.getArticleHistoryActionDropdownItems(articleVersion) %>"
									propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
								/>
							</clay:content-col>
						</li>

					<%
					}
					%>

				</ul>

				<c:if test="<%= JournalArticleServiceUtil.getArticlesCountByArticleId(article.getGroupId(), article.getArticleId()) > 10 %>">
					<div class="c-mt-3 d-flex justify-content-center">
						<clay:link
							displayType="secondary"
							href="<%= journalVersionTabDisplayContext.getViewMoreURL() %>"
							label="view-more"
							type="button"
						/>
					</div>
				</c:if>
			</clay:tabs-panel>
		</clay:tabs>
	</c:when>
	<c:otherwise>
		<div class="sidebar-header">
			<clay:content-row
				cssClass="sidebar-section"
			>
				<clay:content-col
					expand="<%= true %>"
				>
					<h1 class="component-title"><liferay-ui:message arguments="<%= folders.size() + articles.size() %>" key="x-items-are-selected" /></h1>
				</clay:content-col>
			</clay:content-row>
		</div>

		<clay:tabs
			tabsItems="<%= journalDisplayContext.getInfoPanelTabsItems(false) %>"
		>
			<clay:tabs-panel>
				<div class="sidebar-body">
					<p class="sidebar-dt"><liferay-ui:message arguments="<%= folders.size() + articles.size() %>" key="x-items-are-selected" /></p>
				</div>
			</clay:tabs-panel>
		</clay:tabs>
	</c:otherwise>
</c:choose>