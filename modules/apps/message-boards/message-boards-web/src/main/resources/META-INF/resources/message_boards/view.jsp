<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/message_boards/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

String mvcRenderCommandName = ParamUtil.getString(request, "mvcRenderCommandName", "/message_boards/view");

MBCategory category = (MBCategory)request.getAttribute(WebKeys.MESSAGE_BOARDS_CATEGORY);

long categoryId = MBUtil.getCategoryId(request, category);

Set<Long> categorySubscriptionClassPKs = null;
Set<Long> threadSubscriptionClassPKs = null;

if (themeDisplay.isSignedIn()) {
	categorySubscriptionClassPKs = MBSubscriptionUtil.getCategorySubscriptionClassPKs(user.getUserId());
	threadSubscriptionClassPKs = MBSubscriptionUtil.getThreadSubscriptionClassPKs(user.getUserId());
}

long groupThreadsUserId = ParamUtil.getLong(request, "groupThreadsUserId");

String assetTagName = ParamUtil.getString(request, "tag");

boolean useAssetEntryQuery = Validator.isNotNull(assetTagName);

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	mvcRenderCommandName
).buildPortletURL();

int cur1 = ParamUtil.getInteger(request, "cur1");

if (cur1 > 0) {
	portletURL.setParameter("cur1", String.valueOf(cur1));
}

int cur2 = ParamUtil.getInteger(request, "cur2");

if (cur2 > 0) {
	portletURL.setParameter("cur2", String.valueOf(cur2));
}

portletURL.setParameter("mbCategoryId", String.valueOf(categoryId));

String searchCategoryId = ParamUtil.getString(request, "searchCategoryId");

if (Validator.isNotNull(searchCategoryId)) {
	portletURL.setParameter("searchCategoryId", searchCategoryId);
}

String keywords = ParamUtil.getString(request, "keywords");

if (Validator.isNotNull(keywords)) {
	portletURL.setParameter("keywords", keywords);
}

String orderByCol = ParamUtil.getString(request, "orderByCol", "modified-date");

boolean orderByAsc = false;

String orderByType = ParamUtil.getString(request, "orderByType", "desc");

if (orderByType.equals("asc")) {
	orderByAsc = true;
}

OrderByComparator threadOrderByComparator = null;

if (orderByCol.equals("modified-date")) {
	threadOrderByComparator = ThreadModifiedDateComparator.getInstance(orderByAsc);
}

MBListDisplayContext mbListDisplayContext = MBDisplayContextUtil.getMBListDisplayContext(request, response, categoryId, mvcRenderCommandName);

request.setAttribute("view.jsp-categorySubscriptionClassPKs", categorySubscriptionClassPKs);
request.setAttribute("view.jsp-threadSubscriptionClassPKs", threadSubscriptionClassPKs);

request.setAttribute("view.jsp-viewCategory", Boolean.TRUE.toString());
%>

<portlet:actionURL name="/message_boards/edit_category" var="restoreTrashEntriesURL">
	<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.RESTORE %>" />
</portlet:actionURL>

<liferay-trash:undo
	portletURL="<%= restoreTrashEntriesURL %>"
/>

<%@ include file="/message_boards/nav.jspf" %>

