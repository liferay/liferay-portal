<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceOrderEditDisplayContext commerceOrderEditDisplayContext = (CommerceOrderEditDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceOrder commerceOrder = commerceOrderEditDisplayContext.getCommerceOrder();

boolean hasPermission = commerceOrderEditDisplayContext.hasModelPermission(commerceOrder, ActionKeys.UPDATE);
%>

<div class="row">
	<c:if test="<%= !commerceOrder.isOpen() %>">
		<div class="col-12 mb-4">
			<commerce-ui:step-tracker
				spritemap="<%= themeDisplay.getPathThemeSpritemap() %>"
				steps="<%= commerceOrderEditDisplayContext.getOrderSteps() %>"
			/>
		</div>
	</c:if>

	<div class="col-12">
		<commerce-ui:panel
			elementClasses="flex-fill"
			title='<%= LanguageUtil.get(request, "details") %>'
		>
			<div class="row vertically-divided">
				<div class="col-xl-3">
					<liferay-portlet:renderURL var="editNameURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_name" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<%
					String name = commerceOrder.getName();
					%>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "default"
							).build()
						%>'
						actionLabel='<%= hasPermission ? LanguageUtil.get(request, Validator.isNull(name) ? "add" : "edit") : null %>'
						actionUrl="<%= hasPermission ? editNameURL: null %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "name") %>'
					>
						<c:choose>
							<c:when test="<%= Validator.isNull(name) %>">
								<span class="text-muted">
									<liferay-ui:message key="click-add-to-insert" />
								</span>
							</c:when>
							<c:otherwise>
								<%= HtmlUtil.escape(name) %>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<%
					AccountEntry accountEntry = commerceOrder.getAccountEntry();
					%>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "account-info") %>'
					>
						<c:choose>
							<c:when test="<%= Validator.isNull(accountEntry) %>">
								<span class="text-muted">
									<%= StringPool.BLANK %>
								</span>
							</c:when>
							<c:otherwise>
								<p class="mb-0" data-qa-id="commerceOrderAccountEntryName"><%= HtmlUtil.escape(accountEntry.getName()) %></p>
								<p class="mb-0">#<%= accountEntry.getAccountEntryId() %></p>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<liferay-portlet:renderURL var="editPurchaseOrderNumberURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_purchase_order_number" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<%
					String purchaseOrderNumber = commerceOrder.getPurchaseOrderNumber();
					%>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "default"
							).build()
						%>'
						actionLabel='<%= hasPermission ? LanguageUtil.get(request, Validator.isNull(purchaseOrderNumber) ? "add" : "edit") : null %>'
						actionUrl="<%= hasPermission ? editPurchaseOrderNumberURL: null %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "purchase-order-number") %>'
					>
						<c:choose>
							<c:when test="<%= Validator.isNull(purchaseOrderNumber) %>">
								<span class="text-muted">
									<liferay-ui:message key="click-add-to-insert" />
								</span>
							</c:when>
							<c:otherwise>
								<%= HtmlUtil.escape(purchaseOrderNumber) %>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "channel") %>'
					>
						<%= HtmlUtil.escape(commerceOrderEditDisplayContext.getCommerceChannelName()) %>
					</commerce-ui:info-box>
				</div>

				<div class="col-xl-3">
					<liferay-portlet:renderURL var="editBillingAddressURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_billing_address" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<liferay-portlet:renderURL var="selectBillingAddressURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/select_commerce_order_billing_address" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<%
					CommerceAddress billingCommerceAddress = commerceOrder.getBillingAddress();

					String billingCommerceAddressActionLabel = null;
					String billingCommerceAddressActionTitle = null;
					String billingCommerceAddressActionURL = null;

					if (hasPermission) {
						billingCommerceAddressActionLabel = LanguageUtil.get(request, (billingCommerceAddress == null) ? "add" : "edit");
						billingCommerceAddressActionTitle = LanguageUtil.get(request, (billingCommerceAddress == null) ? "add-billing-address" : "edit-billing-address");

						billingCommerceAddressActionURL = editBillingAddressURL;

						if (commerceOrder.isOpen()) {
							billingCommerceAddressActionURL = selectBillingAddressURL;
						}
					}
					%>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"containerCssClasses", "modal-height-md"
							).put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "lg"
							).put(
								"title", billingCommerceAddressActionTitle
							).build()
						%>'
						actionLabel="<%= billingCommerceAddressActionLabel %>"
						actionUrl="<%= billingCommerceAddressActionURL %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "billing-address") %>'
					>
						<c:choose>
							<c:when test="<%= billingCommerceAddress == null %>">
								<span class="text-muted">
									<liferay-ui:message key="click-add-to-insert" />
								</span>
							</c:when>
							<c:otherwise>
								<c:if test="<%= !Validator.isBlank(billingCommerceAddress.getSubtype()) %>">
									<p class="mb-0">
										<%= HtmlUtil.escape(billingCommerceAddress.getSubtype(locale)) %>
									</p>
								</c:if>

								<p class="mb-0">
									<%= HtmlUtil.escape(billingCommerceAddress.getStreet1()) %>
								</p>

								<c:if test="<%= !Validator.isBlank(billingCommerceAddress.getStreet2()) %>">
									<p class="mb-0">
										<%= HtmlUtil.escape(billingCommerceAddress.getStreet2()) %>
									</p>

									<p class="mb-0">
										<%= HtmlUtil.escape(billingCommerceAddress.getStreet3()) %>
									</p>
								</c:if>

								<p class="mb-0">
									<%= commerceOrderEditDisplayContext.getDescriptiveAddress(billingCommerceAddress) %>
								</p>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<liferay-portlet:renderURL var="editShippingAddressURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_shipping_address" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<liferay-portlet:renderURL var="selectShippingAddressURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/select_commerce_order_shipping_address" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<%
					CommerceAddress shippingCommerceAddress = commerceOrder.getShippingAddress();

					String shippingCommerceAddressActionLabel = null;
					String shippingCommerceAddressActionTitle = null;
					String shippingCommerceAddressActionURL = null;

					if (hasPermission) {
						shippingCommerceAddressActionLabel = LanguageUtil.get(request, (shippingCommerceAddress == null) ? "add" : "edit");
						shippingCommerceAddressActionTitle = LanguageUtil.get(request, (shippingCommerceAddress == null) ? "add-shipping-address" : "edit-shipping-address");

						shippingCommerceAddressActionURL = editShippingAddressURL;

						if (commerceOrder.isOpen()) {
							shippingCommerceAddressActionURL = selectShippingAddressURL;
						}
					}
					%>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"containerCssClasses", "modal-height-md"
							).put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "lg"
							).put(
								"title", shippingCommerceAddressActionTitle
							).build()
						%>'
						actionLabel="<%= shippingCommerceAddressActionLabel %>"
						actionUrl="<%= shippingCommerceAddressActionURL %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "shipping-address") %>'
					>
						<c:choose>
							<c:when test="<%= shippingCommerceAddress == null %>">
								<span class="text-muted">
									<liferay-ui:message key="click-add-to-insert" />
								</span>
							</c:when>
							<c:otherwise>
								<c:if test="<%= !Validator.isBlank(shippingCommerceAddress.getSubtype()) %>">
									<p class="mb-0">
										<%= HtmlUtil.escape(shippingCommerceAddress.getSubtype(locale)) %>
									</p>
								</c:if>

								<p class="mb-0">
									<%= HtmlUtil.escape(shippingCommerceAddress.getStreet1()) %>
								</p>

								<c:if test="<%= !Validator.isBlank(shippingCommerceAddress.getStreet2()) %>">
									<p class="mb-0">
										<%= HtmlUtil.escape(shippingCommerceAddress.getStreet2()) %>
									</p>

									<p class="mb-0">
										<%= HtmlUtil.escape(shippingCommerceAddress.getStreet3()) %>
									</p>
								</c:if>

								<p class="mb-0">
									<%= commerceOrderEditDisplayContext.getDescriptiveAddress(shippingCommerceAddress) %>
								</p>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<liferay-portlet:renderURL var="editPaymentTermsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_payment_terms" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"containerCssClasses", "modal-height-md"
							).put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "xl"
							).put(
								"title", (commerceOrder.getPaymentCommerceTermEntryId() == 0) ? LanguageUtil.get(request, "payment-terms") : LanguageUtil.get(request, "edit-payment-terms")
							).build()
						%>'
						actionLabel='<%= commerceOrderEditDisplayContext.hasManageCommerceOrderPaymentTermsPermission() ? LanguageUtil.get(request, (commerceOrder.getPaymentCommerceTermEntryId() == 0) ? "add" : "edit") : null %>'
						actionUrl="<%= commerceOrderEditDisplayContext.hasManageCommerceOrderPaymentTermsPermission() ? editPaymentTermsURL : null %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "payment-terms") %>'
					>
						<c:choose>
							<c:when test="<%= commerceOrder.getPaymentCommerceTermEntryId() == 0 %>">
								<span class="text-muted">
									<liferay-ui:message key="click-add-to-insert" />
								</span>
							</c:when>
							<c:otherwise>
								<p class="mb-0">
									<%= commerceOrder.getPaymentCommerceTermEntryName() %>
								</p>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<liferay-portlet:renderURL var="editDeliveryTermsURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_delivery_terms" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"containerCssClasses", "modal-height-md"
							).put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "xl"
							).put(
								"title", (commerceOrder.getDeliveryCommerceTermEntryId() == 0) ? LanguageUtil.get(request, "delivery-terms") : LanguageUtil.get(request, "edit-delivery-terms")
							).build()
						%>'
						actionLabel='<%= commerceOrderEditDisplayContext.hasManageCommerceOrderDeliveryTermsPermission() ? LanguageUtil.get(request, (commerceOrder.getDeliveryCommerceTermEntryId() == 0) ? "add" : "edit") : null %>'
						actionUrl="<%= commerceOrderEditDisplayContext.hasManageCommerceOrderDeliveryTermsPermission() ? editDeliveryTermsURL : null %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "delivery-terms") %>'
					>
						<c:choose>
							<c:when test="<%= commerceOrder.getDeliveryCommerceTermEntryId() == 0 %>">
								<span class="text-muted">
									<liferay-ui:message key="click-add-to-insert" />
								</span>
							</c:when>
							<c:otherwise>
								<p class="mb-0">
									<%= commerceOrder.getDeliveryCommerceTermEntryName() %>
								</p>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>
				</div>

				<div class="col-xl-3">
					<c:if test="<%= commerceOrder.getOrderDate() != null %>">
						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "order-date") %>'
						>
							<%= commerceOrderEditDisplayContext.getCommerceOrderDateTime(commerceOrder.getOrderDate()) %>
						</commerce-ui:info-box>
					</c:if>

					<liferay-portlet:renderURL var="editRequestedDeliveryDateURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_requested_delivery_date" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<%
					Date requestedDeliveryDate = commerceOrder.getRequestedDeliveryDate();

					String requestedDeliveryDateActionLabel = null;
					String requestedDeliveryDateActionURL = null;

					if (hasPermission) {
						requestedDeliveryDateActionLabel = LanguageUtil.get(request, (requestedDeliveryDate == null) ? "add" : "edit");
						requestedDeliveryDateActionURL = editRequestedDeliveryDateURL;
					}
					%>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "default"
							).build()
						%>'
						actionLabel="<%= requestedDeliveryDateActionLabel %>"
						actionUrl="<%= requestedDeliveryDateActionURL %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "requested-delivery-date") %>'
					>
						<c:choose>
							<c:when test="<%= requestedDeliveryDate == null %>">
								<span class="text-muted">
									<liferay-ui:message key="click-add-to-insert" />
								</span>
							</c:when>
							<c:otherwise>
								<%= commerceOrderEditDisplayContext.getCommerceOrderDateTime(requestedDeliveryDate) %>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<commerce-ui:info-box
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "order-type") %>'
					>
						<%= HtmlUtil.escape(commerceOrderEditDisplayContext.getCommerceOrderTypeName(LanguageUtil.getLanguageId(locale))) %>
					</commerce-ui:info-box>
				</div>

				<div class="col-xl-3">
					<liferay-portlet:renderURL var="editPrintedNoteURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
						<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_printed_note" />
						<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
					</liferay-portlet:renderURL>

					<%
					String printedNote = commerceOrder.getPrintedNote();
					%>

					<commerce-ui:info-box
						actionContext='<%=
							HashMapBuilder.<String, Object>put(
								"containerCssClasses", "modal-height-md"
							).put(
								"namespace", liferayPortletResponse.getNamespace()
							).put(
								"refreshOnClose", true
							).put(
								"size", "default"
							).build()
						%>'
						actionLabel='<%= hasPermission ? LanguageUtil.get(request, Validator.isNull(printedNote) ? "add" : "edit") : null %>'
						actionUrl="<%= hasPermission ? editPrintedNoteURL: null %>"
						elementClasses="py-3"
						title='<%= LanguageUtil.get(request, "printed-note") %>'
					>
						<c:choose>
							<c:when test="<%= Validator.isNull(printedNote) %>">
								<span class="text-muted">
									<liferay-ui:message key="click-add-to-insert" />
								</span>
							</c:when>
							<c:otherwise>
								<%= HtmlUtil.escape(printedNote) %>
							</c:otherwise>
						</c:choose>
					</commerce-ui:info-box>

					<c:if test="<%= Validator.isNotNull(commerceOrder.getAdvanceStatus()) %>">
						<commerce-ui:info-box
							elementClasses="py-3"
							title='<%= LanguageUtil.get(request, "external-order-status") %>'
						>
							<%= HtmlUtil.escape(commerceOrder.getAdvanceStatus()) %>
						</commerce-ui:info-box>
					</c:if>
				</div>
			</div>
		</commerce-ui:panel>
	</div>

	<div class="col-12">
		<commerce-ui:panel
			bodyClasses="p-0"
			title='<%= LanguageUtil.get(request, "items") %>'
		>
			<frontend-data-set:classic-display
				contextParams='<%=
					HashMapBuilder.<String, String>put(
						"commerceOrderId", String.valueOf(commerceOrder.getCommerceOrderId())
					).build()
				%>'
				dataProviderKey="<%= CommerceOrderFDSNames.ORDER_ITEMS %>"
				id="<%= CommerceOrderFDSNames.ORDER_ITEMS %>"
				itemsPerPage="<%= 10 %>"
				nestedItemsKey="orderItemId"
				nestedItemsReferenceKey="orderItems"
				propsTransformer="{CommerceOrderItemsFDSPropsTransformer} from commerce-order-web"
			/>
		</commerce-ui:panel>
	</div>

	<div class="col-12">
		<liferay-portlet:renderURL var="editOrderSummaryURL" windowState="<%= LiferayWindowState.POP_UP.toString() %>">
			<portlet:param name="mvcRenderCommandName" value="/commerce_order/edit_commerce_order_summary" />
			<portlet:param name="commerceOrderId" value="<%= String.valueOf(commerceOrderEditDisplayContext.getCommerceOrderId()) %>" />
		</liferay-portlet:renderURL>

		<commerce-ui:modal
			id="order-summary-modal"
			refreshPageOnClose="<%= true %>"
			size="lg"
			title='<%= LanguageUtil.get(request, "order-summary") %>'
			url="<%= editOrderSummaryURL %>"
		/>

		<commerce-ui:panel
			actionLabel='<%= commerceOrderEditDisplayContext.hasManageCommerceOrderPricesPermission() ? LanguageUtil.get(request, "edit") : null %>'
			actionTargetId="order-summary-modal"
			actionUrl="<%= commerceOrderEditDisplayContext.hasManageCommerceOrderPricesPermission() ? editOrderSummaryURL : null %>"
			title='<%= LanguageUtil.get(request, "order-summary") %>'
		>
			<div id="summary-root"></div>

			<liferay-frontend:component
				context='<%=
					HashMapBuilder.<String, Object>put(
						"commerceOrderId", commerceOrderEditDisplayContext.getCommerceOrderId()
					).put(
						"dataSetDisplayId", CommerceOrderFDSNames.ORDER_ITEMS
					).put(
						"portletId", portletDisplay.getRootPortletId()
					).build()
				%>'
				module="{summary} from commerce-order-web"
			/>
		</commerce-ui:panel>
	</div>
</div>