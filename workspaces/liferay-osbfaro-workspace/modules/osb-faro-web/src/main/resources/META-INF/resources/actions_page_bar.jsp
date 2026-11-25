<%--
/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
PageBarActionsDisplayContext pageBarActionsDisplayContext = new PageBarActionsDisplayContext(request);
%>

<div class="align-items-center d-flex">

	<%
	String settingsURL = pageBarActionsDisplayContext.getSettingsURL();
	%>

	<c:if test="<%= settingsURL != null %>">
		<clay:link
			aria-label='<%= LanguageUtil.get(request, "settings") %>'
			borderless="<%= true %>"
			displayType="secondary"
			href="<%= settingsURL %>"
			icon="cog"
			monospaced="<%= true %>"
			small="<%= true %>"
			title='<%= LanguageUtil.get(request, "settings") %>'
			type="button"
		/>

		<span class="align-self-stretch border-left mx-2 my-1"></span>
	</c:if>

	<clay:dropdown-menu
		borderless="<%= true %>"
		cssClass="rounded-lg text-nowrap"
		displayType="secondary"
		dropdownItems="<%= pageBarActionsDisplayContext.getLanguageDropdownItems() %>"
		icon="automatic-translate"
		label="<%= pageBarActionsDisplayContext.getLanguageLabel() %>"
		small="<%= true %>"
	/>

	<clay:dropdown-menu
		cssClass="border ml-2 rounded-circle text-secondary"
		displayType="unstyled"
		dropdownItems="<%= pageBarActionsDisplayContext.getUserDropdownItems() %>"
		label="<%= user.getInitials() %>"
		monospaced="<%= true %>"
	/>
</div>