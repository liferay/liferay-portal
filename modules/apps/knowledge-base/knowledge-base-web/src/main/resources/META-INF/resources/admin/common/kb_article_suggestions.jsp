<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/common/init.jsp" %>

<%
KBArticle kbArticle = (KBArticle)request.getAttribute(KBWebKeys.KNOWLEDGE_BASE_KB_ARTICLE);

boolean showAdminSuggestionView = false;

if (AdminPermission.contains(permissionChecker, scopeGroupId, KBActionKeys.VIEW_SUGGESTIONS) || KBArticlePermission.contains(permissionChecker, kbArticle, KBActionKeys.UPDATE)) {
	showAdminSuggestionView = true;
}

KBArticleURLHelper kbArticleURLHelper = new KBArticleURLHelper(renderRequest, renderResponse);

int kbCommentsCount = 0;
int pendingKBCommentsCount = 0;

if (showAdminSuggestionView) {
	kbCommentsCount = KBCommentLocalServiceUtil.getKBCommentsCount(KBArticle.class.getName(), kbArticle.getResourcePrimKey());
	pendingKBCommentsCount = KBCommentLocalServiceUtil.getKBCommentsCount(KBArticle.class.getName(), kbArticle.getResourcePrimKey(), new int[] {KBCommentConstants.STATUS_IN_PROGRESS, KBCommentConstants.STATUS_NEW});
}
else {
	kbCommentsCount = KBCommentLocalServiceUtil.getKBCommentsCount(themeDisplay.getUserId(), KBArticle.class.getName(), kbArticle.getResourcePrimKey());
}

RatingsType ratingsType = PortletRatingsDefinitionUtil.getRatingsType(themeDisplay.getCompanyId(), themeDisplay.getScopeGroupId(), KBArticle.class.getName());

if (ratingsType == null) {
	ratingsType = RatingsType.THUMBS;
}
%>

