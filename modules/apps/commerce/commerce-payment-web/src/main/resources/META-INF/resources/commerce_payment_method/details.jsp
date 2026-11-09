<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommercePaymentMethodGroupRelsDisplayContext commercePaymentMethodsDisplayContext = (CommercePaymentMethodGroupRelsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommercePaymentMethodGroupRel commercePaymentMethodGroupRel = commercePaymentMethodsDisplayContext.getCommercePaymentMethodGroupRel();
%>

<portlet:actionURL name="/commerce_payment_methods/edit_commerce_payment_method_group_rel" var="commercePaymentMethodGroupRelActionURL" />

<aui:form action="<%= commercePaymentMethodGroupRelActionURL %>" enctype="multipart/form-data" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commercePaymentMethodGroupRel != null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commerceChannelId" type="hidden" value="<%= commercePaymentMethodsDisplayContext.getCommerceChannelId() %>" />
	<aui:input name="commercePaymentIntegrationKey" type="hidden" value="<%= commercePaymentMethodsDisplayContext.getCommercePaymentIntegrationKey() %>" />
	<aui:input name="commercePaymentMethodEngineKey" type="hidden" value="<%= commercePaymentMethodsDisplayContext.getCommercePaymentMethodEngineKey() %>" />
	<aui:input name="commercePaymentMethodGroupRelId" type="hidden" value="<%= commercePaymentMethodsDisplayContext.getCommercePaymentMethodGroupRelId() %>" />

	<liferay-ui:error exception="<%= CommercePaymentMethodGroupRelNameException.class %>" message="please-enter-a-valid-name" />

	<commerce-ui:panel>
		<aui:input label="name" localized="<%= true %>" name="nameMapAsXML" required="<%= true %>" type="text" value='<%= BeanParamUtil.getString(commercePaymentMethodGroupRel, request, "name", commercePaymentMethodsDisplayContext.getCommercePaymentMethodEngineName(locale)) %>' />

		<aui:input label="description" localized="<%= true %>" name="descriptionMapAsXML" placeholder="<%= commercePaymentMethodsDisplayContext.getCommercePaymentMethodEngineDescription(locale) %>" type="text" value='<%= BeanParamUtil.getString(commercePaymentMethodGroupRel, request, "description", commercePaymentMethodsDisplayContext.getCommercePaymentMethodEngineDescription(locale)) %>' />

		<aui:model-context bean="<%= commercePaymentMethodGroupRel %>" model="<%= CommercePaymentMethodGroupRel.class %>" />

		<%
		String thumbnailSrc = null;

		if (commercePaymentMethodGroupRel != null) {
			thumbnailSrc = commercePaymentMethodGroupRel.getImageURL(themeDisplay);
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

		<aui:input checked="<%= (commercePaymentMethodGroupRel == null) ? false : commercePaymentMethodGroupRel.isActive() %>" name="active" type="toggle-switch" />
	</commerce-ui:panel>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>