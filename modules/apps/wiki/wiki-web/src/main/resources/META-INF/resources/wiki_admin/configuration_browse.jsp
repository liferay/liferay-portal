<%--
/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/wiki/init.jsp" %>

<%
WikiConfigurationDisplayContext wikiConfigurationDisplayContext = new WikiConfigurationDisplayContext(request, renderRequest, renderResponse);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(wikiConfigurationDisplayContext.getBackURL());
portletDisplay.setURLBackTitle("wiki");
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
					verticalNavItems="<%= wikiConfigurationDisplayContext.getSettingsVerticalNavItemList() %>"
				/>
			</c:if>

			<p class="c-mb-1 sheet-tertiary-title text-2 text-secondary">
				<liferay-ui:message key="notifications" />
			</p>

			<clay:vertical-nav
				verticalNavItems="<%= wikiConfigurationDisplayContext.getNotificationsVerticalNavItemList() %>"
			/>
		</clay:col>

		<clay:col
			lg="9"
		>
			<h2>
				<%= wikiConfigurationDisplayContext.getTitle() %>
			</h2>

			<liferay-portlet:actionURL portletConfiguration="<%= true %>" var="configurationActionURL">
				<portlet:param name="navigation" value="<%= wikiConfigurationDisplayContext.getNavigation() %>" />
				<portlet:param name="serviceName" value="<%= WikiConstants.SERVICE_NAME %>" />
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
								<%= wikiConfigurationDisplayContext.getSubtitle() %>
							</clay:content-col>
						</clay:content-row>
					</h3>

					<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= Constants.UPDATE %>" />

					<liferay-ui:error embed="<%= false %>" key="emailPageAddedBody" message="please-enter-a-valid-body" />
					<liferay-ui:error embed="<%= false %>" key="emailPageAddedSubject" message="please-enter-a-valid-subject" />
					<liferay-ui:error embed="<%= false %>" key="emailPageUpdatedBody" message="please-enter-a-valid-body" />
					<liferay-ui:error embed="<%= false %>" key="emailPageUpdatedSubject" message="please-enter-a-valid-subject" />

					<%
					MailTemplatesHelper mailTemplatesHelper = new MailTemplatesHelper(wikiRequestHelper);

					Map<String, String> definitionTerms = mailTemplatesHelper.getEmailNotificationDefinitionTerms();
					%>

					<c:choose>
						<c:when test='<%= Objects.equals(wikiConfigurationDisplayContext.getNavigation(), "page-added-email") %>'>
							<div class="c-px-4 panel-group-flush">
								<liferay-frontend:email-notification-settings
									emailBodyLocalizedValuesMap="<%= siteWikiGroupServiceConfiguration.emailPageAddedBody() %>"
									emailDefinitionTerms="<%= definitionTerms %>"
									emailEnabled="<%= siteWikiGroupServiceConfiguration.emailPageAddedEnabled() %>"
									emailParam="emailPageAdded"
									emailSubjectLocalizedValuesMap="<%= siteWikiGroupServiceConfiguration.emailPageAddedSubject() %>"
								/>
							</div>
						</c:when>
						<c:when test='<%= Objects.equals(wikiConfigurationDisplayContext.getNavigation(), "page-updated-email") %>'>
							<div class="c-px-4 panel-group-flush">
								<liferay-frontend:email-notification-settings
									emailBodyLocalizedValuesMap="<%= siteWikiGroupServiceConfiguration.emailPageUpdatedBody() %>"
									emailDefinitionTerms="<%= definitionTerms %>"
									emailEnabled="<%= siteWikiGroupServiceConfiguration.emailPageUpdatedEnabled() %>"
									emailParam="emailPageUpdated"
									emailSubjectLocalizedValuesMap="<%= siteWikiGroupServiceConfiguration.emailPageUpdatedSubject() %>"
								/>
							</div>
						</c:when>
						<c:when test='<%= Objects.equals(wikiConfigurationDisplayContext.getNavigation(), "rss") && PortalUtil.isRSSFeedsEnabled() %>'>
							<div class="c-px-4 panel-group-flush">
								<liferay-rss:rss-settings
									delta="<%= GetterUtil.getInteger(siteWikiGroupServiceConfiguration.rssDelta()) %>"
									displayStyle="<%= siteWikiGroupServiceConfiguration.rssDisplayStyle() %>"
									enabled="<%= siteWikiGroupServiceConfiguration.enableRss() %>"
									feedType="<%= siteWikiGroupServiceConfiguration.rssFeedType() %>"
								/>
							</div>
						</c:when>
						<c:otherwise>
							<div class="c-px-4">
								<liferay-frontend:fieldset>
									<aui:input cssClass="lfr-input-text-container" label="name" name="preferences--emailFromName--" value="<%= siteWikiGroupServiceConfiguration.emailFromName() %>">
										<aui:validator errorMessage="please-enter-a-valid-name" name="required" />
									</aui:input>

									<aui:input cssClass="lfr-input-text-container" label="address" name="preferences--emailFromAddress--" value="<%= siteWikiGroupServiceConfiguration.emailFromAddress() %>">
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
						href="<%= wikiConfigurationDisplayContext.getBackURL() %>"
						label="cancel"
						type="button"
					/>
				</aui:button-row>
			</aui:form>
		</clay:col>
	</clay:row>
</clay:container-fluid>