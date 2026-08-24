<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<portlet:actionURL name="/portal_instances/add_db_partition_company" var="addDBPartitionCompanyURL" />

<div class="add-db-partition-company-alert-container"></div>

<clay:container-fluid>
	<liferay-frontend:edit-form
		action="<%= addDBPartitionCompanyURL %>"
		method="post"
		name="fm"
		onSubmit="event.preventDefault();"
		validateOnBlur="<%= false %>"
	>
		<div class="add-db-partition-company-content">
			<div class="px-4 py-2">
				<aui:input label="schema-name" name="schemaName" required="<%= true %>" type="text" />

				<aui:input name="name" type="text" />

				<aui:input label="virtual-host" name="virtualHostname" type="text" />

				<aui:input label="web-id" name="webId" type="text" />
			</div>
		</div>

		<div class="add-db-partition-company-loading align-items-center d-none flex-column justify-content-center">
			<span aria-hidden="true" class="loading-animation mb-4"></span>

			<p class="text-3 text-center text-secondary"><liferay-ui:message key="the-creation-of-the-instance-may-take-some-time-.closing-the-window-will-not-cancel-the-process" /></p>
		</div>

		<input hidden type="submit" />
	</liferay-frontend:edit-form>
</clay:container-fluid>

<liferay-frontend:component
	module="{AddDBPartitionCompany} from portal-instances-web"
/>