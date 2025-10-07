<%--
/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/layout/init.jsp" %>

<clay:alert
	cssClass="alert-inline justify-content-between ml-3 mt-4 p-3"
	defaultTitleDisabled="<%= true %>"
	displayType="warning"
>
	<div class="align-items-center d-flex flex-row">
		<liferay-ui:message key="empty-layout-alert-message" />

		<clay:link
			cssClass="alert-btn btn btn-secondary ml-4"
			href='<%= (String)request.getAttribute("editURL") %>'
			label='<%= LanguageUtil.get(request, "edit-page") %>'
			role="button"
		/>
	</div>
</clay:alert>

<liferay-layout:layout-common />