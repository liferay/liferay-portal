<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String validDomains = ParamUtil.getString(request, "validDomains");

String message;

if (Validator.isNotNull(validDomains)) {
	message = "the-following-email-domains-are-shared-across-all-of-this-users-accounts";
}
else {
	message = "there-are-no-shared-email-domains-for-this-users-accounts";
}
%>

<clay:container-fluid>
	<clay:alert
		message="<%= message %>"
	/>

	<c:if test="<%= Validator.isNotNull(validDomains) %>">
		<liferay-ui:search-container>
			<liferay-ui:search-container-results
				results="<%= ListUtil.fromArray(StringUtil.split(validDomains)) %>"
			/>

			<liferay-ui:search-container-row
				className="java.lang.String"
				modelVar="domain"
			>
				<liferay-ui:search-container-column-text
					value="<%= HtmlUtil.escape(domain) %>"
				/>
			</liferay-ui:search-container-row>

			<liferay-ui:search-iterator
				markupView="lexicon"
			/>
		</liferay-ui:search-container>
	</c:if>
</clay:container-fluid>