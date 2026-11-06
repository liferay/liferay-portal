<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long structureId = ParamUtil.getLong(request, "structureId");

DDMStructure structure = DDMStructureServiceUtil.getStructure(structureId);

String title = LanguageUtil.format(request, "x-history", structure.getName(locale), false);

PortletURL portletURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCPath(
	"/view_structure_history.jsp"
).setRedirect(
	redirect
).setParameter(
	"structureId", structureId
).buildPortletURL();

PortletURL backURL = PortletURLBuilder.createRenderURL(
	renderResponse
).setMVCPath(
	"/edit_structure.jsp"
).setRedirect(
	redirect
).setParameter(
	"classNameId", PortalUtil.getClassNameId(DDMStructure.class)
).setParameter(
	"classPK", structure.getStructureId()
).buildPortletURL();
%>

<c:choose>
	<c:when test="<%= ddmDisplay.isShowBackURLInTitleBar() %>">

		<%
		portletDisplay.setShowBackIcon(true);
		portletDisplay.setURLBack(backURL.toString());

		renderResponse.setTitle(title);
		%>

	</c:when>
	<c:otherwise>
		<liferay-ui:header
			backURL="<%= backURL.toString() %>"
			cssClass="container-fluid container-fluid-max-xl"
			title="<%= title %>"
		/>
	</c:otherwise>
</c:choose>

<aui:form action="<%= portletURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<liferay-ui:search-container
		searchContainer="<%= new StructureSearch(renderRequest, portletURL) %>"
		total="<%= DDMStructureVersionServiceUtil.getStructureVersionsCount(structureId) %>"
	>
		<liferay-ui:search-container-results
			results="<%= DDMStructureVersionServiceUtil.getStructureVersions(structureId, searchContainer.getStart(), searchContainer.getEnd(), searchContainer.getOrderByComparator()) %>"
		/>

		<liferay-ui:search-container-row
			className="com.liferay.dynamic.data.mapping.model.DDMStructureVersion"
			keyPropertyName="structureVersionId"
			modelVar="structureVersion"
		>
			<portlet:renderURL var="rowURL">
				<portlet:param name="mvcPath" value="/view_structure.jsp" />
				<portlet:param name="redirect" value="<%= redirect %>" />
				<portlet:param name="structureVersionId" value="<%= String.valueOf(structureVersion.getStructureVersionId()) %>" />
				<portlet:param name="formBuilderReadOnly" value="<%= Boolean.TRUE.toString() %>" />
			</portlet:renderURL>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="id"
				property="structureVersionId"
			/>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="version"
				property="version"
			/>

			<liferay-ui:search-container-column-status
				href="<%= rowURL %>"
				name="status"
				status="<%= structureVersion.getStatus() %>"
			/>

			<liferay-ui:search-container-column-text
				href="<%= rowURL %>"
				name="author"
				value="<%= HtmlUtil.escape(PortalUtil.getUserName(structureVersion)) %>"
			/>

			<liferay-ui:search-container-column-jsp
				align="right"
				cssClass="entry-action"
				path="/structure_version_action.jsp"
			/>
		</liferay-ui:search-container-row>

		<liferay-ui:search-iterator
			markupView="lexicon"
		/>
	</liferay-ui:search-container>
</aui:form>