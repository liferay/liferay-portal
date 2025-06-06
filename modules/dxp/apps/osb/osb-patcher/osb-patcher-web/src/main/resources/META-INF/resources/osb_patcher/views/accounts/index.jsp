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

<liferay-util:include page="/osb_patcher/views/toolbar.jsp" servletContext="<%= application %>">
	<liferay-util:param name="tabs1" value="accounts" />
</liferay-util:include>

<portlet:renderURL var="viewPatcherAccountsURL">
	<portlet:param name="mvcRenderCommandName" value="/patcher/index_accounts" />
</portlet:renderURL>

<aui:button-row>
	<clay:col>
		<aui:form action="" method="get" name="fm">
			<aui:fieldset cssClass="account-search" id="searchFieldset">
				<aui:input inlineField="<%= true %>" label="" name="accountEntryCode" placeholder="find-account" size="30" title="find-account" type="text" />
			</aui:fieldset>
		</aui:form>
	</clay:col>

	<clay:col
		cssClass="osb-patcher-loader-container"
	>
		<clay:container
			cssClass="osb-patcher-loader"
			id="loader"
			name="loader"
		/>
	</clay:col>

	<clay:col>
		<portlet:renderURL var="createPatcherBuildURL">
			<portlet:param name="mvcRenderCommandName" value="/patcher/add_builds" />
			<portlet:param name="redirect" value="<%= viewPatcherAccountsURL %>" />
		</portlet:renderURL>

		<aui:button disabled='<%= !PatcherPermission.contains(permissionChecker, "builds", PatcherActionKeys.CREATE) %>' href="<%= createPatcherBuildURL %>" value="create-build-for-new-account" />
	</clay:col>
</aui:button-row>

<liferay-ui:search-container
	searchContainer="<%= patcherAccountsDisplayContext.getSearchContainer() %>"
