<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long templateId = ParamUtil.getLong(request, "templateId");

long classNameId = ParamUtil.getLong(request, "classNameId");
long classPK = ParamUtil.getLong(request, "classPK");

DDMStructure structure = null;

long structureClassNameId = PortalUtil.getClassNameId(DDMStructure.class);

if ((classPK > 0) && (structureClassNameId == classNameId)) {
	structure = DDMStructureLocalServiceUtil.getStructure(classPK);
}
%>

<liferay-util:include page="/navigation_bar.jsp" servletContext="<%= application %>" />

<clay:management-toolbar
	clearResultsURL="<%= ddmDisplayContext.getClearResultsURL() %>"
	creationMenu="<%= ddmDisplayContext.getTemplateCreationMenu() %>"
	disabled="<%= ddmDisplayContext.isDisabledManagementBar(DDMWebKeys.DYNAMIC_DATA_MAPPING_TEMPLATE) %>"
	itemsTotal="<%= ddmDisplayContext.getTotalItems(DDMWebKeys.DYNAMIC_DATA_MAPPING_TEMPLATE) %>"
	orderDropdownItems="<%= ddmDisplayContext.getOrderItemsDropdownItems() %>"
	searchActionURL="<%= ddmDisplayContext.getSelectTemplateSearchActionURL() %>"
	searchFormName="searchForm"
	selectable="<%= false %>"
	sortingOrder="<%= ddmDisplayContext.getOrderByType() %>"
	sortingURL="<%= ddmDisplayContext.getSortingURL() %>"
/>

<aui:form action="<%= ddmDisplayContext.getSelectTemplateSearchActionURL() %>" method="post" name="selectTemplateFm">
	<clay:container-fluid>
		<liferay-ui:search-container
			searchContainer="<%= ddmDisplayContext.getDDMTemplateSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.mapping.model.DDMTemplate"
				keyPropertyName="templateId"
				modelVar="template"
			>
				<liferay-ui:search-container-column-text
					name="id"
					value="<%= String.valueOf(template.getTemplateId()) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand"
					name="name"
				>
					<c:choose>
						<c:when test="<%= template.getTemplateId() != templateId %>">

							<%
							Map<String, Object> data = new HashMap<>();

							if (ddmDisplay.isShowConfirmSelectTemplate()) {
								data.put("confirm-selection", Boolean.TRUE.toString());
								data.put("confirm-selection-message", ddmDisplay.getConfirmSelectTemplateMessage(locale));
							}

							data.put("ddmtemplateid", template.getTemplateId());
							data.put("ddmtemplatekey", template.getTemplateKey());
							data.put("description", template.getDescription(locale));
							data.put("imageurl", template.getTemplateImageURL(themeDisplay));
							data.put("name", template.getName(locale));
							%>

							<aui:a cssClass="selector-button" data="<%= data %>" href="javascript:void(0);">
								<%= HtmlUtil.escape(template.getName(locale)) %>
							</aui:a>
						</c:when>
						<c:otherwise>
							<%= HtmlUtil.escape(template.getName(locale)) %>
						</c:otherwise>
					</c:choose>
				</liferay-ui:search-container-column-text>

				<liferay-ui:search-container-column-jsp
					cssClass="table-cell-expand"
					name="description"
					path="/template_description.jsp"
				/>

				<liferay-ui:search-container-column-date
					name="modified-date"
					value="<%= template.getModifiedDate() %>"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</clay:container-fluid>
</aui:form>