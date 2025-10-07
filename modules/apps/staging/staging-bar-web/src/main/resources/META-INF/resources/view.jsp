<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
LayoutRevision layoutRevision = (LayoutRevision)renderRequest.getAttribute(WebKeys.LAYOUT_REVISION);
List<LayoutSetBranch> layoutSetBranches = (List<LayoutSetBranch>)renderRequest.getAttribute(StagingProcessesWebKeys.LAYOUT_SET_BRANCHES);
liveGroup = (Group)renderRequest.getAttribute(StagingProcessesWebKeys.LIVE_GROUP);
Layout liveLayout = (Layout)renderRequest.getAttribute(StagingProcessesWebKeys.LIVE_LAYOUT);
String liveURL = (String)renderRequest.getAttribute(StagingProcessesWebKeys.LIVE_URL);
String remoteSiteURL = (String)renderRequest.getAttribute(StagingProcessesWebKeys.REMOTE_SITE_URL);
stagingGroup = (Group)renderRequest.getAttribute(StagingProcessesWebKeys.STAGING_GROUP);
String stagingURL = (String)renderRequest.getAttribute(StagingProcessesWebKeys.STAGING_URL);

if (liveLayout != null) {
	request.setAttribute("view.jsp-typeSettingsProperties", liveLayout.getTypeSettingsProperties());
}
%>

