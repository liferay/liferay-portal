<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceShippingMethodsDisplayContext commerceShippingMethodsDisplayContext = (CommerceShippingMethodsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceShippingMethod commerceShippingMethod = commerceShippingMethodsDisplayContext.getCommerceShippingMethod();

long commerceShippingMethodId = 0;

if (commerceShippingMethod != null) {
	commerceShippingMethodId = commerceShippingMethod.getCommerceShippingMethodId();
}
%>

<portlet:actionURL name="/commerce_shipping_methods/edit_commerce_shipping_method" var="editCommerceShippingMethodActionURL" />

<aui:form action="<%= editCommerceShippingMethodActionURL %>" enctype="multipart/form-data" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceShippingMethodId <= 0) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commerceChannelId" type="hidden" value="<%= commerceShippingMethodsDisplayContext.getCommerceChannelId() %>" />
	<aui:input name="commerceShippingMethodId" type="hidden" value="<%= commerceShippingMethodId %>" />
	<aui:input name="commerceShippingMethodEngineKey" type="hidden" value="<%= commerceShippingMethodsDisplayContext.getCommerceShippingMethodEngineKey() %>" />

	<liferay-ui:error exception="<%= CommerceShippingMethodNameException.class %>" message="please-enter-a-valid-name" />

	<c:if test="<%= commerceShippingMethodsDisplayContext.getCommerceShippingOptionsCount() <= 0 %>">
		<div class="alert alert-warning">
			<liferay-ui:message key="there-are-no-shipping-options" />
			<liferay-ui:message key="please-configure-shipping-method" />
		</div>
	</c:if>

	<commerce-ui:panel>
		<aui:input label="name" localized="<%= true %>" name="nameMapAsXML" required="<%= true %>" type="text" value='<%= BeanParamUtil.getString(commerceShippingMethod, request, "name", commerceShippingMethodsDisplayContext.getCommerceShippingMethodEngineName(locale)) %>' />

		<aui:input label="description" localized="<%= true %>" name="descriptionMapAsXML" type="text" value='<%= BeanParamUtil.getString(commerceShippingMethod, request, "description", commerceShippingMethodsDisplayContext.getCommerceShippingMethodEngineDescription(locale)) %>' />

		<aui:model-context bean="<%= commerceShippingMethod %>" model="<%= CommerceShippingMethod.class %>" />

		<aui:input label="tracking-url" name="trackingURL" type="text" value='<%= (commerceShippingMethod == null) ? "" : HtmlUtil.escape(commerceShippingMethod.getTrackingURL()) %>' />

		<%
		String thumbnailSrc = StringPool.BLANK;

		if (commerceShippingMethod != null) {
			thumbnailSrc = commerceShippingMethod.getImageURL(themeDisplay);
		}
		%>

		<c:if test="<%= Validator.isNotNull(thumbnailSrc) %>">
			<div class="row">
				<div class="col-md-4">
					<img class="w-100" src="<%= HtmlUtil.escapeAttribute(thumbnailSrc) %>" />
				</div>
			</div>
		</c:if>

		<aui:input label="icon" name="imageFile" type="file" />

		<aui:input name="priority" />

		<aui:input checked="<%= (commerceShippingMethod == null) ? false : commerceShippingMethod.isActive() %>" inlineLabel="right" labelCssClass="simple-toggle-switch" name="active" type="toggle-switch" />
	</commerce-ui:panel>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>