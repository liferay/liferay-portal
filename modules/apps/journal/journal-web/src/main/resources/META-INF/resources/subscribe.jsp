<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalArticle article = (JournalArticle)request.getAttribute("info_panel.jsp-entry");
JournalFolder folder = (JournalFolder)request.getAttribute("info_panel.jsp-folder");

long folderId = JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID;

if (folder != null) {
	folderId = folder.getFolderId();
}

long ddmStructureId = ParamUtil.getLong(request, "ddmStructureId");

String subscribeActionName = StringPool.BLANK;
String unsubscribeActionName = StringPool.BLANK;
%>

<div class="subscribe-action">
	<c:if test="<%= JournalPermission.contains(permissionChecker, scopeGroupId, ActionKeys.SUBSCRIBE) && JournalUtil.getEmailArticleAnyEventEnabled(journalGroupServiceConfiguration) %>">

		<%
		boolean subscribed = false;
		boolean unsubscribable = true;

		if (ddmStructureId > 0) {
			subscribed = JournalUtil.isSubscribedToStructure(themeDisplay.getCompanyId(), user.getUserId(), ddmStructureId);

			subscribeActionName = "/journal/subscribe_ddm_structure";
			unsubscribeActionName = "/journal/unsubscribe_ddm_structure";
		}
		else if ((ddmStructureId <= 0) && (article != null)) {
			subscribed = JournalUtil.isSubscribedToArticle(themeDisplay.getCompanyId(), scopeGroupId, user.getUserId(), article.getResourcePrimKey());

			subscribeActionName = "/journal/subscribe_article";
			unsubscribeActionName = "/journal/unsubscribe_article";
		}
		else {
			subscribed = JournalUtil.isSubscribedToFolder(themeDisplay.getCompanyId(), scopeGroupId, user.getUserId(), folderId);

			if (subscribed && !JournalUtil.isSubscribedToFolder(themeDisplay.getCompanyId(), scopeGroupId, user.getUserId(), folderId, false)) {
				unsubscribable = false;
			}

			subscribeActionName = "/journal/subscribe_folder";
			unsubscribeActionName = "/journal/unsubscribe_folder";
		}
		%>

		<c:choose>
			<c:when test="<%= subscribed %>">
				<c:choose>
					<c:when test="<%= unsubscribable %>">
						<portlet:actionURL name="<%= unsubscribeActionName %>" var="unsubscribeURL">
							<portlet:param name="redirect" value="<%= currentURL %>" />

							<c:choose>
								<c:when test="<%= ddmStructureId > 0 %>">
									<portlet:param name="ddmStructureId" value="<%= String.valueOf(ddmStructureId) %>" />
								</c:when>
								<c:when test="<%= (ddmStructureId <= 0) && (article != null) %>">
									<portlet:param name="articleId" value="<%= String.valueOf(article.getResourcePrimKey()) %>" />
								</c:when>
								<c:otherwise>
									<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
								</c:otherwise>
							</c:choose>
						</portlet:actionURL>

						<clay:link
							aria-label='<%= LanguageUtil.get(request, "unsubscribe") %>'
							borderless="<%= true %>"
							cssClass="lfr-portal-tooltip"
							displayType="secondary"
							href="<%= unsubscribeURL %>"
							icon="bell-off"
							monospaced="<%= true %>"
							small="<%= true %>"
							title='<%= LanguageUtil.get(request, "unsubscribe") %>'
							type="button"
						/>
					</c:when>
					<c:otherwise>
						<clay:button
							borderless="<%= true %>"
							cssClass="lfr-portal-tooltip"
							disabled="<%= true %>"
							displayType="secondary"
							monospaced="<%= true %>"
							small="<%= true %>"
							title='<%= LanguageUtil.get(request, "subscribed-to-a-parent-folder") %>'
						>
							<clay:icon
								aria-label='<%= LanguageUtil.get(request, "subscribed-to-a-parent-folder") %>'
								symbol="bell-off"
								title='<%= LanguageUtil.get(request, "subscribed-to-a-parent-folder") %>'
							/>
						</clay:button>
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:otherwise>
				<portlet:actionURL name="<%= subscribeActionName %>" var="subscribeURL">
					<portlet:param name="redirect" value="<%= currentURL %>" />

					<c:choose>
						<c:when test="<%= ddmStructureId > 0 %>">
							<portlet:param name="ddmStructureId" value="<%= String.valueOf(ddmStructureId) %>" />
						</c:when>
						<c:when test="<%= (ddmStructureId <= 0) && (article != null) %>">
							<portlet:param name="articleId" value="<%= String.valueOf(article.getResourcePrimKey()) %>" />
						</c:when>
						<c:otherwise>
							<portlet:param name="folderId" value="<%= String.valueOf(folderId) %>" />
						</c:otherwise>
					</c:choose>
				</portlet:actionURL>

				<clay:link
					aria-label='<%= LanguageUtil.get(request, "subscribe") %>'
					borderless="<%= true %>"
					cssClass="lfr-portal-tooltip"
					displayType="secondary"
					href="<%= subscribeURL %>"
					icon="bell-on"
					monospaced="<%= true %>"
					small="<%= true %>"
					title='<%= LanguageUtil.get(request, "subscribe") %>'
					type="button"
				/>
			</c:otherwise>
		</c:choose>
	</c:if>
</div>