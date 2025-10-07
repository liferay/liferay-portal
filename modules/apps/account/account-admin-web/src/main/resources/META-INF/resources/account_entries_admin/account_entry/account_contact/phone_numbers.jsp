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

List<Phone> phones = PhoneServiceUtil.getPhones(className, classPK);
%>

<clay:content-row
	cssClass="sheet-subtitle"
>
	<clay:content-col
		expand="<%= true %>"
	>
		<span class="heading-text"><liferay-ui:message key="phone-numbers" /></span>
	</clay:content-col>

	<clay:content-col>
		<span class="heading-end">
			<c:if test="<%= AccountEntryPermission.contains(permissionChecker, classPK, AccountActionKeys.MANAGE_ADDRESSES) %>">
				<clay:link
					aria-label='<%= LanguageUtil.format(request, "add-x", "phone-numbers") %>'
					cssClass="add-phone-number-link btn btn-secondary btn-sm"
					displayType="null"
					href='<%=
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCPath(
							"/account_entries_admin/account_entry/account_contact/edit_phone_number.jsp"
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
	curParam="phoneNumberCur"
	deltaParam="phoneNumbersDelta"
	emptyResultsMessage='<%= ParamUtil.getString(request, "emptyResultsMessage") %>'
	headerNames="phone-number,type,phone-extension,"
	id="phonesSearchContainer"
	iteratorURL="<%= currentURLObj %>"
	total="<%= phones.size() %>"
>
	<liferay-ui:search-container-results
		calculateStartAndEnd="<%= true %>"
		results="<%= phones %>"
	/>

	<liferay-ui:search-container-row
		className="com.liferay.portal.kernel.model.Phone"
		escapedModel="<%= true %>"
		keyProperty="phoneId"
		modelVar="phone"
	>
		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand"
			name="phone-number"
			property="number"
		/>

		<%
		ListType phoneListType = ListTypeServiceUtil.getListType(phone.getListTypeId());

		String phoneTypeKey = phoneListType.getName();
		%>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-small"
			name="type"
			value="<%= LanguageUtil.get(request, phoneTypeKey) %>"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-small"
			name="phone-extension"
			property="extension"
		/>

		<liferay-ui:search-container-column-text
			cssClass="table-cell-expand-smaller"
		>
			<c:if test="<%= phone.isPrimary() %>">
				<clay:label
					displayType="primary"
					label="primary"
				/>
			</c:if>
		</liferay-ui:search-container-column-text>

		<c:if test="<%= AccountEntryPermission.contains(permissionChecker, classPK, AccountActionKeys.MANAGE_ADDRESSES) %>">
			<liferay-ui:search-container-column-jsp
				cssClass="entry-action-column"
				path="/account_entries_admin/account_entry/account_contact/phone_number_action.jsp"
			/>
		</c:if>
	</liferay-ui:search-container-row>

	<liferay-ui:search-iterator
		markupView="lexicon"
	/>
</liferay-ui:search-container>