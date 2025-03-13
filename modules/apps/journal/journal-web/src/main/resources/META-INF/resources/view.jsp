<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalManagementToolbarDisplayContext journalManagementToolbarDisplayContext = null;

if (!journalDisplayContext.isSearch() || journalDisplayContext.isShowWebContent()) {
	journalManagementToolbarDisplayContext = new JournalManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, journalDisplayContext, trashHelper);
}
else if (journalDisplayContext.isIndexAllArticleVersions() && journalDisplayContext.isShowVersions()) {
	journalManagementToolbarDisplayContext = new JournalArticleVersionsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, journalDisplayContext, trashHelper);
}
else if (journalDisplayContext.isShowComments()) {
	journalManagementToolbarDisplayContext = new JournalArticleCommentsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, journalDisplayContext, trashHelper);
}
else {
	journalManagementToolbarDisplayContext = new JournalManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, journalDisplayContext, trashHelper);
}
%>

<liferay-ui:success key='<%= portletDisplay.getId() + "requestProcessed" %>' message="your-request-completed-successfully" />

<c:if test='<%= MultiSessionMessages.contains(renderRequest, "articleCreated") || MultiSessionMessages.contains(renderRequest, "articlePending") || MultiSessionMessages.contains(renderRequest, "articlePendingScheduled") || MultiSessionMessages.contains(renderRequest, "articleScheduled") || MultiSessionMessages.contains(renderRequest, "articleUpdated") %>'>

	<%
	long id = GetterUtil.getLong(MultiSessionMessages.get(renderRequest, "articleCreated"));

	if (MultiSessionMessages.contains(renderRequest, "articlePending")) {
		id = GetterUtil.getLong(MultiSessionMessages.get(renderRequest, "articlePending"));
	}
	else if (MultiSessionMessages.contains(renderRequest, "articlePendingScheduled")) {
		id = GetterUtil.getLong(MultiSessionMessages.get(renderRequest, "articlePendingScheduled"));
	}
	else if (MultiSessionMessages.contains(renderRequest, "articleScheduled")) {
		id = GetterUtil.getLong(MultiSessionMessages.get(renderRequest, "articleScheduled"));
	}
	else if (MultiSessionMessages.contains(renderRequest, "articleUpdated")) {
		id = GetterUtil.getLong(MultiSessionMessages.get(renderRequest, "articleUpdated"));
	}

	JournalArticle article = JournalArticleLocalServiceUtil.fetchJournalArticle(id);
	%>

	<c:if test="<%= article != null %>">
		<liferay-util:buffer
			var="alertMessage"
		>
			<liferay-util:buffer
				var="articleLink"
			>
				<clay:link
					cssClass="alert-link"
					href='<%=
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCRenderCommandName(
							"/journal/edit_article"
						).setRedirect(
							currentURL
						).setParameter(
							"articleId", article.getArticleId()
						).setParameter(
							"backURLTitle", portletDisplay.getPortletDisplayName()
						).setParameter(
							"folderId", article.getFolderId()
						).setParameter(
							"groupId", article.getGroupId()
						).setParameter(
							"version", article.getVersion()
						).buildString()
					%>'
					label="<%= article.getTitle(locale) %>"
					translated="<%= false %>"
				/>
			</liferay-util:buffer>

			<c:choose>
				<c:when test='<%= MultiSessionMessages.contains(renderRequest, "articleCreated") %>'>
					<liferay-ui:message arguments="<%= articleLink %>" key="x-was-created-successfully" />
				</c:when>
				<c:when test='<%= MultiSessionMessages.contains(renderRequest, "articlePending") %>'>
					<liferay-ui:message arguments="<%= articleLink %>" key="x-has-been-submitted-for-workflow" />
				</c:when>
				<c:when test='<%= MultiSessionMessages.contains(renderRequest, "articlePendingScheduled") %>'>
					<liferay-ui:message arguments="<%= articleLink %>" key="x-has-been-scheduled-and-submitted-for-workflow" />
				</c:when>
				<c:when test='<%= MultiSessionMessages.contains(renderRequest, "articleScheduled") %>'>
					<liferay-ui:message arguments="<%= new Object[] {articleLink, dateTimeFormat.format(article.getDisplayDate())} %>" key="x-will-be-published-on-x" />
				</c:when>
				<c:otherwise>
					<liferay-ui:message arguments="<%= articleLink %>" key="x-was-updated-successfully" />
				</c:otherwise>
			</c:choose>
		</liferay-util:buffer>

		<liferay-frontend:component
			context='<%=
				HashMapBuilder.<String, Object>put(
					"alertMessage", alertMessage
				).build()
			%>'
			module="{SuccessMessageWithLink} from journal-web"
		/>
	</c:if>
</c:if>

<portlet:actionURL name="/journal/restore_trash_entries" var="restoreTrashEntriesURL" />

<liferay-trash:undo
	portletURL="<%= restoreTrashEntriesURL %>"
/>

<clay:navigation-bar
	inverted="<%= true %>"
	navigationItems='<%= journalDisplayContext.getNavigationItems("web-content") %>'
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= journalManagementToolbarDisplayContext %>"
	propsTransformer="{ManagementToolbarPropsTransformer} from journal-web"
/>

<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
	<c:if test="<%= journalDisplayContext.isShowInfoButton() %>">
		<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="/journal/info_panel" var="sidebarPanelURL">
			<portlet:param name="folderId" value="<%= String.valueOf(journalDisplayContext.getFolderId()) %>" />
		</liferay-portlet:resourceURL>

		<liferay-frontend:sidebar-panel
			resourceURL="<%= sidebarPanelURL %>"
			searchContainerId="articles"
		>
			<liferay-util:include page="/info_panel.jsp" servletContext="<%= application %>" />
		</liferay-frontend:sidebar-panel>
	</c:if>

	<clay:container-fluid
		cssClass="container-view sidenav-content"
	>

		<%
		VerticalNavItemList ddmStructureVerticalNavItemList = journalDisplayContext.getDDMStructureVerticalNavItemList();
		%>

		<c:choose>
			<c:when test="<%= ListUtil.isNotEmpty(ddmStructureVerticalNavItemList) %>">
				<clay:row>
					<clay:col
						lg="3"
					>
						<clay:vertical-nav
							verticalNavItems="<%= journalDisplayContext.getVerticalNavItemList() %>"
						/>

						<span class="c-mb-1 c-mt-3 sheet-tertiary-title text-2 text-secondary">
							<liferay-ui:message key="highlighted-structures" />
						</span>

						<clay:vertical-nav
							verticalNavItems="<%= ddmStructureVerticalNavItemList %>"
						/>
					</clay:col>

					<clay:col
						lg="9"
					>
						<clay:sheet
							size="full"
						>
							<h2 class="sheet-title"><%= journalDisplayContext.getTitle() %></h2>

							<%@ include file="/view_form.jspf" %>
						</clay:sheet>
					</clay:col>
				</clay:row>
			</c:when>
			<c:otherwise>
				<%@ include file="/view_form.jspf" %>
			</c:otherwise>
		</c:choose>
	</clay:container-fluid>
</div>

<%@ include file="/friendly_url_changed_message.jspf" %>