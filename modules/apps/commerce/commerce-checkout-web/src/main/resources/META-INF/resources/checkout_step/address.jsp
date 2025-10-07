<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceContext commerceContext = (CommerceContext)request.getAttribute(CommerceWebKeys.COMMERCE_CONTEXT);

AccountEntry accountEntry = commerceContext.getAccountEntry();
CommerceOrder commerceOrder = commerceContext.getCommerceOrder();

BaseAddressCheckoutStepDisplayContext baseAddressCheckoutStepDisplayContext = (BaseAddressCheckoutStepDisplayContext)request.getAttribute(CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_DISPLAY_CONTEXT);

List<CommerceAddress> commerceAddresses = baseAddressCheckoutStepDisplayContext.getCommerceAddresses();

long commerceAddressId = baseAddressCheckoutStepDisplayContext.getCommerceAddressId(request);

CommerceAddress currentCommerceAddress = baseAddressCheckoutStepDisplayContext.getCommerceAddress(commerceAddressId);

boolean hasManageAddressesPermission = baseAddressCheckoutStepDisplayContext.hasPermission(permissionChecker, accountEntry, AccountActionKeys.MANAGE_ADDRESSES);
String paramName = baseAddressCheckoutStepDisplayContext.getParamName();
%>

<liferay-ui:error exception="<%= CommerceOrderDefaultBillingAddressException.class %>" message="no-default-billing-address" />

