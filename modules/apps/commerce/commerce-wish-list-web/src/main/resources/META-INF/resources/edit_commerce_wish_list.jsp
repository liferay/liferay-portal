<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceWishListDisplayContext commerceWishListDisplayContext = (CommerceWishListDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

CommerceWishList commerceWishList = commerceWishListDisplayContext.getCommerceWishList();
%>

<portlet:actionURL name="/commerce_wish_list_content/edit_commerce_wish_list" var="editCommerceWishListActionURL" />

<aui:form action="<%= editCommerceWishListActionURL %>" cssClass="container-fluid container-fluid-max-xl" method="post" name="fm">
	<aui:input name="<%= Constants.CMD %>" type="hidden" value="<%= (commerceWishList == null) ? Constants.ADD : Constants.UPDATE %>" />
	<aui:input name="redirect" type="hidden" value="<%= redirect %>" />
	<aui:input name="commerceWishListId" type="hidden" value="<%= commerceWishListDisplayContext.getCommerceWishListId() %>" />

	<liferay-ui:error exception="<%= CommerceWishListNameException.class %>" message="please-enter-a-valid-name" />

	<aui:model-context bean="<%= commerceWishList %>" model="<%= CommerceWishList.class %>" />

	<div class="sheet">
		<div class="panel-group panel-group-flush">
			<aui:fieldset>
				<aui:input name="name" />

				<aui:input checked='<%= BeanParamUtil.getBoolean(commerceWishList, request, "defaultWishList") %>' inlineLabel="right" label="default" labelCssClass="simple-toggle-switch" name="defaultWishList" type="toggle-switch" />
			</aui:fieldset>
		</div>
	</div>

	<aui:button-row>
		<liferay-frontend:edit-form-buttons
			redirect="<%= redirect %>"
		/>
	</aui:button-row>
</aui:form>