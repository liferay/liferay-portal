<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
List<Group> groups = new ArrayList<>();

SiteCommerceChannelTypeDisplayContext siteCommerceChannelTypeDisplayContext = (SiteCommerceChannelTypeDisplayContext)request.getAttribute("site.jsp-portletDisplayContext");

Group channelSiteGroup = siteCommerceChannelTypeDisplayContext.getChannelSite();

Map<String, Object> site = null;

if (channelSiteGroup != null) {
	groups = Arrays.asList(channelSiteGroup);

	site = HashMapBuilder.<String, Object>put(
		"id", channelSiteGroup.getGroupId()
	).put(
		"name", channelSiteGroup.getName(locale)
	).build();
}

CommerceChannel commerceChannel = siteCommerceChannelTypeDisplayContext.getCommerceChannel();
long commerceChannelId = siteCommerceChannelTypeDisplayContext.getCommerceChannelId();

String searchContainerId = "CommerceChannelSitesSearchContainer";

boolean viewOnly = false;

if (commerceChannel != null) {
	viewOnly = !siteCommerceChannelTypeDisplayContext.hasPermission(commerceChannel.getCommerceChannelId(), ActionKeys.UPDATE);
}
%>

<liferay-util:buffer
	var="removeCommerceChannelSiteIcon"
>
	<liferay-ui:icon
		icon="times"
		markupView="lexicon"
		message="remove"
	/>
</liferay-util:buffer>

<portlet:actionURL name="/commerce_channels/edit_commerce_channel" var="editCommerceChannelActionURL" />

<aui:form action="<%= editCommerceChannelActionURL %>" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="selectSite" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commerceChannelId" type="hidden" value="<%= commerceChannelId %>" />
	<aui:input name="siteGroupId" type="hidden" value="<%= (commerceChannel == null) ? 0 : commerceChannel.getSiteGroupId() %>" />

	<commerce-ui:panel
		bodyClasses="flex-fill"
		title='<%= LanguageUtil.get(request, "details") %>'
	>
		<commerce-ui:info-box
			title='<%= LanguageUtil.get(request, "type") %>'
		>
			<div class="item mb-4 pl-4">
				<liferay-ui:message key="site" />
			</div>
		</commerce-ui:info-box>

		<liferay-ui:search-container
			curParam="commerceChannelSiteCur"
			headerNames="null,null"
			id="<%= searchContainerId %>"
			iteratorURL="<%= currentURLObj %>"
			total="<%= groups.size() %>"
		>
			<liferay-ui:search-container-results
				results="<%= groups %>"
			/>

			<liferay-ui:search-container-row
				className="com.liferay.portal.kernel.model.Group"
				keyProperty="groupId"
				modelVar="group"
			>
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					value="<%= HtmlUtil.escape(group.getName(locale)) %>"
				/>

				<c:if test="<%= !viewOnly %>">
					<liferay-ui:search-container-column-text>
						<a class="float-right modify-link" data-rowId="<%= group.getGroupId() %>" href="javascript:void(0);"><%= removeCommerceChannelSiteIcon %></a>
					</liferay-ui:search-container-column-text>
				</c:if>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>

		<c:if test="<%= !viewOnly %>">
			<aui:button cssClass="mb-4" name="selectSite" value='<%= LanguageUtil.format(locale, "select-x", "site") %>' />
		</c:if>
	</commerce-ui:panel>
</aui:form>

<liferay-frontend:component
	context='<%=
		HashMapBuilder.<String, Object>put(
			"commerceChannelId", commerceChannelId
		).put(
			"portletNamespace", liferayPortletResponse.getNamespace()
		).put(
			"removeCommerceChannelSiteIcon", removeCommerceChannelSiteIcon
		).put(
			"searchContainerId", searchContainerId
		).put(
			"site", site
		).build()
	%>'
	module="{CommerceChannelSite} from commerce-channel-web"
/>