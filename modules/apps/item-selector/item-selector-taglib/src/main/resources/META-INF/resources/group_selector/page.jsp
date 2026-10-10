<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/group_selector/init.jsp" %>

<%
GroupSelectorDisplayContext groupSelectorDisplayContext = new GroupSelectorDisplayContext(liferayPortletRequest);

Set<String> groupTypes = groupSelectorDisplayContext.getGroupTypes();
%>

<liferay-util:html-top
	outputKey="com.liferay.item.selector.taglib#/group_selector/page.jsp"
>
	<aui:link hashedFile="<%= true %>" href="item-selector-taglib/css/group_selector.css" rel="stylesheet" type="text/css" />
</liferay-util:html-top>

<c:if test="<%= !groupSelectorDisplayContext.isShowBreadcrumb() && (groupTypes.size() > 1) %>">
	<clay:container-fluid>
		<div class="btn-group btn-group-sm my-3" role="group">

			<%
			for (String curGroupType : groupTypes) {
			%>

				<a class="btn btn-secondary <%= groupSelectorDisplayContext.isGroupTypeActive(curGroupType) ? "active" : StringPool.BLANK %>" href="<%= groupSelectorDisplayContext.getGroupItemSelectorURL(curGroupType) %>"><%= groupSelectorDisplayContext.getGroupItemSelectorLabel(curGroupType) %></a>

			<%
			}
			%>

		</div>
	</clay:container-fluid>
</c:if>

<clay:container-fluid
	cssClass="lfr-item-viewer"
>
	<c:if test="<%= groupSelectorDisplayContext.isShowBreadcrumb() %>">
		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= groupSelectorDisplayContext.getBreadcrumbEntries() %>"
		/>
	</c:if>

	<c:choose>
		<c:when test="<%= groupSelectorDisplayContext.isShowGroupTypes() %>">
			<liferay-ui:search-container
				searchContainer="<%= groupSelectorDisplayContext.getGroupTypesSearchContainer() %>"
				var="groupTypesSearchContainer"
			>
				<liferay-ui:search-container-row
					className="java.lang.String"
					cssClass="card-page-item card-page-item-directory"
					modelVar="curGroupType"
				>
					<liferay-ui:search-container-column-text
						colspan="<%= 3 %>"
					>
						<clay:navigation-card
							navigationCard="<%= new GroupTypeNavigationCard(groupSelectorDisplayContext, curGroupType) %>"
						/>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="icon"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</c:when>
		<c:otherwise>
			<liferay-ui:search-container
				searchContainer="<%= groupSelectorDisplayContext.getSearchContainer() %>"
				var="listSearchContainer"
			>
				<liferay-ui:search-container-row
					className="com.liferay.portal.kernel.model.Group"
					cssClass="card-page-item card-page-item-directory"
					modelVar="curGroup"
				>
					<liferay-ui:search-container-column-text
						colspan="<%= 3 %>"
					>
						<c:choose>
							<c:when test="<%= groupSelectorDisplayContext.isShowBreadcrumb() %>">
								<clay:vertical-card
									verticalCard="<%= new GroupVerticalCard(curGroup, groupSelectorDisplayContext, request) %>"
								/>
							</c:when>
							<c:otherwise>
								<clay:navigation-card
									navigationCard="<%= new GroupNavigationCard(curGroup, groupSelectorDisplayContext, request) %>"
								/>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-column-text>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					displayStyle="icon"
					markupView="lexicon"
				/>
			</liferay-ui:search-container>
		</c:otherwise>
	</c:choose>
</clay:container-fluid>