>
	<liferay-ui:search-container-row
		className="com.liferay.osb.patcher.model.PatcherAccount"
		escapedModel="<%= true %>"
		keyProperty="patcherAccountId"
		modelVar="patcherAccount"
	>
		<liferay-ui:search-container-column-text>
			<h5>
				<portlet:renderURL var="viewPatcherAccountURL">
					<portlet:param name="mvcRenderCommandName" value="/patcher/view_accounts" />
					<portlet:param name="patcherBuildAccountEntryCode" value="<%= patcherAccount.getAccountEntryCode() %>" />
				</portlet:renderURL>

				<a href="<%= viewPatcherAccountURL %>">
					<%= patcherAccount.getAccountEntryCode() %>
				</a>
			</h5>

			<c:set value="<%= PatcherProductVersionUtil.getPatcherProductVersions(patcherAccount) %>" var="patcherProductVersions" />

			<table class="account-table">

				<%
				for (PatcherProductVersion patcherProductVersion : PatcherProductVersionUtil.getPatcherProductVersions(patcherAccount)) {
				%>

					<tr>
						<td class="slim">
							<portlet:renderURL var="viewPatcherAccountPatcherProductVersionURL">
								<portlet:param name="mvcRenderCommandName" value="/patcher/view_accounts" />
								<portlet:param name="patcherBuildAccountEntryCode" value="<%= patcherAccount.getAccountEntryCode() %>" />
								<portlet:param name="patcherProductVersionId" value="<%= String.valueOf(patcherProductVersion.getPatcherProductVersionId()) %>" />
							</portlet:renderURL>

							<a href="<%= viewPatcherAccountPatcherProductVersionURL %>">
								<c:out value="<%= patcherProductVersion.getName() %>" />
							</a>
						</td>

						<%
						PatcherBuild patcherBuild = PatcherBuildUtil.fetchLastModifiedPatcherBuild(patcherAccount.getPatcherAccountId(), patcherProductVersion.getPatcherProductVersionId());
						%>

						<td class="slim">

							<%
							PatcherProjectVersion curPatcherProjectVersion = PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherBuild.getPatcherProjectVersionId());
							%>

							<%= curPatcherProjectVersion.getName() %>
						</td>
						<td class="wide">

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
						<td class="slim">
							<span class="relative-date">
								<fmt:formatDate
									pattern="yyyy-MM-dd HH:mm:ss z"
									timeZone="<%= timeZone %>"
									value="<%= patcherBuild.getStatusDate() %>"
								/>
							</span>
						</td>
						<td>
							<c:choose>
								<c:when test="<%= PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild) %>">

									<%
									String fileName = patcherBuild.getFileName();
									%>

									<span class="passed"><liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherBuild.getStatus()) %>" /></span> (<clay:link href='<%= fileName.contains("/liferay-dxp-") ? "https://releases-cdn.liferay.com/dxp/hotfix" : patcherConfiguration.patcherBuildDownloadURL() + "/" + fileName %>' label="download" target="_blank" />)
								</c:when>
								<c:when test="<%= (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_FAILED) || (patcherBuild.getStatus() == WorkflowConstants.STATUS_BUILD_FAILED_MERGING_ONLY) %>">
									<span class="failed"><liferay-ui:message key="<%= WorkflowConstants.getStatusLabel(patcherBuild.getStatus()) %>" /></span>

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
						<td>

							<%
							String qaStatusCSSClass = StringPool.BLANK;

							if (PatcherBuildUtil.isTestingPassed(patcherBuild)) {
								qaStatusCSSClass = "passed";
							}
							else if (PatcherBuildUtil.isTestingFailed(patcherBuild)) {
								qaStatusCSSClass = "failed";
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

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />getText(result) {
		return result.accountEntryCode || '';
	}

	Liferay.on(
		'allPortletsReady',
		function(event) {
			if (typeof moment !== 'undefined') {
				AUI().all('.relative-date').each(
					function(node, index, nodeList) {
						var date = node.getData('date');

						var relativeTime = moment(date).fromNow();

						node.text(relativeTime);
					}
				);
			 }

			AUI().use(
				'autocomplete', 'autocomplete-filters', 'liferay-portlet-url',
				function(A) {
					<portlet:renderURL var="viewPatcherAccountsURL">
						<portlet:param name="mvcRenderCommandName" value="/patcher/index_accounts" />
					</portlet:renderURL>

					var patcherAccountsJSONURL = Liferay.PortletURL.createURL('<%= viewPatcherAccountsURL %>.json');

					patcherAccountsJSONURL.setPortletId('<%= PatcherPortletKeys.PATCHER %>');

					patcherAccountsJSONURL.setParameter('limit', 10000);

					var loader = A.one("#<portlet:namespace />loader");

					loader.hide();

					var responseData;

					var autoComplete = new A.AutoCompleteList(
						{
							activateFirstItem: true,
							align: {
								node: '#<portlet:namespace />searchFieldset input',
								points: ['tl', 'bl']
							},
							inputNode: '#<portlet:namespace />accountEntryCode',
							maxResults: 20,
							minQueryLength: 0,
							queryDelay: 0,
							resultListLocator: 'data',
							resultTextLocator: <portlet:namespace />getText,
							source: function() {
								var inputValue = A.one("#<portlet:namespace />accountEntryCode").get('value');

								if (!inputValue) {
									return;
								}

								var myAjaxRequest = A.io.request(
									patcherAccountsJSONURL.toString(),
									{
										autoLoad: true,
										data: {
											<portlet:namespace />keywords:inputValue
										},
										dataType: 'json',
										method: 'GET',
										on: {
											success:function() {
												var results = [];

												var data = this.get('responseData');

												data.data.forEach(
													function(d) {
														if (results.length < 20) {
															results.push({raw: d, display: d.accountEntryCode, text: d.accountEntryCode})
														}
													}
												);

												loader.hide();

												autoComplete.fire("results", {data: data, query: inputValue, results: results});

												responseData = data;
											}
										},
										sync:false
									}
								);

								loader.show();

								myAjaxRequest.start();

								return responseData;
							}
						}
					);

					autoComplete.render();

					autoComplete.on(
						'select',
						function(event) {
							event.preventDefault();

							<portlet:renderURL var="viewPatcherAccountURL">
								<portlet:param name="mvcRenderCommandName" value="/patcher/index_accounts" />
							</portlet:renderURL>

							var viewPatcherAccountURL = Liferay.PortletURL.createURL('<%= viewPatcherAccountURL %>');

							viewPatcherAccountURL.setPortletId('<%= PatcherPortletKeys.PATCHER %>');

							viewPatcherAccountURL.setParameter('accountEntryCode', event.result.text);

							window.location.href = viewPatcherAccountURL.toString();
						}
					);
				}
			);
		}
	);
</aui:script>