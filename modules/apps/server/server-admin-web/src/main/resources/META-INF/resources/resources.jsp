<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<liferay-ui:error exception="<%= CaptchaConfigurationException.class %>" message="a-captcha-error-occurred-please-contact-an-administrator" />
<liferay-ui:error exception="<%= CaptchaException.class %>" message="captcha-verification-failed" />
<liferay-ui:error exception="<%= CaptchaTextException.class %>" message="text-verification-failed" />

<liferay-ui:error exception="<%= VerifyException.class %>">

	<%
	VerifyException verifyException = (VerifyException)errorException;
	%>

	<pre><%= HtmlUtil.escape(verifyException.getMessage()) %></pre>
</liferay-ui:error>

<liferay-ui:error key="verifyDatabaseStateErrors">

	<%
	List<String> errorMessages = (List<String>)SessionErrors.get(renderRequest, "verifyDatabaseStateErrors");
	%>

	<pre><%= HtmlUtil.escape(StringUtil.merge(errorMessages, StringPool.NEW_LINE)) %></pre>
</liferay-ui:error>

<c:if test='<%= SessionMessages.contains(renderRequest, "verifyDatabaseStateWarnings") %>'>

	<%
	List<String> warnMessages = (List<String>)SessionMessages.get(renderRequest, "verifyDatabaseStateWarnings");
	%>

	<clay:alert
		displayType="warning"
	>
		<pre><%= HtmlUtil.escape(StringUtil.merge(warnMessages, StringPool.NEW_LINE)) %></pre>
	</clay:alert>
</c:if>

<%
String[] installedPatches = PatcherValues.INSTALLED_PATCH_NAMES;

Date modifiedDate = PortalUtil.getUptime();

long uptimeDiff = System.currentTimeMillis() - modifiedDate.getTime();

long days = uptimeDiff / Time.DAY;
long hours = (uptimeDiff / Time.HOUR) % 24;
long minutes = (uptimeDiff / Time.MINUTE) % 60;
long seconds = (uptimeDiff / Time.SECOND) % 60;

Runtime runtime = Runtime.getRuntime();

long totalMemory = runtime.totalMemory();

long usedMemory = totalMemory - runtime.freeMemory();
%>

