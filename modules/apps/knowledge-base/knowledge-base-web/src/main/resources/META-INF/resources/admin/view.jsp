<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
long kbFolderClassNameId = PortalUtil.getClassNameId(KBFolderConstants.getClassName());

long parentResourceClassNameId = ParamUtil.getLong(request, "parentResourceClassNameId", kbFolderClassNameId);

long parentResourcePrimKey = ParamUtil.getLong(request, "parentResourcePrimKey", KBFolderConstants.DEFAULT_PARENT_FOLDER_ID);

KBAdminManagementToolbarDisplayContext kbAdminManagementToolbarDisplayContext = new KBAdminManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, renderRequest, renderResponse, portletConfig, trashHelper);
%>

<portlet:actionURL name="/knowledge_base/restore_kb_object" var="restoreTrashEntriesURL" />

<liferay-trash:undo
	portletURL="<%= restoreTrashEntriesURL %>"
/>

<liferay-util:include page="/admin/common/vertical_menu.jsp" servletContext="<%= application %>" />

<div class="knowledge-base-admin-content">
	<clay:management-toolbar
		actionDropdownItems="<%= kbAdminManagementToolbarDisplayContext.getActionDropdownItems() %>"
		additionalProps='<%=
			HashMapBuilder.<String, Object>put(
				"trashEnabled", kbAdminManagementToolbarDisplayContext.isTrashEnabled()
			).build()
		%>'
		clearResultsURL="<%= String.valueOf(kbAdminManagementToolbarDisplayContext.getSearchURL()) %>"
		creationMenu="<%= kbAdminManagementToolbarDisplayContext.getCreationMenu() %>"
		disabled="<%= kbAdminManagementToolbarDisplayContext.isDisabled() %>"
		infoPanelId="infoPanelId"
		itemsTotal="<%= kbAdminManagementToolbarDisplayContext.getTotal() %>"
		orderDropdownItems="<%= kbAdminManagementToolbarDisplayContext.getOrderByDropdownItems() %>"
		propsTransformer="{ManagementToolbarPropsTransformer} from knowledge-base-web"
		searchActionURL="<%= String.valueOf(kbAdminManagementToolbarDisplayContext.getSearchURL()) %>"
		searchContainerId="kbObjects"
		selectable="<%= true %>"
		showInfoButton="<%= kbAdminManagementToolbarDisplayContext.isShowInfoButton() %>"
		sortingOrder="<%= kbAdminManagementToolbarDisplayContext.getOrderByType() %>"
		sortingURL="<%= String.valueOf(kbAdminManagementToolbarDisplayContext.getSortingURL()) %>"
		viewTypeItems="<%= kbAdminManagementToolbarDisplayContext.getViewTypeItems() %>"
	/>

	<div class="closed sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
		<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= false %>" id="infoPanel" var="sidebarPanelURL">
			<portlet:param name="parentResourceClassNameId" value="<%= String.valueOf(parentResourceClassNameId) %>" />
			<portlet:param name="parentResourcePrimKey" value="<%= String.valueOf(parentResourcePrimKey) %>" />
			<portlet:param name="showSidebarHeader" value="<%= Boolean.TRUE.toString() %>" />
		</liferay-portlet:resourceURL>

		<liferay-frontend:sidebar-panel
			resourceURL="<%= sidebarPanelURL %>"
			searchContainerId="kbObjects"
		>

			<%
			request.setAttribute(KBWebKeys.SHOW_SIDEBAR_HEADER, Boolean.TRUE);
			%>

			<liferay-util:include page="/admin/info_panel.jsp" servletContext="<%= application %>" />
		</liferay-frontend:sidebar-panel>

		<clay:container-fluid
			cssClass="container-view sidenav-content"
			size="xxxl"
		>

			<%
			KBAdminViewDisplayContext kbAdminViewDisplayContext = new KBAdminViewDisplayContext(parentResourceClassNameId, parentResourcePrimKey, request, liferayPortletResponse);

			kbAdminViewDisplayContext.populatePortletBreadcrumbEntries(currentURLObj);
			%>

			<liferay-portlet:actionURL name="/knowledge_base/delete_kb_articles_and_folders" varImpl="deleteKBArticlesAndFoldersURL" />

			<aui:form action="<%= deleteKBArticlesAndFoldersURL %>" name="fm">
				<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= kbAdminManagementToolbarDisplayContext.isTrashEnabled() ? Constants.MOVE_TO_TRASH : Constants.DELETE %>" />
				<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

				<liferay-ui:error exception="<%= KBArticlePriorityException.class %>" message='<%= LanguageUtil.format(request, "please-enter-a-priority-that-is-greater-than-x", "0", false) %>' translateMessage="<%= false %>" />

				<c:if test='<%= SessionMessages.contains(renderRequest, "importedKBArticlesCount") %>'>

					<%
					int importedKBArticlesCount = GetterUtil.getInteger(SessionMessages.get(renderRequest, "importedKBArticlesCount"));
					%>

					<c:choose>
						<c:when test="<%= importedKBArticlesCount > 0 %>">
							<div class="alert alert-success">
								<liferay-ui:message key="your-request-completed-successfully" />

								<liferay-ui:message arguments="<%= importedKBArticlesCount %>" key="a-total-of-x-articles-have-been-imported" />
							</div>
						</c:when>
						<c:otherwise>
							<clay:alert
								displayType="warning"
								message='<%= LanguageUtil.format(request, "the-content-was-imported-but-no-articles-were-found-with-one-of-the-supported-extensions-x", HtmlUtil.escape(StringUtil.merge(kbGroupServiceConfiguration.markdownImporterArticleExtensions(), StringPool.COMMA_AND_SPACE))) %>'
								title="Alert"
							/>
						</c:otherwise>
					</c:choose>
				</c:if>

				<liferay-site-navigation:breadcrumb
					breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, false, false, false, false, true) %>"
				/>

				<%
				SearchContainer<Object> kbAdminManagementToolbarDisplayContextSearchContainer = kbAdminManagementToolbarDisplayContext.getSearchContainer();
				%>

				<c:choose>
					<c:when test="<%= kbAdminManagementToolbarDisplayContextSearchContainer.hasResults() || kbAdminManagementToolbarDisplayContext.isSearch() %>">
						<c:choose>
							<c:when test='<%= Objects.equals(kbAdminManagementToolbarDisplayContext.getDisplayStyle(), "descriptive") %>'>
								<liferay-util:include page="/admin/view_descriptive.jsp" servletContext="<%= application %>" />
							</c:when>
							<c:otherwise>
								<liferay-util:include page="/admin/view_table.jsp" servletContext="<%= application %>" />
							</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<liferay-frontend:empty-result-message
							actionDropdownItems="<%= kbAdminManagementToolbarDisplayContext.getEmptyStateActionDropdownItems() %>"
							animationType="<%= AnimationType.EMPTY %>"
							buttonCssClass="secondary"
							title='<%= (parentResourcePrimKey == KBFolderConstants.DEFAULT_PARENT_FOLDER_ID) ? LanguageUtil.get(request, "knowledge-base-is-empty") : LanguageUtil.get(request, "this-folder-is-empty") %>'
						/>
					</c:otherwise>
				</c:choose>
			</aui:form>
		</clay:container-fluid>
	</div>
