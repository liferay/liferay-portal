<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/osb_patcher/views/init.jsp" %>

<%
PatcherAccountsDisplayContext patcherAccountsDisplayContext = new PatcherAccountsDisplayContext(request, renderRequest, renderResponse);
%>

<clay:navigation-bar
	navigationItems='<%= patcherDisplayContext.getNavigationItems("accounts") %>'
/>

<clay:management-toolbar
	managementToolbarDisplayContext="<%= new PatcherAccountsManagementToolbarDisplayContext(request, liferayPortletRequest, liferayPortletResponse, patcherAccountsDisplayContext.getSearchContainer()) %>"
/>

<liferay-ui:search-container
	searchContainer="<%= patcherAccountsDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherAccount"
		escapedModel="<%= true %>"
		keyProperty="patcherAccountId"
		modelVar="patcherAccount"
	>
		<liferay-ui:search-container-column-text
			colspan="<%= 2 %>"
		>
			<h5>
				<portlet:renderURL var="viewPatcherAccountURL">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_accounts" />
					<portlet:param name="accountEntryCode" value="<%= patcherAccount.getAccountEntryCode() %>" />
					<portlet:param name="redirect" value="<%= currentURL %>" />
				</portlet:renderURL>

				<a href="<%= viewPatcherAccountURL %>">
					<%= patcherAccount.getAccountEntryCode() %>
				</a>
			</h5>

			<table class="mt-3 table table-bordered table-sm table-striped">

				<%
				for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions(patcherAccount)) {
				%>

					<tr>
						<td class="col-md-2">
							<portlet:renderURL var="viewPatcherAccountPatcherProductVersionURL">
								<portlet:param name="mvcRenderCommandName" value="/patcher/view_accounts" />
								<portlet:param name="accountEntryCode" value="<%= patcherAccount.getAccountEntryCode() %>" />
								<portlet:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProductVersion.getPatcherProductVersionId()) %>" />
								<portlet:param name="redirect" value="<%= currentURL %>" />
							</portlet:renderURL>

							<a href="<%= viewPatcherAccountPatcherProductVersionURL %>">
								<c:out value="<%= patcherProductVersion.getName() %>" />
							</a>
						</td>

						<%
						PatcherBuild patcherBuild = PatcherBuildUtil.fetchLastModifiedPatcherBuild(patcherAccount.getPatcherAccountId(), patcherProductVersion.getPatcherProductVersionId());
						%>

						<td class="col-md-1">

							<%
							PatcherProjectVersion curPatcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherBuild.getPatcherProjectVersionId());
							%>

							<%= curPatcherProjectVersion.getName() %>
						</td>
						<td class="col-md-3">

							<%
							List<String> patcherFixPackNames = PatcherFixPackUtil.getPatcherFixPackNames(patcherBuild.getName());

							for (String patcherFixPackName : PatcherFixPackUtil.getPatcherFixPackNames(patcherBuild.getName())) {
							%>

								<%= patcherFixPackName %>

							<%
							}

							List<String> tickets = PatcherUtil.getTickets(patcherBuild.getName());
							%>

							<c:if test="<%= !patcherFixPackNames.isEmpty() && !tickets.isEmpty() %>">
								+
							</c:if>

							<c:choose>
								<c:when test="<%= tickets.size() > 4 %>">
									<liferay-ui:message arguments="<%= tickets.size() %>" key="x-tickets" />
								</c:when>
								<c:otherwise>
									<c:out value="<%= ListUtil.toString(tickets, StringPool.BLANK, StringPool.COMMA_AND_SPACE) %>" />
								</c:otherwise>
							</c:choose>
						</td>
						<td class="col-md-1">

							<%
							Date statusDate = patcherBuild.getStatusDate();
							%>

							<span class="lfr-portal-tooltip" title="<%= dateTimeFormat.format(patcherBuild.getStatusDate()) %>">
								<liferay-ui:message arguments="<%= LanguageUtil.getTimeDescription(request, System.currentTimeMillis() - statusDate.getTime(), true) %>" key="x-ago" translateArguments="<%= false %>" />
							</span>
						</td>
						<td class="col-md-2">
							<c:choose>
								<c:when test="<%= PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild) %>">
									<span class="text-success"><liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherBuild.getStatus()) %>" /></span> (<clay:link href='<%= patcherConfiguration.patcherBuildDownloadURL() + "/" + patcherBuild.getFileName() %>' label="download" target="_blank" />)
								</c:when>
								<c:when test="<%= (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_FAILED) || (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_FAILED_MERGING_ONLY) %>">
									<span class="text-danger"><liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherBuild.getStatus()) %>" /></span>

									<%
									for (Map<String, String> jenkinsResults : JenkinsUtil.getJenkinsResults(patcherBuild)) {
										String jenkinsJobName = jenkinsResults.get("jobName");
									%>

										<c:if test='<%= jenkinsJobName.contains("hotfix") || jenkinsJobName.contains("dist") || jenkinsJobName.contains("agent") %>'>
											(<clay:link href='<%= jenkinsResults.get("statusURL") %>' label="check-log" target="_blank" />)
										</c:if>

									<%
									}
									%>

								</c:when>
							</c:choose>
						</td>
						<td class="col-md-3">

							<%
							String qaStatusCSSClass = StringPool.BLANK;

							if (PatcherBuildUtil.isTestingPassed(patcherBuild)) {
								qaStatusCSSClass = "text-success";
							}
							else if (PatcherBuildUtil.isTestingFailed(patcherBuild)) {
								qaStatusCSSClass = "text-danger";
							}
							%>

							<span class="<%= qaStatusCSSClass %>"><liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherBuild.getQaStatus()) %>" /></span>

							<c:if test="<%= (patcherBuild.getQaStatus() == WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED) || (patcherBuild.getQaStatus() == WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY) || (patcherBuild.getQaStatus() == WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED) || (patcherBuild.getQaStatus() == WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY) %>">

								<%
								List<Map<String, String>> jenkinsResults = JenkinsUtil.getJenkinsResults(patcherBuild);

								Map<String, String> jenkinsResult = jenkinsResults.get(0);
								%>

								(<clay:link href='<%= jenkinsResult.get("statusURL") %>' label="view-results" target="_blank" />)
							</c:if>
						</td>
					</tr>

				<%
				}
				%>

			</table>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		displayStyle="descriptive"
		markupView="lexicon"
	/>
</liferay-ui:search-container>