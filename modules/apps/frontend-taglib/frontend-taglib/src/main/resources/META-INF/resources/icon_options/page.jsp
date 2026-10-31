<%--
/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */
--%>

<%@ include file="/icon_options/init.jsp" %>

<%
IconOptionsTag iconOptionsTag = (IconOptionsTag)request.getAttribute(IconOptionsTag.class.getName());
%>

<clay:dropdown-menu
	aria-label='<%= LanguageUtil.get(request, "options") %>'
	borderless="<%= true %>"
	cssClass='<%= iconOptionsTag.getCssClass() + " component-action portlet-options" %>'
	displayType="secondary"
	dropdownItems="<%= iconOptionsTag.getDropdownItems() %>"
	icon="ellipsis-v"
	monospaced="<%= iconOptionsTag.isMonospaced() %>"
	propsTransformer="{PortletOptionsDropdownPropsTransformer} from frontend-taglib"
	small="<%= true %>"
	title='<%= LanguageUtil.get(request, "options") %>'
/>

<%
for (PortletConfigurationIcon portletConfigurationIcon : iconOptionsTag.getPortletConfigurationIcons()) {
	portletConfigurationIcon.include(request, PipingServletResponseFactory.createPipingServletResponse(pageContext));
}
%>