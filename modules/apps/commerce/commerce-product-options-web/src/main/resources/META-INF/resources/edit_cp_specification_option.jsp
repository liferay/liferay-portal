<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CPSpecificationOption cpSpecificationOption = (CPSpecificationOption)request.getAttribute(CPWebKeys.CP_SPECIFICATION_OPTION);

long cpSpecificationOptionId = BeanParamUtil.getLong(cpSpecificationOption, request, "CPSpecificationOptionId");

renderResponse.setTitle(LanguageUtil.get(request, "specifications"));
%>

<portlet:actionURL name="/cp_specification_options/edit_cp_specification_option" var="editProductSpecificationOptionActionURL" />

<aui:form action="<%= editProductSpecificationOptionActionURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (cpSpecificationOption == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="cpSpecificationOptionId" type="hidden" value="<%= String.valueOf(cpSpecificationOptionId) %>" />

	<div class="lfr-form-content">
		<liferay-frontend:form-navigator
			backURL="<%= portletDisplay.getURLBack() %>"
			formModelBean="<%= cpSpecificationOption %>"
			id="<%= CPSpecificationOptionFormNavigatorConstants.FORM_NAVIGATOR_ID_COMMERCE_PRODUCT_SPECIFICATION_OPTION %>"
		/>
	</div>
</aui:form>

<liferay-frontend:component
	module="{EditCPSpecificationOption} from commerce-product-options-web"
/>