<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String referringPortletResource = ParamUtil.getString(request, "referringPortletResource");

Map<String, Object> componentContext = journalDisplayContext.getComponentContext();
%>

<liferay-ui:search-container
	cssClass='<%= journalDisplayContext.isSearch() ? "pt-0" : StringPool.BLANK %>'
	emptyResultsMessage="no-web-content-was-found"
	id="articles"
	searchContainer="<%= journalDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="Object"
		modelVar="object"
	>

		<%
		JournalArticle curArticle = null;
		JournalFolder curFolder = null;

		Object result = row.getObject();

		if (result instanceof JournalFolder) {
			curFolder = (JournalFolder)result;
		}
		else {
			curArticle = journalDisplayContext.getLatestArticle((JournalArticle)result);
		}
		%>

		<c:choose>
			<c:when test="<%= curArticle != null %>">

				<%
				String title = curArticle.getTitle(locale);

				if (Validator.isNull(title)) {
					title = curArticle.getTitle(LocaleUtil.fromLanguageId(curArticle.getDefaultLanguageId()));
				}

				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", journalDisplayContext.getAvailableActions(curArticle)
					).put(
						"draggable", !BrowserSnifferUtil.isMobile(request) && (JournalArticlePermission.contains(permissionChecker, curArticle, ActionKeys.DELETE) || JournalArticlePermission.contains(permissionChecker, curArticle, ActionKeys.UPDATE))
					).put(
						"title", HtmlUtil.escape(title)
					).build());

				row.setPrimaryKey(HtmlUtil.escape(curArticle.getArticleId()));

				String editURL = StringPool.BLANK;

				if (JournalArticlePermission.contains(permissionChecker, curArticle, ActionKeys.UPDATE)) {
					editURL = JournalPortletUtil.getEditArticlePortletURL(curArticle, request, portletDisplay, currentURL, referringPortletResource);
				}
				%>

				<c:choose>
					<c:when test='<%= Objects.equals(journalDisplayContext.getDisplayStyle(), "descriptive") %>'>
						<liferay-ui:search-container-column-icon
							icon="web-content"
							toggleRowChecker="<%= true %>"
						/>

						<liferay-ui:search-container-column-text
							colspan="<%= 2 %>"
						>
							<div class="d-flex">
								<c:choose>
									<c:when test="<%= editURL != StringPool.BLANK %>">
										<clay:link
											cssClass="d-block lfr-portal-tooltip text-dark text-truncate"
											href="<%= editURL %>"
											label="<%= title %>"
											title="<%= HtmlUtil.escape(title) %>"
											translated="<%= false %>"
										/>
									</c:when>
									<c:otherwise>
										<span class="d-block lfr-portal-tooltip text-dark text-truncate" title="<%= HtmlUtil.escape(title) %>">
											<%= HtmlUtil.escape(title) %>
										</span>
									</c:otherwise>
								</c:choose>

								<c:if test="<%= !journalDisplayContext.hasGuestViewPermission(curArticle) %>">
									<clay:icon
										aria-label='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
										cssClass="c-ml-2 c-mt-1 lfr-portal-tooltip text-4 text-secondary"
										data-title='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
										symbol="password-policies"
									/>
								</c:if>
							</div>

							<span class="c-pb-1 c-pt-1 text-secondary">
								<%= journalDisplayContext.getArticleSubtitle(curArticle) %>
							</span>

							<c:if test="<%= journalDisplayContext.isShowBreadcrumb(curArticle.getFolder()) %>">
								<c:choose>
									<c:when test="<%= curArticle.getFolderId() != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID %>">
										<liferay-site-navigation:breadcrumb
											breadcrumbEntries="<%= JournalPortletUtil.getPortletBreadcrumbEntries(curArticle.getFolder(), request, true, liferayPortletResponse) %>"
											cssClass="c-pl-0 c-pt-0"
										/>
									</c:when>
									<c:otherwise>
										<liferay-site-navigation:breadcrumb
											breadcrumbEntries="<%= JournalPortletUtil.getPortletBreadcrumbEntries(null, request, true, liferayPortletResponse) %>"
											cssClass="c-pl-0 c-pt-0"
										/>
									</c:otherwise>
								</c:choose>
							</c:if>

							<span class="align-items-center d-flex text-default">
								<c:if test="<%= !curArticle.isApproved() && curArticle.hasApprovedVersion() %>">
									<clay:label
										displayType="success"
										label="approved"
									/>
								</c:if>

								<clay:label
									displayType="<%= WorkflowConstants.getStatusStyle(curArticle.getStatus()) %>"
									label="<%= WorkflowConstants.getStatusLabel(curArticle.getStatus()) %>"
								/>

							<c:if test="<%= curArticle.isScheduled() %>">

								<%
								String scheduledArticleMessage = journalDisplayContext.getScheduledArticleMessage(curArticle);
								%>

									<span aria-label="<%= scheduledArticleMessage %>" class="icon-tooltip lfr-portal-tooltip" title="<%= scheduledArticleMessage %>">
										<clay:icon
											cssClass="mt-0"
											symbol="question-circle-full"
										/>
								</span>
							</c:if>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-text>
							<clay:dropdown-actions
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"trashEnabled", componentContext.get("trashEnabled")
									).build()
								%>'
								aria-label='<%= LanguageUtil.format(request, "actions-for-x", HtmlUtil.escape(title), false) %>'
								dropdownItems="<%= journalDisplayContext.getArticleActionDropdownItems(curArticle) %>"
								propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
							/>
						</liferay-ui:search-container-column-text>
					</c:when>
					<c:when test='<%= Objects.equals(journalDisplayContext.getDisplayStyle(), "icon") %>'>
						<liferay-ui:search-container-column-text>
							<clay:vertical-card
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"trashEnabled", componentContext.get("trashEnabled")
									).build()
								%>'
								propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
								verticalCard="<%= new JournalArticleVerticalCard(curArticle, renderRequest, renderResponse, searchContainer.getRowChecker(), assetDisplayPageFriendlyURLProvider, trashHelper, journalDisplayContext) %>"
							/>
						</liferay-ui:search-container-column-text>
					</c:when>
					<c:otherwise>
						<c:if test="<%= !journalWebConfiguration.journalArticleForceAutogenerateId() || journalWebConfiguration.journalArticleShowId() %>">
							<liferay-ui:search-container-column-text
								name="id"
								value="<%= HtmlUtil.escape(curArticle.getArticleId()) %>"
							/>
						</c:if>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand table-cell-minw-200 table-title"
							name="title"
						>
							<div class="autofit-row">
								<div class="autofit-col pr-1">
									<clay:sticker
										cssClass="sticker-document"
										displayType="secondary"
										icon="web-content"
									/>
								</div>

								<div class="autofit-col autofit-col-expand pl-1">
									<div class="table-title">
										<clay:link
											href="<%= editURL %>"
											label="<%= title %>"
											translated="<%= false %>"
										/>

										<c:if test="<%= !journalDisplayContext.hasGuestViewPermission(curArticle) %>">
											<clay:icon
												aria-label='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
												cssClass="c-ml-1 c-mt-0 lfr-portal-tooltip text-4 text-secondary"
												data-title='<%= LanguageUtil.get(request, "not-visible-to-guest-users") %>'
												symbol="password-policies"
											/>
										</c:if>
									</div>
								</div>
							</div>
						</liferay-ui:search-container-column-text>

						<c:if test="<%= !journalDisplayContext.hasHighlightedDDMStructure() %>">
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200 text-truncate"
								name="description"
								value="<%= StringUtil.shorten(HtmlUtil.stripHtml(curArticle.getDescription(locale)), 200) %>"
							/>
						</c:if>

						<c:if test="<%= journalDisplayContext.isSearch() %>">
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smallest table-cell-minw-200"
								name="path"
							>
								<c:if test="<%= journalDisplayContext.isShowBreadcrumb(curArticle.getFolder()) %>">
									<c:choose>
										<c:when test="<%= curArticle.getFolderId() != JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID %>">
											<liferay-site-navigation:breadcrumb
												breadcrumbEntries="<%= JournalPortletUtil.getPortletBreadcrumbEntries(curArticle.getFolder(), request, true, liferayPortletResponse) %>"
												cssClass="c-pl-0 c-pt-0"
											/>
										</c:when>
										<c:otherwise>
											<liferay-site-navigation:breadcrumb
												breadcrumbEntries="<%= JournalPortletUtil.getPortletBreadcrumbEntries(null, request, true, liferayPortletResponse) %>"
												cssClass="c-pl-0 c-pt-0"
											/>
										</c:otherwise>
									</c:choose>
								</c:if>
							</liferay-ui:search-container-column-text>
						</c:if>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand-smallest table-cell-minw-100"
							name="author"
							value="<%= HtmlUtil.escape(curArticle.getUserName()) %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="text-nowrap"
							name="status"
						>
							<c:if test="<%= !curArticle.isApproved() && curArticle.hasApprovedVersion() %>">
								<clay:label
									displayType="success"
									label="approved"
								/>
							</c:if>

							<clay:label
								displayType="<%= WorkflowConstants.getStatusStyle(curArticle.getStatus()) %>"
								label="<%= WorkflowConstants.getStatusLabel(curArticle.getStatus()) %>"
							/>
						</liferay-ui:search-container-column-text>

						<c:if test="<%= !journalDisplayContext.isHighlightedDDMStructure() %>">

							<%
							DDMStructure ddmStructure = curArticle.getDDMStructure();
							%>

							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smallest table-cell-minw-100"
								name="type"
								value="<%= HtmlUtil.escape(ddmStructure.getName(locale)) %>"
							/>
						</c:if>

						<liferay-ui:search-container-column-date
							cssClass="table-cell-expand-smallest"
							name="modified-date"
							userName="<%= curArticle.getStatusByUserName() %>"
							value="<%= curArticle.getModifiedDate() %>"
						/>

						<liferay-ui:search-container-column-date
							cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
							name="display-date"
							value="<%= curArticle.getDisplayDate() %>"
						/>

						<liferay-ui:search-container-column-date
							cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
							name="create-date"
							value="<%= curArticle.getCreateDate() %>"
						/>

						<liferay-ui:search-container-column-text>
							<clay:dropdown-actions
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"trashEnabled", componentContext.get("trashEnabled")
									).build()
								%>'
								aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
								dropdownItems="<%= journalDisplayContext.getArticleActionDropdownItems(curArticle) %>"
								propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
							/>
						</liferay-ui:search-container-column-text>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="<%= curFolder != null %>">

				<%
				row.setData(
					HashMapBuilder.<String, Object>put(
						"actions", journalDisplayContext.getAvailableActions(curFolder)
					).put(
						"draggable", !BrowserSnifferUtil.isMobile(request) && (JournalFolderPermission.contains(permissionChecker, curFolder, ActionKeys.DELETE) || JournalFolderPermission.contains(permissionChecker, curFolder, ActionKeys.UPDATE))
					).put(
						"folder", true
					).put(
						"folder-id", curFolder.getFolderId()
					).put(
						"title", HtmlUtil.escape(curFolder.getName())
					).build());
				row.setPrimaryKey(String.valueOf(curFolder.getPrimaryKey()));

				PortletURL rowURL = PortletURLBuilder.createRenderURL(
					liferayPortletResponse
				).setParameter(
					"displayStyle", journalDisplayContext.getDisplayStyle()
				).setParameter(
					"folderId", curFolder.getFolderId()
				).setParameter(
					"groupId", curFolder.getGroupId()
				).buildPortletURL();
				%>

				<c:choose>
					<c:when test='<%= Objects.equals(journalDisplayContext.getDisplayStyle(), "descriptive") %>'>
						<liferay-ui:search-container-column-icon
							icon="folder"
							toggleRowChecker="<%= true %>"
						/>

						<liferay-ui:search-container-column-text
							colspan="<%= 2 %>"
						>
							<div class="d-flex">
								<c:choose>
									<c:when test="<%= rowURL.toString() != StringPool.BLANK %>">
										<clay:link
											cssClass="d-block lfr-portal-tooltip text-dark text-truncate"
											href="<%= rowURL.toString() %>"
											label="<%= HtmlUtil.escape(curFolder.getName()) %>"
											title="<%= HtmlUtil.escape(curFolder.getName()) %>"
											translated="<%= false %>"
										/>
									</c:when>
									<c:otherwise>
										<span class="d-block lfr-portal-tooltip text-dark text-truncate" title="<%= HtmlUtil.escape(curFolder.getName()) %>">
											<%= HtmlUtil.escape(curFolder.getName()) %>
										</span>
									</c:otherwise>
								</c:choose>
							</div>

							<span class="c-pt-1 text-secondary">
								<%= journalDisplayContext.getFolderSubtitle(curFolder) %>
							</span>

							<c:if test="<%= journalDisplayContext.isShowBreadcrumb(curFolder.getParentFolder()) %>">
								<liferay-site-navigation:breadcrumb
									breadcrumbEntries="<%= JournalPortletUtil.getPortletBreadcrumbEntries(curFolder.getParentFolder(), request, true, liferayPortletResponse) %>"
									cssClass="c-pl-0 c-pt-0"
								/>
							</c:if>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-text>
							<clay:dropdown-actions
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"trashEnabled", componentContext.get("trashEnabled")
									).build()
								%>'
								aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
								dropdownItems="<%= journalDisplayContext.getFolderActionDropdownItems(curFolder) %>"
								propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
							/>
						</liferay-ui:search-container-column-text>
					</c:when>
					<c:when test='<%= Objects.equals(journalDisplayContext.getDisplayStyle(), "icon") %>'>

						<%
						row.setCssClass("card-page-item card-page-item-directory " + row.getCssClass());
						%>

						<liferay-ui:search-container-column-text
							colspan="<%= 2 %>"
						>
							<clay:horizontal-card
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"trashEnabled", componentContext.get("trashEnabled")
									).build()
								%>'
								horizontalCard="<%= new JournalFolderHorizontalCard(curFolder, journalDisplayContext.getDisplayStyle(), renderRequest, renderResponse, searchContainer.getRowChecker(), trashHelper) %>"
								propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
							/>
						</liferay-ui:search-container-column-text>
					</c:when>
					<c:otherwise>
						<c:if test="<%= !journalWebConfiguration.journalArticleForceAutogenerateId() || journalWebConfiguration.journalArticleShowId() %>">
							<liferay-ui:search-container-column-text
								name="id"
								value="<%= HtmlUtil.escape(String.valueOf(curFolder.getFolderId())) %>"
							/>
						</c:if>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand table-cell-minw-200 table-list-title"
							name="title"
						>
							<div class="autofit-row">
								<div class="autofit-col pr-1">
									<clay:sticker
										cssClass="sticker-document"
										displayType="secondary"
										icon="folder"
									/>
								</div>

								<div class="autofit-col autofit-col-expand pl-1">
									<div class="table-title">
										<clay:link
											href="<%= rowURL.toString() %>"
											label="<%= HtmlUtil.escape(curFolder.getName()) %>"
											translated="<%= false %>"
										/>
									</div>
								</div>
							</div>
						</liferay-ui:search-container-column-text>

						<c:if test="<%= !journalDisplayContext.hasHighlightedDDMStructure() %>">
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand table-cell-minw-200 text-truncate"
								name="description"
								value="<%= StringUtil.shorten(HtmlUtil.stripHtml(curFolder.getDescription()), 200) %>"
							/>
						</c:if>

						<c:if test="<%= journalDisplayContext.isSearch() %>">
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smallest table-cell-minw-200"
								name="path"
							>
								<c:if test="<%= journalDisplayContext.isShowBreadcrumb(curFolder.getParentFolder()) %>">
									<liferay-site-navigation:breadcrumb
										breadcrumbEntries="<%= JournalPortletUtil.getPortletBreadcrumbEntries(curFolder.getParentFolder(), request, true, liferayPortletResponse) %>"
										cssClass="c-pl-0 c-pt-0"
									/>
								</c:if>
							</liferay-ui:search-container-column-text>
						</c:if>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand-smallest table-cell-minw-150"
							name="author"
							value="<%= HtmlUtil.escape(PortalUtil.getUserName(curFolder)) %>"
						/>

						<liferay-ui:search-container-column-text
							name="status"
							value="--"
						/>

						<c:if test="<%= !journalDisplayContext.isHighlightedDDMStructure() %>">
							<liferay-ui:search-container-column-text
								cssClass="table-cell-expand-smallest table-cell-minw-150"
								name="type"
								value='<%= LanguageUtil.get(request, "folder") %>'
							/>
						</c:if>

						<liferay-ui:search-container-column-date
							cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
							name="modified-date"
							value="<%= curFolder.getModifiedDate() %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
							name="display-date"
							value="--"
						/>

						<liferay-ui:search-container-column-date
							cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
							name="create-date"
							value="<%= curFolder.getCreateDate() %>"
						/>

						<liferay-ui:search-container-column-text>
							<clay:dropdown-actions
								additionalProps='<%=
									HashMapBuilder.<String, Object>put(
										"trashEnabled", componentContext.get("trashEnabled")
									).build()
								%>'
								aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
								dropdownItems="<%= journalDisplayContext.getFolderActionDropdownItems(curFolder) %>"
								propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
							/>
						</liferay-ui:search-container-column-text>
					</c:otherwise>
				</c:choose>
			</c:when>
		</c:choose>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		displayStyle="<%= journalDisplayContext.getDisplayStyle() %>"
		markupView="lexicon"
		resultRowSplitter="<%= journalDisplayContext.getResultRowSplitter() %>"
		searchContainer="<%= searchContainer %>"
	/>
</liferay-ui:search-container>

<portlet:renderURL var="moveEntryURL">
	<portlet:param name="mvcPath" value="/move_articles_and_folders.jsp" />
	<portlet:param name="redirect" value="<%= currentURL %>" />
</portlet:renderURL>

<portlet:actionURL var="editEntryURL" />

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"editEntryURL", editEntryURL
		).put(
			"moveEntryURL", moveEntryURL
		).put(
			"searchContainerId", "articles"
		).build()
	%>'
	module="{Navigation} from journal-web"
	servletContext="<%= application %>"
/>