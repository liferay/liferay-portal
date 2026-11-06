<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
SimpleSiteItemSelectorViewDisplayContext simpleSiteItemSelectorViewDisplayContext = (SimpleSiteItemSelectorViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new SimpleSiteItemSelectorViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, simpleSiteItemSelectorViewDisplayContext.getSearchContainer()) %>"
/>

<div class="container-fluid container-fluid-max-xl" id="<portlet:namespace />siteSelectorWrapper">
	<liferay-ui:search-container
		id="sites"
		searchContainer="<%= simpleSiteItemSelectorViewDisplayContext.getSearchContainer() %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.portal.kernel.model.Group"
			keyPropertyName="groupId"
			modelVar="group"
		>
			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
				value="<%= HtmlUtil.escape(group.getName(locale)) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="channel"
				value="<%= HtmlUtil.escape(simpleSiteItemSelectorViewDisplayContext.getChannelUsingSite(group.getGroupId())) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
			>
				<c:choose>
					<c:when test="<%= simpleSiteItemSelectorViewDisplayContext.isSiteAvailable(group.getGroupId()) %>">
						<aui:button cssClass="selector-button" data='<%= HashMapBuilder.<String, Object>put("id", group.getGroupId()).put("name", group.getName(locale)).build() %>' value="choose" />
					</c:when>
					<c:otherwise>
						<liferay-ui:message key="that-site-is-already-associated-with-another-channel" />
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-column-text>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</div>