<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="/portal_instances/copy_instance" var="copyInstanceURL" />

<div class="copy-instance-alert-container"></div>

<clay:container-fluid>
	<liferay-frontend:edit-form
		action="<%= copyInstanceURL %>"
		method="post"
		name="fm"
		onSubmit="event.preventDefault();"
		validateOnBlur="<%= false %>"
	>
		<div class="copy-instance-content">
			<div class="px-4 py-2">
				<aui:model-context model="<%= Company.class %>" />

				<aui:input name="name" required="<%= true %>" type="text" />

				<aui:input name="sourceCompanyId" type="hidden" value='<%= ParamUtil.getLong(request, "companyId") %>' />

				<aui:input fieldParam="virtualHostname" label="virtual-host" model="<%= VirtualHost.class %>" name="hostname" required="<%= true %>" />

				<aui:input name="webId" required="<%= true %>" />

				<aui:input helpMessage="destination-company-id-help" label="destination-company-id" name="destinationCompanyId" type="text" />
			</div>
		</div>

		<div class="align-items-center copy-instance-loading d-none flex-column justify-content-center">
			<span aria-hidden="true" class="loading-animation mb-4"></span>

			<p class="text-3 text-center text-secondary"><liferay-ui:message key="the-creation-of-the-instance-may-take-some-time-.closing-the-window-will-not-cancel-the-process" /></p>
		</div>

		<input hidden type="submit" />
	</liferay-frontend:edit-form>
</clay:container-fluid>

<liferay-frontend:component
	module="{CopyInstance} from portal-instances-web"
/>