<c:if test="<%= !GetterUtil.getBoolean(request.getAttribute(CommerceCheckoutWebKeys.SHOW_ERROR_NO_BILLING_ADDRESS)) %>">
	<div class="form-group-autofit">
		<c:if test="<%= !commerceOrder.isGuestOrder() %>">
			<c:if test="<%= baseAddressCheckoutStepDisplayContext.hasPermission(permissionChecker, accountEntry, AccountActionKeys.VIEW_ADDRESSES) %>">
				<aui:select label='<%= "choose-" + baseAddressCheckoutStepDisplayContext.getTitle() %>' name="commerceAddress" onChange='<%= liferayPortletResponse.getNamespace() + "selectAddress();" %>' wrapperCssClass="commerce-form-group-item-row form-group-item">
					<c:choose>
						<c:when test="<%= hasManageAddressesPermission %>">
							<aui:option label="add-new-address" value="0" />
						</c:when>
						<c:otherwise>
							<aui:option label="choose-address" value="0" />
						</c:otherwise>
					</c:choose>

					<%
					for (CommerceAddress commerceAddress : commerceAddresses) {
					%>

						<aui:option data-city="<%= HtmlUtil.escapeAttribute(commerceAddress.getCity()) %>" data-country="<%= HtmlUtil.escapeAttribute(String.valueOf(commerceAddress.getCountryId())) %>" data-name="<%= HtmlUtil.escapeAttribute(commerceAddress.getName()) %>" data-phone-number="<%= HtmlUtil.escapeAttribute(commerceAddress.getPhoneNumber()) %>" data-region="<%= HtmlUtil.escapeAttribute(String.valueOf(commerceAddress.getRegionId())) %>" data-street-1="<%= HtmlUtil.escapeAttribute(commerceAddress.getStreet1()) %>" data-street-2="<%= Validator.isNotNull(commerceAddress.getStreet2()) ? HtmlUtil.escapeAttribute(commerceAddress.getStreet2()) : StringPool.BLANK %>" data-street-3="<%= Validator.isNotNull(commerceAddress.getStreet3()) ? HtmlUtil.escapeAttribute(commerceAddress.getStreet3()) : StringPool.BLANK %>" data-subtype="<%= commerceAddress.getSubtype() %>" data-type="<%= commerceAddress.getType() %>" data-zip="<%= HtmlUtil.escapeAttribute(commerceAddress.getZip()) %>" label="<%= HtmlUtil.escape(commerceAddress.getName()) %>" selected="<%= commerceAddressId == commerceAddress.getCommerceAddressId() %>" value="<%= commerceAddress.getCommerceAddressId() %>" />

					<%
					}
					%>

				</aui:select>
			</c:if>
		</c:if>

		<aui:input disabled="<%= commerceAddresses.isEmpty() ? true : false %>" name="<%= paramName %>" type="hidden" value="<%= commerceAddressId %>" />

		<aui:input name="newAddress" type="hidden" value='<%= ((commerceAddressId > 0) || !hasManageAddressesPermission) ? "0" : "1" %>' />
	</div>

	<liferay-ui:error exception="<%= CommerceAddressCityException.class %>" message="please-enter-a-valid-city" />
	<liferay-ui:error exception="<%= CommerceAddressCountryException.class %>" message="please-enter-a-valid-country" />
	<liferay-ui:error exception="<%= CommerceAddressNameException.class %>" message="please-enter-a-valid-name" />
	<liferay-ui:error exception="<%= CommerceAddressStreetException.class %>" message="please-enter-a-valid-street" />
	<liferay-ui:error exception="<%= CommerceAddressZipException.class %>" message="please-enter-a-valid-zip" />
	<liferay-ui:error exception="<%= CommerceOrderBillingAddressException.class %>" message="please-enter-a-valid-address" />
	<liferay-ui:error exception="<%= CommerceOrderShippingAddressException.class %>" message="please-enter-a-valid-address" />
	<liferay-ui:error exception="<%= CommerceOrderShippingAndBillingException.class %>" message="please-enter-a-valid-country-for-the-billing-address" />

	<aui:model-context bean="<%= baseAddressCheckoutStepDisplayContext.getCommerceAddress(commerceAddressId) %>" model="<%= CommerceAddress.class %>" />

	<div class="address-fields">

		<%
		boolean shippingUsedAsBilling = baseAddressCheckoutStepDisplayContext.isShippingUsedAsBilling();
		%>

		<div>
			<react:component
				module="{AddressSubtypeAutocomplete} from commerce-checkout-web"
				props="<%= baseAddressCheckoutStepDisplayContext.getContext(currentCommerceAddress, Objects.equals(CommerceCheckoutWebKeys.SHIPPING_ADDRESS_PARAM_NAME, paramName) ? (shippingUsedAsBilling || (commerceAddressId == 0) ? CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING : CommerceAddressConstants.ADDRESS_TYPE_SHIPPING) : CommerceAddressConstants.ADDRESS_TYPE_BILLING) %>"
			/>
		</div>

		<div class="form-group-autofit">
			<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="name" name="name" placeholder="name" wrapperCssClass="form-group-item" />

			<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="phone-number" name="phoneNumber" placeholder="phone-number" wrapperCssClass="form-group-item" />
		</div>

		<div class="form-group-autofit">
			<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="address" name="street1" placeholder="address" wrapperCssClass="form-group-item" />

			<aui:select disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="country" name="countryId" placeholder="country" title="country" wrapperCssClass="form-group-item">
				<aui:validator errorMessage='<%= LanguageUtil.get(request, "please-enter-a-valid-country") %>' name="min">1</aui:validator>
			</aui:select>
		</div>

		<c:choose>
			<c:when test="<%= (commerceAddressId > 0) && (!Validator.isBlank(currentCommerceAddress.getStreet2()) || !Validator.isBlank(currentCommerceAddress.getStreet3())) %>">
				<div class="form-group-autofit">
					<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="address-2" name="street2" placeholder="address-2" wrapperCssClass="form-group-item" />
					<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="address-3" name="street3" placeholder="address-3" wrapperCssClass="form-group-item" />
				</div>
			</c:when>
			<c:otherwise>
				<div class="add-street-link form-group-autofit">
					<aui:a hidden="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" href="javascript:void(0);" label="+-add-address-line" onClick='<%= liferayPortletResponse.getNamespace() + "addStreetAddress();" %>' />
				</div>

				<div class="add-street-fields form-group-autofit hide">
					<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="address-2" name="street2" placeholder="address-2" wrapperCssClass="form-group-item" />

					<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="address-3" name="street3" placeholder="address-3" wrapperCssClass="form-group-item" />
				</div>
			</c:otherwise>
		</c:choose>

		<div class="form-group-autofit">
			<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="zip" name="zip" placeholder="zip" wrapperCssClass="form-group-item" />

			<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="city" name="city" placeholder="city" wrapperCssClass="form-group-item" />

			<aui:select disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" label="region" name="regionId" placeholder="region" title="region" wrapperCssClass="form-group-item" />

			<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" id="commerceRegionIdInput" label="region" name="regionId" placeholder="regionId" title="region" wrapperCssClass="d-none form-group-item" />

			<aui:input disabled="<%= (commerceAddressId > 0) || !hasManageAddressesPermission %>" id="commerceRegionIdName" label="region" name="regionId" placeholder="regionName" title="region" wrapperCssClass="d-none form-group-item" />
		</div>

		<div class="form-group-autofit">
			<c:if test="<%= commerceOrder.isGuestOrder() %>">
				<aui:input name="email" required="<%= true %>" type="text" wrapperCssClass="form-group-item">
					<aui:validator name="email" />
				</aui:input>
			</c:if>
		</div>
	</div>

	<c:if test="<%= Objects.equals(CommerceCheckoutWebKeys.SHIPPING_ADDRESS_PARAM_NAME, paramName) && baseAddressCheckoutStepDisplayContext.hasPermission(permissionChecker, accountEntry, AccountActionKeys.MANAGE_ADDRESSES) && baseAddressCheckoutStepDisplayContext.hasViewBillingAddressPermission(permissionChecker, accountEntry) %>">
		<div class="shipping-as-billing">
			<aui:input checked="<%= shippingUsedAsBilling || (commerceAddressId == 0) %>" disabled="<%= false %>" label="use-shipping-address-as-billing-address" name="use-as-billing" type="checkbox" />
		</div>
	</c:if>

	<c:if test="<%= baseAddressCheckoutStepDisplayContext.isCommerceOrderMultishipping() && Objects.equals(CommerceCheckoutWebKeys.SHIPPING_ADDRESS_PARAM_NAME, paramName) %>">
		<div class="panel-body">
			<div class="h5">
				<liferay-ui:message key="delivery-groups" />
			</div>

			<frontend-data-set:classic-display
				contextParams='<%=
					HashMapBuilder.<String, String>put(
						"commerceOrderId", String.valueOf(commerceOrder.getCommerceOrderId())
					).build()
				%>'
				dataProviderKey="<%= CommerceCheckoutFDSNames.DELIVERY_GROUPS %>"
				id="<%= CommerceCheckoutFDSNames.DELIVERY_GROUPS %>"
				itemsPerPage="<%= 10 %>"
				propsTransformer="{DeliveryGroupFDSPropsTransformer} from commerce-checkout-web"
				selectedItemsKey="name"
				showManagementBar="<%= false %>"
				showSearch="<%= false %>"
			/>
		</div>
	</c:if>

	<aui:script>
		Liferay.provide(
			window,
			'<portlet:namespace />addStreetAddress',
			function <portlet:namespace />addStreetAddress() {
				const addStreetFields = document.querySelector('.add-street-fields');
				const addStreetLink = document.querySelector('.add-street-link');

				if (addStreetFields) {
					addStreetFields.classList.remove('hide');
				}
				if (addStreetLink) {
					addStreetLink.classList.add('hide');
				}
			},
			['aui-base']
		);

		Liferay.provide(
			window,
			'<portlet:namespace />clearAddressFields',
			function <portlet:namespace />clearAddressFields() {
				const addressFieldsInputs = document.querySelectorAll(
					'.address-fields input'
				);
				const addressFieldsSelect = document.querySelectorAll(
					'.address-fields select'
				);

				addressFieldsInputs.forEach((input) => {
					input.value = '';
				});

				addressFieldsSelect.forEach((select) => {
					select.selectedIndex = 0;
				});

				const useAsBillingField = document.getElementById(
					'<portlet:namespace />use-as-billing'
				);

				if (useAsBillingField) {
					useAsBillingField.checked = <%= shippingUsedAsBilling %>;
				}
			},
			['aui-base']
		);

		Liferay.provide(
			window,
			'<portlet:namespace />selectAddress',
			function <portlet:namespace />selectAddress() {
				const commerceAddress = document.getElementById(
					'<portlet:namespace />commerceAddress'
				);
				const commerceAddressParamName = document.getElementById(
					'<%= liferayPortletResponse.getNamespace() + paramName %>'
				);
				const newAddress = document.getElementById(
					'<portlet:namespace />newAddress'
				);

				if (newAddress && commerceAddress && commerceAddressParamName) {
					const commerceAddressVal = commerceAddress.value;

					if (commerceAddressVal === '0') {
						<portlet:namespace />clearAddressFields();

						if (<%= hasManageAddressesPermission %>) {
							<portlet:namespace />toggleAddressFields(false);
						}
					}
					else {
						<portlet:namespace />updateAddressFields(
							commerceAddress.selectedIndex
						);
						Liferay.Form.get(
							'<portlet:namespace />fm'
						).formValidator.validate();
					}

					commerceAddressParamName.value = commerceAddressVal;
					newAddress.value = Number(commerceAddressVal === '0');
				}
			},
			['aui-base']
		);

		Liferay.provide(
			window,
			'<portlet:namespace />toggleAddressFields',
			function <portlet:namespace />toggleAddressFields(state) {
				Liferay.Util.toggleDisabled(
					document.querySelectorAll(
						'.address-fields input:not([type=hidden]'
					),
					state
				);
				Liferay.Util.toggleDisabled(
					document.querySelectorAll('.address-fields select'),
					state
				);

				const commerceRegionIdInput = document.getElementById(
					'<portlet:namespace />commerceRegionIdInput'
				);
				const commerceRegionIdName = document.getElementById(
					'<portlet:namespace />commerceRegionIdName'
				);
				const commerceRegionIdSelect = document.getElementById(
					'<portlet:namespace />regionId'
				);

				commerceRegionIdInput.name = 'commerceRegionIdInputDisabled';
				commerceRegionIdName.name = 'commerceRegionIdInputDisabled';
				commerceRegionIdSelect.name = '<portlet:namespace />regionId';

				commerceRegionIdInput.parentElement.classList.add('d-none');
				commerceRegionIdName.parentElement.classList.add('d-none');
				commerceRegionIdSelect.parentElement.classList.remove('d-none');
			},
			['aui-base']
		);

		Liferay.provide(
			window,
			'<portlet:namespace />updateAddressFields',
			function <portlet:namespace />updateAddressFields(selectedVal) {
				if (!selectedVal || selectedVal === '0') {
					return;
				}

				const commerceAddress = document.getElementById(
					'<portlet:namespace />commerceAddress'
				);

				if (commerceAddress) {
					<portlet:namespace />addStreetAddress();
					<portlet:namespace />toggleAddressFields(true);

					const city = document.getElementById('<portlet:namespace />city');
					const countryId = document.getElementById(
						'<portlet:namespace />countryId'
					);
					const commerceRegionIdInput = document.getElementById(
						'<portlet:namespace />commerceRegionIdInput'
					);
					const commerceRegionIdName = document.getElementById(
						'<portlet:namespace />commerceRegionIdName'
					);
					const commerceRegionIdSelect = document.getElementById(
						'<portlet:namespace />regionId'
					);
					const name = document.getElementById('<portlet:namespace />name');
					const phoneNumber = document.getElementById(
						'<portlet:namespace />phoneNumber'
					);
					const street1 = document.getElementById(
						'<portlet:namespace />street1'
					);
					const street2 = document.getElementById(
						'<portlet:namespace />street2'
					);
					const street3 = document.getElementById(
						'<portlet:namespace />street3'
					);
					const zip = document.getElementById('<portlet:namespace />zip');

					if (
						city &&
						countryId &&
						commerceRegionIdInput &&
						commerceRegionIdSelect &&
						commerceRegionIdName &&
						name &&
						phoneNumber &&
						street1 &&
						street2 &&
						street3 &&
						zip
					) {
						const selectedOption =
							commerceAddress.options[commerceAddress.selectedIndex];

						city.value = selectedOption.dataset.city;
						commerceRegionIdInput.value = selectedOption.dataset.region;
						countryId.value = selectedOption.dataset.country;
						name.value = selectedOption.dataset.name;
						phoneNumber.value = selectedOption.dataset.phoneNumber;
						street1.value = selectedOption.dataset['street-1'];
						street2.value = selectedOption.dataset['street-2'];
						street3.value = selectedOption.dataset['street-3'];
						zip.value = selectedOption.dataset.zip;

						commerceRegionIdInput.name = '<portlet:namespace />regionId';
						commerceRegionIdName.name = 'commerceRegionIdNameIgnore';
						commerceRegionIdSelect.name = 'commerceRegionIdSelectIgnore';

						commerceRegionIdInput.parentElement.classList.add('d-none');
						commerceRegionIdName.parentElement.classList.remove('d-none');
						commerceRegionIdSelect.parentElement.classList.add('d-none');

						Liferay.Service(
							'/region/get-regions',
							{
								active: true,
								countryId: parseInt(selectedOption.dataset.country, 10),
							},
							function setUIOnlyInputRegionName(regions) {
								for (let i = 0; i < regions.length; i++) {
									if (
										regions[i].regionId ===
										selectedOption.dataset.region
									) {
										commerceRegionIdName.value = regions[i].name;

										break;
									}
								}
							}
						);

						const useAsBillingField = document.getElementById(
							'<portlet:namespace />use-as-billing'
						);

						if (useAsBillingField) {
							useAsBillingField.checked =
								selectedOption.dataset.type ==
								<%= CommerceAddressConstants.ADDRESS_TYPE_BILLING_AND_SHIPPING %>;
						}
					}
				}
			},
			['aui-base']
		);
	</aui:script>

	<aui:script>
		Liferay.component(
			'<portlet:namespace />countrySelects',
			new Liferay.DynamicSelect([
				{
					select: '<portlet:namespace />countryId',
					selectData: function (callback) {
						function injectCountryPlaceholder(list) {
							const callbackList = [
								{
									countryId: '0',
									nameCurrentValue:
										'- <liferay-ui:message key="select-country" />',
								},
							];

							list.forEach((listElement) => {
								callbackList.push(listElement);
							});

							callback(callbackList);
						}

						Liferay.Service(
							'/commerce.commercecountrymanagerimpl/<%= baseAddressCheckoutStepDisplayContext.getCommerceCountrySelectionMethodName() %>-by-channel-id',
							{
								channelId:
									<%= commerceContext.getCommerceChannelId() %>,
								end: -1,
								start: -1,
							},
							injectCountryPlaceholder
						);
					},
					selectDesc: 'nameCurrentValue',
					selectId: 'countryId',
					selectNullable: <%= false %>,
					selectSort: '<%= true %>',
					selectVal:
						'<%= BeanParamUtil.getLong(currentCommerceAddress, request, "countryId", 0) %>',
				},
				{
					select: '<portlet:namespace />regionId',
					selectData: function (callback, selectKey) {
						function injectRegionPlaceholder(list) {
							const callbackList = [
								{
									regionId: '0',
									name: '- <liferay-ui:message key="select-region" />',
									nameCurrentValue:
										'- <liferay-ui:message key="select-region" />',
								},
							];

							list.forEach((listElement) => {
								callbackList.push(listElement);
							});

							callback(callbackList);
						}

						Liferay.Service(
							'/region/get-regions',
							{
								active: true,
								countryId: Number(selectKey),
							},
							injectRegionPlaceholder
						);
					},
					selectDesc: 'name',
					selectId: 'regionId',
					selectNullable: <%= false %>,
					selectVal:
						'<%= BeanParamUtil.getLong(currentCommerceAddress, request, "regionId", 0) %>',
				},
			])
		);
	</aui:script>
</c:if>