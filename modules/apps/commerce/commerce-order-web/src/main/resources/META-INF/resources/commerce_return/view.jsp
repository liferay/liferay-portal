<%--
/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/init.jsp" %>

<%
CommerceReturnListDisplayContext commerceReturnListDisplayContext = (CommerceReturnListDisplayContext)request.getAttribute(WebKeys.PORTLET_DISPLAY_CONTEXT);

request.setAttribute("view.jsp-portletURL", commerceReturnListDisplayContext.getPortletURL());
%>

<div id="<portlet:namespace />returnDefinitionsContainer">
	<aui:form action="<%= commerceReturnListDisplayContext.getPortletURL() %>" method="post" name="fm">
		<aui:input name="redirect" type="hidden" value="<%= String.valueOf(commerceReturnListDisplayContext.getPortletURL()) %>" />

		<frontend-data-set:headless-display
			additionalProps='<%=
				HashMapBuilder.<String, Object>put(
					"namespace", liferayPortletResponse.getNamespace()
				).build()
			%>'
			apiURL="/o/commerce/returns?nestedFields=accountEntry"
			fdsActionDropdownItems="<%= commerceReturnListDisplayContext.getFDSActionDropdownItems() %>"
			fdsSortItemList="<%= commerceReturnListDisplayContext.getFDSSortItemList() %>"
			formName="fm"
			id="<%= CommerceReturnFDSNames.RETURNS %>"
			propsTransformer="{deleteCommerceReturnsPropsTransformer} from commerce-order-web"
			selectedItemsKey="id"
			selectionType="multiple"
			style="fluid"
		/>
	</aui:form>
</div>