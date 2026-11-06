<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceChannelItemSelectorViewDisplayContext commerceChannelItemSelectorViewDisplayContext = (CommerceChannelItemSelectorViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

SearchContainer<CommerceChannel> commerceChannelSearchContainer = commerceChannelItemSelectorViewDisplayContext.getSearchContainer();
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new CommerceChannelsItemSelectorViewManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, commerceChannelSearchContainer) %>"
/>

<div class="container-fluid container-fluid-max-xl" id="<portlet:namespace />commerceChannelSelectorWrapper">
	<liferay-ui:search-container
		id="commerceChannels"
		searchContainer="<%= commerceChannelSearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.commerce.product.model.CommerceChannel"
			cssClass="commerce-channel-row"
			escapedModel="<%= true %>"
			keyPropertyName="commerceChannelId"
			modelVar="commerceChannel"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"commerce-channel-id", commerceChannel.getCommerceChannelId()
				).put(
					"name", commerceChannel.getName()
				).build());
			%>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				property="name"
			/>

			<liferay-ui:search-container-column-date
				cssClass="table-cell-expand"
				name="create-date"
				property="createDate"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
			searchContainer="<%= commerceChannelSearchContainer %>"
		/>
	</liferay-ui:search-container>
</div>