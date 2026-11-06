<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPDefinitionItemSelectorViewDisplayContext cpDefinitionItemSelectorViewDisplayContext = (CPDefinitionItemSelectorViewDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

SearchContainer<CPDefinition> cpDefinitionSearchContainer = cpDefinitionItemSelectorViewDisplayContext.getSearchContainer();

String displayStyle = cpDefinitionItemSelectorViewDisplayContext.getDisplayStyle();

String itemSelectedEventName = cpDefinitionItemSelectorViewDisplayContext.getItemSelectedEventName();
%>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new CPDefinitionItemSelectorViewManagementToolbarDisplayContext(cpDefinitionItemSelectorViewDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
/>

<div class="container-fluid container-fluid-max-xl" id="<portlet:namespace />cpDefinitionSelectorWrapper">
	<liferay-ui:search-container
		id="cpDefinitions"
		searchContainer="<%= cpDefinitionSearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.commerce.product.model.CPDefinition"
			cssClass="commerce-product-definition-row"
			keyPropertyName="CPDefinitionId"
			modelVar="cpDefinition"
		>

			<%
			row.setData(
				HashMapBuilder.<String, Object>put(
					"cp-definition-id", cpDefinition.getCPDefinitionId()
				).put(
					"name", cpDefinition.getName(themeDisplay.getLanguageId())
				).build());

			CPType cpType = cpDefinitionItemSelectorViewDisplayContext.getCPType(cpDefinition.getProductTypeName());

			String thumbnailSrc = cpDefinition.getDefaultImageThumbnailSrc(AccountConstants.ACCOUNT_ENTRY_ID_ADMIN);
			%>

			<c:choose>
				<c:when test="<%= Validator.isNotNull(thumbnailSrc) %>">
					<liferay-ui:search-container-column-image
						name="image"
						src="<%= thumbnailSrc %>"
					/>
				</c:when>
				<c:otherwise>
					<liferay-ui:search-container-column-icon
						icon="documents-and-media"
						name="image"
					/>
				</c:otherwise>
			</c:choose>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
			>
				<div class="commerce-product-definition-title" data-id="<%= cpDefinition.getCPDefinitionId() %>">
					<%= HtmlUtil.escape(cpDefinition.getName(themeDisplay.getLanguageId())) %>
				</div>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="type"
			>
				<%= HtmlUtil.escapeAttribute(cpType.getLabel(locale)) %>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="sku"
				value="<%= HtmlUtil.escape(cpDefinitionItemSelectorViewDisplayContext.getSku(cpDefinition, locale)) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="modified-date"
				value="<%= cpDefinitionItemSelectorViewDisplayContext.getModifiedDate(cpDefinition, themeDisplay) %>"
			/>

			<liferay-ui:search-container-column-status
				cssClass="table-cell-expand"
				name="status"
				status="<%= cpDefinition.getStatus() %>"
			/>

			<c:if test="<%= cpDefinitionItemSelectorViewDisplayContext.isSingleSelection() %>">
				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
				>
					<aui:button cssClass="selector-button" value="choose" />
				</liferay-ui:search-container-column-text>
			</c:if>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= displayStyle %>"
			markupView="lexicon"
			searchContainer="<%= cpDefinitionSearchContainer %>"
		/>
	</liferay-ui:search-container>
</div>

<c:choose>
	<c:when test="<%= cpDefinitionItemSelectorViewDisplayContext.isSingleSelection() %>">
		<aui:script use="aui-base">
			A.one('#<portlet:namespace />cpDefinitions').delegate(
				'click',
				function (event) {
					var row = this.ancestor('tr');

					var data = row.getDOM().dataset;

					Liferay.Util.getOpener().Liferay.fire(
						'<%= HtmlUtil.escapeJS(itemSelectedEventName) %>',
						{
							data: {id: data.cpDefinitionId, name: data.name},
						}
					);

					var popupWindow = Liferay.Util.getWindow();

					if (popupWindow !== null) {
						Liferay.Util.getWindow().hide();
					}
				},
				'.selector-button'
			);
		</aui:script>
	</c:when>
</c:choose>