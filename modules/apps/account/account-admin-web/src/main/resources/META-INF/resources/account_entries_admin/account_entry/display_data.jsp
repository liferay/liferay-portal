<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_DISPLAY);

String[] types = GetterUtil.getStringValues(request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_ALLOWED_TYPES), AccountConstants.ACCOUNT_ENTRY_TYPES);
%>

<clay:sheet-section>
	<aui:input name="emailAddress" type="hidden" value="<%= accountEntryDisplay.getEmailAddress() %>" />

	<h3 class="sheet-subtitle">
		<liferay-ui:message key="account-display-data" />
	</h3>

	<liferay-frontend:logo-selector
		currentLogoURL="<%= accountEntryDisplay.getLogoURL() %>"
		defaultLogoURL="<%= accountEntryDisplay.getDefaultLogoURL() %>"
		label='<%= LanguageUtil.get(request, "image") %>'
	/>

	<aui:input bean="<%= accountEntryDisplay %>" label="account-name" model="<%= AccountEntry.class %>" name="name" />

	<c:choose>
		<c:when test="<%= accountEntryDisplay.getAccountEntryId() > 0 %>">
			<aui:input disabled="<%= true %>" label="type" name="type" value="<%= LanguageUtil.get(request, accountEntryDisplay.getType()) %>" />
		</c:when>
		<c:otherwise>
			<aui:select label="type" name="type">

				<%
				for (String type : types) {
				%>

					<aui:option label="<%= LanguageUtil.get(request, type) %>" value="<%= type %>" />

				<%
				}
				%>

			</aui:select>
		</c:otherwise>
	</c:choose>

	<aui:input helpMessage="tax-id-help" label="tax-id" name="taxIdNumber" type="text" value="<%= accountEntryDisplay.getTaxIdNumber() %>">
		<aui:validator name="maxLength"><%= ModelHintsUtil.getMaxLength(AccountEntry.class.getName(), "taxIdNumber") %></aui:validator>
	</aui:input>

	<liferay-ui:error embed="<%= false %>" key="<%= DuplicateAccountEntryExternalReferenceCodeException.class.getName() %>" message="the-given-external-reference-code-belongs-to-another-account" />

	<aui:input bean="<%= accountEntryDisplay %>" label="external-reference-code" model="<%= AccountEntry.class %>" name="externalReferenceCode" />

	<c:if test="<%= accountEntryDisplay.getAccountEntryId() > 0 %>">
		<aui:input cssClass="disabled" label="account-id" name="accountEntryId" readonly="true" type="text" value="<%= String.valueOf(accountEntryDisplay.getAccountEntryId()) %>" />
	</c:if>

	<aui:field-wrapper cssClass="form-group lfr-input-text-container">
		<aui:input name="description" type="textarea" value="<%= accountEntryDisplay.getDescription() %>" />
	</aui:field-wrapper>
</clay:sheet-section>