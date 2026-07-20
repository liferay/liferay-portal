<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
JournalArticle article = journalDisplayContext.getArticle();

Map<String, Object> componentContext = journalDisplayContext.getComponentContext();

JournalHistoryDisplayContext journalHistoryDisplayContext = new JournalHistoryDisplayContext(renderRequest, renderResponse, journalDisplayContext.getArticle());

JournalHistoryManagementToolbarDisplayContext journalHistoryManagementToolbarDisplayContext = new JournalHistoryManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, article, journalHistoryDisplayContext);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(journalHistoryDisplayContext.getBackURL());
portletDisplay.setURLBackTitle(portletDisplay.getPortletDisplayName());

renderResponse.setTitle(article.getTitle(locale));
%>

<clay:navigation-bar
	navigationItems="<%= journalHistoryDisplayContext.getNavigationItems() %>"
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= journalHistoryManagementToolbarDisplayContext %>"
	propsTransformer="{ArticleHistoryManagementToolbarPropsTransformer} from journal-web"
/>

<aui:form action="<%= journalHistoryDisplayContext.getPortletURL() %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="referringPortletResource" type="hidden" value="<%= journalHistoryDisplayContext.getReferringPortletResource() %>" />
	<aui:input name="groupId" type="hidden" value="<%= String.valueOf(article.getGroupId()) %>" />

	<liferay-ui:search-container
		id="articleVersions"
		searchContainer="<%= journalHistoryDisplayContext.getArticleSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.journal.model.JournalArticle"
			modelVar="articleVersion"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"actions", journalHistoryManagementToolbarDisplayContext.getAvailableActions(articleVersion)
				).build());

			row.setPrimaryKey(articleVersion.getArticleId() + JournalPortlet.VERSION_SEPARATOR + articleVersion.getVersion());
			%>

			<c:choose>
				<c:when test='<%= Objects.equals(journalHistoryDisplayContext.getDisplayStyle(), "descriptive") %>'>
					<liferay-ui:search-container-column-text>
						<liferay-user:user-portrait
							userId="<%= articleVersion.getStatusByUserId() %>"
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
							<liferay-ui:message arguments="<%= new String[] {HtmlUtil.escape(articleVersion.getStatusByUserName()), modifiedDateDescription} %>" key="x-modified-x-ago" />
						</div>

						<div class="h5">
							<%= HtmlUtil.escape(articleVersion.getTitle(locale)) %>
						</div>

						<div class="h6 text-default">
							<liferay-portal-workflow:status
								showStatusLabel="<%= false %>"
								status="<%= articleVersion.getStatus() %>"
								version="<%= String.valueOf(articleVersion.getVersion()) %>"
							/>
						</div>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							additionalProps='<%=
								HashMapBuilder.<String, Object>put(
									"trashEnabled", componentContext.get("trashEnabled")
								).build()
							%>'
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= journalDisplayContext.getArticleHistoryActionDropdownItems(articleVersion) %>"
							propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(journalHistoryDisplayContext.getDisplayStyle(), "icon") %>'>
					<liferay-ui:search-container-column-text>
						<clay:vertical-card
							additionalProps='<%=
								HashMapBuilder.<String, Object>put(
									"trashEnabled", componentContext.get("trashEnabled")
								).build()
							%>'
							propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
							verticalCard="<%= new JournalArticleHistoryVerticalCard(articleVersion, renderRequest, renderResponse, searchContainer.getRowChecker(), assetDisplayPageFriendlyURLProvider, trashHelper) %>"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
				<c:when test='<%= Objects.equals(journalHistoryDisplayContext.getDisplayStyle(), "list") %>'>
					<liferay-ui:search-container-column-text
						name="id"
						value="<%= HtmlUtil.escape(articleVersion.getArticleId()) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200 table-title"
						name="title"
						value="<%= HtmlUtil.escape(articleVersion.getTitle(locale)) %>"
					/>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-100"
						name="version"
						orderable="<%= true %>"
					/>

					<liferay-ui:search-container-column-status
						name="status"
					/>

					<liferay-ui:search-container-column-date
						cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
						name="modified-date"
						orderable="<%= true %>"
						property="modifiedDate"
					/>

					<c:if test="<%= article.getDisplayDate() != null %>">
						<liferay-ui:search-container-column-date
							cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
							name="display-date"
							orderable="<%= true %>"
							property="displayDate"
						/>
					</c:if>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-100"
						name="author"
						value="<%= HtmlUtil.escape(articleVersion.getStatusByUserName()) %>"
					/>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							additionalProps='<%=
								HashMapBuilder.<String, Object>put(
									"trashEnabled", componentContext.get("trashEnabled")
								).build()
							%>'
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= journalDisplayContext.getArticleHistoryActionDropdownItems(articleVersion) %>"
							propsTransformer="{ElementsDefaultPropsTransformer} from journal-web"
						/>
					</liferay-ui:search-container-column-text>
				</c:when>
			</c:choose>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= journalHistoryDisplayContext.getDisplayStyle() %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>

<c:if test='<%= FeatureFlagManagerUtil.isEnabled(themeDisplay.getCompanyId(), "LPD-72278") %>'>
	<div id="<portlet:namespace />addToLaunchModal">
		<react:component
			componentId="addToLaunchModal"
			module="{AddToLaunchModal} from launch-web"
		/>
	</div>
</c:if>