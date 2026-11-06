<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
AccountEntryDisplay accountEntryDisplay = (AccountEntryDisplay)request.getAttribute(AccountWebKeys.ACCOUNT_ENTRY_DISPLAY);

AccountEntry accountEntry = AccountEntryLocalServiceUtil.getAccountEntry(accountEntryDisplay.getAccountEntryId());

String className = AccountEntry.class.getName();
long classPK = accountEntry.getAccountEntryId();

List<EmailAddress> emailAddresses = EmailAddressServiceUtil.getEmailAddresses(className, classPK);
%>

<clay:content-row
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="email-addresses" /></span>
	</clay:content-col>

	<clay:content-col>
		<span class="heading-end">
			<c:if test="<%= AccountEntryPermission.contains(permissionChecker, classPK, AccountActionKeys.MANAGE_ADDRESSES) %>">
				<clay:link
					aria-label='<%= LanguageUtil.format(request, "add-x", "email-addresses") %>'
					cssClass="add-email-address-link btn btn-secondary btn-sm"
					displayType="null"
					href='<%=
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCPath(
							"/account_entries_admin/account_entry/account_contact/edit_email_address.jsp"
						).setRedirect(
							currentURL
						).setParameter(
							"className", className
						).setParameter(
							"classPK", classPK
						).buildString()
					%>'
					label="add"
					role="button"
				/>
			</c:if>
		</span>
	</clay:content-col>
</clay:content-row>

<liferay-ui:search-container
	compactEmptyResultsMessage="<%= true %>"
	cssClass="lfr-search-container-wrapper"
	curParam="emailAddressesCur"
	deltaParam="emailAddressesDelta"
	emptyResultsMessage='<%= ParamUtil.getString(request, "emptyResultsMessage") %>'
	headerNames="email-address,type,"
	id="emailAddressesSearchContainer"
	iteratorURL="<%= currentURLObj %>"
	total="<%= emailAddresses.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= emailAddresses %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.EmailAddress"
		escapedModel="<%= true %>"
		keyPropertyName="emailAddressId"
		modelVar="emailAddress"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="email-address"
			property="address"
		/>

		<%
		ListType emailAddressListType = ListTypeServiceUtil.getListType(emailAddress.getListTypeId());

		String emailAddressTypeKey = emailAddressListType.getName();
		%>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-small"
			name="type"
			value="<%= LanguageUtil.get(request, emailAddressTypeKey) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-smaller"
		>
			<c:if test="<%= emailAddress.isPrimary() %>">
				<clay:label
					displayType="primary"
					label="primary"
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<c:if test="<%= AccountEntryPermission.contains(permissionChecker, classPK, AccountActionKeys.MANAGE_ADDRESSES) %>">
			<liferay-ui:search-container-column-jsp
				cssClass="entry-action-column"
				path="/account_entries_admin/account_entry/account_contact/email_address_action.jsp"
			/>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>