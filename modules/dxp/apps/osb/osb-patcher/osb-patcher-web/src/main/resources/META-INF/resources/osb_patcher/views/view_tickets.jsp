<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
long patcherBuildId = ParamUtil.getLong(request, "patcherBuildId");
long patcherProjectVersionId = ParamUtil.getLong(request, "patcherProjectVersionId");
%>

<clay:container-fluid
	cssClass="container-form-lg container-no-gutters"
>
	<clay:sheet
		size="full"
	>

		<%
		String tickets = null;

		PatcherBuild patcherBuild = PatcherBuildLocalServiceUtil.fetchPatcherBuild(patcherBuildId);

		if (patcherBuild != null) {
			tickets = patcherBuild.getName();
		}
		else {
			PatcherProjectVersion patcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherProjectVersionId);

			tickets = patcherProjectVersion.getFixedIssues();
		}

		for (String ticket : StringUtil.split(tickets)) {
		%>

			<c:choose>
				<c:when test="<%= PatcherFixPackUtil.containsPatcherFixPackName(ticket) %>">

					<%
					PatcherFixPack patcherFixPack = PatcherFixPackUtil.getPatcherFixPack(ticket, patcherProjectVersionId);
					%>

					<portlet:renderURL var="viewPatcherFixPackURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/view_fix_packs" />
						<portlet:param name="patcherFixPackId" value="<%= String.valueOf(patcherFixPack.getPatcherFixPackId()) %>" />
						<portlet:param name="redirect" value="<%= currentURL %>" />
					</portlet:renderURL>

					<a class="nobr" href="<%= viewPatcherFixPackURL %>"><%= ticket %></a>,
				</c:when>
				<c:otherwise>
					<a class="nobr" href="<%= patcherConfiguration.jiraURL() %>/<%= ticket %>" target="_blank"><%= ticket %></a>,
				</c:otherwise>
			</c:choose>

		<%
		}
		%>

	</clay:sheet>
</clay:container-fluid>