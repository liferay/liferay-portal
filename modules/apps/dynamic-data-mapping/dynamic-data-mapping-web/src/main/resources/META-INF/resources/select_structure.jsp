<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long classPK = ParamUtil.getLong(request, "classPK");
String displayStyle = ParamUtil.getString(request, "displayStyle", "list");

SearchContainer<DDMStructure> structureSearchContainer = ddmDisplayContext.getDDMStructureSearchContainer();
%>

<liferay-util:include page="/structure_navigation_bar.jsp" servletContext="<%= application %>" />

<clay:management-toolbar
	clearResultsURL="<%= ddmDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= ddmDisplayContext.getSelectStructureCreationMenu() %>"
	disabled="<%= ddmDisplayContext.isDisabledManagementBar(DDMWebKeys.DYNAMIC_DATA_MAPPING_STRUCTURE) %>"
	itemsTotal="<%= ddmDisplayContext.getTotalItems(DDMWebKeys.DYNAMIC_DATA_MAPPING_STRUCTURE) %>"
	orderDropdownItems="<%= ddmDisplayContext.getOrderItemsDropdownItems() %>"
	searchActionURL="<%= ddmDisplayContext.getSelectStructureSearchActionURL() %>"
	searchContainerId="<%= ddmDisplayContext.getStructureSearchContainerId() %>"
	searchFormName="searchForm"
	selectable="<%= false %>"
	sortingOrder="<%= ddmDisplayContext.getOrderByType() %>"
	sortingURL="<%= ddmDisplayContext.getSortingURL() %>"
/>

<aui:form action="<%= ddmDisplayContext.getSelectStructureSearchActionURL() %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="selectStructureFm">
	<liferay-ui:search-container
		searchContainer="<%= structureSearchContainer %>"
	>
		<liferay-ui:search-container-row
			className="com.liferay.dynamic.data.mapping.model.DDMStructure"
			keyPropertyName="structureId"
			modelVar="structure"
		>
			<liferay-ui:search-container-column-text
				name="id"
				value="<%= String.valueOf(structure.getStructureId()) %>"
			/>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="name"
			>
				<c:choose>
					<c:when test="<%= ddmDisplay.isEnableSelectStructureLink(structure, classPK) %>">

						<%
						Map<String, Object> data = new HashMap<String, Object>();

						if (ddmDisplay.isShowConfirmSelectStructure()) {
							data.put("confirm-selection", Boolean.TRUE.toString());
							data.put("confirm-selection-message", ddmDisplay.getConfirmSelectStructureMessage(locale));
						}

						data.put("ddmstructureid", structure.getStructureId());
						data.put("ddmstructurekey", structure.getStructureKey());
						data.put("name", structure.getName(locale));
						%>

						<aui:a cssClass="selector-button" data="<%= data %>" href="javascript:void(0);">
							<%= HtmlUtil.escape(structure.getUnambiguousName(structureSearchContainer.getResults(), themeDisplay.getScopeGroupId(), locale)) %>
						</aui:a>
					</c:when>
					<c:otherwise>
						<%= HtmlUtil.escape(structure.getUnambiguousName(structureSearchContainer.getResults(), themeDisplay.getScopeGroupId(), locale)) %>
					</c:otherwise>
				</c:choose>
			</liferay-ui:search-container-column-text>

			<liferay-ui:search-container-column-text
				cssClass="table-cell-expand"
				name="description"
				truncate="<%= true %>"
				value="<%= HtmlUtil.escape(structure.getDescription(locale)) %>"
			/>

			<liferay-ui:search-container-column-date
				name="modified-date"
				value="<%= structure.getModifiedDate() %>"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			displayStyle="<%= displayStyle %>"
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>