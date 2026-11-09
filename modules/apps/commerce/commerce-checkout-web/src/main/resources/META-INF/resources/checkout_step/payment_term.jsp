<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
TermCommerceCheckoutStepDisplayContext termCommerceCheckoutStepDisplayContext = (TermCommerceCheckoutStepDisplayContext)request.getAttribute(CommerceCheckoutWebKeys.COMMERCE_CHECKOUT_STEP_DISPLAY_CONTEXT);

List<CommerceTermEntry> paymentCommerceTermEntries = termCommerceCheckoutStepDisplayContext.getPaymentCommerceTermEntries();

long paymentCommerceTermEntryId = BeanParamUtil.getLong(termCommerceCheckoutStepDisplayContext.getCommerceOrder(), request, "paymentCommerceTermEntryId");
%>

<div id="commercePaymentTermContainer">
	<c:choose>
		<c:when test="<%= paymentCommerceTermEntries.isEmpty() %>">
			<clay:row>
				<clay:col
					size="12"
				>
					<clay:alert
						message="there-are-no-available-payment-terms"
					/>
				</clay:col>
			</clay:row>

			<aui:script use="aui-base">
				var continueButton = A.one('#<portlet:namespace />continue');

				if (continueButton) {
					Liferay.Util.toggleDisabled(continueButton, true);
				}
			</aui:script>
		</c:when>
		<c:otherwise>
			<liferay-ui:error key="paymentTermsInvalid" message="please-select-payment-terms" />

			<ul class="list-group">

				<%
				for (CommerceTermEntry commerceTermEntry : paymentCommerceTermEntries) {
					String paymentTermsId = liferayPortletResponse.getNamespace() + "item_" + commerceTermEntry.getCommerceTermEntryId();
				%>

					<li class="commerce-payment-types list-group-item list-group-item-flex">
						<div class="autofit-col autofit-col-expand p-2 pl-3">
							<aui:input checked="<%= paymentCommerceTermEntryId == commerceTermEntry.getCommerceTermEntryId() %>" cssClass="mr-3" label="<%= commerceTermEntry.getLabel(LanguageUtil.getLanguageId(locale)) %>" labelCssClass="align-items-center d-inline-flex mb-0" name="paymentCommerceTermEntryId" type="radio" value="<%= commerceTermEntry.getCommerceTermEntryId() %>" />
						</div>

						<div class="autofit-col p-2 pr-3">
							<a href="#" id="<%= paymentTermsId %>"><liferay-ui:message key="more-info"></liferay-ui:message></a>

							<liferay-frontend:component
								context='<%=
									HashMapBuilder.<String, Object>put(
										"HTMLElementId", paymentTermsId
									).put(
										"modalContent", commerceTermEntry.getDescription(LanguageUtil.getLanguageId(locale))
									).put(
										"modalTitle", commerceTermEntry.getLabel(LanguageUtil.getLanguageId(locale))
									).build()
								%>'
								module="{attachModalToHTMLElement} from commerce-checkout-web"
							/>
						</div>
					</li>

				<%
				}
				%>

			</ul>
		</c:otherwise>
	</c:choose>
</div>