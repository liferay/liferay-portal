<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
String redirect = ParamUtil.getString(request, "redirect");

long patcherBuildId = ParamUtil.getLong(request, "patcherBuildId");

PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

List<String> patcherBuildTickets = PatcherUtil.getTickets(patcherBuild.getName());

List<String> cumulativeFixedIssues = PatcherProjectVersionUtil.getCumulativePatcherProjectVersionFixedIssues(patcherBuild.getPatcherProjectVersionId());

patcherBuildTickets.retainAll(cumulativeFixedIssues);
%>

<c:if test="<%= !windowState.equals(LiferayWindowState.POP_UP) %>">
	<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
		<liferay-util:param name="tabs1" value="builds" />
	</liferay-util:include>
</c:if>

<liferay-util:include page="/osb_patcher/views/header.jsp" servletContext="<%= application %>">
	<liferay-util:param name="title" value="edit-build" />
	<liferay-util:param name="mvcRenderCommandName" value="/patcher/index_builds" />
</liferay-util:include>

<aui:model-context bean="<%= patcherBuild %>" model="<%= PatcherBuild.class %>" />

<c:if test="<%= !patcherBuildTickets.isEmpty() %>">
	<aui:field-wrapper>
		<liferay-ui:icon
			image="../api/exception"
			message=""
		/>

		<liferay-ui:message arguments="<%= StringUtil.merge(patcherBuildTickets, StringPool.COMMA_AND_SPACE) %>" key="the-tickets-x-will-be-removed-from-the-list-since-they-are-included-in-the-project-version" />
	</aui:field-wrapper>
</c:if>

<portlet:actionURL name="/patcher/update_builds" var="updatePatcherBuildURL" />

<aui:form action="<%= updatePatcherBuildURL %>" method="post">
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="patcherBuildId" type="hidden" value="<%= patcherBuild.getPatcherBuildId() %>" />

	<aui:field-wrapper label="modified-date">
		<fmt:formatDate
			type="both"
			value="<%= patcherBuild.getModifiedDate() %>"
		/>
	</aui:field-wrapper>

	<aui:field-wrapper label="created-by">
		<%= patcherBuild.getUserName() %>
	</aui:field-wrapper>

	<aui:field-wrapper label="build-id">
		<%= patcherBuild.getPatcherBuildId() %>
	</aui:field-wrapper>

	<aui:field-wrapper label="version">
		<%= patcherBuild.getKeyVersion() %>
	</aui:field-wrapper>

	<aui:select disabled="<%= true %>" label="product-version" name="patcherProductVersionId" required="<%= true %>">

		<%
		for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions()) {
		%>

			<aui:option label="<%= patcherProductVersion.getName() %>" value="<%= patcherProductVersion.getPatcherProductVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:select disabled="<%= true %>" label="project-version" name="patcherProjectVersionId" required="<%= true %>" showEmptyOption="<%= true %>">

		<%
		for (PatcherProjectVersion patcherProjectVersion : PatcherProjectVersionLocalServiceUtil.getPatcherProjectVersions()) {
		%>

			<aui:option label="<%= patcherProjectVersion.getName() %>" value="<%= patcherProjectVersion.getPatcherProjectVersionId() %>" />

		<%
		}
		%>

	</aui:select>

	<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="tickets-list" name="patcherBuildName" readonly="<%= true %>" type="textarea" value="<%= patcherBuild.getName() %>" />

	<%
	PatcherAccount patcherAccount = PatcherAccountLocalServiceUtil.getPatcherAccount(patcherBuild.getPatcherAccountId());
	%>

	<aui:input inputCssClass="osb-patcher-input-wide osb-patcher-read-only" label="account-code" name="patcherBuildAccountEntryCode" readonly="<%= true %>" type="text" value="<%= patcherAccount.getAccountEntryCode() %>" />

	<aui:input inputCssClass="osb-patcher-input-wide" name="supportTicket" type="text" />

	<aui:select name="type">
		<aui:option label="<%= PatcherBuildConstants.LABEL_OFFICIAL %>" value="<%= PatcherBuildConstants.TYPE_OFFICIAL %>" />
		<aui:option label="<%= PatcherBuildConstants.LABEL_DEBUG %>" value="<%= PatcherBuildConstants.TYPE_DEBUG %>" />
		<aui:option label="<%= PatcherBuildConstants.LABEL_IGNORE %>" value="<%= PatcherBuildConstants.TYPE_IGNORE %>" />
	</aui:select>

	<aui:input name="mergeOnly" type="checkbox" value="<%= PatcherBuildUtil.isMergeOnly(patcherBuild) %>" />

	<aui:input name="smokeTestOnly" type="checkbox" wrapperCssClass="osb-patcher-display-none" />

	<aui:button-row>
		<aui:button type="submit" value="update" />

		<portlet:renderURL var="viewPatcherBuildsURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/index_builds" />
		</portlet:renderURL>

		<aui:button href="<%= Validator.isNotNull(redirect) ? redirect : viewPatcherBuildsURL %>" value="cancel" />
	</aui:button-row>
</aui:form>