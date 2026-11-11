<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
RedirectNotFoundEntriesDisplayContext redirectNotFoundEntriesDisplayContext = (RedirectNotFoundEntriesDisplayContext)request.getAttribute(RedirectNotFoundEntriesDisplayContext.class.getName());
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= redirectNotFoundEntriesDisplayContext.getRedirectNotFoundEntriesManagementToolbarDisplayContext() %>"
	propsTransformer="{RedirectNotFoundEntriesManagementToolbarPropsTransformer} from redirect-web"
/>

<aui:form action="<%= redirectNotFoundEntriesDisplayContext.getActionURL() %>" cssClass="container-fluid" name="fm">
	<c:choose>
		<c:when test="<%= redirectNotFoundEntriesDisplayContext.hasResults() %>">
			<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
			<aui:input name="ignored" type="hidden" />

			<liferay-ui:search-container
				id="<%= redirectNotFoundEntriesDisplayContext.getSearchContainerId() %>"
				searchContainer="<%= redirectNotFoundEntriesDisplayContext.getSearchContainer() %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.redirect.model.RedirectNotFoundEntry"
					keyProperty="redirectNotFoundEntryId"
					modelVar="redirectNotFoundEntry"
				>

					<%
					row.setData(
						HashMapBuilder.<String, Object>put(
							"actions", redirectNotFoundEntriesDisplayContext.getAvailableActions(redirectNotFoundEntry)
						).build());
					%>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand"
						name="not-found-urls"
					>
						<%= HtmlUtil.escape(redirectNotFoundEntriesDisplayContext.getURL(redirectNotFoundEntry)) %>
					</liferay-ui:search-container-column-text>

					<c:if test='<%= StringUtil.equals("all", ParamUtil.getString(request, "filterType")) %>'>
						<liferay-ui:search-container-column-text
							cssClass="table-cell-expand-smallest table-cell-minw-200 table-column-text-center"
							name="ignored-urls"
						>
							<c:if test="<%= redirectNotFoundEntry.isIgnored() %>">
								<clay:icon
									symbol="hidden"
								/>
							</c:if>
						</liferay-ui:search-container-column-text>
					</c:if>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-column-text-end"
						name="requests"
					>
						<%= redirectNotFoundEntry.getRequestCount() %>
					</liferay-ui:search-container-column-text>

					<liferay-ui:search-container-column-text>
						<clay:dropdown-actions
							aria-label='<%= LanguageUtil.get(request, "show-actions") %>'
							dropdownItems="<%= redirectNotFoundEntriesDisplayContext.getActionDropdownItems(redirectNotFoundEntry) %>"
						/>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</c:when>
		<c:otherwise>
			<liferay-frontend:empty-result-message
				animationType="<%= AnimationType.SEARCH %>"
				description="<%= LanguageUtil.get(request, redirectNotFoundEntriesDisplayContext.getEmptyResultsMessage()) %>"
				title='<%= LanguageUtil.get(request, "all-is-in-order") %>'
			/>
		</c:otherwise>
	</c:choose>
</aui:form>