<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/document_library/init.jsp" %>

<%
DLViewFileEntryMetadataSetsDisplayContext dLViewFileEntryMetadataSetsDisplayContext = (DLViewFileEntryMetadataSetsDisplayContext)request.getAttribute(DLWebKeys.DOCUMENT_LIBRARY_VIEW_FILE_ENTRY_METADATA_SETS_DISPLAY_CONTEXT);
%>

<liferay-util:include page="/document_library/navigation.jsp" servletContext="<%= application %>" />

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new DLViewFileEntryMetadataSetsManagementToolbarDisplayContext(dLViewFileEntryMetadataSetsDisplayContext, request, liferayPortletRequest, liferayPortletResponse) %>"
	propsTransformer="{DDMStructuresManagementToolbarPropsTransformer} from document-library-web"
/>

<portlet:actionURL copyCurrentRenderParameters="<%= true %>" name="/document_library/delete_data_definition" var="deleteDataDefinitionURL" />

<aui:form action="<%= deleteDataDefinitionURL %>" cssClass="container-fluid container-fluid-max-xxxl" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />

	<clay:container-fluid
		size="xxxl"
	>
		<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureReferencedByStructureLinks.class %>" message="the-structure-cannot-be-deleted-because-it-is-required-by-one-or-more-structure-links" />
		<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureReferencedByTemplates.class %>" message="the-structure-cannot-be-deleted-because-it-is-required-by-one-or-more-templates" />
		<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureThatHasChild.class %>" message="the-structure-cannot-be-deleted-because-it-has-one-or-more-substructures" />

		<liferay-site-navigation:breadcrumb
			breadcrumbEntries="<%= BreadcrumbEntriesUtil.getBreadcrumbEntries(request, true, false, false, true, true) %>"
		/>

		<liferay-ui:search-container
			id="ddmStructures"
			rowChecker="<%= new DDMStructureRowChecker(renderResponse) %>"
			searchContainer="<%= dLViewFileEntryMetadataSetsDisplayContext.getStructureSearch() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.mapping.model.DDMStructure"
				keyPropertyName="structureId"
				modelVar="ddmStructure"
			>

				<%
				String rowHREF = StringPool.BLANK;

				if (DDMStructurePermission.contains(permissionChecker, ddmStructure, ActionKeys.UPDATE)) {
					rowHREF = PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCRenderCommandName(
						"/document_library/edit_ddm_structure"
					).setRedirect(
						currentURL
					).setParameter(
						"ddmStructureId", ddmStructure.getStructureId()
					).buildString();
				}
				%>

				<liferay-ui:search-container-column-text
					href="<%= rowHREF %>"
					name="id"
					orderable="<%= true %>"
					orderableProperty="id"
					property="structureId"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-cell-minw-200 table-title"
					href="<%= rowHREF %>"
					name="name"
					value="<%= HtmlUtil.escape(ddmStructure.getName(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-cell-minw-200"
					href="<%= rowHREF %>"
					name="description"
					value="<%= HtmlUtil.escape(ddmStructure.getDescription(locale)) %>"
				/>

				<%
				Group group = GroupLocalServiceUtil.getGroup(ddmStructure.getGroupId());
				%>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand-smallest table-cell-minw-150"
					name="scope"
					value="<%= LanguageUtil.get(request, group.getScopeLabel(themeDisplay)) %>"
				/>

				<liferay-ui:search-container-column-date
					cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
					href="<%= rowHREF %>"
					name="modified-date"
					orderable="<%= true %>"
					orderableProperty="modified-date"
					value="<%= ddmStructure.getModifiedDate() %>"
				/>

				<liferay-ui:search-container-column-jsp
					path="/document_library/ddm/ddm_structure_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</clay:container-fluid>
</aui:form>