<c:if test="<%= themeDisplay.isShowStagingIcon() %>">
	<c:if test="<%= liveGroup != null %>">
		<nav class="navbar navbar-collapse-absolute navbar-expand navbar-underline navigation-bar navigation-bar-secondary staging-navbar">
			<clay:container-fluid>
				<ul class="navbar-nav">
					<c:choose>
						<c:when test="<%= group.isStagingGroup() || group.isStagedRemotely() %>">
							<c:if test="<%= stagingGroup != null %>">
								<li class="nav-item">
									<a class="active nav-link" id="stagingLink" value="staging">
										<liferay-ui:message key="staging" />
									</a>
								</li>
							</c:if>
						</c:when>
						<c:otherwise>
							<li class="nav-item">
								<a class="nav-link" href="<%= (layoutSetBranches != null) ? null : stagingURL %>" value="staging">
									<liferay-ui:message key="staging" />
								</a>
							</li>
						</c:otherwise>
					</c:choose>

					<c:choose>
						<c:when test="<%= group.isStagedRemotely() %>">
							<li class="nav-item">
								<c:choose>
									<c:when test="<%= !remoteSiteURL.isEmpty() %>">
										<clay:link
											cssClass="nav-link"
											href="<%= HtmlUtil.escape(remoteSiteURL) %>"
											icon="home"
											label="go-to-remote-live"
										/>
									</c:when>
									<c:when test="<%= SessionErrors.contains(renderRequest, AuthException.class) %>">
										<clay:button
											cssClass="nav-link"
											displayType="unstyled"
											icon="home"
											label="go-to-remote-live"
										/>

										<liferay-ui:icon
											icon="exclamation-full"
											markupView="lexicon"
											message="an-error-occurred-while-authenticating-user"
											toolTip="<%= true %>"
										/>
									</c:when>
									<c:when test="<%= SessionErrors.contains(renderRequest, RemoteExportException.class) %>">
										<clay:button
											cssClass="nav-link"
											displayType="unstyled"
											icon="home"
											label="go-to-remote-live"
										/>

										<liferay-ui:icon
											icon="exclamation-full"
											markupView="lexicon"
											message="the-connection-to-the-remote-live-site-cannot-be-established-due-to-a-network-problem"
											toolTip="<%= true %>"
										/>
									</c:when>
									<c:otherwise>
										<clay:button
											cssClass="nav-link"
											displayType="unstyled"
											icon="home"
											label="go-to-remote-live"
										/>

										<liferay-ui:icon
											icon="exclamation-full"
											markupView="lexicon"
											message="an-unexpected-error-occurred"
											toolTip="<%= true %>"
										/>
									</c:otherwise>
								</c:choose>
							</li>
						</c:when>
						<c:when test="<%= group.isStagingGroup() %>">
							<c:if test="<%= Validator.isNotNull(liveURL) %>">
								<li class="nav-item">
									<a class="nav-link" href="<%= HtmlUtil.escape(liveURL) %>" value="live">
										<liferay-ui:message key="live" />
									</a>
								</li>
							</c:if>
						</c:when>
						<c:otherwise>
							<li class="nav-item">
								<a class="active nav-link" id="liveLink" value="live">
									<liferay-ui:message key="live" />
								</a>
							</li>
						</c:otherwise>
					</c:choose>
				</ul>
			</clay:container-fluid>
		</nav>

		<c:if test="<%= !layout.isSystem() || layout.isTypeControlPanel() || !Objects.equals(layout.getFriendlyURL(), PropsValues.CONTROL_PANEL_LAYOUT_FRIENDLY_URL) %>">
			<div class="staging-bar">
				<clay:container-fluid>
					<clay:row>
						<c:choose>
							<c:when test="<%= group.isStagingGroup() || group.isStagedRemotely() %>">
								<c:if test="<%= stagingGroup != null %>">
									<liferay-ui:error exception="<%= AuthException.class %>">
										<liferay-ui:message arguments="<%= user.getScreenName() %>" key="an-error-occurred-while-authenticating-user-x-on-the-remote-server" />
									</liferay-ui:error>

									<liferay-ui:error exception="<%= Exception.class %>" message="an-unexpected-error-occurred" />

									<c:choose>
										<c:when test="<%= GetterUtil.getBoolean((String)renderRequest.getAttribute(StagingProcessesWebKeys.BRANCHING_ENABLED)) %>">
											<clay:col>
												<liferay-util:include page="/view_layout_set_branch_details.jsp" servletContext="<%= application %>" />
											</clay:col>

											<clay:col>
												<c:if test="<%= !layoutRevision.isIncomplete() && !layout.isTypeContent() %>">
													<liferay-util:include page="/view_layout_branch_details.jsp" servletContext="<%= application %>" />
												</c:if>
											</clay:col>

											<c:if test="<%= !layout.isTypeContent() %>">
												<clay:col
													cssClass="staging-alert-container"
													id='<%= liferayPortletResponse.getNamespace() + "layoutRevisionStatus" %>'
												>
													<aui:model-context bean="<%= layoutRevision %>" model="<%= LayoutRevision.class %>" />

													<liferay-util:include page="/view_layout_revision_status.jsp" servletContext="<%= application %>" />
												</clay:col>
											</c:if>

											<clay:col
												cssClass="col-auto staging-alert-container"
												id='<%= liferayPortletResponse.getNamespace() + "layoutRevisionDetails" %>'
											>
												<aui:model-context bean="<%= layoutRevision %>" model="<%= LayoutRevision.class %>" />

												<liferay-util:include page="/view_layout_revision_details.jsp" servletContext="<%= application %>" />
											</clay:col>
										</c:when>
										<c:otherwise>
											<clay:col
												cssClass="staging-alert-container"
											>
												<c:choose>
													<c:when test="<%= liveLayout == null %>">
														<span class="last-publication-branch">
															<liferay-ui:message arguments='<%= "<strong>" + HtmlUtil.escape(layout.getName(locale)) + "</strong>" %>' key="page-x-has-not-been-published-to-live-yet" translateArguments="<%= false %>" />
														</span>
													</c:when>
													<c:otherwise>
														<liferay-util:include page="/last_publication_date_message.jsp" servletContext="<%= application %>" />
													</c:otherwise>
												</c:choose>
											</clay:col>

											<clay:col
												cssClass="staging-button-container"
												md="2"
												sm="3"
											>
												<liferay-staging:menu
													cssClass="publish-link test5"
													onlyActions="<%= true %>"
												/>
											</clay:col>
										</c:otherwise>
									</c:choose>
								</c:if>
							</c:when>
							<c:otherwise>
								<clay:col
									cssClass="staging-alert-container"
								>
									<div class="alert alert-warning hide warning-content" id="<portlet:namespace />warningMessage">
										<liferay-ui:message key="an-initial-staging-publish-process-is-in-progress" />
									</div>

									<liferay-util:include page="/last_publication_date_message.jsp" servletContext="<%= application %>" />
								</clay:col>
							</c:otherwise>
						</c:choose>
					</clay:row>
				</clay:container-fluid>

				<c:if test="<%= (layoutRevision != null) && (layoutRevision.isIncomplete() || (layoutRevision.isPending() && StagingUtil.hasWorkflowTask(user.getUserId(), layoutRevision))) %>">
					<div class="staging-bar-level-3-message">
						<div class="staging-bar-level-3-message-container">
							<div class="alert alert-fluid alert-info" role="alert">
								<div class="container-fluid container-fluid-max-xl staging-alert-container">
									<span class="alert-indicator">
										<svg aria-hidden="true" class="lexicon-icon lexicon-icon-info-circle">
											<use xlink:href="<%= themeDisplay.getPathThemeSpritemap() %>#info-circle" />
										</svg>
									</span>

									<%
									String layoutSetBranchName = null;

									LayoutSetBranch layoutSetBranch = (LayoutSetBranch)request.getAttribute(StagingProcessesWebKeys.LAYOUT_SET_BRANCH);

									if ((layoutSetBranch == null) && (layoutRevision != null)) {
										layoutSetBranch = LayoutSetBranchLocalServiceUtil.getLayoutSetBranch(layoutRevision.getLayoutSetBranchId());
									}

									if (layoutSetBranch != null) {
										layoutSetBranchName = HtmlUtil.escape(layoutSetBranchDisplayContext.getLayoutSetBranchDisplayName(layoutSetBranch));
									}
									%>

									<liferay-ui:message arguments="<%= new Object[] {HtmlUtil.escape(layoutRevision.getName(locale)), layoutSetBranchName} %>" key="the-page-x-is-not-enabled-in-x,-but-is-available-in-other-pages-variations" translateArguments="<%= false %>" />
								</div>
							</div>
						</div>
					</div>
				</c:if>
			</div>
		</c:if>
	</c:if>

	<aui:script use="aui-base">
		var staging = document.querySelector('.staging');

		if (staging) {
			var stagingToggle = document.querySelector('.staging-toggle');

			if (stagingToggle) {
				stagingToggle.addEventListener('click', (event) => {
					event.preventDefault();

					staging.classList.toggle('staging-show');
				});
			}
		}

		var stagingLink = document.getElementById('<portlet:namespace />stagingLink');
		var warningMessage = document.getElementById(
			'<portlet:namespace />warningMessage'
		);

		var checkBackgroundTasks = function () {
			Liferay.Service(
				'/backgroundtask.backgroundtask/get-background-tasks-count',
				{
					completed: false,
					groupId: '<%= liveGroup.getGroupId() %>',
					taskExecutorClassName:
						'<%= BackgroundTaskExecutorNames.LAYOUT_STAGING_BACKGROUND_TASK_EXECUTOR %>',
				},
				(obj) => {
					var incomplete = obj > 0;

					if (incomplete) {
						if (stagingLink) {
							stagingLink.classList.add('hide');
						}

						if (warningMessage) {
							warningMessage.classList.remove('hide');
						}

						setTimeout(checkBackgroundTasks, 5000);
					}
					else {
						if (stagingLink) {
							stagingLink.classList.remove('hide');
						}

						if (warningMessage) {
							warningMessage.classList.add('hide');
						}
					}
				}
			);
		};

		checkBackgroundTasks();
	</aui:script>
</c:if>