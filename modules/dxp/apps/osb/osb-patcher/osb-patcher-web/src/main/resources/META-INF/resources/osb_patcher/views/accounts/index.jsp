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
			<portlet:param name="controller" value="builds" />
			<portlet:param name="action" value="create" />
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
					<portlet:param name="controller" value="accounts" />
					<portlet:param name="action" value="view" />
					<portlet:param name="patcherBuildAccountEntryCode" value="<%= patcherAccount.accountEntryCode %>" />
				</portlet:renderURL>

				<a href="<%= viewPatcherAccountURL %>">
					<c:out value="<%= patcherAccount.accountEntryCode %>" />
				</a>
			</h5>

			<c:set value="<%= PatcherProductVersionUtil.getPatcherProductVersions(patcherAccount) %>" var="patcherProductVersions" />

			<table class="account-table">
				<c:forEach items="<%= patcherProductVersions %>" var="patcherProductVersion">
					<tr>
						<td class="slim">
							<portlet:renderURL var="viewPatcherAccountPatcherProductVersionURL">
								<portlet:param name="controller" value="accounts" />
								<portlet:param name="action" value="view" />
								<portlet:param name="patcherBuildAccountEntryCode" value="<%= patcherAccount.accountEntryCode %>" />
								<portlet:param name="patcherProductVersionId" value="<%= patcherProductVersion.patcherProductVersionId %>" />
							</portlet:renderURL>

							<a href="<%= viewPatcherAccountPatcherProductVersionURL %>">
								<c:out value="<%= patcherProductVersion.name %>" />
							</a>
						</td>

						<c:set value="<%= PatcherBuildUtil.fetchLastModifiedPatcherBuild(patcherAccount.patcherAccountId, patcherProductVersion.patcherProductVersionId) %>" var="patcherBuild" />

						<td class="slim">
							<c:set value="<%= PatcherProjectVersionLocalServiceUtil.fetchPatcherProjectVersion(patcherBuild.patcherProjectVersionId) %>" var="patcherProjectVersion" />

							<c:out value="<%= patcherProjectVersion.name %>" />
						</td>
						<td class="wide">
							<c:set value="<%= PatcherFixPackUtil.getPatcherFixPackNames(patcherBuild.name) %>" var="patcherFixPackNames" />

							<c:forEach items="<%= patcherFixPackNames %>" var="patcherFixPackName">
								<c:out value="<%= patcherFixPackName %>" />
							</c:forEach>

							<c:set value="<%= PatcherUtil.getTickets(patcherBuild.name) %>" var="tickets" />

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
							<fmt:formatDate
								pattern="yyyy-MM-dd HH:mm:ss"
								timeZone="<%= timeZone %>"
								value="<%= patcherBuild.statusDate %>"
								var="statusDataDate"
							/>

							<fmt:formatDate
								pattern="yyyy-MM-dd HH:mm:ss z"
								timeZone="<%= timeZone %>"
								value="<%= patcherBuild.statusDate %>"
								var="statusDisplayDate"
							/>

							<span class="relative-date" data-date="<%= statusDataDate %>" title="<%= statusDisplayDate %>">
								<c:out value="<%= statusDisplayDate %>" />
							</span>
						</td>
						<td>
							<c:choose>
								<c:when test="<%= PatcherBuildUtil.isCompleteReadyOrReleased(patcherBuild) %>">
									<c:set var="relevantStatusActionLink">
										(<clay:link href='<%= patcherBuild.fileName.contains("/liferay-dxp-") ? "https://releases-cdn.liferay.com/dxp/hotfix" : patcherConfiguration.patcherBuildDownloadURL() %>/<%= patcherBuild.fileName %>' label="download" target="_blank" />)
									</c:set>

									<c:set value="passed" var="statusCSSClass" />
								</c:when>
								<c:when test="<%= (patcherBuild.status == WorkflowConstants.STATUS_BUILD_FAILED) || (patcherBuild.status == WorkflowConstants.STATUS_BUILD_FAILED_MERGING_ONLY) %>">
									<c:set value="" var="relevantStatusActionLink" />

									<c:set value="<%= JenkinsUtil.getJenkinsResults(patcherBuild) %>" var="jenkinsResults" />

									<c:forEach items="<%= jenkinsResults %>" var="jenkinsResult">
										<c:set value="<%= jenkinsResult.jobName %>" var="jenkinsJobName" />

										<c:if test='<%= jenkinsJobName.contains("hotfix") %>'>
											<c:set var="relevantStatusActionLink">
												(<clay:link href="<%= jenkinsResult.statusURL %>" label="check-log" target="_blank" />)
											</c:set>
										</c:if>

										<c:if test='<%= jenkinsJobName.contains("dist") && ((empty relevantStatusActionLink) || relevantStatusActionLink.contains("agent")) %>'>
											<c:set var="relevantStatusActionLink">
												(<clay:link href="<%= jenkinsResult.statusURL %>" label="check-log" target="_blank" />)
											</c:set>
										</c:if>

										<c:if test='<%= jenkinsJobName.contains("agent") && (empty relevantStatusActionLink) %>'>
											<c:set var="relevantStatusActionLink">
												(<clay:link href="<%= jenkinsResult.statusURL %>" label="check-log" target="_blank" />)
											</c:set>
										</c:if>
									</c:forEach>

									<c:set value="failed" var="statusCSSClass" />
								</c:when>
								<c:otherwise>
									<c:set value="" var="relevantStatusActionLink" />

									<c:set value="" var="statusCSSClass" />
								</c:otherwise>
							</c:choose>

							<span class="<%= statusCSSClass %>"><c:out value="<%= LanguageUtil.get(request, patcherBuildStatusLabel) %>" /></span> <%= relevantStatusActionLink %>
						</td>
						<td>
							<c:choose>
								<c:when test="<%= PatcherBuildUtil.isTestingPassed(patcherBuild) %>">
									<c:set value="passed" var="qaStatusCSSClass" />
								</c:when>
								<c:when test="<%= PatcherBuildUtil.isTestingFailed(patcherBuild) %>">
									<c:set value="failed" var="qaStatusCSSClass" />
								</c:when>
								<c:otherwise>
									<c:set value="" var="qaStatusCSSClass" />
								</c:otherwise>
							</c:choose>

							<c:choose>
								<c:when test="<%= (patcherBuild.qaStatus == WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED) || (patcherBuild.qaStatus == WorkflowConstants.STATUS_BUILD_QA_ANALYSIS_NEEDED_SMOKE_ONLY) || (patcherBuild.qaStatus == WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED) || (patcherBuild.qaStatus == WorkflowConstants.STATUS_BUILD_QA_AUTOMATION_PASSED_SMOKE_ONLY) %>">
									<c:set value="<%= JenkinsUtil.getJenkinsResults(patcherBuild) %>" var="jenkinsResults" />

									<c:set var="relevantQAStatusActionLink">
										(<clay:link href="<%= jenkinsResults[0].statusURL %>" label="view-results" target="_blank" />)
									</c:set>
								</c:when>
								<c:otherwise>
									<c:set value="" var="relevantQAStatusActionLink" />
								</c:otherwise>
							</c:choose>

							<span class="<%= qaStatusCSSClass %>"><c:out value="<%= LanguageUtil.get(request, patcherBuildQAStatusLabel) %>" /></span> <%= relevantQAStatusActionLink %>
						</td>
					</tr>
				</c:forEach>
			</table>
		</liferay-ui:search-container-column-text>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator />
</liferay-ui:search-container>

<aui:script>
	function <portlet:namespace />getText(result) {
		return result.accountEntryCode || '';
	}

	Liferay.provide(
		window,
		'<portlet:namespace />productVersionOnChange',
		function(productVersionId) {
			var namespace = '<portlet:namespace />';

			window.location.href = Liferay.Patcher.updateProductVersionId('<%= viewPatcherAccountsURL %>', productVersionId, namespace);
		},
		['aui-base']
	);

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
						<portlet:param name="controller" value="accounts" />
						<portlet:param name="action" value="index" />
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
								<portlet:param name="controller" value="accounts" />
								<portlet:param name="action" value="index" />
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