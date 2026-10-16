<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
BlogsConfigurationDisplayContext blogsConfigurationDisplayContext = new BlogsConfigurationDisplayContext(request, renderRequest, renderResponse);
BlogsGroupServiceConfiguration blogsGroupServiceConfiguration = ConfigurationProviderUtil.getConfiguration(BlogsGroupServiceConfiguration.class, new ParameterMapSettingsLocator(request.getParameterMap(), new GroupServiceSettingsLocator(themeDisplay.getSiteGroupId(), BlogsConstants.SERVICE_NAME)));
BlogsGroupServiceSettings blogsGroupServiceSettings = BlogsGroupServiceSettings.getInstance(scopeGroupId, request.getParameterMap());

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(blogsConfigurationDisplayContext.getBackURL());
portletDisplay.setURLBackTitle("blogs");
%>

<clay:container-fluid
	cssClass="container-form-lg"
>
	<clay:row>
		<clay:col
			lg="3"
		>
			<c:if test="<%= PortalUtil.isRSSFeedsEnabled() %>">
				<p class="c-mb-1 sheet-tertiary-title text-2 text-secondary">
					<liferay-ui:message key="settings" />
				</p>

				<clay:vertical-nav
					verticalNavItems="<%= blogsConfigurationDisplayContext.getSettingsVerticalNavItemList() %>"
				/>
			</c:if>

			<p class="c-mb-1 sheet-tertiary-title text-2 text-secondary">
				<liferay-ui:message key="notifications" />
			</p>

			<clay:vertical-nav
				verticalNavItems="<%= blogsConfigurationDisplayContext.getNotificationsVerticalNavItemList() %>"
			/>
		</clay:col>

		<clay:col
			lg="9"
		>
			<h2>
				<%= blogsConfigurationDisplayContext.getTitle() %>
			</h2>

			<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL">
				<portlet:param name="navigation" value="<%= blogsConfigurationDisplayContext.getNavigation() %>" />
				<portlet:param name="serviceName" value="<%= BlogsConstants.SERVICE_NAME %>" />
				<portlet:param name="settingsScope" value="group" />
			</liferay-portlet:actionURL>

			<aui:form action="<%= configurationActionURL %>" method="post" name="fm">
				<clay:sheet
					cssClass="c-mb-4 c-mt-4 c-p-0"
					size="full"
				>
					<h3 class="c-pl-4 c-pr-4 c-pt-4 sheet-title">
						<clay:content-row
							verticalAlign="center"
						>
							<clay:content-col>
								<%= blogsConfigurationDisplayContext.getSubtitle() %>
							</clay:content-col>
						</clay:content-row>
					</h3>

					<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />
					<aui:input name="redirect" type="hidden" value="<%= blogsConfigurationDisplayContext.getRedirect() %>" />

					<liferay-ui:error embed="<%= false %>" key="emailEntryAddedBody" message="please-enter-a-valid-body" />
					<liferay-ui:error embed="<%= false %>" key="emailEntryAddedSubject" message="please-enter-a-valid-subject" />
					<liferay-ui:error embed="<%= false %>" key="emailEntryUpdatedBody" message="please-enter-a-valid-body" />
					<liferay-ui:error embed="<%= false %>" key="emailEntryUpdatedSubject" message="please-enter-a-valid-subject" />

					<%
					Map<String, String> emailDefinitionTerms = BlogsUtil.getEmailDefinitionTerms(renderRequest, blogsGroupServiceSettings.getEmailFromAddress(), blogsGroupServiceSettings.getEmailFromName());
					%>

					<c:choose>
						<c:when test='<%= Objects.equals(blogsConfigurationDisplayContext.getNavigation(), "entry-added-email") %>'>
							<div class="c-px-4 panel-group-flush">
								<liferay-frontend:email-notification-settings
									emailBodyLocalizedValuesMap="<%= blogsGroupServiceSettings.getEmailEntryAddedBody() %>"
									emailDefinitionTerms="<%= emailDefinitionTerms %>"
									emailEnabled="<%= blogsGroupServiceSettings.isEmailEntryAddedEnabled() %>"
									emailParam="emailEntryAdded"
									emailSubjectLocalizedValuesMap="<%= blogsGroupServiceSettings.getEmailEntryAddedSubject() %>"
								/>
							</div>
						</c:when>
						<c:when test='<%= Objects.equals(blogsConfigurationDisplayContext.getNavigation(), "entry-updated-email") %>'>
							<div class="c-px-4 panel-group-flush">
								<liferay-frontend:email-notification-settings
									emailBodyLocalizedValuesMap="<%= blogsGroupServiceSettings.getEmailEntryUpdatedBody() %>"
									emailDefinitionTerms="<%= emailDefinitionTerms %>"
									emailEnabled="<%= blogsGroupServiceSettings.isEmailEntryUpdatedEnabled() %>"
									emailParam="emailEntryUpdated"
									emailSubjectLocalizedValuesMap="<%= blogsGroupServiceSettings.getEmailEntryUpdatedSubject() %>"
								/>
							</div>
						</c:when>
						<c:when test='<%= Objects.equals(blogsConfigurationDisplayContext.getNavigation(), "rss") && PortalUtil.isRSSFeedsEnabled() %>'>
							<div class="c-px-4 panel-group-flush">
								<liferay-rss:rss-settings
									delta="<%= GetterUtil.getInteger(blogsGroupServiceConfiguration.rssDelta()) %>"
									displayStyle="<%= blogsGroupServiceConfiguration.rssDisplayStyle() %>"
									enabled="<%= blogsGroupServiceConfiguration.enableRss() %>"
									feedType="<%= blogsGroupServiceConfiguration.rssFeedType() %>"
								/>
							</div>
						</c:when>
						<c:otherwise>
							<div class="c-px-4">
								<liferay-frontend:fieldset>
									<aui:input cssClass="lfr-input-text-container" label="name" name="preferences--emailFromName--" type="text" value="<%= blogsGroupServiceSettings.getEmailFromName() %>">
										<aui:validator errorMessage="please-enter-a-valid-name" name="required" />
									</aui:input>

									<aui:input cssClass="lfr-input-text-container" label="address" name="preferences--emailFromAddress--" type="text" value="<%= blogsGroupServiceSettings.getEmailFromAddress() %>">
										<aui:validator errorMessage="please-enter-a-valid-email-address" name="required" />
										<aui:validator name="email" />
									</aui:input>
								</liferay-frontend:fieldset>
							</div>
						</c:otherwise>
					</c:choose>
				</clay:sheet>

				<aui:button-row>
					<clay:button
						cssClass="c-mr-2"
						label="save"
						type="submit"
					/>

					<clay:link
						displayType="secondary"
						href="<%= blogsConfigurationDisplayContext.getBackURL() %>"
						label="cancel"
						type="button"
					/>
				</aui:button-row>
			</aui:form>
		</clay:col>
	</clay:row>
</clay:container-fluid>