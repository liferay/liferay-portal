<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/admin/init.jsp" %>

<%
PortletURL portletURL = PortletURLBuilder.create(
	ddmFormAdminDisplayContext.getPortletURL()
).setParameter(
	"displayStyle", displayStyle
).buildPortletURL();

FieldSetPermissionCheckerHelper fieldSetPermissionCheckerHelper = ddmFormAdminDisplayContext.getPermissionCheckerHelper();
%>

<clay:container-fluid
	id='<%= liferayPortletResponse.getNamespace() + "formContainer" %>'
>
	<aui:form action="<%= portletURL %>" method="post" name="searchContainerForm">
		<aui:input name="redirect" type="hidden" value="<%= portletURL.toString() %>" />
		<aui:input name="deleteStructureIds" type="hidden" />

		<c:choose>
			<c:when test="<%= ddmFormAdminDisplayContext.hasResults() %>">
				<liferay-ui:search-container
					id="structure"
					searchContainer="<%= ddmFormAdminDisplayContext.getSearchContainer() %>"
				>
					<liferay-ui:search-container-row
						className="com.liferay.dynamic.data.mapping.model.DDMStructure"
						keyPropertyName="structureId"
						modelVar="structure"
					>
						<portlet:renderURL var="rowURL">
							<portlet:param name="mvcRenderCommandName" value="/admin/edit_element_set" />
							<portlet:param name="redirect" value="<%= currentURL %>" />
							<portlet:param name="structureId" value="<%= String.valueOf(structure.getStructureId()) %>" />
							<portlet:param name="displayStyle" value="<%= displayStyle %>" />
						</portlet:renderURL>

						<%
						if (!fieldSetPermissionCheckerHelper.isShowEditIcon(structure)) {
							rowURL = null;
						}
						%>

						<c:choose>
							<c:when test='<%= displayStyle.equals("descriptive") %>'>
								<liferay-ui:search-container-column-icon
									cssClass="asset-icon"
									icon="cards"
								/>

								<liferay-ui:search-container-column-jsp
									colspan="<%= 2 %>"
									href="<%= rowURL %>"
									path="/admin/view_element_set_descriptive.jsp"
								/>

								<liferay-ui:search-container-column-jsp
									path="/admin/element_set_action.jsp"
								/>
							</c:when>
							<c:otherwise>
								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand table-title"
									href="<%= rowURL %>"
									name="name"
									value="<%= HtmlUtil.escape(structure.getName(locale)) %>"
								/>

								<liferay-ui:search-container-column-text
									cssClass="table-cell-expand"
									name="description"
									value="<%= HtmlUtil.escape(structure.getDescription(locale)) %>"
								/>

								<liferay-ui:search-container-column-date
									cssClass="table-cell-expand-smaller"
									name="modified-date"
									value="<%= structure.getModifiedDate() %>"
								/>

								<liferay-ui:search-container-column-jsp
									path="/admin/element_set_action.jsp"
								/>
							</c:otherwise>
						</c:choose>
					</liferay-ui:search-container-row>

					<liferay-ui:search-iterator
						displayStyle="<%= displayStyle %>"
						markupView="lexicon"
					/>
				</liferay-ui:search-container>
			</c:when>
			<c:otherwise>
				<liferay-frontend:empty-result-message
					actionDropdownItems="<%= ddmFormAdminDisplayContext.getEmptyResultMessageActionItemsDropdownItems() %>"
					animationType="<%= ddmFormAdminDisplayContext.getEmptyResultMessageAnimationType() %>"
					buttonCssClass="secondary"
					description="<%= ddmFormAdminDisplayContext.getEmptyResultMessageDescription() %>"
					title="<%= ddmFormAdminDisplayContext.getEmptyResultsMessage() %>"
				/>
			</c:otherwise>
		</c:choose>
	</aui:form>
</clay:container-fluid>

<aui:script use="liferay-ddm-form-portlet"></aui:script>