<c:choose>
	<c:when test='<%= mvcRenderCommandName.equals("/message_boards/view_my_subscriptions") %>'>

		<%
		if (themeDisplay.isSignedIn()) {
			groupThreadsUserId = user.getUserId();
		}

		if (groupThreadsUserId > 0) {
			portletURL.setParameter("groupThreadsUserId", String.valueOf(groupThreadsUserId));
		}

		MBCategoryDisplay categoryDisplay = new MBCategoryDisplay(scopeGroupId, categoryId);
		%>

		<div class="main-content-body mt-4">
			<h3><liferay-ui:message key="my-subscriptions" /></h3>

			<liferay-ui:search-container
				curParam="cur1"
				deltaConfigurable="<%= false %>"
				emptyResultsMessage="you-are-not-subscribed-to-any-categories"
				headerNames="category,categories,threads,posts"
				iteratorURL="<%= portletURL %>"
				total="<%= MBCategoryServiceUtil.getSubscribedCategoriesCount(scopeGroupId, user.getUserId()) %>"
			>
				<liferay-ui:search-container-results
					results="<%= MBCategoryServiceUtil.getSubscribedCategories(scopeGroupId, user.getUserId(), searchContainer.getStart(), searchContainer.getEnd()) %>"
				/>

				<liferay-ui:search-container-row
					className="com.liferay.message.boards.model.MBCategory"
					escapedModel="<%= true %>"
					keyProperty="categoryId"
					modelVar="curCategory"
				>
					<liferay-portlet:renderURL varImpl="rowURL">
						<portlet:param name="mvcRenderCommandName" value="/message_boards/view_category" />
						<portlet:param name="mbCategoryId" value="<%= String.valueOf(curCategory.getCategoryId()) %>" />
					</liferay-portlet:renderURL>

					<%@ include file="/message_boards/subscribed_category_columns.jspf" %>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					type="more"
				/>
			</liferay-ui:search-container>

			<%@ include file="/message_boards/view_threads.jspf" %>

			<%
			PortalUtil.setPageSubtitle(LanguageUtil.get(request, StringUtil.replace("my-subscriptions", CharPool.UNDERLINE, CharPool.DASH)), request);
			PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, TextFormatter.format("my-subscriptions", TextFormatter.O)), portletURL.toString());
			%>

		</div>
	</c:when>
	<c:when test="<%= useAssetEntryQuery && !mbListDisplayContext.isShowMyPosts() %>">
		<liferay-asset:categorization-filter
			assetType="threads"
			portletURL="<%= portletURL %>"
		/>

		<%@ include file="/message_boards/view_threads.jspf" %>
	</c:when>
	<c:when test='<%= mbListDisplayContext.isShowSearch() || mvcRenderCommandName.equals("/message_boards/view") || mvcRenderCommandName.equals("/message_boards/view_category") || mbListDisplayContext.isShowMyPosts() || mbListDisplayContext.isShowRecentPosts() %>'>
		<c:choose>
			<c:when test='<%= mvcRenderCommandName.equals("/message_boards/search") || mvcRenderCommandName.equals("/message_boards/view") || mvcRenderCommandName.equals("/message_boards/view_category") %>'>
				<div class="main-content-body mt-4">
					<c:if test="<%= mbListDisplayContext.isShowSearch() %>">
						<liferay-ui:header
							backURL="<%= redirect %>"
							title="search"
						/>
					</c:if>

					<%
					boolean showAddCategoryButton = MBCategoryPermission.contains(permissionChecker, scopeGroupId, categoryId, ActionKeys.ADD_CATEGORY);
					boolean showAddMessageButton = MBCategoryPermission.contains(permissionChecker, scopeGroupId, categoryId, ActionKeys.ADD_MESSAGE);
					boolean showPermissionsButton = MBResourcePermission.contains(permissionChecker, scopeGroupId, ActionKeys.PERMISSIONS);

					if (showAddMessageButton && !themeDisplay.isSignedIn() && !allowAnonymousPosting) {
						showAddMessageButton = false;
					}
					%>

					<c:if test="<%= category != null %>">

						<%
						MBBreadcrumbUtil.addPortletBreadcrumbEntries(categoryId, request, renderResponse);
						%>

						<liferay-site-navigation:breadcrumb
							breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
						/>
					</c:if>

					<clay:content-row
						floatElements="end"
					>
						<clay:content-col
							expand="<%= true %>"
						>
							<c:choose>
								<c:when test="<%= category != null %>">
									<h3 class="component-title"><%= HtmlUtil.escape(category.getName()) %></h3>
								</c:when>
								<c:otherwise>

									<%
									MBBreadcrumbUtil.addPortletBreadcrumbEntries(categoryId, request, renderResponse);
									%>

									<liferay-site-navigation:breadcrumb
										breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
									/>
								</c:otherwise>
							</c:choose>
						</clay:content-col>

						<clay:content-col>
							<div class="btn-group">
								<c:if test="<%= showAddCategoryButton %>">
									<portlet:renderURL var="editCategoryURL">
										<portlet:param name="mvcRenderCommandName" value="/message_boards/edit_category" />
										<portlet:param name="redirect" value="<%= currentURL %>" />
										<portlet:param name="parentCategoryId" value="<%= String.valueOf(categoryId) %>" />
									</portlet:renderURL>

									<div class="btn-group-item">
										<clay:link
											data-senna-off="<%= true %>"
											displayType="secondary"
											href="<%= editCategoryURL %>"
											label="add-category[message-board]"
											small="<%= true %>"
											type="button"
										/>
									</div>
								</c:if>

								<c:if test="<%= showAddMessageButton %>">
									<portlet:renderURL var="editMessageURL">
										<portlet:param name="mvcRenderCommandName" value="/message_boards/edit_message" />
										<portlet:param name="redirect" value="<%= currentURL %>" />
										<portlet:param name="mbCategoryId" value="<%= String.valueOf(categoryId) %>" />
									</portlet:renderURL>

									<div class="btn-group-item">
										<clay:link
											displayType="primary"
											href="<%= editMessageURL %>"
											label="new-thread"
											small="<%= true %>"
											type="button"
										/>
									</div>
								</c:if>

								<liferay-ui:icon-menu
									direction="left-side"
									icon="<%= StringPool.BLANK %>"
									markupView="lexicon"
									message="actions"
									showWhenSingleIcon="<%= true %>"
								>
									<c:if test="<%= showPermissionsButton %>">

										<%
										String modelResource = "com.liferay.message.boards";
										String modelResourceDescription = themeDisplay.getScopeGroupName();
										String resourcePrimKey = String.valueOf(scopeGroupId);

										if (category != null) {
											modelResource = MBCategory.class.getName();
											modelResourceDescription = category.getName();
											resourcePrimKey = String.valueOf(category.getCategoryId());
										}
										%>

										<liferay-security:permissionsURL
											modelResource="<%= modelResource %>"
											modelResourceDescription="<%= HtmlUtil.escape(modelResourceDescription) %>"
											resourcePrimKey="<%= resourcePrimKey %>"
											var="permissionsURL"
											windowState="<%= LiferayWindowState.POP_UP.toString() %>"
										/>

										<liferay-ui:icon
											label="<%= true %>"
											message="permissions"
											method="get"
											url="<%= permissionsURL %>"
											useDialog="<%= true %>"
										/>
									</c:if>

									<c:choose>
										<c:when test="<%= category == null %>">
											<c:if test="<%= MBResourcePermission.contains(permissionChecker, scopeGroupId, ActionKeys.SUBSCRIBE) && (mbGroupServiceSettings.isEmailMessageAddedEnabled() || mbGroupServiceSettings.isEmailMessageUpdatedEnabled()) %>">
												<c:choose>
													<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), MBCategory.class.getName(), scopeGroupId) %>">
														<portlet:actionURL name="/message_boards/edit_category" var="unsubscribeURL">
															<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
															<portlet:param name="redirect" value="<%= currentURL %>" />
															<portlet:param name="mbCategoryId" value="<%= String.valueOf(MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) %>" />
														</portlet:actionURL>

														<liferay-ui:icon
															label="<%= true %>"
															message="unsubscribe"
															url="<%= unsubscribeURL %>"
														/>
													</c:when>
													<c:otherwise>
														<portlet:actionURL name="/message_boards/edit_category" var="subscribeURL">
															<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
															<portlet:param name="redirect" value="<%= currentURL %>" />
															<portlet:param name="mbCategoryId" value="<%= String.valueOf(MBCategoryConstants.DEFAULT_PARENT_CATEGORY_ID) %>" />
														</portlet:actionURL>

														<liferay-ui:icon
															label="<%= true %>"
															message="subscribe"
															url="<%= subscribeURL %>"
														/>
													</c:otherwise>
												</c:choose>
											</c:if>

											<c:if test="<%= enableRSS %>">
												<liferay-rss:rss
													delta="<%= rssDelta %>"
													displayStyle="<%= rssDisplayStyle %>"
													feedType="<%= rssFeedType %>"
													url="<%= MBRSSUtil.getRSSURL(plid, 0, 0, 0, themeDisplay) %>"
												/>
											</c:if>
										</c:when>
										<c:otherwise>
											<c:if test="<%= enableRSS %>">
												<liferay-rss:rss
													delta="<%= rssDelta %>"
													displayStyle="<%= rssDisplayStyle %>"
													feedType="<%= rssFeedType %>"
													url="<%= MBRSSUtil.getRSSURL(plid, category.getCategoryId(), 0, 0, themeDisplay) %>"
												/>
											</c:if>

											<c:if test="<%= MBCategoryPermission.contains(permissionChecker, category, ActionKeys.SUBSCRIBE) && (mbGroupServiceSettings.isEmailMessageAddedEnabled() || mbGroupServiceSettings.isEmailMessageUpdatedEnabled()) %>">
												<c:choose>
													<c:when test="<%= (categorySubscriptionClassPKs != null) && categorySubscriptionClassPKs.contains(category.getCategoryId()) %>">
														<portlet:actionURL name="/message_boards/edit_category" var="unsubscribeURL">
															<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
															<portlet:param name="redirect" value="<%= currentURL %>" />
															<portlet:param name="mbCategoryId" value="<%= String.valueOf(category.getCategoryId()) %>" />
														</portlet:actionURL>

														<liferay-ui:icon
															label="<%= true %>"
															message="unsubscribe"
															url="<%= unsubscribeURL %>"
														/>
													</c:when>
													<c:otherwise>
														<portlet:actionURL name="/message_boards/edit_category" var="subscribeURL">
															<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
															<portlet:param name="redirect" value="<%= currentURL %>" />
															<portlet:param name="mbCategoryId" value="<%= String.valueOf(category.getCategoryId()) %>" />
														</portlet:actionURL>

														<liferay-ui:icon
															label="<%= true %>"
															message="subscribe"
															url="<%= subscribeURL %>"
														/>
													</c:otherwise>
												</c:choose>
											</c:if>
										</c:otherwise>
									</c:choose>
								</liferay-ui:icon-menu>
							</div>
						</clay:content-col>
					</clay:content-row>

					<%
					SearchContainer categoryEntriesSearchContainer = new SearchContainer(renderRequest, null, null, "cur1", 0, mbListDisplayContext.getCategoryEntriesDelta(), PortletURLUtil.clone(portletURL, renderResponse), null, "there-are-no-threads-or-categories");

					mbListDisplayContext.setCategoryEntriesDelta(categoryEntriesSearchContainer);

					categoryEntriesSearchContainer.setOrderByCol(orderByCol);
					categoryEntriesSearchContainer.setOrderByComparator(CategoryTitleComparator.getInstance(true));
					categoryEntriesSearchContainer.setOrderByType(orderByType);

					mbListDisplayContext.populateCategoriesResultsAndTotal(categoryEntriesSearchContainer);

					request.setAttribute("view.jsp-categoryEntriesSearchContainer", categoryEntriesSearchContainer);
					%>

					<c:if test="<%= categoryEntriesSearchContainer.getTotal() > 0 %>">
						<%@ include file="/message_boards/view_category_entries.jspf" %>
					</c:if>

					<%
					SearchContainer threadEntriesSearchContainer = new SearchContainer(renderRequest, null, null, "cur2", 0, mbListDisplayContext.getThreadEntriesDelta(), PortletURLUtil.clone(portletURL, renderResponse), null, "there-are-no-threads-or-categories");

					mbListDisplayContext.setThreadEntriesDelta(threadEntriesSearchContainer);

					threadEntriesSearchContainer.setOrderByCol(orderByCol);
					threadEntriesSearchContainer.setOrderByComparator(threadOrderByComparator);
					threadEntriesSearchContainer.setOrderByType(orderByType);

					mbListDisplayContext.populateThreadsResultsAndTotal(threadEntriesSearchContainer);
					%>

					<c:if test="<%= threadEntriesSearchContainer.getTotal() > 0 %>">
						<%@ include file="/message_boards/view_thread_entries.jspf" %>
					</c:if>

					<c:if test="<%= (categoryEntriesSearchContainer.getTotal() <= 0) && (threadEntriesSearchContainer.getTotal() <= 0) %>">
						<liferay-frontend:empty-result-message
							animationType="<%= EmptyResultMessageKeys.AnimationType.EMPTY %>"
							title="<%= LanguageUtil.get(resourceBundle, mbListDisplayContext.getEmptyResultsMessage()) %>"
						/>
					</c:if>

					<%
					if (category != null) {
						PortalUtil.setPageSubtitle(category.getName(), request);
						PortalUtil.setPageDescription(category.getDescription(), request);
					}
					%>

				</div>
			</c:when>
			<c:when test="<%= mbListDisplayContext.isShowMyPosts() || mbListDisplayContext.isShowRecentPosts() %>">
				<div class="main-content-body mt-4">
					<c:choose>
						<c:when test="<%= mbListDisplayContext.isShowRecentPosts() %>">
							<clay:content-row
								floatElements="end"
							>
								<clay:content-col
									expand="<%= true %>"
								>
									<h3 class="component-title"><liferay-ui:message key="recent-posts" /></h3>
								</clay:content-col>

								<clay:content-col>
									<div class="btn-group">
										<c:if test="<%= enableRSS %>">
											<liferay-ui:icon-menu
												direction="left-side"
												icon="<%= StringPool.BLANK %>"
												markupView="lexicon"
												message="actions"
												showWhenSingleIcon="<%= true %>"
											>
												<liferay-rss:rss
													delta="<%= rssDelta %>"
													displayStyle="<%= rssDisplayStyle %>"
													feedType="<%= rssFeedType %>"
													message="rss"
													url="<%= MBRSSUtil.getRSSURL(plid, 0, 0, groupThreadsUserId, themeDisplay) %>"
												/>
											</liferay-ui:icon-menu>
										</c:if>
									</div>
								</clay:content-col>
							</clay:content-row>

							<c:if test="<%= groupThreadsUserId > 0 %>">
								<div class="alert alert-info">
									<liferay-ui:message key="filter-by-user" />: <%= HtmlUtil.escape(PortalUtil.getUserName(groupThreadsUserId, StringPool.BLANK)) %>
								</div>
							</c:if>
						</c:when>
						<c:otherwise>

							<%
							if (themeDisplay.isSignedIn()) {
								groupThreadsUserId = user.getUserId();
							}
							%>

							<clay:content-row
								floatElements="end"
							>
								<clay:content-col
									expand="<%= true %>"
								>
									<h3 class="component-title"><liferay-ui:message key="my-posts" /></h3>
								</clay:content-col>

								<clay:content-col>
									<div class="btn-group">
										<c:if test="<%= enableRSS %>">
											<liferay-ui:icon-menu
												direction="left-side"
												icon="<%= StringPool.BLANK %>"
												markupView="lexicon"
												message="actions"
												showWhenSingleIcon="<%= true %>"
											>
												<liferay-rss:rss
													delta="<%= rssDelta %>"
													displayStyle="<%= rssDisplayStyle %>"
													feedType="<%= rssFeedType %>"
													message="rss"
													url="<%= MBRSSUtil.getRSSURL(plid, 0, 0, groupThreadsUserId, themeDisplay) %>"
												/>
											</liferay-ui:icon-menu>
										</c:if>
									</div>
								</clay:content-col>
							</clay:content-row>
						</c:otherwise>
					</c:choose>

					<%
					if (groupThreadsUserId > 0) {
						portletURL.setParameter("groupThreadsUserId", String.valueOf(groupThreadsUserId));
					}

					SearchContainer threadEntriesSearchContainer = new SearchContainer(renderRequest, null, null, "cur1", 0, mbListDisplayContext.getThreadEntriesDelta(), portletURL, null, "there-are-no-threads");

					mbListDisplayContext.setThreadEntriesDelta(threadEntriesSearchContainer);

					threadEntriesSearchContainer.setOrderByCol(orderByCol);
					threadEntriesSearchContainer.setOrderByComparator(threadOrderByComparator);
					threadEntriesSearchContainer.setOrderByType(orderByType);

					mbListDisplayContext.populateThreadsResultsAndTotal(threadEntriesSearchContainer);
					%>

					<%@ include file="/message_boards/view_thread_entries.jspf" %>

					<%
					String pageSubtitle = null;

					if (mbListDisplayContext.isShowMyPosts()) {
						pageSubtitle = "my-posts";
					}
					else if (mbListDisplayContext.isShowRecentPosts()) {
						pageSubtitle = "recent-posts";
					}

					PortalUtil.setPageSubtitle(LanguageUtil.get(request, StringUtil.replace(pageSubtitle, CharPool.UNDERLINE, CharPool.DASH)), request);
					PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, TextFormatter.format(pageSubtitle, TextFormatter.O)), portletURL.toString());
					%>

				</div>
			</c:when>
		</c:choose>
	</c:when>
</c:choose>

<%!
private static final Log _log = LogFactoryUtil.getLog("com_liferay_message_boards_web.message_boards.view_jsp");
%>