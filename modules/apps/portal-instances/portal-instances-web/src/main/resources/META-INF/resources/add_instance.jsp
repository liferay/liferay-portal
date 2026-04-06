<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="/portal_instances/add_instance" var="addInstanceURL" />

<div class="add-instance-alert-container"></div>

<clay:container-fluid>
	<liferay-frontend:edit-form
		action="<%= addInstanceURL %>"
		method="post"
		name="fm"
		onSubmit="event.preventDefault();"
		validateOnBlur="<%= false %>"
	>
		<div class="add-instance-content">
			<div class="px-4 py-2">
				<aui:model-context model="<%= Company.class %>" />

				<aui:input name="webId" required="<%= true %>" />

				<aui:input fieldParam="virtualHostname" label="virtual-host" model="<%= VirtualHost.class %>" name="hostname" />

				<aui:input label="mail-domain" name="mx" />

				<aui:input name="maxUsers" type="number"/>

				<aui:input inlineLabel="right" labelCssClass="simple-toggle-switch" name="active" type="toggle-switch" value="<%= true %>" />

				<%
				SiteInitializerRegistry siteInitializerRegistry = (SiteInitializerRegistry)request.getAttribute(PortalInstancesWebKeys.SITE_INITIALIZER_REGISTRY);

				List<SiteInitializer> siteInitializers = siteInitializerRegistry.getSiteInitializers(company.getCompanyId(), true);
				%>

				<c:if test="<%= !siteInitializers.isEmpty() %>">
					<aui:select label="virtual-instance-initializer" name="siteInitializerKey" showEmptyOption="<%= true %>">

						<%
						for (SiteInitializer siteInitializer : siteInitializers) {
						%>

							<aui:option label="<%= siteInitializer.getName(locale) %>" value="<%= siteInitializer.getKey() %>" />

						<%
						}
						%>

					</aui:select>
				</c:if>

				<c:if test="<%= Validator.isNull(PropsUtil.get(PropsKeys.DEFAULT_ADMIN_PASSWORD)) %>">
					<clay:sheet-section>
						<h3 class="sheet-subtitle">
							<liferay-ui:message key="administrator-user" />
						</h3>

						<aui:input label="field.screen-name" name="defaultAdminScreenName" required="<%= true %>" type="text" />

						<aui:input label="email-address" name="defaultAdminEmailAddress" required="<%= true %>" type="text" />

						<aui:input label="password" name="defaultAdminPassword" required="<%= true %>" type="password" />

						<%
						FullNameDefinition fullNameDefinition = FullNameDefinitionFactory.getInstance(locale);
						%>

						<c:if test='<%= fullNameDefinition.isFieldRequired("first-name") %>'>
							<aui:input label="first-name" name="defaultAdminFirstName" required="<%= true %>" type="text" value="<%= PropsUtil.get(PropsKeys.DEFAULT_ADMIN_FIRST_NAME) %>" />
						</c:if>

						<c:if test='<%= fullNameDefinition.isFieldRequired("middle-name") %>'>
							<aui:input label="middle-name" name="defaultAdminMiddleName" required="<%= true %>" type="text" value="<%= PropsUtil.get(PropsKeys.DEFAULT_ADMIN_MIDDLE_NAME) %>" />
						</c:if>

						<c:if test='<%= fullNameDefinition.isFieldRequired("last-name") %>'>
							<aui:input label="last-name" name="defaultAdminLastName" required="<%= true %>" type="text" value="<%= PropsUtil.get(PropsKeys.DEFAULT_ADMIN_LAST_NAME) %>" />
						</c:if>
					</clay:sheet-section>
				</c:if>
			</div>
		</div>

		<div class="add-instance-loading align-items-center d-none flex-column justify-content-center">
			<span aria-hidden="true" class="loading-animation mb-4"></span>

			<p class="text-3 text-center text-secondary"><liferay-ui:message key="the-creation-of-the-site-may-take-some-time-.closing-the-window-will-not-cancel-the-process" /></p>
		</div>

		<input hidden type="submit" />
	</liferay-frontend:edit-form>
</clay:container-fluid>

<liferay-frontend:component
	module="{AddInstance} from portal-instances-web"
/>