</div>

<%
String kbArticleSuccessMessage = GetterUtil.getString(MultiSessionMessages.get(renderRequest, "kbArticleSuccessMessage"));
%>

<c:if test="<%= Validator.isNotNull(kbArticleSuccessMessage) %>">
	<liferay-frontend:component
		context='<%=
			HashMapBuilder.<String, Object>put(
				"autoClose", 20000
			).put(
				"message", kbArticleSuccessMessage
			).build()
		%>'
		module="{openToast} from knowledge-base-web"
	/>
</c:if>

<div>

	<%
	LockedKBArticleException lockedKBArticleException = (LockedKBArticleException)MultiSessionErrors.get(liferayPortletRequest, LockedKBArticleException.class.getName());
	%>

	<react:component
		module="{LockedKBArticleModal} from knowledge-base-web"
		props='<%=
			HashMapBuilder.<String, Object>put(
				"actionLabel", (lockedKBArticleException != null) ? LanguageUtil.get(request, lockedKBArticleException.getCmd()) : null
			).put(
				"actionURL", (lockedKBArticleException != null) ? lockedKBArticleException.getActionURL() : null
			).put(
				"groupAdmin", permissionChecker.isGroupAdmin(scopeGroupId)
			).put(
				"open", lockedKBArticleException != null
			).put(
				"userName", (lockedKBArticleException != null) ? lockedKBArticleException.getUserName() : null
			).build()
		%>'
	/>
</div>