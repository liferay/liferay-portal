<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
long classNameId = ParamUtil.getLong(request, "classNameId");
long classPK = ParamUtil.getLong(request, "classPK");

DDMStructure structure = null;

long structureClassNameId = PortalUtil.getClassNameId(DDMStructure.class);

if ((classPK > 0) && (structureClassNameId == classNameId)) {
	structure = DDMStructureServiceUtil.getStructure(classPK);
}

boolean showHeader = ParamUtil.getBoolean(request, "showHeader");

boolean controlPanel = false;

if (layout != null) {
	Group group = layout.getGroup();

	controlPanel = group.isControlPanel();
}
%>

<liferay-ui:error exception="<%= RequiredTemplateException.MustNotDeleteTemplateReferencedByTemplateLinks.class %>" message="the-template-cannot-be-deleted-because-it-is-required-by-one-or-more-template-links" />

<portlet:renderURL var="viewTemplateURL">
	<portlet:param name="mvcPath" value="/view_template.jsp" />
</portlet:renderURL>

<liferay-util:include page="/navigation_bar.jsp" servletContext="<%= application %>" />
<liferay-util:include page="/template_management_bar.jsp" servletContext="<%= application %>" />

<c:if test="<%= showHeader %>">
	<c:choose>
		<c:when test="<%= ddmDisplay.isShowBackURLInTitleBar() %>">

			<%
			portletDisplay.setShowBackIcon(true);
			portletDisplay.setURLBack(PortalUtil.escapeRedirect(ddmDisplay.getViewTemplatesBackURL(liferayPortletRequest, liferayPortletResponse, classPK)));

			renderResponse.setTitle(ddmDisplay.getViewTemplatesTitle(structure, controlPanel, ddmDisplayContext.isSearch(), locale));
			%>

		</c:when>
		<c:otherwise>
			<liferay-ui:header
				backURL="<%= ddmDisplay.getViewTemplatesBackURL(liferayPortletRequest, liferayPortletResponse, classPK) %>"
				cssClass="container-fluid container-fluid-max-xl"
				title="<%= ddmDisplay.getViewTemplatesTitle(structure, controlPanel, ddmDisplayContext.isSearch(), locale) %>"
			/>
		</c:otherwise>
	</c:choose>
</c:if>

<aui:form action="<%= viewTemplateURL %>" method="post" name="fm">
	<aui:input name="redirect" type="hidden" value="<%= ddmDisplayContext.getTemplateSearchActionURL() %>" />
	<aui:input name="deleteTemplateIds" type="hidden" />

	<clay:container-fluid
		id='<%= liferayPortletResponse.getNamespace() + "entriesContainer" %>'
	>
		<liferay-ui:search-container
			id="<%= ddmDisplayContext.getTemplateSearchContainerId() %>"
			rowChecker="<%= new DDMTemplateRowChecker(renderResponse) %>"
			searchContainer="<%= ddmDisplayContext.getDDMTemplateSearchContainer() %>"
		>
			<liferay-ui:search-container-row
				className="com.liferay.dynamic.data.mapping.model.DDMTemplate"
				keyPropertyName="templateId"
				modelVar="template"
			>

				<%
				String rowHREF = StringPool.BLANK;

				if (DDMTemplatePermission.contains(permissionChecker, template, ActionKeys.UPDATE)) {
					rowHREF = PortletURLBuilder.createRenderURL(
						renderResponse
					).setMVCPath(
						"/edit_template.jsp"
					).setParameter(
						"classNameId", classNameId
					).setParameter(
						"classPK", template.getClassPK()
					).setParameter(
						"groupId", template.getGroupId()
					).setParameter(
						"structureAvailableFields", liferayPortletResponse.getNamespace() + "getAvailableFields"
					).setParameter(
						"templateId", template.getTemplateId()
					).setParameter(
						"type", template.getType()
					).buildString();
				}
				%>

				<liferay-ui:search-container-row-parameter
					name="rowHREF"
					value="<%= rowHREF %>"
				/>

				<%
				Set<String> excludedColumnNames = ddmDisplay.getViewTemplatesExcludedColumnNames();
				%>

				<c:if test='<%= !excludedColumnNames.contains("id") %>'>
					<liferay-ui:search-container-column-text
						href="<%= rowHREF %>"
						name="id"
						orderable="<%= true %>"
						orderableProperty="id"
						property="templateId"
					/>
				</c:if>

				<c:if test='<%= !excludedColumnNames.contains("name") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand table-cell-minw-200 table-title"
						href="<%= rowHREF %>"
						name="name"
						value="<%= HtmlUtil.escape(template.getName(locale)) %>"
					/>
				</c:if>

				<liferay-ui:search-container-column-jsp
					cssClass="table-cell-expand"
					name="description"
					path="/template_description.jsp"
				/>

				<c:if test='<%= !excludedColumnNames.contains("structure") && (structure == null) %>'>

					<%
					String structureName = StringPool.BLANK;

					if (template.getClassPK() > 0) {
						DDMStructure templateStructure = DDMStructureLocalServiceUtil.getStructure(template.getClassPK());

						structureName = templateStructure.getName(locale);
					}
					%>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-150"
						href="<%= rowHREF %>"
						name="structure"
						value="<%= HtmlUtil.escape(structureName) %>"
					/>
				</c:if>

				<c:if test='<%= !excludedColumnNames.contains("type") && (classNameId == 0) %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest"
						href="<%= rowHREF %>"
						name="type"
						value="<%= HtmlUtil.escape(ddmDisplay.getTemplateType(template, locale)) %>"
					/>
				</c:if>

				<c:if test='<%= !excludedColumnNames.contains("mode") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest"
						href="<%= rowHREF %>"
						name="mode"
						value="<%= LanguageUtil.get(request, HtmlUtil.escape(template.getMode())) %>"
					/>
				</c:if>

				<c:if test='<%= !excludedColumnNames.contains("language") %>'>
					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest table-cell-minw-100"
						href="<%= rowHREF %>"
						name="language"
						value='<%= LanguageUtil.get(request, HtmlUtil.escape(template.getLanguage()) + "[stands-for]") %>'
					/>
				</c:if>

				<c:if test='<%= !excludedColumnNames.contains("scope") %>'>

					<%
					Group group = GroupLocalServiceUtil.getGroup(template.getGroupId());
					%>

					<liferay-ui:search-container-column-text
						cssClass="table-cell-expand-smallest"
						name="scope"
						value="<%= LanguageUtil.get(request, group.getScopeLabel(themeDisplay)) %>"
					/>
				</c:if>

				<c:if test='<%= !excludedColumnNames.contains("modified-date") %>'>
					<liferay-ui:search-container-column-date
						cssClass="table-cell-expand-smallest table-cell-ws-nowrap"
						href="<%= rowHREF %>"
						name="modified-date"
						orderable="<%= true %>"
						orderableProperty="modified-date"
						value="<%= template.getModifiedDate() %>"
					/>
				</c:if>

				<liferay-ui:search-container-column-jsp
					path="/template_action.jsp"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</clay:container-fluid>
</aui:form>