<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
boolean showBackURL = ParamUtil.getBoolean(request, "showBackURL", true);

if (ddmDisplay.getDescription(locale) != null) {
	portletDisplay.setDescription(ddmDisplay.getDescription(locale));
}

if (ddmDisplay.getTitle(locale) != null) {
	renderResponse.setTitle(ddmDisplay.getTitle(locale));
}

List<DDMDisplayTabItem> ddmDisplayTabItems = ddmDisplay.getTabItems();
%>

<c:if test="<%= (ddmDisplayTabItems.size() < 2) && ddmDisplay.isShowBackURLInTitleBar() && showBackURL %>">

	<%
	portletDisplay.setShowBackIcon(true);
	portletDisplay.setURLBack(ddmDisplay.getViewStructuresBackURL(liferayPortletRequest, liferayPortletResponse));
	%>

</c:if>

<liferay-ui:success key='<%= DDMPortletKeys.DYNAMIC_DATA_MAPPING + "requestProcessed" %>' message="your-request-completed-successfully" />

<liferay-util:include page="/navigation_bar.jsp" servletContext="<%= application %>" />

<liferay-util:include page="/management_bar.jsp" servletContext="<%= application %>" />

<aui:form action="<%= ddmDisplayContext.getStructureSearchActionURL() %>" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= ddmDisplayContext.getStructureSearchActionURL() %>" />
	<aui:input name="deleteStructureIds" type="hidden" />

	<clay:container-fluid
		id='<%= liferayPortletResponse.getNamespace() + "entriesContainer" %>'
	>
		<div class="mb-n4 mt-3">
			<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureReferencedByStructureLinks.class %>" message="the-structure-cannot-be-deleted-because-it-is-required-by-one-or-more-structure-links" />
			<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureReferencedByTemplates.class %>" message="the-structure-cannot-be-deleted-because-it-is-required-by-one-or-more-templates" />
			<liferay-ui:error exception="<%= RequiredStructureException.MustNotDeleteStructureThatHasChild.class %>" message="the-structure-cannot-be-deleted-because-it-has-one-or-more-substructures" />
		</div>

		<liferay-ui:search-container
			id="ddmStructures"
			rowChecker="<%= new DDMStructureRowChecker(renderResponse) %>"
			searchContainer="<%= ddmDisplayContext.getDDMStructureSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.mapping.model.DDMStructure"
				keyPropertyName="structureId"
				modelVar="structure"
			>

				<%
				String rowHREF = StringPool.BLANK;

				if (DDMStructurePermission.contains(permissionChecker, structure, ActionKeys.UPDATE)) {
					rowHREF = PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCPath(
						"/edit_structure.jsp"
					).setRedirect(
						currentURL
					).setParameter(
						"classNameId", PortalUtil.getClassNameId(DDMStructure.class)
					).setParameter(
						"classPK", structure.getStructureId()
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
					value="<%= HtmlUtil.escape(structure.getName(locale)) %>"
				/>

				<liferay-ui:search-container-column-text
					cssClass="table-cell-expand table-cell-minw-200"
					href="<%= rowHREF %>"
					name="description"
					value="<%= HtmlUtil.escape(structure.getDescription(locale)) %>"
				/>

				<c:if test="<%= Validator.isNull(storageTypeValue) %>">
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smaller"
						href="<%= rowHREF %>"
						name="storage-type"
						value="<%= LanguageUtil.get(request, HtmlUtil.escape(structure.getStorageType())) %>"
					/>
				</c:if>

				<c:if test="<%= scopeClassNameId == 0 %>">
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-150"
						href="<%= rowHREF %>"
						name="type"
						value="<%= HtmlUtil.escape(ResourceActionsUtil.getModelResource(locale, structure.getClassName())) %>"
					/>
				</c:if>

				<%
				Group group = GroupLocalServiceUtil.getGroup(structure.getGroupId());
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
					value="<%= structure.getModifiedDate() %>"
				/>

				<liferay-ui:search-container-column-jsp
					path="/structure_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</clay:container-fluid>
</aui:form>