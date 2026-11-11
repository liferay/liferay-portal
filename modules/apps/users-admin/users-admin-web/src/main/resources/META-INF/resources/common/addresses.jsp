<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
String className = (String)request.getAttribute("contact_information.jsp-className");
long classPK = (long)request.getAttribute("contact_information.jsp-classPK");

String emptyResultsMessage = ParamUtil.getString(request, "emptyResultsMessage");

List<Address> addresses = AddressServiceUtil.getAddresses(className, classPK);
%>

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
				<clay:link
					aria-label='<%= LanguageUtil.format(request, "add-x", "addresses") %>'
					cssClass="add-address-link btn btn-secondary btn-sm"
					displayType="null"
					href='<%=
						PortletURLBuilder.createRenderURL(
							liferayPortletResponse
						).setMVCPath(
							"/common/edit_address.jsp"
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
			</span>
		</clay:content-col>
	</clay:content-row>
</clay:sheet-header>

<c:if test="<%= addresses.isEmpty() %>">
	<div class="contact-information-empty-results-message-wrapper">
		<liferay-frontend:empty-result-message
			animationType="<%= AnimationType.EMPTY %>"
			title="<%= LanguageUtil.get(resourceBundle, emptyResultsMessage) %>"
		/>
	</div>
</c:if>

<div
	class="<%=
		CSSClasses.builder(
			"addresses-table-wrapper"
		).add(
			"hide", addresses.isEmpty()
		).build()
	%>"
>
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

				<clay:content-col
					cssClass="lfr-search-container-wrapper"
				>
					<liferay-util:include page="/common/address_action.jsp" servletContext="<%= application %>">
						<liferay-util:param name="addressId" value="<%= String.valueOf(address.getAddressId()) %>" />
					</liferay-util:include>
				</clay:content-col>
			</li>

		<%
		}
		%>

	</ul>
</div>