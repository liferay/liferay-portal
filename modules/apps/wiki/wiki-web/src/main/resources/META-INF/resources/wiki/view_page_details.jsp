<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
WikiEngineRenderer wikiEngineRenderer = (WikiEngineRenderer)request.getAttribute(WikiWebKeys.WIKI_ENGINE_RENDERER);
WikiNode node = (WikiNode)request.getAttribute(WikiWebKeys.WIKI_NODE);

WikiPage wikiPage = (WikiPage)request.getAttribute(WikiWebKeys.WIKI_PAGE);

List<FileEntry> attachmentsFileEntries = wikiPage.getAttachmentsFileEntries();

WikiPage initialPage = WikiPageLocalServiceUtil.getPage(wikiPage.getNodeId(), wikiPage.getTitle(), WikiPageConstants.VERSION_DEFAULT);

PortletURL viewPageURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/wiki/view"
).setParameter(
	"nodeName", node.getName()
).setParameter(
	"title", wikiPage.getTitle()
).buildPortletURL();

PortletURL editPageURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCRenderCommandName(
	"/wiki/edit_page"
).setRedirect(
	currentURL
).setParameter(
	"nodeId", node.getNodeId()
).setParameter(
	"title", wikiPage.getTitle()
).buildPortletURL();

PortalUtil.addPortletBreadcrumbEntry(request, wikiPage.getTitle(), viewPageURL.toString());

PortalUtil.addPortletBreadcrumbEntry(request, LanguageUtil.get(request, "details"), currentURL);
%>

<liferay-util:include page="/wiki/top_links.jsp" servletContext="<%= application %>" />

<liferay-util:include page="/wiki/page_tabs.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="details" />
</liferay-util:include>

