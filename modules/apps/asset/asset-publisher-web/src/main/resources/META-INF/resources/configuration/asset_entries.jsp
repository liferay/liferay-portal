<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PortletURL configurationRenderURL = (PortletURL)request.getAttribute("configuration.jsp-configurationRenderURL");

List<AssetEntry> assetEntries = assetPublisherHelper.getAssetEntries(renderRequest, portletPreferences, permissionChecker, assetPublisherDisplayContext.getGroupIds(), true, assetPublisherDisplayContext.isEnablePermissions(), true, AssetRendererFactory.TYPE_LATEST);
%>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	emptyResultsMessage="none"
	iteratorURL="<%= configurationRenderURL %>"
	total="<%= assetEntries.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= assetEntries %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.asset.kernel.model.AssetEntry"
		escapedModel="<%= true %>"
		keyProperty="entryId"
		modelVar="assetEntry"
	>

		<%
		AssetRendererFactory<?> assetRendererFactory = AssetRendererFactoryRegistryUtil.getAssetRendererFactoryByClassName(assetEntry.getClassName());

		AssetRenderer<?> assetRenderer = assetRendererFactory.getAssetRenderer(assetEntry.getClassPK(), AssetRendererFactory.TYPE_LATEST);
		%>

		<liferay-ui:search-container-column-text
			name="title"
			truncate="<%= true %>"
		>
			<div class="d-flex">
				<%= HtmlUtil.escape(assetRenderer.getTitle(locale)) %>

				<c:if test="<%= !assetEntry.isVisible() %>">
					(<div class="ml-1">
						<liferay-portal-workflow:status
							showStatusLabel="<%= false %>"
							status="<%= assetRenderer.getStatus() %>"
							statusMessage='<%= (assetRenderer.getStatus() == 0) ? "not-visible" : WorkflowConstants.getStatusLabel(assetRenderer.getStatus()) %>'
						/>
					</div>)
				</c:if>
			</div>
		</liferay-ui:search-container-column-text>

		<liferay-ui:search-container-column-text
			name="type"
			value="<%= assetRendererFactory.getTypeName(locale) %>"
		/>

		<liferay-ui:search-container-column-date
			name="modified-date"
			value="<%= assetEntry.getModifiedDate() %>"
		/>

		<liferay-ui:search-container-column-jsp
			path="/configuration/asset_selection_action.jsp"
		/>

		<liferay-ui:search-container-column-jsp
			cssClass="entry-action-column"
			path="/configuration/asset_selection_order_action.jsp"
		/>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
		paginate="<%= total > SearchContainer.DEFAULT_DELTA %>"
	/>
</liferay-ui:search-container>

<c:if test='<%= SessionMessages.contains(renderRequest, "deletedMissingAssetEntries") %>'>
	<clay:alert
		displayType="info"
		message="the-selected-assets-have-been-removed-from-the-list-because-they-do-not-belong-in-the-scope-of-this-widget"
	/>
</c:if>

<%
long[] groupIds = assetPublisherDisplayContext.getGroupIds();
%>

<c:if test="<%= ArrayUtil.isNotEmpty(groupIds) %>">
	<div class="d-flex flex-wrap">

		<%
		for (long groupId : groupIds) {
			Group group = GroupLocalServiceUtil.getGroup(groupId);

			String title = LanguageUtil.format(request, (groupIds.length == 1) ? "select" : "select-in-x", HtmlUtil.escape(group.getDescriptiveName(locale)), false);
		%>

				<clay:button
					additionalProps='<%=
						HashMapBuilder.<String, Object>put(
							"assetEntryTypes", assetPublisherDisplayContext.getAssetEntryTypesJSONArray(groupId)
						).put(
							"currentURL", configurationRenderURL.toString()
						).build()
					%>'
					aria-label="<%= title %>"
					cssClass="mr-2"
					displayType="secondary"
					label="<%= title %>"
					propsTransformer="{AssetEntrySelectionDropdownPropsTransformer} from asset-publisher-web"
				/>

		<%
		}
		%>

	</div>
</c:if>

<aui:script>
	function <portlet:namespace />moveSelectionDown(assetEntryOrder) {
		Liferay.Util.postForm(document.<portlet:namespace />fm, {
			data: {
				assetEntryOrder: assetEntryOrder,
				cmd: 'move-selection-down',
				redirect: '<%= HtmlUtil.escapeJS(currentURL) %>',
			},
		});
	}

	function <portlet:namespace />moveSelectionUp(assetEntryOrder) {
		Liferay.Util.postForm(document.<portlet:namespace />fm, {
			data: {
				assetEntryOrder: assetEntryOrder,
				cmd: 'move-selection-up',
				redirect: '<%= HtmlUtil.escapeJS(currentURL) %>',
			},
		});
	}
</aui:script>