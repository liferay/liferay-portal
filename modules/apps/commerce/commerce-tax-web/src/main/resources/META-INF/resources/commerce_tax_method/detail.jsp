<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceTaxMethodsDisplayContext commerceTaxMethodsDisplayContext = (CommerceTaxMethodsDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceTaxMethod commerceTaxMethod = commerceTaxMethodsDisplayContext.getCommerceTaxMethod();

long commerceTaxMethodId = 0;

if (commerceTaxMethod != null) {
	commerceTaxMethodId = commerceTaxMethod.getCommerceTaxMethodId();
}
%>

<portlet:actionURL name="/commerce_tax_methods/edit_commerce_tax_method" var="editCommerceTaxMethodActionURL" />

<aui:form action="<%= editCommerceTaxMethodActionURL %>" enctype="multipart/form-data" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceTaxMethod == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="commerceChannelId" type="hidden" value="<%= commerceTaxMethodsDisplayContext.getCommerceChannelId() %>" />
	<aui:input name="commerceTaxMethodId" type="hidden" value="<%= commerceTaxMethodId %>" />
	<aui:input name="commerceTaxMethodEngineKey" type="hidden" value="<%= commerceTaxMethodsDisplayContext.getCommerceTaxMethodEngineKey() %>" />

	<liferay-ui:error exception="<%= CommerceTaxMethodNameException.class %>" message="please-enter-a-valid-name" />

	<commerce-ui:panel>
		<aui:input label="name" localized="<%= true %>" name="nameMapAsXML" required="<%= true %>" type="text" value='<%= BeanParamUtil.getString(commerceTaxMethod, request, "name", CommerceTaxMethod::getName, commerceTaxMethodsDisplayContext.getCommerceTaxMethodEngineName(locale)) %>' />

		<aui:input label="description" localized="<%= true %>" name="descriptionMapAsXML" type="text" value='<%= BeanParamUtil.getString(commerceTaxMethod, request, "description", CommerceTaxMethod::getDescription, commerceTaxMethodsDisplayContext.getCommerceTaxMethodEngineDescription(locale)) %>' />

		<aui:model-context bean="<%= commerceTaxMethod %>" model="<%= CommerceTaxMethod.class %>" />

		<aui:input checked="<%= (commerceTaxMethod == null) ? false : commerceTaxMethod.getPercentage() %>" name="percentage" type="toggle-switch" />

		<aui:input checked="<%= (commerceTaxMethod == null) ? false : commerceTaxMethod.isActive() %>" name="active" type="toggle-switch" />
	</commerce-ui:panel>

	<aui:button-row>
		<aui:button cssClass="btn-lg" type="submit" />
	</aui:button-row>
</aui:form>