<table class="page-info table table-bordered table-hover table-striped">
	<tr>
		<th class="table-header">
			<liferay-ui:message key="title" />
		</th>
		<td class="table-cell">
			<%= HtmlUtil.escape(wikiPage.getTitle()) %>
		</td>
	</tr>
	<tr>
		<th class="table-header">
			<liferay-ui:message key="format" />
		</th>
		<td class="table-cell">
			<%= WikiUtil.getFormatLabel(wikiEngineRenderer, wikiPage.getFormat(), themeDisplay.getLocale()) %>
		</td>
	</tr>
	<tr>
		<th class="table-header">
			<liferay-ui:message key="latest-version" />
		</th>
		<td class="table-cell">
			<%= wikiPage.getVersion() %>

			<c:if test="<%= wikiPage.isMinorEdit() %>">
				(<liferay-ui:message key="minor-edit" />)
			</c:if>
		</td>
	</tr>
	<tr>
		<th class="table-header">
			<liferay-ui:message key="created-by" />
		</th>
		<td class="table-cell">
			<%= HtmlUtil.escape(Validator.isNotNull(initialPage.getUserName()) ? initialPage.getUserName() : "Liferay") %> (<%= dateTimeFormat.format(initialPage.getCreateDate()) %>)
		</td>
	</tr>
	<tr>
		<th class="table-header">
			<liferay-ui:message key="last-changed-by" />
		</th>
		<td class="table-cell">
			<%= HtmlUtil.escape(wikiPage.getUserName()) %> (<%= dateTimeFormat.format(wikiPage.getModifiedDate()) %>)
		</td>
	</tr>
	<tr>
		<th class="table-header">
			<liferay-ui:message key="attachments" />
		</th>
		<td class="table-cell">
			<%= (attachmentsFileEntries != null) ? attachmentsFileEntries.size() : 0 %>
		</td>
	</tr>

	<c:if test="<%= DocumentConversionUtil.isEnabled() && WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.VIEW) %>">

		<%
		String[] conversions = DocumentConversionUtil.getConversions("html");

		PortletURL exportPageURL = PortletURLBuilder.createActionURL(
			renderResponse
		).setActionName(
			"/wiki/export_page"
		).setParameter(
			"nodeId", node.getNodeId()
		).setParameter(
			"nodeName", node.getName()
		).setParameter(
			"title", wikiPage.getTitle()
		).setParameter(
			"version", wikiPage.getVersion()
		).setWindowState(
			LiferayWindowState.EXCLUSIVE
		).buildPortletURL();
		%>

		<tr>
			<th class="table-header">
				<liferay-ui:message key="convert-to" />
			</th>
			<td class="table-cell">
				<div class="d-inline-flex">

					<%
					for (String conversion : conversions) {
						exportPageURL.setParameter("targetExtension", conversion);
					%>

						<clay:link
							aria-label='<%= LanguageUtil.format(request, "download-x-as-x", new Object[] {HtmlUtil.escape(wikiPage.getTitle()), StringUtil.toUpperCase(conversion)}) %>'
							borderless="<%= true %>"
							data-resource-href="<%= exportPageURL.toString() %>"
							displayType="primary"
							href="<%= exportPageURL.toString() %>"
							icon="<%= DLUtil.getFileIconCssClass(conversion) %>"
							label="<%= StringUtil.toUpperCase(conversion) %>"
							small="<%= true %>"
							type="button"
						/>

					<%
					}
					%>

				</div>
			</td>
		</tr>
	</c:if>

	<c:if test="<%= wikiGroupServiceOverriddenConfiguration.enableRss() %>">
		<tr>
			<th class="table-header">
				<liferay-ui:message key="rss-subscription" />
			</th>
			<td class="table-cell">
				<liferay-rss:rss
					delta="<%= GetterUtil.getInteger(wikiGroupServiceOverriddenConfiguration.rssDelta()) %>"
					displayStyle="<%= wikiGroupServiceOverriddenConfiguration.rssDisplayStyle() %>"
					feedType="<%= wikiGroupServiceOverriddenConfiguration.rssFeedType() %>"
					url='<%= themeDisplay.getPathMain() + "/wiki/rss?nodeId=" + wikiPage.getNodeId() + "&title=" + HtmlUtil.escape(wikiPage.getTitle()) %>'
				/>
			</td>
		</tr>
	</c:if>

	<c:if test="<%= (WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.SUBSCRIBE) || WikiNodePermission.contains(permissionChecker, node, ActionKeys.SUBSCRIBE)) && (wikiGroupServiceOverriddenConfiguration.emailPageAddedEnabled() || wikiGroupServiceOverriddenConfiguration.emailPageUpdatedEnabled()) %>">
		<tr>
			<th class="table-header">
				<liferay-ui:message key="email-subscription" />
			</th>
			<td>
				<table class="lfr-table subscription-info">
					<c:if test="<%= WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.SUBSCRIBE) %>">
						<tr>
							<c:choose>
								<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), WikiPage.class.getName(), wikiPage.getResourcePrimKey()) %>">
									<td>
										<liferay-ui:message key="you-are-subscribed-to-this-page" />
									</td>
									<td>
										<portlet:actionURL name="/wiki/edit_page" var="unsubscribeURL">
											<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
											<portlet:param name="redirect" value="<%= currentURL %>" />
											<portlet:param name="nodeId" value="<%= String.valueOf(wikiPage.getNodeId()) %>" />
											<portlet:param name="title" value="<%= String.valueOf(wikiPage.getTitle()) %>" />
										</portlet:actionURL>

										<clay:link
											aria-label='<%= LanguageUtil.get(request, "unsubscribe") %>'
											href="<%= unsubscribeURL %>"
											icon="times-circle"
											label="unsubscribe"
											small="<%= true %>"
										/>
									</td>
								</c:when>
								<c:otherwise>
									<td>
										<liferay-ui:message key="you-are-not-subscribed-to-this-page" />
									</td>
									<td>
										<portlet:actionURL name="/wiki/edit_page" var="subscribeURL">
											<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
											<portlet:param name="redirect" value="<%= currentURL %>" />
											<portlet:param name="nodeId" value="<%= String.valueOf(wikiPage.getNodeId()) %>" />
											<portlet:param name="title" value="<%= String.valueOf(wikiPage.getTitle()) %>" />
										</portlet:actionURL>

										<clay:link
											aria-label='<%= LanguageUtil.get(request, "subscribe") %>'
											href="<%= subscribeURL %>"
											icon="check-circle-full"
											label="subscribe"
											small="<%= true %>"
										/>
									</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:if>

					<c:if test="<%= WikiNodePermission.contains(permissionChecker, node, ActionKeys.SUBSCRIBE) %>">
						<tr>
							<c:choose>
								<c:when test="<%= SubscriptionLocalServiceUtil.isSubscribed(user.getCompanyId(), user.getUserId(), WikiNode.class.getName(), node.getNodeId()) %>">
									<td>
										<liferay-ui:message key="you-are-subscribed-to-this-wiki" />
									</td>
									<td>
										<portlet:actionURL name="/wiki/edit_node" var="unsubscribeURL">
											<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.UNSUBSCRIBE %>" />
											<portlet:param name="redirect" value="<%= currentURL %>" />
											<portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" />
										</portlet:actionURL>

										<clay:link
											aria-label='<%= LanguageUtil.get(request, "unsubscribe") %>'
											href="<%= unsubscribeURL %>"
											icon="times-circle"
											label="unsubscribe"
											small="<%= true %>"
										/>
									</td>
								</c:when>
								<c:otherwise>
									<td>
										<liferay-ui:message key="you-are-not-subscribed-to-this-wiki" />
									</td>
									<td>
										<portlet:actionURL name="/wiki/edit_node" var="subscribeURL">
											<portlet:param name="<%= Constants.CMD %>" value="<%= Constants.SUBSCRIBE %>" />
											<portlet:param name="redirect" value="<%= currentURL %>" />
											<portlet:param name="nodeId" value="<%= String.valueOf(node.getNodeId()) %>" />
										</portlet:actionURL>

										<clay:link
											aria-label='<%= LanguageUtil.get(request, "subscribe") %>'
											href="<%= subscribeURL %>"
											icon="check-circle-full"
											label="subscribe"
											small="<%= true %>"
										/>
									</td>
								</c:otherwise>
							</c:choose>
						</tr>
					</c:if>
				</table>
			</td>
		</tr>
	</c:if>

	<c:if test="<%= WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.PERMISSIONS) || (WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.UPDATE) && WikiNodePermission.contains(permissionChecker, wikiPage.getNodeId(), ActionKeys.ADD_PAGE)) || WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.DELETE) %>">
		<tr>
			<th class="table-header">
				<liferay-ui:message key="advanced-actions" />
			</th>
			<td class="table-cell">
				<div class="d-inline-flex">
					<c:if test="<%= WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.PERMISSIONS) %>">
						<liferay-security:permissionsURL
							modelResource="<%= WikiPage.class.getName() %>"
							modelResourceDescription="<%= HtmlUtil.escape(wikiPage.getTitle()) %>"
							resourcePrimKey="<%= String.valueOf(wikiPage.getResourcePrimKey()) %>"
							var="permissionsURL"
							windowState="<%= LiferayWindowState.POP_UP.toString() %>"
						/>

						<clay:button
							additionalProps='<%=
								HashMapBuilder.<String, Object>put(
									"url", permissionsURL
								).build()
							%>'
							aria-label='<%= LanguageUtil.get(request, "permissions") %>'
							displayType="link"
							icon="lock"
							label="permissions"
							propsTransformer="{WikiPagePermissionsButtonPropsTransformer} from wiki-web"
							small="<%= true %>"
						/>
					</c:if>

					<c:if test="<%= WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.UPDATE) && WikiNodePermission.contains(permissionChecker, wikiPage.getNodeId(), ActionKeys.ADD_PAGE) %>">
						<clay:link
							aria-label='<%= LanguageUtil.get(request, "copy") %>'
							displayType="link"
							href='<%=
								PortletURLBuilder.create(
									PortletURLUtil.clone(viewPageURL, renderResponse)
								).setMVCRenderCommandName(
									"/wiki/edit_page"
								).setParameter(
									"editTitle", "1"
								).setParameter(
									"nodeId", wikiPage.getNodeId()
								).setParameter(
									"templateNodeId", wikiPage.getNodeId()
								).setParameter(
									"templateTitle", wikiPage.getTitle()
								).setParameter(
									"title", StringPool.BLANK
								).buildString()
							%>'
							icon="paste"
							label="copy"
							small="<%= true %>"
							type="button"
						/>
					</c:if>

					<c:if test="<%= WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.UPDATE) && WikiNodePermission.contains(permissionChecker, wikiPage.getNodeId(), ActionKeys.ADD_PAGE) %>">
						<clay:link
							aria-label='<%= LanguageUtil.get(request, "move") %>'
							displayType="link"
							href='<%=
								PortletURLBuilder.create(
									PortletURLUtil.clone(viewPageURL, renderResponse)
								).setMVCRenderCommandName(
									"/wiki/move_page"
								).setRedirect(
									viewPageURL
								).buildString()
							%>'
							icon="move"
							label="move"
							small="<%= true %>"
							type="button"
						/>
					</c:if>

					<c:if test="<%= WikiPagePermission.contains(permissionChecker, wikiPage, ActionKeys.DELETE) %>">

						<%
						PortletURL deletePageURL = PortletURLBuilder.create(
							PortletURLUtil.clone(editPageURL, PortletRequest.ACTION_PHASE, renderResponse)
						).setActionName(
							"/wiki/edit_page"
						).buildPortletURL();

						if (trashHelper.isTrashEnabled(scopeGroupId)) {
							deletePageURL.setParameter(Constants.CMD, Constants.MOVE_TO_TRASH);
						}
						else {
							deletePageURL.setParameter(Constants.CMD, Constants.DELETE);
						}

						deletePageURL.setParameter(
							"redirect",
							PortletURLBuilder.create(
								PortletURLUtil.clone(viewPageURL, renderResponse)
							).setParameter(
								"title", wikiGroupServiceConfiguration.frontPageName()
							).buildString());
						%>

						<clay:button
							additionalProps='<%=
								HashMapBuilder.<String, Object>put(
									"trashEnabled", trashHelper.isTrashEnabled(scopeGroupId)
								).put(
									"url", deletePageURL.toString()
								).build()
							%>'
							aria-label='<%= LanguageUtil.get(request, "delete") %>'
							displayType="link"
							icon="trash"
							label="delete"
							propsTransformer="{WikiPageDeleteButtonPropsTransformer} from wiki-web"
							small="<%= true %>"
						/>
					</c:if>
				</div>
			</td>
		</tr>
	</c:if>
</table>