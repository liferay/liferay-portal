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

List<String> addressTypes = ListUtil.fromArray(AccountListTypeConstants.ACCOUNT_ENTRY_CONTACT_ADDRESS_TYPE_BILLING, AccountListTypeConstants.ACCOUNT_ENTRY_CONTACT_ADDRESS_TYPE_OTHER, AccountListTypeConstants.ACCOUNT_ENTRY_CONTACT_ADDRESS_TYPE_P_O_BOX, AccountListTypeConstants.ACCOUNT_ENTRY_CONTACT_ADDRESS_TYPE_SHIPPING);

long[] listTypeAddresses = TransformUtil.transformToLongArray(
	addressTypes,
	addressType -> {
		ListType listType = ListTypeLocalServiceUtil.getListType(accountEntry.getCompanyId(), addressType, AccountListTypeConstants.ACCOUNT_ENTRY_CONTACT_ADDRESS);

		return listType.getListTypeId();
	});

List<Address> addresses = accountEntry.getListTypeAddresses(listTypeAddresses);

portletDisplay.setShowBackIcon(true);
portletDisplay.setURLBack(ParamUtil.getString(request, "backURL", String.valueOf(renderResponse.createRenderURL())));
%>

<portlet:actionURL name="/account_admin/edit_account_entry_contact" var="editAccountEntryContactURL" />

<liferay-frontend:edit-form
	action="<%= editAccountEntryContactURL %>"
>
	<aui:input name="classPK" type="hidden" value="<%= String.valueOf(accountEntry.getAccountEntryId()) %>" />

	<liferay-frontend:edit-form-body>
		<clay:sheet-header>
			<clay:content-row
				cssClass="sheet-title"
			>
				<clay:content-col
					expand="<%= true %>"
				>
					<h2 class="heading-text"><liferay-ui:message key="addresses" /></h2>
				</clay:content-col>

				<clay:content-col>
					<span class="heading-end">
						<c:if test="<%= AccountEntryPermission.contains(permissionChecker, classPK, AccountActionKeys.MANAGE_ADDRESSES) %>">
							<clay:link
								aria-label='<%= LanguageUtil.format(request, "add-x", "addresses") %>'
								cssClass="add-address-link btn btn-secondary btn-sm"
								displayType="null"
								href='<%=
									PortletURLBuilder.createRenderURL(
										liferayPortletResponse
									).setMVCPath(
										"/account_entries_admin/account_entry/account_contact/edit_address.jsp"
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
		</clay:sheet-header>

		<c:if test="<%= addresses.isEmpty() %>">
			<div class="contact-information-empty-results-message-wrapper">
				<liferay-frontend:empty-result-message
					animationType="<%= AnimationType.EMPTY %>"
					title='<%= LanguageUtil.get(resourceBundle, "this-account-does-not-have-any-addresses") %>'
				/>
			</div>
		</c:if>

		<div class="<%= addresses.isEmpty() ? "addresses-table-wrapper hide" : "addresses-table-wrapper" %>">
			<ul class="list-group list-group-flush">

				<%
				for (Address address : addresses) {
				%>

					<li class="list-group-item list-group-item-flex">
						<clay:content-col>
							<clay:sticker
								cssClass="sticker-static"
								displayType="secondary"
								icon="picture"
							/>
						</clay:content-col>

						<clay:content-col
							expand="<%= true %>"
						>
							<span class="h3">

								<%
								ListType listType = address.getListType();
								%>

								<liferay-ui:message key="<%= listType.getName() %>" />
							</span>

							<div class="address-display-wrapper list-group-text">
								<liferay-text-localizer:address-display
									address="<%= address %>"
								/>
							</div>

							<c:if test="<%= address.isPrimary() %>">
								<div class="address-primary-label-wrapper">
									<clay:label
										displayType="primary"
										label="primary"
									/>
								</div>
							</c:if>
						</clay:content-col>

						<c:if test="<%= AccountEntryPermission.contains(permissionChecker, classPK, AccountActionKeys.MANAGE_ADDRESSES) %>">
							<clay:content-col
								cssClass="lfr-search-container-wrapper"
							>
								<liferay-util:include page="/account_entries_admin/account_entry/account_contact/address_action.jsp" servletContext="<%= application %>">
									<liferay-util:param name="addressId" value="<%= String.valueOf(address.getAddressId()) %>" />
								</liferay-util:include>
							</clay:content-col>
						</c:if>
					</li>

				<%
				}
				%>

			</ul>
		</div>
	</liferay-frontend:edit-form-body>
</liferay-frontend:edit-form>