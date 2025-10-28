<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
Map<String, Object> componentContext = journalDisplayContext.getComponentContext();
%>

<portlet:actionURL name="/journal/restore_trash_entries" var="restoreTrashEntriesURL" />

<liferay-trash:undo
	portletURL="<%= restoreTrashEntriesURL %>"
/>

<liferay-ui:search-container
	cssClass='<%= journalDisplayContext.isSearch() ? "pt-0" : StringPool.BLANK %>'
	emptyResultsMessage="no-version-was-found"
	searchContainer="<%= journalDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.journal.model.JournalArticle"
		modelVar="articleVersion"
	>

		<%
		row.setPrimaryKey(articleVersion.getArticleId() + JournalPortlet.VERSION_SEPARATOR + articleVersion.getVersion());
		%>

		<c:choose>
			<c:when test='<%= Objects.equals(journalDisplayContext.getDisplayStyle(), "descriptive") %>'>
				<liferay-ui:search-container-column-text>
					<liferay-user:user-portrait
						userId="<%= articleVersion.getUserId() %>"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text
					colspan="<%= 2 %>"
				>

					<%
					Date createDate = articleVersion.getModifiedDate();

					String modifiedDateDescription = LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - createDate.getTime(), true);
					%>

					<div class="h6 text-default">
						<liferay-ui:message arguments="<%= new String[] {HtmlUtil.escape(articleVersion.getUserName()), modifiedDateDescription} %>" key="x-modified-x-ago" />
					</div>

					<div class="h5">
						<%= HtmlUtil.escape(articleVersion.getTitle(locale)) %>
					</div>

					<div>
						<clay:label
							displayType="secondary"
							label='<%= LanguageUtil.format(request, "version-x", String.valueOf(articleVersion.getVersion()), false) %>'
						/>
					</div>

					<liferay-portal-workflow:status
						showStatusLabel="<%= false %>"
						status="<%= articleVersion.getStatus() %>"
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						additionalProps='<%=
							HashMapBuilder.<String, Object>put(
								"trashEnabled", componentContext.get("trashEnabled")
							).build()
						%>'
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= journalDisplayContext.getArticleVersionActionDropdownItems(articleVersion) %>"
						propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
					/>
				</liferay-ui:search-container-column-text>
			</c:when>
			<c:when test='<%= Objects.equals(journalDisplayContext.getDisplayStyle(), "icon") %>'>
				<liferay-ui:search-container-column-text>
					<clay:vertical-card
						verticalCard="<%= new JournalArticleVersionVerticalCard(articleVersion, renderRequest, renderResponse, searchContainer.getRowChecker(), assetDisplayPageFriendlyURLProvider, trashHelper) %>"
					/>
				</liferay-ui:search-container-column-text>
			</c:when>
			<c:when test='<%= Objects.equals(journalDisplayContext.getDisplayStyle(), "list") %>'>
				<liferay-ui:search-container-column-text
					name="id"
					value="<%= HtmlUtil.escape(articleVersion.getArticleId()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="font-weight-semi-bold table-cell-expand"
					name="title"
					value="<%= HtmlUtil.escape(articleVersion.getTitle(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-minw-150"
					name="version"
					orderable="<%= true %>"
				>
					<clay:label
						displayType="secondary"
						label='<%= LanguageUtil.format(request, "version-x", String.valueOf(articleVersion.getVersion()), false) %>'
					/>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-status
					name="status"
				/>

				<liferay-ui:search-container-column-date
					name="modified-date"
					orderable="<%= true %>"
					property="modifiedDate"
				/>

				<c:if test="<%= articleVersion.getDisplayDate() != null %>">
					<liferay-ui:search-container-column-date
						name="display-date"
						orderable="<%= true %>"
						property="displayDate"
					/>
				</c:if>

				<liferay-ui:search-container-column-text
					name="author"
					value="<%= HtmlUtil.escape(PortalUtil.getUserName(articleVersion)) %>"
				/>

				<liferay-ui:search-container-column-text>
					<clay:dropdown-actions
						additionalProps='<%=
							HashMapBuilder.<String, Object>put(
								"trashEnabled", componentContext.get("trashEnabled")
							).build()
						%>'
						aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
						dropdownItems="<%= journalDisplayContext.getArticleVersionActionDropdownItems(articleVersion) %>"
						propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
					/>
				</liferay-ui:search-container-column-text>
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