<c:if test="<%= ratingsType.equals(RatingsType.THUMBS) && themeDisplay.isSignedIn() %>">
	<a name="kbSuggestions"></a>

	<div id="<portlet:namespace />suggestionContainer">

		<%
		PortletURL viewKBArticleURL = kbArticleURLHelper.createViewWithCommentsURL(kbArticle);
		%>

		<liferay-portlet:actionURL name="/knowledge_base/update_kb_comment" var="updateKBCommentURL">
			<portlet:param name="redirect" value="<%= viewKBArticleURL.toString() %>" />
		</liferay-portlet:actionURL>

		<aui:form action="<%= updateKBCommentURL %>" cssClass="mb-5" method="post" name="suggestionFm">
			<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.ADD %>" />
			<aui:input name="classPK" type="hidden" value="<%= kbArticle.getResourcePrimKey() %>" />

			<liferay-ui:error exception="<%= KBCommentContentException.class %>" message="please-enter-valid-content" />

			<aui:model-context model="<%= KBComment.class %>" />

			<aui:input label="do-you-have-any-suggestions" name="content" />

			<aui:button-row cssClass="kb-submit-buttons">
				<aui:button type="submit" value="submit" />
			</aui:button-row>
		</aui:form>
	</div>

	<liferay-ui:success key="suggestionDeleted" message="suggestion-was-deleted-successfully" />

	<liferay-ui:success key="suggestionStatusUpdated" message="suggestion-status-was-updated-successfully" />

	<liferay-ui:success key="suggestionSaved" message="suggestion-was-saved-successfully" />

	<div class="h5 mb-0">
		<c:choose>
			<c:when test="<%= kbCommentsCount == 1 %>">
				<c:choose>
					<c:when test="<%= showAdminSuggestionView %>">
						<liferay-ui:message key="there-is-one-suggestion" />

						<c:if test="<%= pendingKBCommentsCount > 0 %>">
							(<liferay-ui:message arguments="<%= pendingKBCommentsCount %>" key="x-pending" />)
						</c:if>
					</c:when>
					<c:otherwise>
						<liferay-ui:message key="you-sent-one-suggestion-for-this-article" />
					</c:otherwise>
				</c:choose>
			</c:when>
			<c:when test="<%= kbCommentsCount > 1 %>">
				<c:choose>
					<c:when test="<%= showAdminSuggestionView %>">
						<liferay-ui:message arguments="<%= kbCommentsCount %>" key="there-are-x-suggestions" />

						<c:if test="<%= pendingKBCommentsCount > 0 %>">
							(<liferay-ui:message arguments="<%= pendingKBCommentsCount %>" key="x-pending" />)
						</c:if>
					</c:when>
					<c:otherwise>
						<liferay-ui:message arguments="<%= kbCommentsCount %>" key="you-sent-x-suggestions-for-this-article" />
					</c:otherwise>
				</c:choose>
			</c:when>
		</c:choose>
	</div>

	<c:if test="<%= kbCommentsCount > 0 %>">
		<c:choose>
			<c:when test="<%= showAdminSuggestionView %>">

				<%
				KBSuggestionListDisplayContext kbSuggestionListDisplayContext = new KBSuggestionListDisplayContext(request, kbArticle);

				request.setAttribute(KBWebKeys.KNOWLEDGE_BASE_KB_SUGGESTION_LIST_DISPLAY_CONTEXT, kbSuggestionListDisplayContext);

				SearchContainer<KBComment> kbCommentsSearchContainer = new SearchContainer(renderRequest, null, null, SearchContainer.DEFAULT_CUR_PARAM, SearchContainer.DEFAULT_DELTA, currentURLObj, null, kbSuggestionListDisplayContext.getEmptyResultsMessage());

				kbSuggestionListDisplayContext.populateResultsAndTotal(kbCommentsSearchContainer);

				request.setAttribute("view_kb_suggestions.jsp-resultRowSplitter", new KBCommentResultRowSplitter(kbSuggestionListDisplayContext, resourceBundle));

				request.setAttribute("view_kb_suggestions.jsp-searchContainer", kbCommentsSearchContainer);
				%>

				<liferay-util:include page="/admin/common/view_kb_suggestions_by_status.jsp" servletContext="<%= application %>" />
			</c:when>
			<c:otherwise>
				<liferay-portlet:renderURL varImpl="iteratorURL">
					<portlet:param name="expanded" value="<%= Boolean.TRUE.toString() %>" />
				</liferay-portlet:renderURL>

				<liferay-ui:search-container
					emptyResultsMessage="no-comments-found"
					iteratorURL="<%= iteratorURL %>"
					orderByComparator='<%= KBUtil.getKBCommentOrderByComparator("modified-date", "desc") %>'
					total="<%= kbCommentsCount %>"
				>
					<liferay-ui:search-container-results
						results="<%= KBCommentLocalServiceUtil.getKBComments(themeDisplay.getUserId(), KBArticle.class.getName(), kbArticle.getResourcePrimKey(), searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getOrderByComparator()) %>"
					/>

					<liferay-ui:search-container-row
						className="com.liferay.knowledge.base.model.KBComment"
						modelVar="kbComment"
					>
						<liferay-ui:search-container-column-text
							cssClass="kb-column-no-wrap"
							name="comment"
							orderable="<%= true %>"
						>
							<%= HtmlUtil.escape(kbComment.getContent()) %>
						</liferay-ui:search-container-column-text>

						<liferay-ui:search-container-column-date
							cssClass="kb-column-no-wrap"
							name="date"
							orderable="<%= true %>"
							orderableProperty="modified-date"
							value="<%= kbComment.getModifiedDate() %>"
						/>

						<liferay-ui:search-container-column-text
							cssClass="kb-column-no-wrap"
							name="status"
							orderable="<%= true %>"
						>
							<liferay-ui:message key="<%= KBUtil.getStatusLabel(kbComment.getStatus()) %>" />
						</liferay-ui:search-container-column-text>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</c:otherwise>
		</c:choose>
	</c:if>
</c:if>