<div class="sheet">
	<div class="panel-group panel-group-flush">
		<aui:fieldset>
			<div class="alert alert-info">
				<strong><liferay-ui:message key="info" /></strong>: <%= ReleaseInfo.getReleaseInfo() %>
				<c:if test="<%= (installedPatches != null) && (installedPatches.length > 0) %>">
					<strong><liferay-ui:message key="patch" /></strong>: <%= StringUtil.merge(installedPatches, StringPool.COMMA_AND_SPACE) %>
				</c:if>

				<strong><liferay-ui:message key="uptime" /></strong>:

				<c:if test="<%= days > 0 %>">
					<%= days %> <liferay-ui:message key='<%= (days > 1) ? "days" : "day" %>' />,
				</c:if>

				<%
				NumberFormat timeNumberFormat = NumberFormat.getInstance();

				timeNumberFormat.setMaximumIntegerDigits(2);
				timeNumberFormat.setMinimumIntegerDigits(2);
				%>

				<%= timeNumberFormat.format(hours) %>:<%= timeNumberFormat.format(minutes) %>:<%= timeNumberFormat.format(seconds) %>
			</div>

			<div class="meter-wrapper text-center">
				<portlet:resourceURL id="/server_admin/view_chart" var="totalMemoryChartURL">
					<portlet:param name="type" value="total" />
					<portlet:param name="totalMemory" value="<%= String.valueOf(totalMemory) %>" />
					<portlet:param name="usedMemory" value="<%= String.valueOf(usedMemory) %>" />
				</portlet:resourceURL>

				<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="memory-used-vs-total-memory" />" src="<%= totalMemoryChartURL %>" />

				<portlet:resourceURL id="/server_admin/view_chart" var="maxMemoryChartURL">
					<portlet:param name="type" value="max" />
					<portlet:param name="maxMemory" value="<%= String.valueOf(runtime.maxMemory()) %>" />
					<portlet:param name="usedMemory" value="<%= String.valueOf(usedMemory) %>" />
				</portlet:resourceURL>

				<img alt="<liferay-ui:message escapeAttribute="<%= true %>" key="memory-used-vs-max-memory" />" src="<%= maxMemoryChartURL %>" />
			</div>

			<br />

			<%
			NumberFormat basicNumberFormat = NumberFormat.getInstance(locale);
			%>

			<table class="lfr-table memory-status-table">
				<tr>
					<td>
						<span class="font-weight-semi-bold"><liferay-ui:message key="used-memory" /></span>
					</td>
					<td>
						<span class="text-muted"><%= basicNumberFormat.format(usedMemory) %> <liferay-ui:message key="bytes" /></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="font-weight-semi-bold"><liferay-ui:message key="total-memory" /></span>
					</td>
					<td>
						<span class="text-muted"><%= basicNumberFormat.format(runtime.totalMemory()) %> <liferay-ui:message key="bytes" /></span>
					</td>
				</tr>
				<tr>
					<td>
						<span class="font-weight-semi-bold"><liferay-ui:message key="maximum-memory" /></span>
					</td>
					<td>
						<span class="text-muted"><%= basicNumberFormat.format(runtime.maxMemory()) %> <liferay-ui:message key="bytes" /></span>
					</td>
				</tr>
			</table>
		</aui:fieldset>

		<liferay-captcha:captcha />

		<aui:fieldset collapsed="<%= false %>" collapsible="<%= true %>" label="system-actions">
			<ul class="list-group system-action-group">
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="run-the-garbage-collector-to-free-up-memory" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="gc" value="execute" />
					</div>
				</li>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="generate-thread-dump" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="threadDump" value="execute" />
					</div>
				</li>
			</ul>
		</aui:fieldset>

		<aui:fieldset collapsed="<%= false %>" collapsible="<%= true %>" label="cache-actions">
			<ul class="list-group system-action-group">
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="clear-content-cached-by-this-vm" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="cacheSingle" value="execute" />
					</div>
				</li>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="clear-content-cached-across-the-cluster" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="cacheMulti" value="execute" />
					</div>
				</li>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="clear-the-database-cache" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="cacheDb" value="execute" />
					</div>
				</li>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="clear-the-direct-servlet-cache" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="cacheServlet" value="execute" />
					</div>
				</li>
			</ul>
		</aui:fieldset>

		<aui:fieldset collapsed="<%= false %>" collapsible="<%= true %>" label="verification-actions">
			<ul class="list-group system-action-group">
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="verify-the-database-state" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="verifyDatabaseState" value="execute" />
					</div>
				</li>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="verify-membership-policies" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="verifyMembershipPolicies" value="execute" />
					</div>
				</li>
			</ul>
		</aui:fieldset>

		<aui:fieldset collapsed="<%= false %>" collapsible="<%= true %>" label="system-cleanup-actions">
			<ul class="list-group system-action-group">
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="clean-up-all-system-data" />
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="cleanUpAllSystemData" value="execute" />
					</div>
				</li>

				<%
				for (DataCleanup systemDataCleanup : DataCleanupUtil.getSystemDataCleanups()) {
					if (systemDataCleanup.isEnabled() && (ReleaseLocalServiceUtil.fetchRelease(systemDataCleanup.getServletContextName()) == null)) {
						continue;
					}
				%>

					<li class="list-group-item list-group-item-flex">
						<div class="autofit-col autofit-col-expand">
							<p class="list-group-title text-truncate">
								<liferay-ui:message key="<%= systemDataCleanup.getLabel() %>" />

								<span aria-label="<%= LanguageUtil.get(request, systemDataCleanup.getHelpLabel()) %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, systemDataCleanup.getHelpLabel()) %>">
									<clay:icon
										symbol="question-circle-full"
									/>
								</span>
							</p>
						</div>

						<div class="autofit-col">
							<aui:button cssClass="save-server-button" data-cmd="<%= systemDataCleanup.getLabel() %>" disabled="<%= !systemDataCleanup.isEnabled() %>" value="execute" />
						</div>
					</li>

				<%
				}
				%>

				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="reset-preview-and-thumbnail-files-for-documents-and-media" />

							<span aria-label="<%= LanguageUtil.get(request, "reset-preview-and-thumbnail-files-for-documents-and-media-help") %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, "reset-preview-and-thumbnail-files-for-documents-and-media-help") %>">
								<clay:icon
									symbol="question-circle-full"
								/>
							</span>
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="dlDeletePreviews" value="execute" />
					</div>
				</li>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="clean-up-permissions" />

							<span aria-label="<%= LanguageUtil.get(request, "clean-up-permissions-help") %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, "clean-up-permissions-help") %>">
								<clay:icon
									symbol="question-circle-full"
								/>
							</span>
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="cleanUpAddToPagePermissions" value="execute" />
					</div>
				</li>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="clean-up-orphaned-page-revision-portlet-preferences" />

							<span aria-label="<%= LanguageUtil.get(request, "clean-up-orphaned-page-revision-portlet-preferences-help") %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, "clean-up-orphaned-page-revision-portlet-preferences-help") %>">
								<clay:icon
									symbol="question-circle-full"
								/>
							</span>
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="cleanUpLayoutRevisionPortletPreferences" value="execute" />
					</div>
				</li>
				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="clean-up-orphaned-theme-portlet-preferences" />

							<span aria-label="<%= LanguageUtil.get(request, "clean-up-orphaned-theme-portlet-preferences-help") %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, "clean-up-orphaned-theme-portlet-preferences-help") %>">
								<clay:icon
									symbol="question-circle-full"
								/>
							</span>
						</p>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="cleanUpOrphanedPortletPreferences" value="execute" />
					</div>
				</li>
			</ul>
		</aui:fieldset>

		<%
		List<DataCleanup> moduleDataCleanups = TransformUtil.transform(
			DataCleanupUtil.getModuleDataCleanups(),
			moduleDataCleanup -> {
				if (!moduleDataCleanup.isEnabled() || (ReleaseLocalServiceUtil.fetchRelease(moduleDataCleanup.getServletContextName()) != null)) {
					return moduleDataCleanup;
				}

				return null;
			});
		%>

		<c:if test="<%= ListUtil.isNotEmpty(moduleDataCleanups) %>">
			<aui:fieldset collapsed="<%= false %>" collapsible="<%= true %>" label="module-cleanup-actions">
				<ul class="list-group system-action-group">
					<li class="list-group-item list-group-item-flex">
						<div class="autofit-col autofit-col-expand">
							<p class="list-group-title text-truncate">
								<liferay-ui:message key="clean-up-all-module-data" />
							</p>
						</div>

						<div class="autofit-col">
							<aui:button cssClass="save-server-button" data-cmd="cleanUpAllModuleData" value="execute" />
						</div>
					</li>

					<%
					for (DataCleanup moduleDataCleanup : moduleDataCleanups) {
					%>

						<li class="list-group-item list-group-item-flex">
							<div class="autofit-col autofit-col-expand">
								<p class="list-group-title text-truncate">
									<liferay-ui:message key="<%= moduleDataCleanup.getLabel() %>" />

									<span aria-label="<%= LanguageUtil.get(request, moduleDataCleanup.getHelpLabel()) %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, moduleDataCleanup.getHelpLabel()) %>">
										<clay:icon
											symbol="question-circle-full"
										/>
									</span>
								</p>
							</div>

							<div class="autofit-col">
								<aui:button cssClass="save-server-button" data-cmd="<%= moduleDataCleanup.getLabel() %>" disabled="<%= !moduleDataCleanup.isEnabled() %>" value="execute" />
							</div>
						</li>

					<%
					}
					%>

				</ul>
			</aui:fieldset>
		</c:if>

		<aui:fieldset collapsed="<%= false %>" collapsible="<%= true %>" label="regeneration-actions">
			<ul class="list-group system-action-group">
				<c:if test="<%= (audioConverter != null) && audioConverter.isEnabled() %>">
					<li class="list-group-item list-group-item-flex">
						<div class="autofit-col autofit-col-expand">
							<p class="list-group-title text-truncate">
								<liferay-ui:message key="regenerate-preview-of-audio-files-in-documents-and-media" />

								<span aria-label="<%= LanguageUtil.get(request, "regenerate-preview-of-audio-files-in-documents-and-media-help") %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, "regenerate-preview-of-audio-files-in-documents-and-media-help") %>">
									<clay:icon
										symbol="question-circle-full"
									/>
								</span>
							</p>
						</div>

						<%
						List<BackgroundTask> audioPreviewBackgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(CompanyConstants.SYSTEM, "com.liferay.document.library.preview.audio.internal.background.task.AudioPreviewBackgroundTaskExecutor", BackgroundTaskConstants.STATUS_IN_PROGRESS);
						%>

						<div class="autofit-col">
							<span class="<%= (audioPreviewBackgroundTasks.size() > 0) ? StringPool.BLANK : "hide" %> loading-animation loading-animation-sm"></span>
						</div>

						<div class="autofit-col">
							<aui:button cssClass="save-server-button" data-cmd="dlGenerateAudioPreviews" disabled="<%= (audioPreviewBackgroundTasks.size() > 0) ? true : false %>" value="execute" />
						</div>
					</li>
				</c:if>

				<c:if test="<%= DocumentConversionUtil.isEnabled() %>">
					<li class="list-group-item list-group-item-flex">
						<div class="autofit-col autofit-col-expand">
							<p class="list-group-title text-truncate">
								<liferay-ui:message key="regenerate-preview-and-thumbnail-of-openoffice-files-in-documents-and-media" />

								<span aria-label="<%= LanguageUtil.get(request, "regenerate-preview-and-thumbnail-of-openoffice-files-in-documents-and-media-help") %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, "regenerate-preview-and-thumbnail-of-pdf-files-in-documents-and-media-help") %>">
									<clay:icon
										symbol="question-circle-full"
									/>
								</span>
							</p>
						</div>

						<%
						List<BackgroundTask> openOfficeConversionPreviewBackgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(CompanyConstants.SYSTEM, "com.liferay.document.library.document.conversion.internal.background.task.OpenOfficeConversionPreviewBackgroundTaskExecutor", BackgroundTaskConstants.STATUS_IN_PROGRESS);
						%>

						<div class="autofit-col">
							<span class="<%= (openOfficeConversionPreviewBackgroundTasks.size() > 0) ? StringPool.BLANK : "hide" %> loading-animation loading-animation-sm"></span>
						</div>

						<div class="autofit-col">
							<aui:button cssClass="save-server-button" data-cmd="dlGenerateOpenOfficePreviews" disabled="<%= (openOfficeConversionPreviewBackgroundTasks.size() > 0) ? true : false %>" value="execute" />
						</div>
					</li>
				</c:if>

				<li class="list-group-item list-group-item-flex">
					<div class="autofit-col autofit-col-expand">
						<p class="list-group-title text-truncate">
							<liferay-ui:message key="regenerate-preview-and-thumbnail-of-pdf-files-in-documents-and-media" />

							<span aria-label="<%= LanguageUtil.get(request, "regenerate-preview-and-thumbnail-of-pdf-files-in-documents-and-media-help") %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, "regenerate-preview-and-thumbnail-of-pdf-files-in-documents-and-media-help") %>">
								<clay:icon
									symbol="question-circle-full"
								/>
							</span>
						</p>
					</div>

					<%
					List<BackgroundTask> pdfPreviewBackgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(CompanyConstants.SYSTEM, "com.liferay.document.library.preview.pdf.internal.background.task.PDFPreviewBackgroundTaskExecutor", BackgroundTaskConstants.STATUS_IN_PROGRESS);
					%>

					<div class="autofit-col">
						<span class="<%= (pdfPreviewBackgroundTasks.size() > 0) ? StringPool.BLANK : "hide" %> loading-animation loading-animation-sm"></span>
					</div>

					<div class="autofit-col">
						<aui:button cssClass="save-server-button" data-cmd="dlGeneratePDFPreviews" disabled="<%= (pdfPreviewBackgroundTasks.size() > 0) ? true : false %>" value="execute" />
					</div>
				</li>

				<c:if test="<%= (videoConverter != null) && videoConverter.isEnabled() %>">
					<li class="list-group-item list-group-item-flex">
						<div class="autofit-col autofit-col-expand">
							<p class="list-group-title text-truncate">
								<liferay-ui:message key="regenerate-preview-and-thumbnail-of-video-files-in-documents-and-media" />

								<span aria-label="<%= LanguageUtil.get(request, "regenerate-preview-and-thumbnail-of-video-files-in-documents-and-media-help") %>" class="lfr-portal-tooltip pl-2" tabindex="0" title="<%= LanguageUtil.get(request, "regenerate-preview-and-thumbnail-of-video-files-in-documents-and-media-help") %>">
									<clay:icon
										symbol="question-circle-full"
									/>
								</span>
							</p>
						</div>

						<%
						List<BackgroundTask> videoPreviewBackgroundTasks = BackgroundTaskManagerUtil.getBackgroundTasks(CompanyConstants.SYSTEM, "com.liferay.document.library.preview.video.internal.background.task.VideoPreviewBackgroundTaskExecutor", BackgroundTaskConstants.STATUS_IN_PROGRESS);
						%>

						<div class="autofit-col">
							<span class="<%= (videoPreviewBackgroundTasks.size() > 0) ? StringPool.BLANK : "hide" %> loading-animation loading-animation-sm"></span>
						</div>

						<div class="autofit-col">
							<aui:button cssClass="save-server-button" data-cmd="dlGenerateVideoPreviews" disabled="<%= (videoPreviewBackgroundTasks.size() > 0) ? true : false %>" value="execute" />
						</div>
					</li>
				</c:if>
			</ul>
		</aui:fieldset>
	</div>
</div>